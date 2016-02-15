/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.local;

import com.lahenry.nimbus.io.InputStreamProgress;
import com.lahenry.nimbus.clouds.interfaces.ICloudModel;
import com.lahenry.nimbus.clouds.interfaces.ICloudProgress;
import com.lahenry.nimbus.clouds.interfaces.ICloudTransfer;
import com.lahenry.nimbus.io.OutputToInputStream;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Histogram;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author henry
 */
public class LocalModel implements ICloudModel<java.io.File>
{
    private static final Logit LOG = Logit.create(LocalModel.class.getName());

    public LocalModel()
    {
    }

    @Override
    public GlobalCacheKey getGlobalCacheKey()
    {
        return GlobalCache.getInstance().getKey(this);
    }

    @Override
    public boolean loginViaAuthCode(String authCode)
    {
        return true;
    }

    @Override
    public boolean loginViaStoredId(String uniqueid)
    {
        return true;
    }

    @Override
    public String getUniqueId()
    {
        return "";
    }

    @Override
    public String getDisplayName()
    {
        return "";
    }

    @Override
    public String getEmail()
    {
        return "";
    }

    @Override
    public String getAuthUrl()
    {
        return "";
    }

    @Override
    public File getRoot()
    {
        File root = FileSystemView.getFileSystemView().getHomeDirectory();
        return root;
    }

    @Override
    public File getItemById(String id)
    {
        File file = new File(id);
        return file;
    }

    @Override
    public String getIdByItem(File item)
    {
        return item.getAbsolutePath();
    }

    @Override
    public List<File> getChildrenItems(File parent)
    {
        List<File> list = new ArrayList<>();

        if (!parent.isDirectory())
        {
            // nothing to do
            return list;
        }

        list.addAll(Arrays.asList(parent.listFiles()));

        return list;
    }

    @Override
    public boolean isFolder(File item)
    {
        return item.isDirectory();
    }

    @Override
    public String getName(File item)
    {
        return item.getName();
    }

    @Override
    public String getAbsolutePath(File item)
    {
        return item.getAbsolutePath();
    }

    @Override
    public void transfer(final ICloudTransfer<?, java.io.File> transfer)
    {
        LOG.entering("transfering", new Object[]{transfer});

        final int BUFFER_SIZE = 256*1024;
        final InputStream is = transfer.getInputStream();
        OutputStream os = null;

        try
        {
            final Histogram hist = new Histogram();

            final byte[] buffer = new byte[BUFFER_SIZE];

            final ICloudProgress progress = transfer.getProgressHandler();

            long totalSent = 0;
            int readSize = 0;

            os = new BufferedOutputStream(new FileOutputStream(transfer.getTargetObject()), BUFFER_SIZE);

            progress.initalize();
            progress.start(transfer.getFilesize());

            final long startTime = System.nanoTime();
            while (transfer.getCanTransfer() && ((readSize = is.read(buffer)) > 0))
            {
                hist.insert(readSize);

                totalSent += readSize;

                os.write(buffer, 0, readSize);

                progress.progress(totalSent);
            }

            if (transfer.getCanTransfer())
            {
                final long elapsedNano = System.nanoTime() - startTime;

                LOG.fine(Tools.formatTransferMsg(elapsedNano, totalSent));
                LOG.finer(hist.toString());

                progress.finish();

                File outputFile = (File) transfer.getTargetObject();
                transfer.setTransferredObject(new File(outputFile.getAbsolutePath()));
            }
            else
            {
                LOG.warning("Transferred aborted");
            }
        }
        catch (IOException ex)
        {
            LOG.throwing("transfer", ex);
        }
        finally
        {
            try
            {
                LOG.fine("Closing input stream");
                if (is != null) is.close();
            }
            catch (IOException ex)
            {
                LOG.throwing("transfer", ex);
            }

            try
            {
                LOG.fine("Closing output stream");
                if (os != null)
                {
                    os.flush();
                    os.close();
                }
            }
            catch (IOException ex)
            {
                LOG.throwing("transfer", ex);
            }
        }
    }

    @Override
    public InputStream getDownloadStream(File downloadFile)
    {
        // TODO: error checking
        // caller must close stream
        try
        {
            final int BUFFER_SIZE = 256*1024;
            InputStream inputstream = new FileInputStream(downloadFile);

            if (true)
            {
                final String name = getName(downloadFile);

                InputStream isprog = new InputStreamProgress(inputstream)
                {
                    @Override
                    public void progress(long offset, int bytesRead)
                    {
                        //LOG.finer("File:'"+name+"' Offset:"+offset+" BytesRead:"+bytesRead);
                    }

                    @Override
                    public void trace(String msg)
                    {
                        LOG.finer("[trace] "+msg);
                    }
                };

                inputstream = isprog;
            }

            if (true)
            {
                inputstream = new BufferedInputStream(inputstream, BUFFER_SIZE);
            }

            if (false)
            {
                try
                {
                    OutputToInputStream o2istream = new OutputToInputStream(BUFFER_SIZE, inputstream);
                    o2istream.startReading();

                    inputstream = o2istream;
                }
                catch (IOException ex)
                {
                    LOG.throwing("getDownloadStream", ex);
                    LOG.fine("Not using piped streams");
                }
            }

            return inputstream;
        }
        catch (FileNotFoundException ex)
        {
            LOG.throwing("getDownloadStream", ex);
        }
        //catch (IOException ex)
        //{
        //    LOG.throwing("getDownloadStream", ex);
        //}

        return null;
    }

    @Override
    public long getFileSize(File item)
    {
        return item.length();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.dropbox;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.util.Collector;
import com.lahenry.nimbus.accountmanager.AccountInfo;
import com.lahenry.nimbus.accountmanager.AccountManager;
import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.ICloudModel;
import com.lahenry.nimbus.clouds.interfaces.ICloudProgress;
import com.lahenry.nimbus.clouds.interfaces.ICloudTransfer;
import com.lahenry.nimbus.io.InputStreamProgress;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author henry
 */
public class DropboxModel implements ICloudModel<DbxEntry>
{
    private static final Logit LOG = Logit.create(DropboxModel.class.getName());

    private static final String APP_KEY = "954i1xyd8mu6o7m";
    private static final String APP_SECRET = "htc1ejxcr081hjg";

    private static final String SECRET_ACCESS = "access";

    private final DbxAppInfo m_appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
    private final DbxRequestConfig m_config = new DbxRequestConfig(AppInfo.NAME, Locale.getDefault().toString());
    private final DbxWebAuthNoRedirect m_webAuth = new DbxWebAuthNoRedirect(m_config, m_appInfo);

    private DbxClient m_client;

    private DbxAccountInfo m_userInfo;

    private DbxEntry m_root;

    public DropboxModel()
    {
        LOG.entering("<init>");
    }

    @Override
    public GlobalCacheKey getGlobalCacheKey()
    {
        return GlobalCache.getInstance().getKey(this);
    }

    @Override
    public String getAuthUrl()
    {
        LOG.entering("getAuthUrl");
        String url = m_webAuth.start();
        LOG.info(url);
        return url;
    }

    @Override
    public boolean loginViaAuthCode(String authCode)
    {
        LOG.entering("loginViaAuthCode", new Object[]{authCode});

        m_client = null; // make sure the previous object is released

        if (Tools.isNullOrEmpty(authCode))
        {
            LOG.severe("Auth code not valid");
            return false;
        }

        try
        {
            // This will fail if the user enters an invalid authorization code.
            LOG.fine("Getting access token");
            DbxAuthFinish authFinish = m_webAuth.finish(authCode);
            String accessToken = authFinish.accessToken;

            LOG.info("Access token: "+ accessToken);

            return loginViaAccessToken(accessToken);
        }
        catch (DbxException ex)
        {
            LOG.throwing("login", ex);
            return false;
        }
    }

    protected boolean loginViaAccessToken(String accesstoken)
    {
        LOG.entering("loginViaAccessToken", new Object[]{accesstoken});

        // getting the client
        m_client = new DbxClient(m_config, accesstoken);

        try
        {
            LOG.fine("Getting user info");
            m_userInfo = m_client.getAccountInfo();
        }
        catch (DbxException ex)
        {
            LOG.throwing("loginViaAccessToken", ex);
            m_client = null;
            return false;
        }

        if (m_userInfo != null)
        {
            AccountManager manager = AccountManager.getInstance();
            if (manager != null)
            {
                AccountInfo info = AccountInfo.createInstance(CloudType.DROPBOX, getUniqueId());
                info.setName(getDisplayName());
                info.addSecret(SECRET_ACCESS, accesstoken);

                manager.addAccountInfo(info);
            }

            manager.exportAsFile();
        }

        getRoot();

        return true;
    }

    @Override
    public boolean loginViaStoredId(String uniqueid)
    {
        LOG.entering("loginViaStoredId", new Object[]{uniqueid});

        AccountManager manager = AccountManager.getInstance();
        if (manager == null)
        {
            LOG.fine("Failed to get account manager");
            return false;
        }

        AccountInfo info = manager.getAccountInfo(uniqueid);

        String accesstoken = info.getSecret(SECRET_ACCESS);

        return loginViaAccessToken(accesstoken);
    }

    @Override
    public String getUniqueId()
    {
        return Long.toString(m_userInfo.userId);
    }

    @Override
    public String getDisplayName()
    {
        return m_userInfo.displayName;
    }

    @Override
    public String getEmail()
    {
        return "(notsupoorted)";
    }

    @Override
    public DbxEntry getRoot()
    {
        LOG.entering("getRoot");

        if (m_root != null)
        {
            return m_root;
        }

        m_root = getItemById(DropboxConstants.FOLDER_ROOT);
        return m_root;
    }

    @Override
    public DbxEntry getItemById(String id)
    {
        LOG.entering("getItemById", id);

        try
        {
            DbxEntry entry = m_client.getMetadata(id);
            //LOG.fine(entry.toStringMultiline());
            return entry;
        }
        catch (DbxException ex)
        {
            LOG.throwing("getItemById", ex);
        }

        return null;
    }

    @Override
    public String getIdByItem(DbxEntry item)
    {
        return item.path;
    }

    @Override
    public List<DbxEntry> getChildrenItems(DbxEntry parent)
    {
        LOG.entering("getChildrenItems", parent);

        if (!parent.isFolder())
        {
            LOG.warning("Entry '"+parent.path+"' is not a folder");
            return null;
        }

        try
        {
            DbxEntry.WithChildrenC<ArrayList<DbxEntry>> items = m_client.getMetadataWithChildrenC(parent.path, new Collector.ArrayListCollector<DbxEntry>());

            final List<DbxEntry> list = items.children;

            if (false)
            {
                for (DbxEntry entry : list)
                {
                    LOG.finer(entry.toStringMultiline());
                }
            }

            return list;
        }
        catch (DbxException ex)
        {
            LOG.throwing("getChildrenItems", ex);
        }

        return null;
    }

    @Override
    public boolean isFolder(DbxEntry item)
    {
        return item.isFolder();
    }

    @Override
    public String getName(DbxEntry item)
    {
        return item.name;
    }

    @Override
    public String getAbsolutePath(DbxEntry item)
    {
        return item.path;
    }

    @Override
    public void transfer(final ICloudTransfer<?, DbxEntry> transfer)
    {
        LOG.entering("transfering", new Object[]{transfer});

        final InputStream is = transfer.getInputStream();
        OutputStream os = null;

        try
        {
            final int BUFFER_SIZE = 256*1024;
            final byte[] buffer = new byte[BUFFER_SIZE];

            final DbxEntry uploadFile = transfer.getTargetObject();
            // TODO: check if uploadFile is a file

            final DbxClient.Uploader uploader = m_client
                .startUploadFileChunked(
                    BUFFER_SIZE,
                    uploadFile.path,
                    DbxWriteMode.add(),
                    uploadFile.asFile().numBytes);

            // get the output stream
            os = uploader.getBody();

            long totalSent = 0;
            int readSize = 0;

            ICloudProgress progress = transfer.getProgressHandler();

            progress.initalize();
            progress.start(transfer.getFilesize());

            final long startTime = System.nanoTime();
            while (transfer.getCanTransfer() && ((readSize = is.read(buffer)) > 0))
            {
                totalSent += readSize;

                os.write(buffer, 0, readSize);

                progress.progress(totalSent);
            }

            if (transfer.getCanTransfer())
            {
                final long elapsedNano = System.nanoTime() - startTime;

                DbxEntry outputFile = uploader.finish();

                LOG.fine(Tools.formatTransferMsg(elapsedNano, outputFile.asFile().numBytes));

                transfer.setTransferredObject(outputFile);

                progress.finish();
            }
            else
            {
                LOG.warning("Transferred aborted");
            }
        }
        catch (IOException | DbxException ex)
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
                if (is != null)
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
    public InputStream getDownloadStream(DbxEntry downloadFile)
    {
        LOG.entering("getDownloadStream", new Object[]{downloadFile});

        try
        {
            final String name = getName(downloadFile);
            DbxClient.Downloader downloader = m_client.startGetFile(downloadFile.path, downloadFile.asFile().rev);
            InputStream is = downloader.body;
            InputStream isprog = new InputStreamProgress(is)
            {
                @Override
                public void progress(long offset, int bytesRead)
                {
                    LOG.finer("File:'"+name+"' Offset:"+offset+" BytesRead:"+bytesRead);
                }

                @Override
                public void trace(String msg)
                {
                    LOG.finer("[trace] "+msg);
                }
            };

            return isprog;
        }
        catch (DbxException ex)
        {
            LOG.throwing("getDownloadStream", ex);
        }

        return null;
    }

    @Override
    public long getFileSize(DbxEntry item)
    {
        return item.asFile().numBytes;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.clouds.google.drive.io;

import com.google.api.services.drive.Drive;
import com.lahenry.nimbus.io.OutputStreamProgress;
import com.lahenry.nimbus.io.interfaces.IPipedStreamActions;
import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author henry
 */
public class GDrivePipedStreamActions
        implements IPipedStreamActions
{
    private static final Logit LOG = Logit.create(GDrivePipedStreamActions.class.getName());

    private final Drive.Files.Get m_request;

    public GDrivePipedStreamActions(Drive.Files.Get request)
    {
        m_request = request;
    }

    @Override
    public void onFillStream(boolean abort, final PipedOutputStream pout) throws IOException
    {
        OutputStream outputstream = pout;

        // performance would be slow if this is enabled
        if (false)
        {
            OutputStreamProgress osprog = new OutputStreamProgress(outputstream)
            {
                @Override
                public void progress(long offset, int bytesWritten)
                {
                    //LOG.finer("File:'"+name+"' Offset:"+offset+" BytesRead:"+bytesRead);
                }

                @Override
                public void trace(String msg)
                {
                    LOG.finer("onFillStream() "+msg);
                }
            };

            outputstream = osprog;
        }

        m_request.executeMediaAndDownloadTo(outputstream);
    }

    @Override
    public void onClose() throws IOException
    {
        LOG.entering("close");
    }

}

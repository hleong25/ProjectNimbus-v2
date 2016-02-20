/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.io.interfaces.IPipedStreamActions;
import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author henry
 */
public class PipedStreamActions
        implements IPipedStreamActions
{
    private static final Logit LOG = Logit.create(PipedStreamActions.class.getName());

    private final InputStream m_inputstream;

    public PipedStreamActions(InputStream inputstream)
    {
        m_inputstream = inputstream;
    }

    @Override
    public void onFillStream(AtomicBoolean abort, final PipedOutputStream pout) throws IOException
    {
        final int BUFFERED_SIZE = 256*1024;
        //long total = 0;
        int bytesRead = 0;

        byte[] buffer = new byte[BUFFERED_SIZE];

        try
        {
            while ((!abort.get()) && ((bytesRead = m_inputstream.read(buffer)) > 0))
            {
                pout.write(buffer, 0, bytesRead);
                pout.flush();
            }
        }
        finally
        {
            // TODO: Close stream in cases
            //       1. Closing window
            //       2. Finish streaming
            LOG.fine("Finished reading stream. Closing streams.");
            m_inputstream.close();
            pout.close();
        }
    }

    @Override
    public void onClose() throws IOException
    {
        LOG.entering("onClose");
        //m_inputstream.close();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.clouds.local.io;

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
public class LocalPipedStreamActions
        implements IPipedStreamActions
{
    private static final Logit LOG = Logit.create(LocalPipedStreamActions.class.getName());

    private final InputStream m_inputstream;

    public LocalPipedStreamActions(InputStream inputstream)
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
            LOG.fine("finally... closing stream");
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

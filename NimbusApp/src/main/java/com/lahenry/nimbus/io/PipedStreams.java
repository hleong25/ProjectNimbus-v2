/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import com.lahenry.nimbus.io.interfaces.IPipedStreamActions;

/**
 * Class PipedStreams
 *
 * The purpose on PipedStreams is to provide paired PipedInputStream and PipedOutputStream.
 *
 * The InputStream is linked to an OutputStream.
 *
 *
 * When putting data into an OutputStream, the InputStream can read the data.
 *
 * @author henry
 */
public class PipedStreams
{
    private static final Logit LOG = Logit.create(PipedStreams.class.getName());

    protected PipedOutputStream m_pout;
    protected PipedInputStream m_pin;

    protected Thread m_thread = null;

    protected volatile boolean m_abort = false;

    public PipedStreams() throws IOException
    {
        final int BUFFERED_SIZE = 256*1024;

        m_pout = new PipedOutputStream();
        m_pin = new PipedInputStream(m_pout, BUFFERED_SIZE);
    }

    public PipedInputStream getInputStream()
    {
        return m_pin;
    }

    public PipedOutputStream getOutputStream()
    {
        return m_pout;
    }

    public void fillStream(final IPipedStreamActions iface)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    iface.onFillStream(m_abort, m_pout);
                    iface.onClose();
                }
                catch (IOException ex)
                {
                    LOG.throwing("fillStream.run", ex);
                }
                finally
                {
                    PipedStreams.this.close();
                }
            }
        };

        m_thread = new Thread(runnable);
        m_thread.start();
    }

    public void close()
    {
        LOG.entering("close");

        if (m_abort)
        {
            LOG.warning("Already closed");
            return;
        }

        m_abort = true;

        if (m_thread == null)
        {
            try
            {
                LOG.fine("Waiting for thread to stop");
                m_thread.join(5000);
                m_thread.interrupt();
                LOG.fine("Thread stopped");
            }
            catch (InterruptedException ex)
            {
                LOG.throwing("close", ex);
            }
        }

        try
        {
            m_pout.close();
        }
        catch (IOException ex)
        {
            LOG.throwing("close", ex);
        }

        //try
        //{
        //    m_pin.close();
        //}
        //catch (IOException ex)
        //{
        //    LOG.throwing("close", ex);
        //}
    }
}

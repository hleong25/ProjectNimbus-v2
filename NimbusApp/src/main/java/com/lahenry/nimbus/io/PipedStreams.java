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
import java.io.InputStream;
import java.io.OutputStream;

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

    protected final IPipedStreamActions m_pipedactions;
    protected final PipedOutputStream m_pout;
    protected final PipedInputStream m_pin;

    protected final Thread m_thread;

    protected volatile boolean m_abort = false;

    public PipedStreams(IPipedStreamActions pipedactions) throws IOException
    {
        final int BUFFERED_SIZE = 256*1024;

        m_pipedactions = pipedactions;

        m_pin  = new PipedInputStream(BUFFERED_SIZE);
        m_pout = new PipedOutputStream(m_pin);

        Runnable runnable = new Runnable()
        {
            @Override
            protected void finalize() throws Throwable
            {
                super.finalize();
                LOG.entering("fillStream.finalize");
            }

            @Override
            public void run()
            {
                try
                {
                    m_pipedactions.onFillStream(m_abort, m_pout);
                    m_pipedactions.onClose();
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
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        LOG.entering("finalize");

        close();
    }

    public InputStream getInputStream()
    {
        return m_pin;
    }

    public OutputStream getOutputStream()
    {
        return m_pout;
    }

    public void fillStream()
    {
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

        if (m_thread != null)
        {
            try
            {
                LOG.fine("Waiting for thread to stop");
                m_thread.join(1000);
                m_thread.interrupt();
                LOG.fine("Thread stopped");
            }
            catch (InterruptedException ex)
            {
                LOG.throwing("close", ex);
            }
        }

        if (true)
        {
            try
            {
                LOG.fine("Closing pipe output stream");
                m_pout.close();
            }
            catch (IOException ex)
            {
                LOG.throwing("close", ex);
            }
        }

        try
        {
            LOG.fine("Closing pipe input stream");
            m_pin.close();
        }
        catch (IOException ex)
        {
            LOG.throwing("close", ex);
        }

        LOG.exiting("close");
    }
}

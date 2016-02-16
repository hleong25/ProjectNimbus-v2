/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author henry
 */
public class PipedStreams
{
    private static final Logit LOG = Logit.create(PipedStreams.class.getName());

    protected final int BUFFERED_SIZE = 256*1024;

    protected PipedOutputStream m_pout;
    protected PipedInputStream m_pin;

    protected InputStream m_externalsrc;

    protected Thread m_thread = null;

    protected boolean m_stopfill = false;

    public PipedStreams() throws IOException
    {
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

    protected void fillPipeOutputStream(final InputStream dataInputSource) throws IOException
    {
        long total = 0;
        int bytesRead = 0;

        byte[] buffer = new byte[BUFFERED_SIZE];

        while (!m_stopfill && ((bytesRead = dataInputSource.read(buffer)) > 0))
        {
            m_pout.write(buffer, 0, bytesRead);
            total += bytesRead;
        }
    }

    public void fillStream(final InputStream inputStream)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    fillPipeOutputStream(inputStream);

                    inputStream.close();

                    close();
                }
                catch (IOException ex)
                {
                    LOG.throwing("fillStream.run", ex);
                }
            }
        };

        m_thread = new Thread(runnable);
        m_thread.start();
    }

    public void close()
    {
        m_stopfill = true;

        if (m_thread == null)
        {
            try
            {
                m_thread.join(10000);
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

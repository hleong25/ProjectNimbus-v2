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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author henry
 */
public class OutputToInputStream extends PipedInputStream
{
    private static final Logit LOG = Logit.create(OutputToInputStream.class.getName());

    protected final PipedOutputStream m_pout;
    protected final InputStream m_inputstreamsrc;

    private class CloseStream
    {
        private boolean m_close;

        public CloseStream()
        {
            m_close = false;
        }

        public void setClose(boolean val)
        {
            m_close = val;
        }

        public boolean getClose()
        {
            return m_close;
        }
    };

    protected final CloseStream m_closeobj = new CloseStream();

    protected final Thread m_thread;

    public OutputToInputStream(int bufferSize, final InputStream inputstreamsrc) throws IOException
    {
        super(bufferSize);

        m_pout = new PipedOutputStream(this);
        m_inputstreamsrc = inputstreamsrc;

        Runnable runnable = new Runnable() {
            @Override
            public void run()
            {
                final int BUFFER_SIZE = 256*1024;
                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesRead = 0;
                long total = 0;

                try
                {
                    while ((!m_closeobj.getClose()) &&
                           (m_inputstreamsrc.available() > 0) &&
                           ((bytesRead = m_inputstreamsrc.read(buffer)) >= 0))
                    {
                        if (bytesRead <= -1) break;
                        LOG.finer("Write:"+bytesRead+" Total:"+total+" Data:"+Arrays.toString(Arrays.copyOf(buffer, 16)));
                        try
                        {
                            m_pout.write(buffer, 0, bytesRead);
                            total += bytesRead;
                        }
                        catch (IOException ex)
                        {
                            LOG.throwing("<init>.runnable.run.while", ex);
                            m_closeobj.setClose(true);
                        }
                    }
                }
                catch (IOException ex)
                {
                    LOG.throwing("<init>.runnable.run", ex);
                }
                finally
                {
                    m_closeobj.setClose(true);

                    //try
                    //{
                    //    m_inputstreamsrc.close();
                    //    //m_pout.close();
                    //}
                    //catch (IOException ex)
                    //{
                    //    LOG.throwing("<init>.runnable.run.finally", ex);
                    //}
                }
            }
        };

        m_thread = new Thread(runnable);
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        LOG.entering("close");

        m_closeobj.setClose(true);

        m_inputstreamsrc.close();
        //m_pout.close();

        if (m_thread.isAlive())
        {
            try
            {
                LOG.fine("Waiting for thread to kill");
                m_thread.join(10000);
                LOG.fine("Done waiting");
            }
            catch (InterruptedException ex)
            {
                LOG.throwing("close", ex);;
            }
        }
    }

    public void startReading()
    {
        m_thread.start();
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();

        close();
    }

    public boolean canRead()
    {
        return !m_closeobj.getClose();
    }

}

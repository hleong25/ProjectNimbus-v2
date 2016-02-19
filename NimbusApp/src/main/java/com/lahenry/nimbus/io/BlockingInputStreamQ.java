/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author henry
 */
public class BlockingInputStreamQ
        extends InputStreamProxy
{
    private static final Logit LOG = Logit.create(BlockingInputStreamQ.class.getName());

    private class ReadArgs
    {
        public byte[] buffer = null;
        public int offset = 0;
        public int length = 0;
    };

    private class ReturnArgs
    {
        public byte[] buffer = null;
        public int length = 0;
    };

    protected final BlockingQueue<ReadArgs> m_readArgQ;
    protected final BlockingQueue<ReturnArgs> m_returnArgQ;

    protected final AtomicBoolean m_abort = new AtomicBoolean(false);

    protected final Thread m_thread;

    public BlockingInputStreamQ(InputStream stream)
    {
        super(stream);

        m_readArgQ = new ArrayBlockingQueue<>(1);
        m_returnArgQ = new ArrayBlockingQueue<>(1);

        m_thread = createThread();
        m_thread.start();
    }

    protected Thread createThread()
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while (!m_abort.get())
                    {
                        ReadArgs args = m_readArgQ.poll(100, TimeUnit.MILLISECONDS);

                        if (args == null)
                        {
                            continue;
                        }

                        int bytesRead = m_istream.read(args.buffer, args.offset, args.length);

                        ReturnArgs returnArgs = new ReturnArgs();
                        returnArgs.buffer = args.buffer;
                        returnArgs.length = bytesRead;

                        m_returnArgQ.put(returnArgs);
                    }

                    //m_istream.close();
                }
                catch (IOException | InterruptedException ex)
                {
                    LOG.throwing("thread", ex);
                }
                finally
                {
                    try
                    {
                        LOG.fine("Closing stream");
                        m_istream.close();
                    }
                    catch (IOException ex)
                    {
                        LOG.throwing("thread", ex);
                    }
                }
            }
        };

        return new Thread(runnable);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();

        this.close();
    }

    @Override
    public int read() throws IOException
    {
        byte[] b = new byte[1];
        int bytesRead = this.read(b, 0, b.length);
        if (bytesRead != -1)
        {
            return b[0];
        }
        else
        {
            return bytesRead;
        }
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        //LOG.entering("read");

        try
        {
            ReadArgs readArgs = new ReadArgs();
            readArgs.buffer = b;
            readArgs.offset = off;
            readArgs.length = len;

            m_readArgQ.put(readArgs);

            ReturnArgs returnArgs = m_returnArgQ.take();
            b = returnArgs.buffer;
            return returnArgs.length;
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("read", ex);
            return -1;
        }
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        LOG.entering("close");

        m_abort.set(true);
        
        try
        {
            // wait up to 1 sec before interrupting it
            m_thread.join(1000);
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("close", ex);
        }
        finally
        {
            // calling thread.interrupt() should be no-op
            m_thread.interrupt();
        }
    }
}

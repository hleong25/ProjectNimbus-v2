/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author henry
 */
public abstract class InputStreamProxy
        extends InputStream
{
    private static final Logit LOG = Logit.create(InputStreamProxy.class.getName());

    protected final InputStream m_istream;

    public InputStreamProxy(InputStream istream)
    {
        super();

        LOG.entering("<init>", new Object[]{istream});
        m_istream = istream;
    }

    protected InputStreamProxy()
    {
        LOG.entering("<init>");
        m_istream = null;
    }

    @Override
    public int read() throws IOException
    {
        LOG.entering("read");
        return m_istream.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        LOG.entering("read(b)");
        return m_istream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        LOG.entering("read(b,off,len)");
        return m_istream.read(b, off, len);
    }

    @Override
    public int available() throws IOException
    {
        LOG.entering("available");
        return m_istream.available();
    }

    @Override
    public void close() throws IOException
    {
        LOG.entering("close");
        m_istream.close();
    }

    @Override
    public synchronized void reset() throws IOException
    {
        LOG.entering("reset");
        m_istream.reset();
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        LOG.entering("mark");
        m_istream.mark(readlimit);
    }

    @Override
    public boolean markSupported()
    {
        LOG.entering("markSupported");
        return m_istream.markSupported();
    }

    @Override
    public long skip(long n) throws IOException
    {
        LOG.entering("skip");
        return m_istream.skip(n);
    }

}

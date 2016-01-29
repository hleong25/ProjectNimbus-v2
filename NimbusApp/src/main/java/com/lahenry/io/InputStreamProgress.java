/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author henry
 */
public abstract class InputStreamProgress extends InputStream
{
    protected final InputStream m_istream;
    protected long m_offset;

    public InputStreamProgress(InputStream istream)
    {
        m_istream = istream;
        m_offset = 0;
    }

    public abstract void progress(long offset, int bytesRead);

    @Override
    public int read () throws IOException
    {
        int nextByte = m_istream.read();
        int bytesRead = 1;
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        return nextByte;
    }

    @Override
    public int read (byte[] b) throws IOException
    {
        int bytesRead = m_istream.read(b);
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        return bytesRead;
    }

    @Override
    public int read (byte[] b, int off, int len) throws IOException
    {
        int bytesRead = m_istream.read(b, off, len);
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException
    {
        long bytesSkip = m_istream.skip(n);
        m_offset += bytesSkip;
        return bytesSkip;
    }

    @Override
    public synchronized void reset () throws IOException
    {
        m_istream.reset();
        m_offset = 0;
    }

    @Override
    public int available () throws IOException
    {
        return m_istream.available();
    }

    @Override
    public void close () throws IOException
    {
        m_istream.close();
    }

    @Override
    public synchronized void mark (int readlimit)
    {
        m_istream.mark(readlimit);
    }

    @Override
    public boolean markSupported ()
    {
        return m_istream.markSupported();
    }

}

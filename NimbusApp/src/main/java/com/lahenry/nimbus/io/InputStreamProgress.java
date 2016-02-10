/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.io;

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
    public abstract void trace(String msg);

    @Override
    public int read () throws IOException
    {
        trace("read()");
        int nextByte = m_istream.read();
        int bytesRead = 1;
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        return nextByte;
    }

    @Override
    public int read (byte[] b) throws IOException
    {
        trace("read(b[len="+b.length+"])");
        int bytesRead = m_istream.read(b);
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        return bytesRead;
    }

    @Override
    public int read (byte[] b, int off, int len) throws IOException
    {
        trace("read(b[size="+b.length+",off="+off+",len="+len+"]) curroffset="+m_offset);
        int bytesRead = m_istream.read(b, off, len);
        progress(m_offset, bytesRead);
        m_offset += bytesRead;
        //trace("read(b[len="+b.length+",off="+off+",len="+len+"])="+bytesRead);
        //trace("read()="+Arrays.toString(Arrays.copyOfRange(b, 0, 100)));
        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException
    {
        trace("skip(n="+n+"])");
        long bytesSkip = m_istream.skip(n);
        m_offset += bytesSkip;
        return bytesSkip;
    }

    @Override
    public synchronized void reset () throws IOException
    {
        trace("reset()");
        m_istream.reset();
        m_offset = 0;
    }

    @Override
    public int available () throws IOException
    {
        trace("available()");
        return m_istream.available();
    }

    @Override
    public void close () throws IOException
    {
        trace("close()");
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

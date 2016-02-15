/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author henry
 */
public abstract class OutputStreamProgress extends OutputStream
{

    protected final OutputStream m_ostream;
    protected long m_offset;

    public OutputStreamProgress(OutputStream ostream)
    {
        m_ostream = ostream;
        m_offset = 0;
    }

    public abstract void progress(long offset, int bytesWritten);
    public abstract void trace(String msg);

    @Override
    public void close() throws IOException
    {
        trace("close()");
        m_ostream.close();
    }

    @Override
    public void flush() throws IOException
    {
        trace("flush()");
        m_ostream.flush();
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        trace("write(b[size="+b.length+"])");
        m_ostream.write(b);
        m_offset += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        trace("write(b[size="+b.length+",off="+off+",len="+len+"]) curroffset="+m_offset);
        m_ostream.write(b, off, len);
        m_offset += len;
    }

    @Override
    public void write(int b) throws IOException
    {
        trace("write(int) curroffset="+m_offset);
        m_ostream.write(b);
        m_offset += 1;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.io.InputStreamProgress;
import com.lahenry.nimbus.utils.Logit;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.gstreamer.Buffer;
import org.gstreamer.ClockTime;
import org.gstreamer.FlowReturn;
import org.gstreamer.Format;
import org.gstreamer.elements.CustomSrc;
import org.gstreamer.io.StreamLock;
import org.gstreamer.lowlevel.GstAPI.GstSegmentStruct;

/**
 *
 * @author henry
 * @param <T> - Main data type for the cloud
 * @param <CC> - ICloudController
 */
public class CloudChannelSrc<T, CC extends ICloudController<T>>
        extends CustomSrc
{
    private static final Logit LOG = Logit.create(CloudChannelSrc.class.getName());

    final protected CC m_controller;
    final protected T m_file;

    protected InputStream m_stream;

    private long channelPosition = 0;
    private StreamLock lock = null;

    public CloudChannelSrc(String name, CC controller, T file)
    {
        super(CloudChannelSrc.class, name);

        LOG.entering("<init>", new Object[]{name, controller, file});

        m_controller = controller;
        m_file = file;

        m_stream = getInputStream();

        LOG.fine("Stream="+m_stream);

        setFormat(Format.BYTES);
    }

    private InputStream getInputStream()
    {
        LOG.entering("getInputStream");

        if (m_stream != null)
        {
            try
            {
                m_stream.close();
            }
            catch (IOException ex)
            {
                LOG.throwing("getInputStream", ex);
            }
        }

        InputStream istream = m_controller.getDownloadStream(m_file);
        if (true)
        {
            return istream;
        }
        else
        {
            InputStream streamprog = new InputStreamProgress(istream)
            {
                @Override
                public void progress(long offset, int bytesRead)
                {
                    LOG.finer("File:'"+m_controller.getItemName(m_file)+"' Offset:"+offset+" BytesRead:"+bytesRead);
                }

                @Override
                public void trace(String msg)
                {
                    LOG.finer("[trace] "+msg);
                }
            };

            return streamprog;
        }
    }

    private void readFully(long offset, int size, Buffer buffer) throws IOException
    {
        LOG.entering("readFully", new Object[]{offset, size, buffer});

        byte[] tmpbuffer = new byte[size];
        long position = channelPosition;

        if (offset != channelPosition)
        {
            m_stream = getInputStream();
            m_stream.skip(offset);
        }

        buffer.setOffset(position);

        int bytesRead = m_stream.read(tmpbuffer);

        if (bytesRead < 0)
        {
            throw new EOFException();
        }

        position += bytesRead;
        channelPosition = position;

        ByteBuffer dstBuffer = buffer.getByteBuffer();
        dstBuffer.put(tmpbuffer, 0, bytesRead);

        // Adjust the endpoint in the case of EOF
        buffer.setLastOffset(position);
        buffer.setTimestamp(ClockTime.NONE);
    }

    @Override
    protected FlowReturn srcFillBuffer(long offset, int size, Buffer buffer)
    {
        //LOG.entering("srcFillBuffer", new Object[]{offset, size, buffer});

        try
        {
            readFully(offset, size, buffer);
            return FlowReturn.OK;
        }
        catch (IOException ex)
        {
            signalError();
            LOG.throwing("srcFillBuffer", ex);
            return FlowReturn.UNEXPECTED;
        }
    }

    @Override
    public boolean srcIsSeekable()
    {
        return true;
    }

    @Override
    protected boolean srcSeek(GstSegmentStruct segment)
    {
        LOG.entering("srcSeek", new Object[]{segment});

        try
        {
            m_stream = getInputStream();
            m_stream.skip(segment.start);
            segment.last_stop = segment.start;
            segment.time = segment.start;
            segment.write();
            return true;
        }
        catch (IOException ex)
        {
            signalError();
            LOG.throwing("srcSeek", ex);
            return false;
        }
    }

    @Override
    protected long srcGetSize()
    {
        return m_controller.getFileSize(m_file);
    }

	private void signalError()
    {
		if (null != lock)
        {
			lock.setDone();
		}
	}

	public void setNotifyOnError(StreamLock lock)
    {
		this.lock = lock;
	}
}

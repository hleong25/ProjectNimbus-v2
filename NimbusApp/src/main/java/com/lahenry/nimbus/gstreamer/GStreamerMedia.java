/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.Logit;
import java.io.InputStream;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.StateChangeReturn;

/**
 *
 * @author henry
 */
public abstract class GStreamerMedia
{
    private static final Logit LOG = Logit.create(GStreamerMedia.class.getName());
    private static int m_gst_init_count = 0;

    public enum ACTION
    {
        REWIND,
        PAUSE,
        PLAY,
        STOP,
        FORWARD,
    };

    public static final String ICON_REWIND = "<html><center>Rewind Step</center></html>";
    public static final String ICON_PLAY = "<html><center>Play</center></html>";
    public static final String ICON_PAUSE = "<html><center>Pause</center></html>";
    public static final String ICON_STOP = "<html><center>Stop</center></html>";
    public static final String ICON_FORWARD = "<html><center>Forward Step</center></html>";

    protected final String m_name;
    protected final InputStream m_istream;
    protected final Pipeline m_pipe;

    public GStreamerMedia(String name, InputStream istream)
    {
        LOG.entering("<init>", new Object[]{name, istream});

        if (m_gst_init_count++ <= 0)
        {
            m_gst_init_count = 1; // always set it to one

            LOG.info("Initializing GStreamer");

            Gst.init(AppInfo.NAME, new String[]{});

            LOG.info(Gst.getVersionString());
        }

        m_name = name;
        m_istream = istream;
        m_pipe = new Pipeline("Pipeline:"+m_name+"/"+istream.toString());

    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();

        close();
    }

    public void close()
    {
        LOG.entering("close");

        stop();

        LOG.finer("GStreamer quit()/main()");
        Gst.quit();
        Gst.main();

        if (--m_gst_init_count <= 0)
        {
            m_gst_init_count = 0; // always set it to 0

            if (false) // only turn on for testing and performance
            {
                LOG.fine("Cleaning up GStreamer");
                Gst.deinit();
            }
        }
    }

    public abstract boolean init();

    public boolean pause()
    {
        LOG.entering("pause");

        StateChangeReturn state = m_pipe.pause();
        return state != StateChangeReturn.FAILURE;
    }

    public boolean play()
    {
        LOG.entering("play");

        StateChangeReturn state = m_pipe.play();
        return state != StateChangeReturn.FAILURE;
    }

    public boolean stop()
    {
        LOG.entering("stop");

        StateChangeReturn state = m_pipe.stop();
        return state != StateChangeReturn.FAILURE;
    }
}

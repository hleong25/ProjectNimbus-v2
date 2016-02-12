/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.Logit;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.StateChangeReturn;

/**
 *
 * @author henry
 * @param <T> - Main data type for the cloud
 * @param <CC> - ICloudController
 */
public abstract class GStreamerMedia<T, CC extends ICloudController<T>>
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

    protected final String m_id;
    protected final String m_name;
    protected final CC m_controller;
    protected final T m_file;
    protected final Pipeline m_pipe;

    public GStreamerMedia(String name, CC controller, T file)
    {
        LOG.entering("<init>", new Object[]{name, controller, file});

        if (m_gst_init_count++ <= 0)
        {
            m_gst_init_count = 1; // always set it to one

            LOG.info("Initializing GStreamer");

            final String GSTREAMER_ARGS[] = new String[]{
                "--gst-debug-level=2",
                "--gst-debug-no-color=1",
            };

            Gst.init(AppInfo.NAME, GSTREAMER_ARGS);

            LOG.info(Gst.getVersionString());
        }

        m_name = name;
        m_controller = controller;
        m_file = file;

        m_id = m_controller+"/"+m_controller.getItemName(m_file);

        m_pipe = new Pipeline("Pipeline:"+m_id);
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

    protected Element createElement(String factoryName)
    {
        return createElement(factoryName, factoryName);
    }

    protected Element createElement(String factoryName, String name)
    {
        Element element = ElementFactory.make(factoryName, name+":"+m_id);
        return element;
    }

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

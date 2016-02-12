/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.utils.Logit;
import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Element.PAD_ADDED;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Structure;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.swing.VideoComponent;

/**
 *
 * @author henry
 * @param <T> - Main data type for the cloud
 * @param <CC> - ICloudController
 */
public class GStreamerVideo<T, CC extends ICloudController<T>>
        extends GStreamerMedia
{
    private static final Logit LOG = Logit.create(GStreamerVideo.class.getName());

    protected final Element m_istreamsrc;
    protected final VideoComponent m_videocomponent;

    public GStreamerVideo(String name, CC controller, T file)
    {
        super(name, controller, file);

        LOG.entering("<init>", new Object[]{name, controller, file});

        m_istreamsrc = new CloudChannelSrc(m_name, m_controller, m_file);
        m_videocomponent = new VideoComponent();
    }

    @Override
    public boolean init()
    {
        Element src = new CloudChannelSrc(m_name, m_controller, m_file);

        DecodeBin2 decodeBin = (DecodeBin2) createElement("decodebin2");

        m_pipe.addMany(src, decodeBin);
        src.link(decodeBin);

        final Bin audiobin = new Bin("audio bin");

        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audiobin.addMany(conv, sink);
        Element.linkMany(conv, sink);

        audiobin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));

        m_pipe.add(audiobin);

        decodeBin.connect(new PAD_ADDED() {
            public void padAdded(Element element, Pad pad) {
                /* only link once */
                if (pad.isLinked()) {
                    return;
                }
                /* check media type */
                Caps caps = pad.getCaps();
                for (int idx=0, max=caps.size(); idx < max; idx++)
                {
                    Structure struct = caps.getStructure(idx);
                    LOG.fine("Caps struct["+idx+"]:"+struct.getName());

                    if (struct.getName().startsWith("audio/")) {
                        LOG.fine("Linking audio pad: " + struct.getName());
                        pad.link(audiobin.getStaticPad("sink"));
                    } else if (struct.getName().startsWith("video/")) {
                        LOG.fine("Linking video pad: " + struct.getName());
                        pad.link(m_videocomponent.getElement().getStaticPad("sink"));

                        // Make the video frame visible
                        //SwingUtilities.invokeLater(new Runnable() {
                        //    public void run() {
                        //        frame.setVisible(true);
                        //    }
                        //});
                    } else {
                        LOG.fine("Unknown["+idx+"] pad [" + struct.getName() + "]");
                    }
                }
            }
        });

        Bus bus = m_pipe.getBus();

        bus.connect(new Bus.INFO()
        {
            @Override
            public void infoMessage(GstObject source, int code, String message)
            {
                LOG.fine("Info: code=" + code + " message=" + message);
            }
        });

        bus.connect(new Bus.WARNING()
        {
            @Override
            public void warningMessage(GstObject source, int code, String message)
            {
                LOG.fine("Warning: code=" + code + " message=" + message);
            }
        });

        bus.connect(new Bus.ERROR() {
            @Override
            public void errorMessage(GstObject source, int code, String message) {
                LOG.fine("Error: code=" + code + " message=" + message);
            }
        });

        bus.connect(new Bus.EOS() {
            @Override
            public void endOfStream(GstObject source) {
                m_pipe.stop();
            }
        });

        m_pipe.add(m_videocomponent.getElement());

        return true;
    }

    public VideoComponent getVideoComponent()
    {
        return m_videocomponent;
    }

}

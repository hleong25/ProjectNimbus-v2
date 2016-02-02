/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.utils.Logit;
import java.io.InputStream;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Element.PAD_ADDED;
import org.gstreamer.ElementFactory;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Structure;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.io.InputStreamSrc;
import org.gstreamer.swing.VideoComponent;

/**
 *
 * @author henry
 */
public class GStreamerVideo extends GStreamerMedia
{
    private static final Logit LOG = Logit.create(GStreamerVideo.class.getName());

    protected final Element m_istreamsrc;
    protected final VideoComponent m_videocomponent;

    public GStreamerVideo(String name, InputStream istream)
    {
        super(name, istream);

       // from gstreamer-java/src/org/gstreamer/example/DecodeBinPlayer.java

        LOG.entering("<init>", new Object[]{name, istream});

        m_istreamsrc = new InputStreamSrc(m_istream, m_name);
        m_videocomponent = new VideoComponent();
    }

    @Override
    public boolean init()
    {
        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "decodebin2:"+m_name+"/"+m_istream.toString());
        Element decodeQueue = ElementFactory.make("queue", "Decode Queue:"+m_name+"/"+m_istream.toString());
        m_pipe.addMany(m_istreamsrc, decodeQueue, decodeBin);
        Element.linkMany(m_istreamsrc, decodeQueue, decodeBin);

        decodeBin.connect(new PAD_ADDED() {
            public void padAdded(Element element, Pad pad) {
                /* only link once */
                if (pad.isLinked()) {
                    return;
                }
                /* check media type */
                Caps caps = pad.getCaps();
                Structure struct = caps.getStructure(0);
                if (struct.getName().startsWith("audio/")) {
                    LOG.fine("Linking audio pad: " + struct.getName());
                    //pad.link(audioBin.getStaticPad("sink"));
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
                    LOG.fine("Unknown pad [" + struct.getName() + "]");
                }
            }
        });

        Bus bus = m_pipe.getBus();

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

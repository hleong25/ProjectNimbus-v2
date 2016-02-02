/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.utils.Logit;
import java.io.InputStream;
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
import org.gstreamer.io.InputStreamSrc;
import org.gstreamer.swing.VideoComponent;

/**
 *
 * @author henry
 */
public class GStreamerVideo extends GStreamerMedia
{
    private static final Logit LOG = Logit.create(GStreamerVideo.class.getName());

    public GStreamerVideo(String name, InputStream istream)
    {
        super(name, istream);

        LOG.entering("<init>", new Object[]{name, istream});
    }

    @Override
    public boolean init()
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                LOG.entering("run");

                final Element src = new InputStreamSrc(m_istream, m_name);
                final DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");

                Element decodeQueue = ElementFactory.make("queue", "Decode Queue");
                m_pipe.addMany(src, decodeQueue, decodeBin);
                Element.linkMany(src, decodeQueue, decodeBin);

                final VideoComponent videoComponent = new VideoComponent();

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
                            pad.link(videoComponent.getElement().getStaticPad("sink"));

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
                    public void errorMessage(GstObject source, int code, String message) {
                        LOG.fine("Error: code=" + code + " message=" + message);
                    }
                });
                bus.connect(new Bus.EOS() {

                    public void endOfStream(GstObject source) {
                        m_pipe.stop();
                        //System.exit(0);
                    }

                });

                //Element videosink = videoComponent.getElement();
                //m_pipe.addMany(videosink);
                //decodeBin.link(videosink);
                //Element.linkMany(videosink);

                // Now create a JFrame to display the video output
                javax.swing.JFrame frame = new javax.swing.JFrame("Swing Video Test");
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.add(videoComponent, java.awt.BorderLayout.CENTER);
                videoComponent.setPreferredSize(new java.awt.Dimension(720, 480));
                frame.pack();
                frame.setVisible(true);

                // Start the pipeline processing
                m_pipe.add(videoComponent.getElement());
                m_pipe.play();
            }
        });

        return true;
    }

}

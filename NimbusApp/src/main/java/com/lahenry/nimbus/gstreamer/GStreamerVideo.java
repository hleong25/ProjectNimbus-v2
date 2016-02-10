/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gstreamer;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.io.InputStreamProgress;
import com.lahenry.nimbus.utils.Logit;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.io.InputStreamSrc;
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
        final String localfile = "/home/henry/Videos/SampleVideo_720x480_20mb.mp4";
        Element src = new CloudChannelSrc(m_name, m_controller, m_file);
        //if (true) try {
        //    final FileInputStream srcFile = new FileInputStream(localfile);

        //    InputStream isprog = new InputStreamProgress(srcFile) {
        //        @Override
        //        public void progress(long offset, int bytesRead)
        //        {
        //            LOG.finer("File:'"+localfile+"' Offset:"+offset+" BytesRead:"+bytesRead);
        //        }

        //        @Override
        //        public void trace(String msg)
        //        {
        //            LOG.finer("[trace] "+msg);
        //        }
        //    };

        //    //src = new InputStreamSrc(srcFile, "input file");
        //    src = new InputStreamSrc(isprog, "input file");
        //} catch (Exception ex) {
        //    LOG.throwing("init", ex);
        //    //throw new RuntimeException(ex);
        //    return false;
        //}

        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");
        Pipeline pipe = m_pipe;
        pipe.addMany(src, decodeBin);
        src.link(decodeBin);

        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");

        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, sink);
        Element.linkMany(conv, sink);
        audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));

        pipe.add(audioBin);

        decodeBin.connect(new PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
                /* only link once */
                Pad audioPad = audioBin.getStaticPad("sink");
                if (pad.isLinked()) {
                    return;
                }

                /* check media type */
                Caps caps = pad.getCaps();
                Structure struct = caps.getStructure(0);
                if (struct.getName().startsWith("audio/")) {
                    System.out.println("Got audio pad");
                    /* link'n'play */
                    pad.link(audioPad);
                }
			}
		});
        Bus bus = pipe.getBus();
        bus.connect(new Bus.TAG() {
            public void tagsFound(GstObject source, TagList tagList) {
                System.out.println("Got TAG event");
                for (String tag : tagList.getTagNames()) {
                    System.out.println("Tag " + tag + " = " + tagList.getValue(tag, 0));
                }
            }
        });
        bus.connect(new Bus.ERROR() {
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println("Error: code=" + code + " message=" + message);
            }
        });
        bus.connect(new Bus.EOS() {

            public void endOfStream(GstObject source) {
                System.out.println("Got EOS!");
            }

        });


        return true;
    }

    public boolean init_good_audio()
    {
        final String localfile = "/home/henry/Videos/SampleVideo_720x480_20mb.mp4";
        Element src = null;
        if (true) try {
            final FileInputStream srcFile = new FileInputStream(localfile);

            InputStream isprog = new InputStreamProgress(srcFile)
            {
                @Override
                public void progress(long offset, int bytesRead)
                {
                    LOG.finer("File:'"+localfile+"' Offset:"+offset+" BytesRead:"+bytesRead);
                }

                @Override
                public void trace(String msg)
                {
                    LOG.finer("[trace] "+msg);
                }
            };

            src = new InputStreamSrc(srcFile, "input file");
            //src = new InputStreamSrc(isprog, "input file");
        } catch (Exception ex) {
            LOG.throwing("init", ex);
            //throw new RuntimeException(ex);
            return false;
        }

        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");
        Pipeline pipe = m_pipe;
        pipe.addMany(src, decodeBin);
        src.link(decodeBin);

        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");

        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, sink);
        Element.linkMany(conv, sink);
        audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));

        pipe.add(audioBin);

        decodeBin.connect(new PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
                /* only link once */
                Pad audioPad = audioBin.getStaticPad("sink");
                if (pad.isLinked()) {
                    return;
                }

                /* check media type */
                Caps caps = pad.getCaps();
                Structure struct = caps.getStructure(0);
                if (struct.getName().startsWith("audio/")) {
                    System.out.println("Got audio pad");
                    /* link'n'play */
                    pad.link(audioPad);
                }
			}
		});
        Bus bus = pipe.getBus();
        bus.connect(new Bus.TAG() {
            public void tagsFound(GstObject source, TagList tagList) {
                System.out.println("Got TAG event");
                for (String tag : tagList.getTagNames()) {
                    System.out.println("Tag " + tag + " = " + tagList.getValue(tag, 0));
                }
            }
        });
        bus.connect(new Bus.ERROR() {
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println("Error: code=" + code + " message=" + message);
            }
        });
        bus.connect(new Bus.EOS() {

            public void endOfStream(GstObject source) {
                System.out.println("Got EOS!");
            }

        });


        return true;
    }

    public boolean init1()
    {
        DecodeBin2 decodeBin = (DecodeBin2) createElement("decodebin2");

        InputStreamSrc iss = (InputStreamSrc) m_istreamsrc;

        if (true)
        {
            final int BUFFER_SIZE = 256*1024;
            final String name = "/home/henry/Videos/SampleVideo_720x480_20mb.mp4";
            InputStream fis;
            try {
                fis = new FileInputStream(name);
            }
            catch (FileNotFoundException ex) {
                LOG.throwing("init", ex);
                fis = null;
            }
            InputStream isprog = new InputStreamProgress(fis)
            {
                @Override
                public void progress(long offset, int bytesRead)
                {
                    LOG.finer("File:'"+name+"' Offset:"+offset+" BytesRead:"+bytesRead);
                }

                @Override
                public void trace(String msg)
                {
                    LOG.finer("[trace] "+msg);
                }
            };
            InputStream is = new BufferedInputStream(isprog, BUFFER_SIZE);
            is = fis;
            iss = new InputStreamSrc(is, name);
        }

        m_pipe.addMany(iss, decodeBin);
        m_istreamsrc.link(decodeBin);

        final Bin audiobin = new Bin("audio bin");

        //Element audioconvert = createElement("audioconvert");
        //Element rtpL16depay = createElement("rtpL16depay");
        //Element udpsink = createElement("udpsink");

        //udpsink.set("host", "127.0.0.1");
        //udpsink.set("port", "5000");

        //audiobin.addMany(audioconvert, rtpL16depay, udpsink);
        //Element.linkMany(audioconvert, rtpL16depay, udpsink);

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
                        //pad.link(m_videocomponent.getElement().getStaticPad("sink"));

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

        //m_pipe.add(m_videocomponent.getElement());

        return true;
    }

    public VideoComponent getVideoComponent()
    {
        return m_videocomponent;
    }

}

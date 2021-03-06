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
import org.gstreamer.TagList;
import org.gstreamer.elements.DecodeBin2;

/**
 *
 * @author henry
 */
public class GStreamerAudio<T, CC extends ICloudController<T>>
        extends GStreamerMedia<T, CC>
{
    private static final Logit LOG = Logit.create(GStreamerAudio.class.getName());

    protected CloudChannelSrc<T, CC> m_source = null;

    public GStreamerAudio(String name, CC controller, T file)
    {
        super(name, controller, file);

        LOG.entering("<init>", new Object[]{name, controller, file});
    }

    @Override
    public boolean init()
    {
        m_source = new CloudChannelSrc<T, CC>(m_name, m_controller, m_file);
        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");

        m_pipe.addMany(m_source, decodeBin);
        m_source.link(decodeBin);

        /* create audio output */
        final Bin audioBin = new Bin("Audio Bin");

        Element conv = ElementFactory.make("audioconvert", "Audio Convert");
        Element sink = ElementFactory.make("autoaudiosink", "sink");
        audioBin.addMany(conv, sink);
        Element.linkMany(conv, sink);
        audioBin.addPad(new GhostPad("sink", conv.getStaticPad("sink")));

        m_pipe.add(audioBin);

        decodeBin.connect(new PAD_ADDED() {
            @Override
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
                    LOG.info("PAD_ADDED().padAdded() Got audio pad");
                    /* link'n'play */
                    pad.link(audioPad);
                }
			}
		});

        Bus bus = m_pipe.getBus();
        bus.connect(new Bus.TAG() {
            @Override
            public void tagsFound(GstObject source, TagList tagList) {
                //LOG.info("Bus.TAG().tagsFound() Got TAG event");
                for (String tag : tagList.getTagNames()) {
                    LOG.info("Bus.TAG().tagsFound() Tag " + tag + " = " + tagList.getValue(tag, 0));
                }
            }
        });

        bus.connect(new Bus.ERROR() {
            @Override
            public void errorMessage(GstObject source, int code, String message) {
                LOG.info("Bus.ERROR().errorMessage() Error: code=" + code + " message=" + message);
            }
        });

        bus.connect(new Bus.EOS() {
            @Override
            public void endOfStream(GstObject source) {
                LOG.info("Bus.EOS().endOfStream() Got EOS!");
                m_source.close();
            }

        });

        //bus.connect(new Bus.ASYNC_DONE()
        //{
        //    @Override
        //    public void asyncDone (GstObject source)
        //    {
        //        LOG.info("Bus.ASYNC_DONE().asyncDone() AsyncDone");
        //    }
        //});

        bus.connect(new Bus.BUFFERING()
        {
            @Override
            public void bufferingData (GstObject source, int percent)
            {
                LOG.info("Bus.BUFFERING().bufferingData() percent:"+percent);
            }
        });

        return true;
    }

    @Override
    public void close()
    {
        super.close();
        LOG.entering("close");

        m_source.close();
    }

}

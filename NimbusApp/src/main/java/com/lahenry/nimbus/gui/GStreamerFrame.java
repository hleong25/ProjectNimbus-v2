/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui;

import com.lahenry.nimbus.gui.helpers.BusyTaskCursor;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.Logit;
import java.awt.Component;
import java.io.InputStream;
import org.gstreamer.Bin;
import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.Element.PAD_ADDED;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.elements.DecodeBin2;
import org.gstreamer.io.InputStreamSrc;

/**
 *
 * @author henry
 */
public class GStreamerFrame extends javax.swing.JFrame
{
    private static final Logit LOG = Logit.create(GStreamerFrame.class.getName());

    private Pipeline m_pipe;

    /**
     * Creates new form GStreamerFrame
     */
    public GStreamerFrame ()
    {
        initComponents();

        Gst.init(AppInfo.NAME, new String[]{});
    }

    public static void show(final Component parent, final String title, final InputStream istream)
    {
        LOG.entering("show", new Object[]{istream});

        BusyTaskCursor.doTask(parent, new BusyTaskCursor.IBusyTask()
        {
            @Override
            public void run()
            {
                GStreamerFrame frame = new GStreamerFrame();

                frame.setTitle(title);

                frame.init(title, istream);

                frame.setVisible(true);

                frame.play();
            }
        });

    }

    public void init(final String title, final InputStream istream)
    {
        m_pipe = new Pipeline("main pipeline");

        Element src = new InputStreamSrc(istream, title);
        DecodeBin2 decodeBin = (DecodeBin2) ElementFactory.make("decodebin2", "Decode Bin");
        m_pipe.addMany(src, decodeBin);
        src.link(decodeBin);

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
                LOG.info("Bus.TAG().tagsFound() Got TAG event");
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

        //m_pipe.play();
        //Gst.main();

    }

    public void play()
    {
        LOG.info("Playing stream");
        m_pipe.play();
    }

    public void stop()
    {
        LOG.info("Stopping stream");
        m_pipe.stop();
    }

    @Override
    public void setTitle(String title)
    {
        final String APP_NAME = " - " + AppInfo.NAME;

        if (title.endsWith(APP_NAME))
            super.setTitle(title);
        else
            super.setTitle(title + APP_NAME);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        LOG.entering("formWindowClosing");

        stop();

        LOG.finer("Gst quit()/main()");
        Gst.quit();
        Gst.main();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main (String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(GStreamerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(GStreamerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(GStreamerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(GStreamerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run ()
            {
                new GStreamerFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

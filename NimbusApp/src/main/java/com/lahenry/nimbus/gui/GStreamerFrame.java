/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.gstreamer.GStreamerAudio;
import com.lahenry.nimbus.gstreamer.GStreamerMedia;
import com.lahenry.nimbus.gstreamer.GStreamerVideo;
import com.lahenry.nimbus.gui.helpers.BusyTaskCursor;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.Logit;
import java.awt.Component;
import org.gstreamer.swing.VideoComponent;

/**
 *
 * @author henry
 */
public class GStreamerFrame extends javax.swing.JFrame
{
    private static final Logit LOG = Logit.create(GStreamerFrame.class.getName());

    private GStreamerMedia<?, ?> m_gst;

    /**
     * Creates new form GStreamerFrame
     */
    public GStreamerFrame ()
    {
        initComponents();

        // disable buttons for now
        btnRewind.setVisible(false);
        btnStop.setVisible(false);
        btnForward.setVisible(false);
    }

    /**
     * @param <T> - Main data type for the cloud
     * @param <CC> - ICloudController
     */
    public static <T, CC extends ICloudController<T>> void showVideo(final Component parent,
                                                                     final String title,
                                                                     final CC controller,
                                                                     final T file)
    {
        LOG.entering("showVideo", new Object[]{parent, title, controller, file});

        LOG.fine("Creating new GStreamerFrame");
        final GStreamerFrame frame = new GStreamerFrame();
        frame.setTitle(title);

        LOG.fine("Creating new GStreamerVideo");
        frame.m_gst = new GStreamerVideo<T, CC>(title, controller, file);

        frame.setupVideo();

        showAndPlay(parent, frame);
    }

    /**
     * @param <T> - Main data type for the cloud
     * @param <CC> - ICloudController
     */
    public static <T, CC extends ICloudController<T>> void showAudio(final Component parent,
                                                                     final String title,
                                                                     final CC controller,
                                                                     final T file)
    {
        LOG.entering("showAudio", new Object[]{parent, title, controller, file});

        LOG.fine("Creating new GStreamerFrame");
        final GStreamerFrame frame = new GStreamerFrame();
        frame.setTitle(title);

        LOG.fine("Creating new GStreamerAudio");
        frame.m_gst = new GStreamerAudio<T, CC>(title, controller, file);

        showAndPlay(parent, frame);
    }
    
    protected static void showAndPlay(final Component parent, final GStreamerFrame frame)
    {
        LOG.entering("showAndPlay", new Object[]{parent, frame});

        BusyTaskCursor.doTask(parent, new BusyTaskCursor.IBusyTask()
        {
            @Override
            public void run()
            {
                try
                {
                    frame.m_gst.init();

                    frame.setVisible(true);

                    frame.mediaAction(GStreamerMedia.ACTION.PLAY);
                }
                catch (Exception ex)
                {
                    LOG.throwing("showAndPlay", ex);

                    if (frame != null)
                    {
                        frame.dispose();
                    }
                }
            }
        });
    }

    protected void setupVideo()
    {
        LOG.entering("setupVideo");

        VideoComponent videocomponent = ((GStreamerVideo)m_gst).getVideoComponent();
        add(videocomponent, java.awt.BorderLayout.CENTER);
        videocomponent.setPreferredSize(new java.awt.Dimension(720, 480));

        pack();
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

    public void mediaAction(GStreamerMedia.ACTION action)
    {
        LOG.entering("mediaAction", new Object[]{action});

        switch (action)
        {
            case PAUSE:
                btnPause.setVisible(false);
                btnPlay.setVisible(true);
                m_gst.pause();
                break;

            case PLAY:
                btnPause.setVisible(true);
                btnPlay.setVisible(false);
                m_gst.play();
                break;

            case STOP:
                btnPause.setVisible(false);
                btnPlay.setVisible(true);
                m_gst.stop();
                break;

            default:
                LOG.fine("Action:"+action+" not implemented");
                break;
        }
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

        jPanel1 = new javax.swing.JPanel();
        btnRewind = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        btnRewind.setText(GStreamerMedia.ICON_REWIND);
        jPanel1.add(btnRewind);

        btnPause.setText(GStreamerMedia.ICON_PAUSE);
        btnPause.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPauseActionPerformed(evt);
            }
        });
        jPanel1.add(btnPause);

        btnPlay.setText(GStreamerMedia.ICON_PLAY);
        btnPlay.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPlayActionPerformed(evt);
            }
        });
        jPanel1.add(btnPlay);

        btnStop.setText(GStreamerMedia.ICON_STOP);
        btnStop.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnStopActionPerformed(evt);
            }
        });
        jPanel1.add(btnStop);

        btnForward.setText(GStreamerMedia.ICON_FORWARD);
        jPanel1.add(btnForward);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        LOG.entering("formWindowClosing");

        m_gst.close();
    }//GEN-LAST:event_formWindowClosing

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPauseActionPerformed
    {//GEN-HEADEREND:event_btnPauseActionPerformed
        mediaAction(GStreamerMedia.ACTION.PAUSE);
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPlayActionPerformed
    {//GEN-HEADEREND:event_btnPlayActionPerformed
        mediaAction(GStreamerMedia.ACTION.PLAY);
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStopActionPerformed
    {//GEN-HEADEREND:event_btnStopActionPerformed
        mediaAction(GStreamerMedia.ACTION.STOP);
    }//GEN-LAST:event_btnStopActionPerformed

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
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnRewind;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}

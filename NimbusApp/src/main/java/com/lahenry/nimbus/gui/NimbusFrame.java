/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui;

import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.clouds.interfaces.ICloudPanel;
import com.lahenry.nimbus.gui.interfaces.ILayoutToCloudPanelProxy;
import com.lahenry.nimbus.gui.layout.AllCardsPanel.ViewType;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.Logit;
import java.awt.CardLayout;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public class NimbusFrame extends javax.swing.JFrame
{
    private static final Logit LOG = Logit.create(NimbusFrame.class.getName());

    private final Runnable m_run;

    private final List<ICloudPanel<?, ?>> m_cloudPanels = new ArrayList<>();

    /**
     * Creates new form NimbusFrame
     */
    public NimbusFrame()
    {
        LOG.entering("<init>");
        initComponents();

        m_cloudPanels.add(pnlLocal);
        m_cloudPanels.add(pnlGoogleDrive);
        m_cloudPanels.add(pnlDropbox);

        m_run = new Runnable()
        {
            @Override
            public void run()
            {
                setVisible(true);
            }
        };
    }

    public static NimbusFrame setupMainPanel(CloudType type, ICloudController<?> controller)
    {
        LOG.entering("setupMainPanel");

        NimbusFrame frame = new NimbusFrame();

        String cardName = type.toString();

        frame.setTitle(cardName);
        ((CardLayout)frame.pnlCards.getLayout()).show(frame.pnlCards, cardName);

        final ICloudPanel<?, ?> pnl = frame.getCurrentCloudPanel();
        pnl.initPanel(controller);

        return frame;
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

        pnlMain = new javax.swing.JPanel();
        pnlCards = new javax.swing.JPanel();
        pnlLocal = new com.lahenry.nimbus.clouds.local.LocalPanel();
        pnlGoogleDrive = new com.lahenry.nimbus.clouds.google.drive.GDrivePanel();
        pnlDropbox = new com.lahenry.nimbus.clouds.dropbox.DropboxPanel();
        mnubar = new javax.swing.JMenuBar();
        mnuNimbus = new javax.swing.JMenu();
        mnuOpenNewCloud = new javax.swing.JMenuItem();
        mnuView = new javax.swing.JMenu();
        mnuViewLargeIcons = new javax.swing.JMenuItem();
        mnuViewDetailed = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuViewRefresh = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        pnlMain.setMinimumSize(new java.awt.Dimension(400, 300));
        pnlMain.setPreferredSize(new java.awt.Dimension(400, 300));
        pnlMain.setLayout(new java.awt.BorderLayout());

        pnlCards.setLayout(new java.awt.CardLayout());
        pnlCards.add(pnlLocal, "Local File System");
        pnlCards.add(pnlGoogleDrive, "Google Drive");
        pnlCards.add(pnlDropbox, "Dropbox");

        pnlMain.add(pnlCards, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

        mnuNimbus.setText("Nimbus");

        mnuOpenNewCloud.setText("Open New Cloud");
        mnuOpenNewCloud.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mnuOpenNewCloudActionPerformed(evt);
            }
        });
        mnuNimbus.add(mnuOpenNewCloud);

        mnubar.add(mnuNimbus);

        mnuView.setText("View");

        mnuViewLargeIcons.setText("Large Icons");
        mnuViewLargeIcons.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mnuViewLargeIconsActionPerformed(evt);
            }
        });
        mnuView.add(mnuViewLargeIcons);

        mnuViewDetailed.setText("Detailed Mode");
        mnuViewDetailed.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mnuViewDetailedActionPerformed(evt);
            }
        });
        mnuView.add(mnuViewDetailed);
        mnuView.add(jSeparator1);

        mnuViewRefresh.setText("Refresh");
        mnuViewRefresh.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mnuViewRefreshActionPerformed(evt);
            }
        });
        mnuView.add(mnuViewRefresh);

        mnubar.add(mnuView);

        setJMenuBar(mnubar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuOpenNewCloudActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuOpenNewCloudActionPerformed
    {//GEN-HEADEREND:event_mnuOpenNewCloudActionPerformed
        NimbusAccountManagerFrame.showMe();
    }//GEN-LAST:event_mnuOpenNewCloudActionPerformed

    private void mnuViewLargeIconsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuViewLargeIconsActionPerformed
    {//GEN-HEADEREND:event_mnuViewLargeIconsActionPerformed
        setPanelView(ViewType.LARGE_ICONS);
    }//GEN-LAST:event_mnuViewLargeIconsActionPerformed

    private void mnuViewDetailedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuViewDetailedActionPerformed
    {//GEN-HEADEREND:event_mnuViewDetailedActionPerformed
        setPanelView(ViewType.DETAILED_MODE);
    }//GEN-LAST:event_mnuViewDetailedActionPerformed

    private void mnuViewRefreshActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_mnuViewRefreshActionPerformed
    {//GEN-HEADEREND:event_mnuViewRefreshActionPerformed
        ICloudPanel<?, ?> pnl = getCurrentCloudPanel();
        ILayoutToCloudPanelProxy proxy = (ILayoutToCloudPanelProxy)pnl;

        proxy.refreshCurrentView();
    }//GEN-LAST:event_mnuViewRefreshActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        getCurrentCloudPanel().disposePanel();
    }//GEN-LAST:event_formWindowClosing

    @Override
    public void setTitle(String title)
    {
        final String APP_NAME = " - " + AppInfo.Name;

        if (title.endsWith(APP_NAME))
            super.setTitle(title);
        else
            super.setTitle(title + APP_NAME);
    }

    public void runLater()
    {
        LOG.entering("runLater");

        java.awt.EventQueue.invokeLater(m_run);
    }

    public void runAndWait()
    {
        LOG.entering("runAndWait");

        try
        {
            LOG.fine("EventQueue.invokeAndWait(run)");
            java.awt.EventQueue.invokeAndWait(m_run);
        }
        catch (InterruptedException | InvocationTargetException ex)
        {
            LOG.throwing("runAndWait", ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenu mnuNimbus;
    private javax.swing.JMenuItem mnuOpenNewCloud;
    private javax.swing.JMenu mnuView;
    private javax.swing.JMenuItem mnuViewDetailed;
    private javax.swing.JMenuItem mnuViewLargeIcons;
    private javax.swing.JMenuItem mnuViewRefresh;
    private javax.swing.JMenuBar mnubar;
    private javax.swing.JPanel pnlCards;
    private com.lahenry.nimbus.clouds.dropbox.DropboxPanel pnlDropbox;
    private com.lahenry.nimbus.clouds.google.drive.GDrivePanel pnlGoogleDrive;
    private com.lahenry.nimbus.clouds.local.LocalPanel pnlLocal;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    private ICloudPanel<?, ?> getCurrentCloudPanel()
    {
        for (Component comp : pnlCards.getComponents() ) {
            if (comp.isVisible() == true) {
                return (ICloudPanel<?, ?>)comp;
            }
        }
        return null;
    }

    private void setPanelView(ViewType type)
    {
        getCurrentCloudPanel().setPanelView(type);
    }
}

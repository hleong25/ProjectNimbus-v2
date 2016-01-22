/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.mainapp;

import com.lahenry.nimbus.gui.NimbusAccountManagerFrame;
import com.lahenry.nimbus.utils.Logit;

/**
 *
 * @author henry
 */
public class MainGuiApp
{
    private static final Logit LOG = Logit.create(MainGuiApp.class.getName());

    public static void main(String[] args)
    {
        Logit.init();
        LOG.entering("main");

        setupPlatform();

        //setupLookAndFeel();

        //PickCloudFrame.showMe();
        NimbusAccountManagerFrame.showMe();
    }

    private static void setupLookAndFeel()
    {
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if (AppInfo.Name.equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            LOG.throwing("setupLookAndFeel", ex);
        }

    }

    private static void setupPlatform()
    {
        // http://stackoverflow.com/questions/22604218/set-a-dynamic-apple-menu-title-for-java-program-in-netbeans-7-4
        // http://stackoverflow.com/questions/3154638/setting-java-swing-application-name-on-mac

        // the application menu for Mac OS X must be set very early in the cycle
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac"))
        {
            // to set the name of the app in the Mac App menu:
            System.setProperty("apple.awt.application.name", AppInfo.Name);

            //to show the menu bar at the top of the screen:
            //System.setProperty("apple.laf.useScreenMenuBar", "true");

            // to show a more mac-like file dialog box
            //System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }
    }

}

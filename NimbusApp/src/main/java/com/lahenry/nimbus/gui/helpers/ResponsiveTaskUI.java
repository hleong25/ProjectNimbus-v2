/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.helpers;

import com.lahenry.nimbus.utils.Logit;

/**
 *
 * @author henry
 */
public class ResponsiveTaskUI
{
    private static final Logit LOG = Logit.create(ResponsiveTaskUI.class.getName());

    public interface IResponsiveTask
    {
        void run();
    }

    private ResponsiveTaskUI()
    {
        // do nothing
    }

    public static void doTask(final IResponsiveTask taskIFace)
    {
        Thread thread = new Thread() {
            @Override
            public void run()
            {
                taskIFace.run();
            }
        };

        thread.start();
    }

    public static void yield()
    {
        try
        {
            // suspend this thread via sleep() and yeild control to other threads
            Thread.sleep(10);
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("yield", ex);
        }
    }
}

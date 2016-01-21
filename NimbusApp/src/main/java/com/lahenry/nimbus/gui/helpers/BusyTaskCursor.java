/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.helpers;

import java.awt.Component;
import java.awt.Cursor;

/**
 *
 * @author henry
 */
public final class BusyTaskCursor
{
    protected final static Cursor CURSOR_BUSY = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    protected final static Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();

    public interface IBusyTask
    {
        void run();
    }

    private BusyTaskCursor()
    {
        // empty
    }

    public static void doTask(final Component component, final IBusyTask taskIface)
    {
        ResponsiveTaskUI.doTask(new ResponsiveTaskUI.IResponsiveTask()
        {
            @Override
            public void run()
            {
                try
                {
                    //Tools.logit("BusyTaskCursor.doTask() cursor busy");
                    component.setCursor(CURSOR_BUSY);

                    ResponsiveTaskUI.yield();

                    //Tools.logit("BusyTaskCursor.doTask() taskIface.run()");
                    taskIface.run();
                }
                finally
                {
                    //Tools.logit("BusyTaskCursor.doTask() cursor default");
                    component.setCursor(CURSOR_DEFAULT);
                }
            }
        });

    }

}

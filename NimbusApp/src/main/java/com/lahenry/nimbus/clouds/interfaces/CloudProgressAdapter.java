/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import com.lahenry.nimbus.gui.components.FileItemPanel;
import com.lahenry.nimbus.gui.helpers.ResponsiveTaskUI;

/**
 *
 * @author henry
 */
public class CloudProgressAdapter
    implements ICloudProgress
{
    protected final FileItemPanel m_pnl;
    private long m_size = 0;

    public CloudProgressAdapter(FileItemPanel pnl)
    {
        m_pnl = pnl;
    }

    @Override
    public void initalize()
    {
        m_size = 0;
    }

    @Override
    public void start(long size)
    {
        m_size = size;
    }

    @Override
    public void progress(long bytesSent)
    {
        m_pnl.setProgress((int)(bytesSent*100/m_size));
        ResponsiveTaskUI.yield();
    }

    @Override
    public void finish()
    {
        m_pnl.setProgress(100);
        ResponsiveTaskUI.yield();
    }

}

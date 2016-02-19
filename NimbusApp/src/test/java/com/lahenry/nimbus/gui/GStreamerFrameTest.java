/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui;

import com.lahenry.nimbus.clouds.local.LocalController;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author henry
 */
public class GStreamerFrameTest
{
    private final LocalController m_controller;

    public GStreamerFrameTest()
    {
        m_controller = new LocalController();
    }


    @Test
    public void testShowVideo()
    {
        //String filename = "/home/henry/Videos/SampleVideo_720x480_20mb.mkv";
        //File file = m_controller.getItemById(filename, false);

        //GStreamerFrame.showVideo(null, file.getName(), m_controller, file);

    }

    @Test
    public void testShowAudio()
    {
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

/**
 *
 * @author henry
 */
public interface ICloudProgress
{
    void initalize();
    void start(long size);
    void progress(long bytesSent);
    void finish();
}

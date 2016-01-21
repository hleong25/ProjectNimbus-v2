/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author henry
 * @param <S> = Source object
 * @param <T> = Target object
 */
public interface ICloudTransfer<S, T>
{
    long getFilesize();

    S getSourceObject();
    T getTargetObject();

    void setTransferredObject(T obj);
    T getTransferredObject();

    InputStream getInputStream();

    void setProgressHandler(ICloudProgress progress);
    ICloudProgress getProgressHandler();

    void setCanTransfer(AtomicBoolean canTransfer);
    boolean getCanTransfer();
}

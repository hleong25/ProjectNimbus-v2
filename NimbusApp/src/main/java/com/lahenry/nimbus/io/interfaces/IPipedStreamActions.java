/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.io.interfaces;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author henry
 */
public interface IPipedStreamActions
{
    void onFillStream(AtomicBoolean abort, final PipedOutputStream pout) throws IOException;
    void onClose() throws IOException;
}

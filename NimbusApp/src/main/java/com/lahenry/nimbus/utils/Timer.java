/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author henry
 */
public class Timer
{
    protected long m_starttime;

    public Timer()
    {
        m_starttime = System.nanoTime();
    }

    public void reset()
    {
        m_starttime = System.nanoTime();
    }

    public long getElapsedTime(final TimeUnit outputAs)
    {
        long elapsed = System.nanoTime() - m_starttime;
        return outputAs.convert(elapsed, TimeUnit.NANOSECONDS);
    }

    public long getElapsedTimeAsMilliseconds()
    {
        return getElapsedTime(TimeUnit.MILLISECONDS);
    }
}

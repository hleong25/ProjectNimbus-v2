/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.io;

import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author henry
 */
public abstract class InputStreamProxy
        extends InputStream
{
    private static final Logit LOG = Logit.create(InputStreamProxy.class.getName());

    protected final InputStream m_istream;

    public InputStreamProxy(InputStream istream)
    {
        m_istream = istream;
    }

    public InputStreamProxy()
    {
        this.m_istream = null;
    }

    @Override
    public int read() throws IOException
    {
        return m_istream.read();
    }

}

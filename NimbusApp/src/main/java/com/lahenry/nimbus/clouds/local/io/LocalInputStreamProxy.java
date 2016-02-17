/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.clouds.local.io;

import com.lahenry.nimbus.io.InputStreamProxy;
import com.lahenry.nimbus.io.PipedStreams;
import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author henry
 */
public class LocalInputStreamProxy
        extends InputStreamProxy
{
    private static final Logit LOG = Logit.create(LocalInputStreamProxy.class.getName());

    private final PipedStreams m_pipedstreams;

    public LocalInputStreamProxy(InputStream inputstream, PipedStreams pipedstreams)
    {
        super(inputstream);

        m_pipedstreams = pipedstreams;
    }

    @Override
    public void close() throws IOException
    {
        m_istream.close();
    }

}

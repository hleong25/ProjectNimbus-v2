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
public class PipedInputStreamProxy
        extends InputStreamProxy
{
    private static final Logit LOG = Logit.create(PipedInputStreamProxy.class.getName());

    private final PipedStreams m_pipedstreams;

    public PipedInputStreamProxy(InputStream inputstream, PipedStreams pipedstreams)
    {
        super(inputstream);
        LOG.entering("<init>");

        m_pipedstreams = pipedstreams;

        this.setDebug(false);
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        LOG.entering("close");
        m_pipedstreams.close();
    }

}

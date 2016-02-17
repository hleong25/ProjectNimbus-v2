/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lahenry.nimbus.clouds.local.io;

import com.lahenry.nimbus.io.interfaces.IPipedStreamActions;
import com.lahenry.nimbus.utils.Logit;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author henry
 */
public class LocalPipedStreamActions
        implements IPipedStreamActions
{
    private static final Logit LOG = Logit.create(LocalPipedStreamActions.class.getName());

    private final InputStream m_inputstream;

    public LocalPipedStreamActions(InputStream inputstream)
    {
        m_inputstream = inputstream;
    }

    @Override
    public void onFillStream(boolean abort, final PipedOutputStream pout) throws IOException
    {
        final int BUFFERED_SIZE = 256*1024;
        //long total = 0;
        int bytesRead = 0;

        byte[] buffer = new byte[BUFFERED_SIZE];

        while ((!abort) && ((bytesRead = m_inputstream.read(buffer)) > 0))
        {
            pout.write(buffer, 0, bytesRead);
            //total += bytesRead;
        }
    }

    @Override
    public void onClose() throws IOException
    {
        m_inputstream.close();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.components;

import com.lahenry.nimbus.utils.Logit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author henry
 */
public class ImagePanel extends JPanel
{
    private static final Logit LOG = Logit.create(ImagePanel.class.getName());

    private BufferedImage m_image;

    public ImagePanel()
    {
        super();

        // do not use -- only used for JavaBeans
        m_image = new BufferedImage(800, 640, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = m_image.createGraphics();
        g.setPaint(Color.CYAN);
        g.fillRect(0, 0, m_image.getWidth(), m_image.getHeight());

        setPreferredSize(new Dimension(800, 600));
    }

    public void setImage(final InputStream istream) throws IOException
    {
        LOG.entering("setImage", new Object[]{istream});

        if (istream == null)
        {
            throw new IOException("Image stream is null");
        }

        final List<IOException> listExceptions = new ArrayList<>(1);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run ()
            {
                try
                {
                    ImageIO.setUseCache(false);
                    //LOG.fine("Start reading image from stream");

                    long starttime = System.nanoTime();
                    m_image = ImageIO.read(istream);
                    long elapsed = System.nanoTime() - starttime;

                    if (m_image == null)
                    {
                        throw new IOException("Image buffer is null");
                    }

                    elapsed = TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);

                    if (elapsed > 1500)
                    {
                        //LOG.fine(String.format("Done reading image from stream in %.3fs", (elapsed/1000.0)));
                        LOG.fine(String.format("Reading image from stream took %.3fs", (elapsed/1000.0)));
                    }

                    setPreferredSize(new Dimension(m_image.getWidth(), m_image.getHeight()));
                }
                catch (IOException ex)
                {
                    LOG.throwing("setImage", ex);
                    listExceptions.add(ex);
                    //throw ex;
                }
            }
        });

        thread.start();

        try
        {
            thread.join();

            if (!listExceptions.isEmpty())
            {
                throw listExceptions.get(0);
            }

            this.invalidate();
        }
        catch (IOException ex)
        {
            throw ex;
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("setImage", ex);
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        //LOG.entering("paintComponent");
        super.paintComponent(g);
        g.drawImage(m_image, 0, 0, null);
    }

}

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

    public void setImage(InputStream istream) throws IOException
    {
        LOG.entering("setImage", new Object[]{istream});

        try
        {
            LOG.fine("Start reading image from stream");
            // this is slow... why??
            m_image = ImageIO.read(istream);
            LOG.fine("Done reading image from stream");

            setPreferredSize(new Dimension(m_image.getWidth(), m_image.getHeight()));
        }
        catch (IOException ex)
        {
            LOG.throwing("setImage", ex);

            throw ex;
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

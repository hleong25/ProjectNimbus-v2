/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.helpers;

import com.lahenry.nimbus.gui.components.FileItemPanel;
import com.lahenry.nimbus.utils.Logit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author henry
 */
public class FileItemPanelGroup
{
    private static final Logit LOG = Logit.create(FileItemPanelGroup.class.getName());

    private final List<FileItemPanel> m_allPanels = new ArrayList<>();
    private final List<FileItemPanel> m_selectedPanels = new ArrayList<>();

    public FileItemPanelGroup()
    {
    }

    public void add(FileItemPanel pnl)
    {
        if (m_allPanels.contains(pnl))
        {
            return;
        }
        m_allPanels.add(pnl);

        pnl.setGroup(this);
        pnl.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (!SwingUtilities.isLeftMouseButton(e))
                {
                    return;
                }
               
                if (!e.isControlDown())
                {
                    reset();
                }

                FileItemPanel thispnl = (FileItemPanel) e.getSource();

                if (!m_selectedPanels.contains(thispnl))
                {
                    m_selectedPanels.add(thispnl);
                    thispnl.setHighlight(true);
                }
            }
        });
    }

    public void remove(FileItemPanel pnl)
    {
        m_allPanels.remove(pnl);
    }

    // not sure if this is needed
    private void clear()
    {
        reset();
        m_allPanels.clear();
    }

    public void reset()
    {
        for (FileItemPanel oldPnls : m_selectedPanels)
        {
            oldPnls.setHighlight(false);
        }
        m_selectedPanels.clear();
    }

    public final List<FileItemPanel> getSelected()
    {
        LOG.fine("count:"+m_selectedPanels.size());
        return m_selectedPanels;
    }
}

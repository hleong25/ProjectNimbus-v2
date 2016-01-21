/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author henry
 */
public abstract class FileItemPanelMouseAdapter<T> extends MouseAdapter
{
    protected final T m_item;

    public FileItemPanelMouseAdapter(T item)
    {
        m_item = item;
    }

    public abstract boolean isFolder(final T item);
    public abstract void onOpenFolder(final T item);

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //Tools.logit("LocalFileItemPanelMouseAdapter.mouseClicked()");

        if (e.getClickCount() == 2)
        {
            //Tools.logit("LocalFileItemPanelMouseAdapter.mouseClicked() click count = 2");
            if (isFolder(m_item))
            {
                onOpenFolder(m_item);
            }
        }
    }

}

package com.lahenry.nimbus.clouds.local.gui;


import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.gui.components.IFileItem;
import java.io.File;
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author henry
 */
public class LocalFileItem implements IFileItem<File>
{
    protected final ICloudController<File> m_controller;
    protected final File m_item;

    public LocalFileItem(ICloudController<File> controller, File item)
    {
        m_controller = controller;
        m_item = item;
    }

    @Override
    public ICloudController<File> getCloudController()
    {
        return m_controller;
    }

    @Override
    public File getCloudObject()
    {
        return m_item;
    }

    @Override
    public ImageIcon getIcon()
    {
        String path;

        if (m_item.isDirectory())
        {
            //Tools.logit("Path="+m_item.getAbsolutePath());
            path = "images/google/icon/Close-Folder-icon-64.png";
        }
        else
        {
            //Tools.logit("File="+m_item.getAbsolutePath());
            path = "images/google/icon/docs-64.png";
        }

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));

        return icon;
    }

    @Override
    public String getLabel()
    {
        return m_item.getName();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.google.drive.gui;

import com.google.api.services.drive.model.File;
import com.lahenry.nimbus.clouds.google.drive.GDriveConstants;
import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.gui.components.IFileItem;
import com.lahenry.nimbus.utils.Logit;
import javax.swing.ImageIcon;

/**
 *
 * @author henry
 */
public class GDriveFileItem implements IFileItem<File>
{
    private static final Logit LOG = Logit.create(GDriveFileItem.class.getName());

    protected final ICloudController<File> m_controller;
    protected final File m_item;

    public GDriveFileItem(ICloudController<File> controller, File item)
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

        //Tools.logit("Item="+m_item.getTitle()+" Mime="+m_item.getMimeType());

        if (m_item.getMimeType() == null)
        {
            LOG.fine("Item="+m_item.getTitle()+" Mime=(null)");
            path = "images/google/icon/drive-64.png";
        }
        else if (m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_DOCUMENT))
        {
            path = "images/google/icon/docs-64.png";
        }
        else if (m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_FOLDER))
        {
            path = "images/google/icon/Close-Folder-icon-64.png";
        }
        else if (m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_PRESENTATION))
        {
            path = "images/google/icon/presentations-64.png";
        }
        else if (m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_SPREADSHEET))
        {
            path = "images/google/icon/spreadsheets-64.png";
        }
        else if (m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_VIDEO))
        {
            path = "images/google/icon/video-64.gif";
        }
        else
        {
            LOG.fine("Item="+m_item.getTitle()+" Mime="+m_item.getMimeType());
            path = "images/google/icon/drive-64.png";
        }

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));

        return icon;
    }

    @Override
    public String getLabel()
    {
        return m_item.getTitle();
    }

}

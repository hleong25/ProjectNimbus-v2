/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.google.drive.gui;

import com.google.api.services.drive.model.File;
import com.lahenry.nimbus.clouds.google.drive.GDriveConstants;
import com.lahenry.nimbus.gui.components.FileItemPanelMouseAdapter;

/**
 *
 * @author henry
 */
public abstract class GDriveFileItemPanelMouseAdapter
    extends FileItemPanelMouseAdapter<File>
{
    public GDriveFileItemPanelMouseAdapter(File item)
    {
        super(item);
    }

    @Override
    public boolean isFolder(final File item)
    {
        return m_item.getMimeType().equals(GDriveConstants.MIME_TYPE_FOLDER);
    }

}

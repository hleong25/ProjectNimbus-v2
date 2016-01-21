/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.dropbox.gui;

import com.dropbox.core.DbxEntry;
import com.lahenry.nimbus.gui.components.FileItemPanelMouseAdapter;

/**
 *
 * @author henry
 */
public abstract class DropboxFileItemPanelMouseAdapter
    extends FileItemPanelMouseAdapter<DbxEntry>
{
    public DropboxFileItemPanelMouseAdapter(DbxEntry item)
    {
        super(item);
    }

    @Override
    public boolean isFolder(final DbxEntry item)
    {
        return item.isFolder();
    }
}

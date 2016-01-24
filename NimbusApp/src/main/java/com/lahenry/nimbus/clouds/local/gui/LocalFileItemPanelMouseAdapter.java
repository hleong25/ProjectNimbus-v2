/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.local.gui;

import com.lahenry.nimbus.gui.components.FileItemPanelMouseAdapter;
import com.lahenry.nimbus.utils.Logit;
import java.io.File;

/**
 *
 * @author henry
 */
public abstract class LocalFileItemPanelMouseAdapter
    extends FileItemPanelMouseAdapter<File>
{
    private static final Logit LOG = Logit.create(LocalFileItemPanelMouseAdapter.class.getName());

    public LocalFileItemPanelMouseAdapter(File item)
    {
        super(item);
    }

    @Override
    public boolean isFolder(final File item)
    {
        return item.isDirectory();
    }

}

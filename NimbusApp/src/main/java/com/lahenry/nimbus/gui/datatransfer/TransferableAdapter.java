/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.datatransfer;

import com.dropbox.core.DbxEntry;
import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public abstract class TransferableAdapter<T>
    implements Transferable
{
    public static final DataFlavor LocalFileFlavor = new DataFlavor(java.io.File.class, "Nimbus Local File");
    public static final DataFlavor GDriveFileFlavor = new DataFlavor(com.google.api.services.drive.model.File.class, "Nimbus Google Drive File");
    public static final DataFlavor DropboxFileFlavor = new DataFlavor(DbxEntry.class, "Nimbus Google Drive File");

    protected final GlobalCacheKey m_globalCacheKey;
    protected List<T> m_list = new ArrayList<>();

    protected TransferableAdapter(ICloudController controller)
    {
        m_globalCacheKey = GlobalCache.getInstance().getKey(controller);
    }

    public GlobalCacheKey getGlobalCacheKey()
    {
        return m_globalCacheKey;
    }

    public void add(T obj)
    {
        m_list.add(obj);
    }

    public static boolean isNimbusDataFlavorSupported(DataFlavor flavor)
    {
        if ((flavor == LocalFileFlavor) ||
            (flavor == GDriveFileFlavor) ||
            (flavor == DropboxFileFlavor) )
        {
            return true;
        }
        return false;
    }

    @Override
    public abstract DataFlavor[] getTransferDataFlavors();

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        for (DataFlavor knownFlavor : getTransferDataFlavors())
        {
            if (flavor.equals(knownFlavor))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (!isDataFlavorSupported(flavor))
        {
            throw new UnsupportedFlavorException(flavor);
        }

        return new TransferableContainer<T>(m_globalCacheKey, m_list);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.datatransfer;

import com.dropbox.core.DbxEntry;
import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.gui.components.FileItemPanel;
import com.lahenry.nimbus.utils.Logit;
import java.awt.datatransfer.DataFlavor;
import java.util.List;

/**
 *
 * @author henry
 */
public class ListDropboxTransferable
    extends TransferableAdapter<DbxEntry>
{
    private static final Logit LOG = Logit.create(ListDropboxTransferable.class.getName());

    private static final DataFlavor[] DropboxFlavors = new DataFlavor[]{
        DropboxFileFlavor
    };

    public ListDropboxTransferable(ICloudController controller)
    {
        super(controller);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return DropboxFlavors;
    }

    public static ListDropboxTransferable createInstance(ICloudController controller, List<FileItemPanel> pnls)
    {
        ListDropboxTransferable list = new ListDropboxTransferable(controller);

        for (FileItemPanel pnl : pnls)
        {
            if (pnl.getFileItem().getCloudController().getCloudType() != CloudType.DROPBOX)
            {
                continue;
            }

            list.add((DbxEntry) pnl.getFileItem().getCloudObject());
        }

        return list;
    }
}

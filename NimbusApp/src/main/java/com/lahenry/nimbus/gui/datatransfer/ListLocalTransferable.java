/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.datatransfer;

import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.gui.components.FileItemPanel;
import com.lahenry.nimbus.utils.Logit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

/**
 *
 * @author henry
 */
public class ListLocalTransferable
    extends TransferableAdapter<File>
{
    private static final Logit LOG = Logit.create(ListLocalTransferable.class.getName());

    private static final DataFlavor[] LocalFlavors = new DataFlavor[]{
        LocalFileFlavor
    };

    public ListLocalTransferable(ICloudController controller)
    {
        super(controller);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return LocalFlavors;
    }

    public static ListLocalTransferable createInstance(ICloudController controller, List<FileItemPanel> pnls)
    {
        LOG.entering("createInstance", new Object[] {controller, pnls});

        ListLocalTransferable list = new ListLocalTransferable(controller);

        for (FileItemPanel pnl : pnls)
        {
            LOG.fine(pnl.getFileItem().getCloudController().getCloudType().toString());
            if (pnl.getFileItem().getCloudController().getCloudType() != CloudType.LOCAL_FILE_SYSTEM)
            {
                continue;
            }

            list.add((File) pnl.getFileItem().getCloudObject());
        }

        return list;
    }
}

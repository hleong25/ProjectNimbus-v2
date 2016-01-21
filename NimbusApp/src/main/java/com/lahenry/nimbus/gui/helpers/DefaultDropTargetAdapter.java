/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.helpers;

import com.lahenry.nimbus.gui.datatransfer.TransferableAdapter;
import com.lahenry.nimbus.gui.datatransfer.TransferableContainer;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.List;

/**
 *
 * @author henry
 */
public abstract class DefaultDropTargetAdapter extends DropTargetAdapter
{
    private static final Logit Log = Logit.create(DefaultDropTargetAdapter.class.getName());

    public DefaultDropTargetAdapter()
    {
        // do nothing
    }

    public abstract boolean onAction_drop(TransferableContainer tc);

    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dtde)
    {
        Log.entering("drop");

        // Accept copy drops
        dtde.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = dtde.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors)
        {
            try
            {
                final TransferableContainer tc;

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType())
                {
                    Log.fine("Drag&Drop from system");
                    Log.fine("Flavor mime: "+flavor.getMimeType());
                    // Get all of the dropped files
                    // HL: this might not work
                    // TODO: check for unchecked or unsafe operations
                    tc = new TransferableContainer(GlobalCacheKey.Empty, (List) transferable.getTransferData(flavor));
                }
                else if (TransferableAdapter.isNimbusDataFlavorSupported(flavor))
                {
                    Log.fine("Nimbus Drag&Drop");
                    Log.fine("Flavor mime: "+flavor.getMimeType());
                    Log.fine(transferable.toString());
                    tc = (TransferableContainer) transferable.getTransferData(flavor);
                }
                else
                {
                    tc = null;
                }

                ResponsiveTaskUI.doTask(new ResponsiveTaskUI.IResponsiveTask()
                {
                    @Override
                    public void run()
                    {
                        if (tc != null)
                        {
                            onAction_drop(tc);
                        }
                    }
                });
            }
            catch (Exception ex)
            {
                Log.throwing("drop", ex);
            }
        }

        // Inform that the drop is complete
        dtde.dropComplete(true);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.helpers;

import com.lahenry.nimbus.clouds.local.LocalController;
import com.lahenry.nimbus.gui.datatransfer.TransferableAdapter;
import com.lahenry.nimbus.gui.datatransfer.TransferableContainer;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public abstract class DefaultDropTargetAdapter extends DropTargetAdapter
{
    private static final Logit LOG = Logit.create(DefaultDropTargetAdapter.class.getName());

    public DefaultDropTargetAdapter()
    {
        // do nothing
    }

    public abstract boolean onAction_drop(TransferableContainer tc);

    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dtde)
    {
        LOG.entering("drop");

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
                    LOG.fine("Drag&Drop from system");
                    LOG.fine("Flavor mime: "+flavor.getMimeType());
                    // Get all of the dropped files

                    final GlobalCacheKey key;
                    GlobalCache gc = GlobalCache.getInstance();
                    ArrayList<GlobalCacheKey> list = gc.findKey(LocalController.class.getName());

                    if (list.isEmpty())
                    {
                        key = GlobalCacheKey.Empty;
                    }
                    else
                    {
                        key = list.get(0);
                    }

                    tc = new TransferableContainer(key, (List) transferable.getTransferData(flavor));
                }
                else if (TransferableAdapter.isNimbusDataFlavorSupported(flavor))
                {
                    LOG.fine("Nimbus Drag&Drop");
                    LOG.fine("Flavor mime: "+flavor.getMimeType());
                    LOG.fine(transferable.toString());
                    tc = (TransferableContainer) transferable.getTransferData(flavor);
                }
                else
                {
                    tc = null;
                }

                LOG.fine("Source cache key="+tc.getSourceCacheKey());

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
                LOG.throwing("drop", ex);
            }
        }

        // Inform that the drop is complete
        dtde.dropComplete(true);
    }
}

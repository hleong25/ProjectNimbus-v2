/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import com.lahenry.nimbus.defines.FileType;
import com.lahenry.nimbus.gui.GStreamerFrame;
import com.lahenry.nimbus.gui.ImageViewerFrame;
import com.lahenry.nimbus.gui.components.FileItemPanel;
import com.lahenry.nimbus.gui.datatransfer.TransferableContainer;
import com.lahenry.nimbus.gui.helpers.BusyTaskCursor;
import com.lahenry.nimbus.gui.helpers.FileItemPanelGroup;
import com.lahenry.nimbus.gui.helpers.ResponsiveTaskUI;
import com.lahenry.nimbus.gui.helpers.XferHolder;
import com.lahenry.nimbus.gui.interfaces.ILayoutToCloudPanelProxy;
import com.lahenry.nimbus.gui.layout.AllCardsPanel;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.FileUtils;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author henry
 * @param <T> - Main data type for the cloud
 * @param <CC> - ICloudController
 */
public abstract class CloudPanelAdapter<T, CC extends ICloudController<T>>
    extends javax.swing.JPanel
    implements ICloudPanel<T, CC>,
               ILayoutToCloudPanelProxy
{
    private static final Logit LOG = Logit.create(CloudPanelAdapter.class.getName());

    protected final Map<T, List<Component>> m_cachedComponents = new HashMap<>();

    protected AtomicBoolean m_canTransfer = new AtomicBoolean(true);

    protected CC m_controller;

    protected T m_currentPath;

    protected /*ICloudTransfer*/ Object m_xferObject;

    protected CloudPanelAdapter()
    {
        LOG.entering("<init>");
    }

    @Override
    public void disposePanel()
    {
        LOG.entering("disposePanel");
        m_canTransfer.set(false);

        Tools.wait(m_xferObject);
    }

    @Override
    public void setCurrentPath(T path)
    {
        m_currentPath = path;
    }

    @Override
    public void responsiveShowFiles(final T path, final boolean useCache)
    {
        LOG.entering("responsiveShowFiles", new Object[]{getAbsolutePath(path), useCache});

        BusyTaskCursor.doTask(this, new BusyTaskCursor.IBusyTask()
        {
            @Override
            public void run()
            {
                showFiles(path, useCache);
            }
        });
    }

    @Override
    public void responsiveOpenFile(final T item)
    {
        LOG.entering("responsiveOpenFile", new Object[]{getAbsolutePath(item)});

        String name = m_controller.getItemName(item);
        FileType filetype = m_controller.getFileType(item);

        switch (filetype)
        {
            case IMAGE:
            {
                InputStream istream = m_controller.getDownloadStream(item);
                ImageViewerFrame.show(this, name, istream);
            }
            break;

            case AUDIO:
            {
                GStreamerFrame.showAudio(this, name, m_controller, item);
            }
            break;

            case VIDEO:
            {
                GStreamerFrame.showVideo(this, name, m_controller, item);
            }
            break;

            default:
            {
                String msg = "Cannot open '"+name+"' as type "+FileUtils.getMimeType(name);
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    }

    @Override
    public List<Component> getFiles(final T parent, final boolean useCache)
    {
        LOG.entering("getFiles", new Object[]{getAbsolutePath(parent), useCache});

        List<Component> list;

        if (useCache && m_cachedComponents.containsKey(parent))
        {
            LOG.fine(String.format("Cache hit '%s'", getAbsolutePath(parent)));
            list = m_cachedComponents.get(parent);
        }
        else
        {
            list = new ArrayList<>();

            FileItemPanelGroup group = new FileItemPanelGroup();

            // show parent link
            {
                T grandParentFile = m_controller.getParent(parent);

                if (grandParentFile != null)
                {
                    FileItemPanel pnl = createFileItemPanel(grandParentFile);

                    pnl.setLabel("..");

                    group.add(pnl);
                    list.add(pnl);
                }
            }

            // get all files in this folder
            final List<T> files = m_controller.getChildrenItems(parent, useCache);

            LOG.fine("Total files: "+files.size());

            for (T file : files)
            {
                FileItemPanel pnl = createFileItemPanel(file);
                group.add(pnl);
                list.add(pnl);
            }

            m_cachedComponents.put(parent, list);
        }

        return list;
    }

    @Override
    public void showFiles(final T parent, final boolean useCache)
    {
        LOG.entering("showFiles", new Object[]{getAbsolutePath(parent), useCache});

        if (m_xferObject != null)
        {
            JOptionPane.showMessageDialog(this, "Transferring in progress...", AppInfo.NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        setCurrentPath(parent);

        List<Component> list = getFiles(parent, useCache);

        if (!list.isEmpty())
        {
            // must reset the highlights
            FileItemPanel pnl = (FileItemPanel) list.get(0);
            if (pnl.getGroup() != null)
            {
                pnl.getGroup().reset();
            }
        }

        final JPanel pnlFiles = getFilesPanel();

        // remove all items first
        pnlFiles.removeAll();

        // add the components to the panel
        for (Component pnl : list)
        {
            pnlFiles.add(pnl);
        }

        // make sure repaint happens
        pnlFiles.revalidate();
        pnlFiles.repaint();

        // for keyreleased to work properly
        pnlFiles.requestFocusInWindow();
    }

    @Override
    public void setPanelView(AllCardsPanel.ViewType type)
    {
        AllCardsPanel pnl = getFilesPanel();
        pnl.setView(type);
    }

    @Override
    public void proxyKeyReleased(KeyEvent evt)
    {
        LOG.entering("proxyKeyReleased", evt);
        if (evt.getKeyCode() == KeyEvent.VK_F5)
        {
            LOG.fine("KeyEvent.VK_F5");
            responsiveShowFiles(m_currentPath, false);
        }
    }

    @Override
    public void refreshCurrentView()
    {
        LOG.entering("refreshCurrentView");
        responsiveShowFiles(m_currentPath, false);
    }

    @Override
    public List<XferHolder<?, T>> generateTransferList(TransferableContainer tc)
    {
        LOG.entering("generateTransferList", new Object[]{tc});

        final List<XferHolder<?, T>> uploadFiles = new ArrayList<>();

        final JPanel pnlFiles = getFilesPanel();

        final GlobalCacheKey sourceCacheKey = tc.getSourceCacheKey();

        for (Object obj : tc.getList())
        {
            LOG.fine(obj.toString());

            XferHolder<?, T> holder = createXferHolder(sourceCacheKey, obj);
            holder.xfer.setCanTransfer(m_canTransfer);

            uploadFiles.add(holder);

            // show the new item being added
            pnlFiles.add(holder.pnl);
            pnlFiles.revalidate();

            ResponsiveTaskUI.yield();
        }

        return uploadFiles;
    }

    @Override
    public boolean doTransferLoop(List<XferHolder<?, T>> list)
    {
        LOG.entering("doTransferLoop", new Object[]{list});

        // Loop them through
        for (XferHolder<?, T> holder : list)
        {
            // Print out the file path
            LOG.fine("Source: "+holder.xfer.getSourceObject()+"\nTarget: "+holder.xfer.getTargetObject());

            holder.xfer.setProgressHandler(new CloudProgressAdapter(holder.pnl));

            m_xferObject = holder.xfer;

            m_controller.transfer(holder.xfer);

            if (!m_canTransfer.get())
            {
                return false;
            }
        }

        m_xferObject = null;

        return true;
    }

    @Override
    public boolean onAction_drop(TransferableContainer tc)
    {
        LOG.entering("onAction_drop", new Object[]{tc});

        if (m_xferObject != null)
        {
            JOptionPane.showMessageDialog(this, "Cannot start new transfer when transferring in progress...", AppInfo.NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        List<XferHolder<?, T>> uploadFiles = generateTransferList(tc);

        if (doTransferLoop(uploadFiles))
        {
            showFiles(m_currentPath, false);
        }

        return true;
    }
}

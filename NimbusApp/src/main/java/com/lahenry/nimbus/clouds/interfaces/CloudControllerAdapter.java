/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import com.lahenry.nimbus.accountmanager.AccountInfo;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.awt.Component;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author henry
 */
public abstract class CloudControllerAdapter<T>
    implements ICloudController<T>
{
    private static final Logit LOG = Logit.create(CloudControllerAdapter.class.getName());

    protected final ICloudModel<T> m_model;

    protected final GlobalCache.IProperties m_gcprops;

    protected final Comparator<T> m_comparatorFiles;

    protected final Map<T, List<T>> m_cachedListFiles = new HashMap<>();
    protected final Map<String, T> m_cachedFiles = new HashMap<>();

    protected String m_rootFolder;

    protected CloudControllerAdapter(final String className, ICloudModel<T> model)
    {
        LOG.entering("<init>");

        m_model = model;

        m_gcprops = new GlobalCache.IProperties()
        {
            @Override
            public String getPackageName()
            {
                return "controllers/"+className;
            }
        };

        m_comparatorFiles = new Comparator<T>()
        {
            @Override
            public int compare(T f1, T f2)
            {
                //boolean f1_isdir = f1.getMimeType().equals(GDriveConstants.MIME_TYPE_FOLDER);
                //boolean f2_isdir = f2.getMimeType().equals(GDriveConstants.MIME_TYPE_FOLDER);

                boolean f1_isdir = m_model.isFolder(f1);
                boolean f2_isdir = m_model.isFolder(f2);

                if (f1_isdir ^ f2_isdir)
                {
                    return f1_isdir ? -1 : 1;
                }

                String f1_name = m_model.getName(f1);
                String f2_name = m_model.getName(f2);
                return f1_name.compareTo(f2_name);

                // return f1.getTitle().compareTo(f2.getTitle());
            }
        };
    }

    @Override
    public boolean login(Component parentComponent, String uniqueid)
    {
        LOG.entering("login", new Object[]{"parentComponent", uniqueid});
        boolean login_ok = false;

        if (uniqueid.equals(AccountInfo.NEW_ACCOUNT))
        {
            String authCode = getAuthCode(parentComponent);
            login_ok = m_model.loginViaAuthCode(authCode);
        }
        else
        {
            login_ok = m_model.loginViaStoredId(uniqueid);
        }

        if (login_ok)
        {
            GlobalCache.getInstance().put(m_gcprops, m_model.getUniqueId(), this);
        }

        return login_ok;
    }

    @Override
    public String getUniqueId()
    {
        LOG.entering("getUniqueId");
        return m_model.getUniqueId();
    }

    protected String getAuthCode(Component parentComponent)
    {
        LOG.entering("getAuthCode", new Object[]{"parentComponent"});

        try
        {
            final String authUrl = m_model.getAuthUrl();

            // For OSX, must set mrj.version to 3.1 or above in commandline
            // example: java -Dmrj.version="10.10" app.jar

            BrowserLauncher launcher = new BrowserLauncher();
            launcher.setNewWindowPolicy(true);

            LOG.fine("Opening new browser to "+authUrl);
            launcher.openURLinBrowser(authUrl);
        }
        catch (BrowserLaunchingInitializingException | UnsupportedOperatingSystemException ex)
        {
            LOG.throwing("getAuthCode", ex);
        }

        String authCode = JOptionPane.showInputDialog(parentComponent, "Input the authentication code here");

        if (authCode != null)
            authCode = authCode.trim();

        LOG.info("Auth code: "+authCode);
        return authCode;
    }

    @Override
    public T getRoot()
    {
        LOG.entering("getRoot");

        T root = m_model.getRoot();
        return root;
    }

    @Override
    public T getItemById(String id, boolean useCache)
    {
        LOG.entering("getItemById", new Object[]{id, useCache});

        if (useCache && m_cachedFiles.containsKey(id))
        {
            LOG.info("Cache hit: "+id);
            return m_cachedFiles.get(id);
        }

        T file;

        if (id.equals(m_rootFolder))
        {
            file = m_model.getRoot();
        }
        else
        {
            file = m_model.getItemById(id);
        }

        if (file != null)
        {
            String fileId = m_model.getIdByItem(file);
            LOG.info("Add cache: "+fileId);
            m_cachedFiles.put(fileId, file);
        }

        return file;
    }

    @Override
    public List<T> getChildrenItems(T parent, boolean useCache)
    {
        LOG.entering("getChildrenItems", new Object[]{parent, useCache});

        if (parent == null)
        {
            parent =  m_model.getRoot();
        }

        if (useCache && m_cachedListFiles.containsKey(parent))
        {
            LOG.info("Cache hit: "+m_model.getIdByItem(parent));
            return m_cachedListFiles.get(parent);
        }

        List<T> files =  m_model.getChildrenItems(parent);

        Collections.sort(files, m_comparatorFiles);

        LOG.fine("Add cache: "+m_model.getIdByItem(parent));
        m_cachedListFiles.put(parent, files);

        return files;
    }

    @Override
    public void transfer(ICloudTransfer</*source*/?, /*target*/T> transfer)
    {
        try
        {
            m_model.transfer(transfer);
        }
        finally
        {
            Tools.notifyAll(transfer);
        }
    }

    @Override
    public InputStream getDownloadStream(T downloadFile)
    {
        return m_model.getDownloadStream(downloadFile);
    }

}

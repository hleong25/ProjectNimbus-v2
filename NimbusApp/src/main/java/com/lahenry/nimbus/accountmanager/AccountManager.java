/*
w
yellowa13dsadyfdsfsDFSFfdsfsdfdsfsdyel
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.accountmanager;

import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.NimbusDatastore;
import com.lahenry.nimbus.utils.Tools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author henry
 */
public class AccountManager
{
    private static final Logit LOG = Logit.create(AccountManager.class.getName());
    private static AccountManager m_singleton = null;
    private static boolean m_createdSingleton = false;

    private final String FILE_ACCOUNTS = "accounts";

    private final String ELEM_ROOT = "nimbus";

    private final DocumentBuilder m_docbuilder;

    private final Map<String, AccountInfo> m_accounts;

    protected AccountManager() throws ParserConfigurationException
    {
        m_accounts = new HashMap<>();

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        m_docbuilder = dbfactory.newDocumentBuilder();

        parseCredsFile();
    }

    static public AccountManager getInstance()
    {
        if (!m_createdSingleton && (m_singleton == null))
        {
            // create the singleton only once
            m_createdSingleton = true;
            try
            {
                m_singleton = new AccountManager();
            }
            catch (ParserConfigurationException ex)
            {
                LOG.throwing("getInstance", ex);
                return null;
            }
        }
        return m_singleton;
    }

    protected boolean parseCredsFile()
    {
        File accountsFile = NimbusDatastore.getFile("creds", FILE_ACCOUNTS);
        if (!accountsFile.canRead())
        {
            LOG.warning("Failed to read creds file");
            return false;
        }

        LOG.fine("Reading credentials: " + accountsFile.getAbsolutePath());

        try
        {
            Document doc;
            Element root;

            doc = m_docbuilder.parse(accountsFile);
            root = doc.getDocumentElement();
            root.normalize();

            if (!root.getNodeName().equals(ELEM_ROOT))
            {
                LOG.severe("Failed to parse accounts file because root element not '"+ELEM_ROOT+"'.");
                return false;
            }

            NodeList nodes = root.getElementsByTagName(AccountInfo.ELEM_ROOT);

            for (int idx = 0, end = nodes.getLength(); idx < end; ++idx)
            {
                Node node = nodes.item(idx);

                switch (node.getNodeType())
                {
                    case Node.ELEMENT_NODE:
                        {
                            Element elem = (Element) node;
                            AccountInfo info = AccountInfo.createInstance(elem);

                            if (info != null)
                            {
                                addAccountInfo(info);
                            }
                            else
                            {
                                LOG.warning("Failed to parse #"+idx+" account info");
                            }

                        }
                        break;
                }
            }

            return true;
        }
        catch (SAXException | IOException ex)
        {
            LOG.throwing("parseCredsFile", ex);
        }
        return false;
    }

    public boolean addAccountInfo(AccountInfo account)
    {
        m_accounts.put(account.getId(), account);
        return true;
    }

    public AccountInfo getAccountInfo(String id)
    {
        //LOG.entering("getAccountInfo");
        AccountInfo info = m_accounts.get(id);
        return info;

    }

    public List<AccountInfo> getAccounts()
    {
        List<AccountInfo> list = new ArrayList<>(m_accounts.values());
        return list;
    }

    @Override
    public String toString()
    {
        return serialize();
    }

    public String serialize()
    {
        Document doc;
        Element root;

        doc = m_docbuilder.newDocument();
        root = doc.createElement(ELEM_ROOT);
        doc.appendChild(root);

        for (AccountInfo info : m_accounts.values())
        {
            Element frag = info.serialize(doc);
            root.appendChild(frag);
        }

        return Tools.xmlToString(doc);
    }

    public boolean exportAsFile()
    {
        BufferedWriter bw = NimbusDatastore.getWriterNoThrow("creds", FILE_ACCOUNTS);

        if (bw == null)
        {
            return false;
        }

        PrintWriter writer = new PrintWriter(bw);

        writer.print(toString());
        writer.flush();
        writer.close();

        return true;
    }
}

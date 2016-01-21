/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.accountmanager;

import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author henry
 */
public class AccountInfo
{
    private static final Logit Log = Logit.create(AccountInfo.class.getName());

    public static final String NEW_ACCOUNT = "@@newacount@@";

    public static final String ELEM_ROOT = "account";
    private static final String ELEM_TYPE = "type";
    private static final String ELEM_NAME = "name";
    private static final String ELEM_SECRET = "secret";

    private static final String ATTR_VERSION = "version";
    private static final String ATTR_ID = "id";

    private int m_version = 1; // update as needed
    private final CloudType m_type;
    private String m_id;
    private String m_name;
    private Map<String, String> m_secret;

    protected AccountInfo(CloudType type, String id)
    {
        m_type = type;
        m_id = id;
        m_name = "";
        m_secret = new HashMap<>();
    }

    public static AccountInfo createNewAccount(CloudType type)
    {
        return createInstance(type, NEW_ACCOUNT);
    }

    public static AccountInfo createInstance(CloudType type, String id)
    {
        AccountInfo info = new AccountInfo(type, id);
        return info;
    }

    public static AccountInfo createInstance(Element fragment)
    {
        String data;

        if (!fragment.getNodeName().equals(ELEM_ROOT))
        {
            Log.warning("Parsing element, not '"+ELEM_ROOT+"'");
            return null;
        }

        // get version
        int version = 0;
        {
            data = fragment.getAttribute(ATTR_VERSION);

            try
            {
                version = Integer.parseInt(data);
            }
            catch (NumberFormatException ex)
            {
                Log.warning("Failed to parse version");
                return null;
            }
        }

        // get account id
        String id = fragment.getAttribute(ATTR_ID);

        AccountInfo info = null;

        // parse each child element
        NodeList nodes = fragment.getChildNodes();

        for (int idx = 0, end = nodes.getLength(); idx < end; ++idx)
        {
            Node node = nodes.item(idx);
            if (node.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            Element elem = (Element) nodes.item(idx);
            String nodeName = elem.getNodeName();

            if (nodeName.equals(ELEM_TYPE))
            {
                data = elem.getTextContent();

                if (Tools.isNullOrEmpty(data))
                {
                    continue;
                }

                CloudType cloudType;
                if (data.equals(CloudType.GOOGLE_DRIVE.toString()))
                {
                    cloudType = CloudType.GOOGLE_DRIVE;
                }
                else if (data.equals(CloudType.DROPBOX.toString()))
                {
                    cloudType = CloudType.DROPBOX;
                }
                else
                {
                    Log.severe("Unknown cloud type: "+data);
                    return null;
                }

                info = AccountInfo.createInstance(cloudType, id);
            }
            else if (nodeName.equals(ELEM_NAME))
            {
                data = elem.getTextContent();
                info.setName(data);
            }
            else if (nodeName.equals(ELEM_SECRET))
            {
                NodeList secrets = elem.getChildNodes();

                for (int idx_secrets = 0, end_secrets = secrets.getLength();
                     idx_secrets < end_secrets;
                     ++idx_secrets)
                {
                    Node secret = secrets.item(idx_secrets);
                    if (secret.getNodeType() != Node.ELEMENT_NODE)
                    {
                        continue;
                    }

                    Element elem_secret = (Element) secret;

                    String key = elem_secret.getNodeName();
                    String value = elem_secret.getTextContent();

                    if (Tools.isNullOrEmpty(key) || Tools.isNullOrEmpty(value))
                    {
                        continue;
                    }

                    info.addSecret(key, value);
                }
            }
        }

        return info;
    }

    private void setVersion(int version)
    {
        m_version = version;
    }

    public int getVersion()
    {
        return m_version;
    }

    public CloudType getType()
    {
        return m_type;
    }

    public String getId()
    {
        return m_id;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public String getName()
    {
        return m_name;
    }

    public void addSecret(String key, String secret)
    {
        m_secret.put(key, secret);
    }

    public String getSecret(String key)
    {
        return m_secret.get(key);
    }

    public Element serialize(Document doc)
    {
        Element child;
        Element fragment = doc.createElement(ELEM_ROOT);

        fragment.setAttribute(ATTR_VERSION, String.valueOf(getVersion()));
        fragment.setAttribute(ATTR_ID, getId());

        {
            child = doc.createElement(ELEM_TYPE);
            fragment.appendChild(child);
            child.setTextContent(getType().toString());
        }

        {
            child = doc.createElement(ELEM_NAME);
            fragment.appendChild(child);
            child.setTextContent(getName());
        }

        {
            child = doc.createElement(ELEM_SECRET);
            fragment.appendChild(child);

            for (Map.Entry<String, String> entry : m_secret.entrySet())
            {
                Element secret = doc.createElement(entry.getKey());
                secret.setTextContent(entry.getValue());

                child.appendChild(secret);
            }
        }

        return fragment;
    }
}

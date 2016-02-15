/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author henry
 */
public final class GlobalCache
    extends HashMap<GlobalCacheKey, Object>
{
    private static final Logit LOG = Logit.create(GlobalCache.class.getName());

    public interface IProperties
    {
        String getPackageName();
    }

    private static GlobalCache m_global = null;

    private GlobalCache()
    {
        // do nothing
    }

    public static GlobalCache getInstance()
    {
        if (m_global == null)
        {
            m_global = new GlobalCache();
        }

        return m_global;
    }

    public boolean containsKey(String key)
    {
        GlobalCacheKey gck = new GlobalCacheKey(key);
        return super.containsKey(gck);
    }

    @Override
    public Object put(GlobalCacheKey key, Object value)
    {
        throw new NoSuchMethodError("Use put(IProperties, String, Object)");
    }

    public Object put(final IProperties props, String key, Object value)
    {
        String pkgkey = ((props != null) ? props.getPackageName()+"/" : "")+key;
        GlobalCacheKey gck = new GlobalCacheKey(pkgkey);

        if (value != null)
        {
            if (containsKey(gck) && (get(gck) != value))
            {
                LOG.info("Overriding cache:"+gck);
            }
            else
            {
                LOG.info("Adding cache:"+gck);
            }

            return super.put(gck, value);
        }
        LOG.warning("Adding cache failed.");
        return null;
    }

    public GlobalCacheKey getKey(final Object needle)
    {
        for (Map.Entry<GlobalCacheKey, Object> entry : entrySet())
        {
            if (entry.getValue() == needle)
            {
                return entry.getKey();
            }
        }
        return GlobalCacheKey.Empty;
    }

    public ArrayList<GlobalCacheKey> findKey(final String needle)
    {
        ArrayList<GlobalCacheKey> list = new ArrayList<>();

        for (Map.Entry<GlobalCacheKey, Object> entry : entrySet())
        {
            if (entry.getValue().toString().indexOf(needle) != -1)
            {
                list.add(entry.getKey());
            }
        }

        if (list.isEmpty())
        {
            list.add(GlobalCacheKey.Empty);
        }

        return list;
    }
}

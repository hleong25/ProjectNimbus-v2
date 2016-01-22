/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author henry
 */
public final class GlobalCacheKey
    implements Serializable
{
    private static final Logit LOG = Logit.create(GlobalCacheKey.class.getName());

    private final String m_key;

    public static final GlobalCacheKey Empty = new GlobalCacheKey("");

    public GlobalCacheKey(String key)
    {
        m_key = key;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.m_key);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GlobalCacheKey other = (GlobalCacheKey) obj;
        if (!Objects.equals(this.m_key, other.m_key))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return m_key;
    }
}

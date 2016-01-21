/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.datatransfer;

import com.lahenry.nimbus.utils.GlobalCacheKey;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author henry
 */
public class TransferableContainer<T>
    implements Serializable
{
    protected GlobalCacheKey m_sourceCacheKey;
    protected List<T> m_list;

    public TransferableContainer(GlobalCacheKey sourceCacheKey, List<T> list)
    {
        m_sourceCacheKey = sourceCacheKey;
        m_list = list;
    }

    public GlobalCacheKey getSourceCacheKey()
    {
        return m_sourceCacheKey;
    }

    public List<T> getList()
    {
        return m_list;
    }

    public String toString()
    {
        return "[TransferableContainer] "+m_sourceCacheKey+" count:"+m_list.size();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import java.io.File;

/**
 *
 * @author henry
 */
public class LocalToLocalTransferAdapter
    extends CloudTransferAdapter<File, File>
{
    private static final Logit Log = Logit.create(LocalToLocalTransferAdapter.class.getName());

    public LocalToLocalTransferAdapter(GlobalCacheKey sourceCacheKey,
                                       File source,
                                       GlobalCacheKey targetCacheKey,
                                       File target)
    {
        super(sourceCacheKey, source, targetCacheKey, target);
    }

    @Override
    public long getFilesize()
    {
        return m_source.length();
    }

}

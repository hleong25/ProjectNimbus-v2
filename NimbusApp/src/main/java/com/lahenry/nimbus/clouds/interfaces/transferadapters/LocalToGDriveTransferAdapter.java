/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;


/**
 *
 * @author henry
 */
public class LocalToGDriveTransferAdapter
    extends CloudTransferAdapter<java.io.File, com.google.api.services.drive.model.File>
{
    private static final Logit Log = Logit.create(LocalToGDriveTransferAdapter.class.getName());

    public LocalToGDriveTransferAdapter(GlobalCacheKey sourceCacheKey,
                                        java.io.File source,
                                        GlobalCacheKey targetCacheKey,
                                        com.google.api.services.drive.model.File target)
    {
        super(sourceCacheKey, source, targetCacheKey, target);
    }

    @Override
    public long getFilesize()
    {
        return m_source.length();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.google.api.services.drive.model.File;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;

/**
 *
 * @author henry
 */
public class GDriveToLocalTransferAdapter
    extends CloudTransferAdapter<com.google.api.services.drive.model.File, java.io.File>
{
    private static final Logit Log = Logit.create(GDriveToLocalTransferAdapter.class.getName());

    public GDriveToLocalTransferAdapter(GlobalCacheKey sourceCacheKey,
                                        File source,
                                        GlobalCacheKey targetCacheKey,
                                        java.io.File target)
    {
        super(sourceCacheKey, source, targetCacheKey, target);
    }

    @Override
    public long getFilesize()
    {
        return m_source.getFileSize();
    }

}

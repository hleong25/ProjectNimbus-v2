/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.dropbox.core.DbxEntry;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;

/**
 *
 * @author henry
 */
public class LocalToDropboxTransferAdapter
    extends CloudTransferAdapter<java.io.File, DbxEntry>
{
    private static final Logit LOG = Logit.create(LocalToDropboxTransferAdapter.class.getName());

    public LocalToDropboxTransferAdapter(GlobalCacheKey sourceCacheKey,
                                         java.io.File source,
                                         GlobalCacheKey targetCacheKey,
                                         DbxEntry target)
    {
        super(sourceCacheKey, source, targetCacheKey, target);
    }

    @Override
    public long getFilesize()
    {
        return m_source.length();
    }

}

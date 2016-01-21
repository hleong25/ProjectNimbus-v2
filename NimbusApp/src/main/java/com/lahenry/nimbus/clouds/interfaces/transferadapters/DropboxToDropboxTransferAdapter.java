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
public class DropboxToDropboxTransferAdapter
    extends CloudTransferAdapter<DbxEntry, DbxEntry>
{
    private static final Logit Log = Logit.create(DropboxToDropboxTransferAdapter.class.getName());

    public DropboxToDropboxTransferAdapter(GlobalCacheKey sourceCacheKey,
                                           DbxEntry source,
                                           GlobalCacheKey targetCacheKey,
                                           DbxEntry target)
    {
        super(sourceCacheKey, source, targetCacheKey, target);
    }

    @Override
    public long getFilesize()
    {
        return m_source.asFile().numBytes;
    }

}

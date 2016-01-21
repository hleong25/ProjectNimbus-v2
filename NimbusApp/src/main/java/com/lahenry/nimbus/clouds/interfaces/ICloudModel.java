/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import com.lahenry.nimbus.utils.GlobalCacheKey;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author henry
 */
public interface ICloudModel<T>
{
    GlobalCacheKey getGlobalCacheKey();

    String getAuthUrl();

    boolean loginViaAuthCode(String authCode);
    boolean loginViaStoredId(String uniqueid);

    String getUniqueId();
    String getDisplayName();
    String getEmail();

    T getRoot();
    T getItemById(String id);
    String getIdByItem(T item);
    List<T> getChildrenItems(T parent);

    boolean isFolder(T item);
    String getName(T item);
    String getAbsolutePath(T item);

    // target must be of type T
    void transfer(final ICloudTransfer</*source*/?, /*target*/T> transfer);
    InputStream getDownloadStream(T downloadFile);
}

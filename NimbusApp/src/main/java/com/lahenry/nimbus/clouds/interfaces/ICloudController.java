/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces;

import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.defines.FileType;
import java.awt.Component;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author henry
 */
public interface ICloudController<T>
{
    CloudType getCloudType();

    boolean login(Component parentComponent, String uniqueid);
    String getUniqueId();

    T getRoot();
    T getItemById(String id, boolean useCache);
    T getParent(T item);
    List<T> getChildrenItems(T parent, boolean useCache);

    // target must be of type T
    void transfer(ICloudTransfer</*source*/?, /*target*/T> transfer);
    InputStream getDownloadStream(T downloadFile);

    String getItemName(T item);

    FileType getFileType(T item);
    long getFileSize(T item);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.local;

import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.CloudControllerAdapter;
import com.lahenry.nimbus.utils.FileUtils;
import com.lahenry.nimbus.utils.Logit;
import java.io.File;

/**
 *
 * @author henry
 */
public class LocalController
    extends CloudControllerAdapter<java.io.File>
{
    private static final Logit LOG = Logit.create(LocalController.class.getName());

    public LocalController()
    {
        super(LocalController.class.getName(), new LocalModel());

        LOG.entering("<init>");

        //m_rootFolder = m_model.getIdFromItem(getRoot());
    }

    @Override
    public CloudType getCloudType()
    {
        return CloudType.LOCAL_FILE_SYSTEM;
    }

    @Override
    public File getParent(File item)
    {
        return item.getParentFile();
    }

    @Override
    public String getItemName(File item)
    {
        return item.getName();
    }

    @Override
    public boolean isTypeImage (File item)
    {
        return FileUtils.isImage(item.getName());
    }

    @Override
    public boolean isTypeAudio (File item)
    {
        return FileUtils.isAudio(item.getName());
    }

    @Override
    public boolean isTypeVideo (File item)
    {
        return FileUtils.isVideo(item.getName());
    }
}

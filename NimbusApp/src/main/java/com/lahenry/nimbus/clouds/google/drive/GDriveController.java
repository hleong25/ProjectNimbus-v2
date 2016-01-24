/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.google.drive;

import com.google.api.services.drive.model.File;
import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.CloudControllerAdapter;
import com.lahenry.nimbus.utils.Logit;

// TODO: this class has an unchecked or unsafe operation

/**
 *
 * @author henry
 */
public class GDriveController
    extends CloudControllerAdapter<com.google.api.services.drive.model.File>
{
    private static final Logit LOG = Logit.create(GDriveController.class.getName());

    public GDriveController()
    {
        super(GDriveController.class.getName(), new GDriveModel());

        LOG.entering("<init>");

        m_rootFolder = GDriveConstants.FOLDER_ROOT;
    }

    @Override
    public CloudType getCloudType()
    {
        return CloudType.GOOGLE_DRIVE;
    }

    @Override
    public File getParent(File item)
    {
        LOG.entering("getParent", (item != null ? item.getId() : "null"));

        if (item == null)
        {
            LOG.warning("item is null");
            return null;
        }

        if (!item.getParents().isEmpty())
        {
            String parentID = item.getParents().get(0).getId();
            return getItemById(parentID, true);
        }

        return null;
    }

    @Override
    public String getItemName (File item)
    {
        return item.getTitle();
    }
}

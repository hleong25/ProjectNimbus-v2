/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.dropbox.core.DbxEntry;
import com.google.api.services.drive.model.ParentReference;
import com.lahenry.nimbus.utils.Logit;
import java.util.Arrays;

/**
 *
 * @author henry
 */
public class CloudFileUtils
{
    private static final Logit Log = Logit.create(CloudFileUtils.class.getName());

    private CloudFileUtils()
    {
        // empty
    }

    public static java.io.File convertToLocal(java.io.File outputPath,
                                              Object inputObject)
    {
        Log.entering("convertToLocal", new Object[]{outputPath, inputObject});
        final java.io.File outputFile;

        if (inputObject instanceof java.io.File)
        {
            java.io.File input = (java.io.File)inputObject;
            outputFile = new java.io.File(outputPath, input.getName());
        }
        else if (inputObject instanceof com.google.api.services.drive.model.File)
        {
            com.google.api.services.drive.model.File input = (com.google.api.services.drive.model.File)inputObject;
            outputFile = new java.io.File(outputPath, input.getTitle());
        }
        else if (inputObject instanceof com.dropbox.core.DbxEntry)
        {
            com.dropbox.core.DbxEntry input = (com.dropbox.core.DbxEntry)inputObject;
            outputFile = new java.io.File(outputPath, input.name);
        }
        else
        {
            throw new IllegalArgumentException("Input cloud type '"+inputObject.getClass().getName()+"' not supported");
        }

        Log.fine("Details of outputFile: "+outputFile);
        return outputFile;
    }

    public static com.google.api.services.drive.model.File convertToGDrive(com.google.api.services.drive.model.File outputPath,
                                                                           Object inputObject)
    {
        Log.entering("convertToLocal", new Object[]{outputPath, inputObject});
        final com.google.api.services.drive.model.File outputFile = new com.google.api.services.drive.model.File();

        ParentReference parentRef = new ParentReference();
        parentRef.setId(outputPath.getId());

        outputFile.setParents(Arrays.asList(parentRef));

        if (inputObject instanceof java.io.File)
        {
            java.io.File input = (java.io.File)inputObject;

            outputFile.setTitle(input.getName());
            outputFile.setFileSize(input.length());
            //outputFile.setMimeType(mimeType);
        }
        else if (inputObject instanceof com.google.api.services.drive.model.File)
        {
            com.google.api.services.drive.model.File input = (com.google.api.services.drive.model.File)inputObject;

            outputFile.setTitle(input.getTitle());
            outputFile.setFileSize(input.getFileSize());
            //outputFile.setMimeType(mimeType);
        }
        else if (inputObject instanceof com.dropbox.core.DbxEntry)
        {
            com.dropbox.core.DbxEntry input = (com.dropbox.core.DbxEntry)inputObject;

            outputFile.setTitle(input.name);
            outputFile.setFileSize(input.asFile().numBytes);
        }
        else
        {
            throw new IllegalArgumentException("Input cloud type '"+inputObject.getClass().getName()+"' not supported");
        }

        Log.fine("Details of outputFile: "+outputFile);
        return outputFile;
    }

    public static com.dropbox.core.DbxEntry convertToDropbox(com.dropbox.core.DbxEntry outputPath,
                                                             Object inputObject)
    {
        Log.entering("convertToLocal", new Object[]{outputPath, inputObject});

        final String path;
        final String iconName;
        final boolean mightHaveThumbnail;
        final long numBytes;
        final String humanSize;
        final java.util.Date lastModified;
        final java.util.Date clientMtime;
        final String rev = "1";

        if (inputObject instanceof java.io.File)
        {
            java.io.File input = (java.io.File)inputObject;

            path = outputPath.path + "/" + input.getName();
            iconName = null;
            mightHaveThumbnail = true;
            numBytes = input.length();
            humanSize = String.valueOf(numBytes);
            lastModified = new java.util.Date(input.lastModified());
            clientMtime = lastModified;
        }
        else if (inputObject instanceof com.google.api.services.drive.model.File)
        {
            com.google.api.services.drive.model.File input = (com.google.api.services.drive.model.File)inputObject;

            path = outputPath.path + "/" + input.getTitle();
            iconName = null;
            mightHaveThumbnail = true;
            numBytes = input.getFileSize();
            humanSize = String.valueOf(numBytes);
            lastModified = new java.util.Date(input.getModifiedDate().getValue());
            clientMtime = lastModified;
        }
        else if (inputObject instanceof com.dropbox.core.DbxEntry)
        {
            com.dropbox.core.DbxEntry input = (com.dropbox.core.DbxEntry)inputObject;

            if (input.isFolder())
            {
                throw new IllegalArgumentException("Dropbox input type is folder, must be file");
            }

            path = outputPath.path + "/" + input.name;
            iconName = null;
            mightHaveThumbnail = true;
            numBytes = input.asFile().numBytes;
            humanSize = String.valueOf(numBytes);
            lastModified = input.asFile().lastModified;
            clientMtime = lastModified;
        }
        else
        {
            throw new IllegalArgumentException("Input cloud type '"+inputObject.getClass().getName()+"' not supported");
        }

        final DbxEntry outputFile = new DbxEntry.File(path,
                                                      iconName,
                                                      mightHaveThumbnail,
                                                      numBytes,
                                                      humanSize,
                                                      lastModified,
                                                      clientMtime,
                                                      rev);

        Log.fine("Details of outputFile: "+outputFile);
        return outputFile;
    }
}

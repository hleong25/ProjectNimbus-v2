/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

/**
 *
 * @author henry
 */
public final class FileUtils
{
    private static final Logit LOG = Logit.create(FileUtils.class.getName());
    private static final MimetypesFileTypeMap MIMETYPES = new MimetypesFileTypeMap();

    public static final String MIME_TYPE_UNKNOWN = "unknown";

    public static boolean mkdir(String abspath)
    {
        File path = new File(abspath);
        if (path.exists())
        {
            return true;
        }
        return path.mkdirs();
    }

    public static String getMimeType(String filename)
    {
        // update META-INF/mime.types from
        // http://svn.apache.org/viewvc/httpd/httpd/branches/2.4.x/docs/conf/mime.types?view=markup

        String type = MIMETYPES.getContentType(filename);

        LOG.finer("File: "+filename+" Type:"+type);

        return (type != null) ? type : FileUtils.MIME_TYPE_UNKNOWN;
    }

    public static boolean isImage(String filename)
    {
        return FileUtils.getMimeType(filename).startsWith("image/");
    }

    public static boolean isAudio(String filename)
    {
        return FileUtils.getMimeType(filename).startsWith("audio/");
    }

    public static boolean isVideo(String filename)
    {
        return FileUtils.getMimeType(filename).startsWith("video/");
    }
}

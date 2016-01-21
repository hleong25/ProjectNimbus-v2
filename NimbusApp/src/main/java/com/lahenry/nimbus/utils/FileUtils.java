/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.io.File;

/**
 *
 * @author henry
 */
public final class FileUtils
{
    private static final Logit Log = Logit.create(FileUtils.class.getName());

    public static boolean mkdir(String abspath)
    {
        File path = new File(abspath);
        if (path.exists())
        {
            return true;
        }
        return path.mkdirs();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds;

/**
 *
 * @author henry
 */

public enum CloudType
{
    LOCAL_FILE_SYSTEM {
        @Override
        public String toString()
        {
            return "Local File System";
        }
    },

    GOOGLE_DRIVE {
        @Override
        public String toString()
        {
            return "Google Drive";
        }
    },

    DROPBOX {
        @Override
        public String toString()
        {
            return "Dropbox";
        }
    },
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.gui.components;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import javax.swing.ImageIcon;

/**
 *
 * @author henry
 */
public class DefaultFileItem implements IFileItem
{
    public enum FileType
    {
        FOLDER,
        FILE;
    }

    private FileType m_type;
    private String m_label;

    private DefaultFileItem(FileType type, String label)
    {
        m_type = type;
        m_label = label;
    }

    public static DefaultFileItem createRootItem()
    {
        return new DefaultFileItem(FileType.FOLDER, "..");
    }

    public static DefaultFileItem createFolder(String label)
    {
        return new DefaultFileItem(FileType.FOLDER, label);
    }

    public static DefaultFileItem createFile(String label)
    {
        return new DefaultFileItem(FileType.FILE, label);
    }

    @Override
    public ImageIcon getIcon()
    {
        String path;

        switch (m_type)
        {
            case FOLDER:
                path = "resources/icons/icon-dir.png";
                break;

            case FILE:
            default:
                path = "resources/icons/icon-file.png";
                break;

        }

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));

        return icon;
    }

    @Override
    public String getLabel()
    {
        return m_label;
    }

    @Override
    public ICloudController getCloudController()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getCloudObject()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

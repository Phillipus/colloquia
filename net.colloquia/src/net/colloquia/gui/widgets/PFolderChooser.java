package net.colloquia.gui.widgets;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import net.colloquia.util.*;

/**
*/
public class PFolderChooser
extends PFileChooser
{
    protected Frame owner;

    public PFolderChooser(Frame owner, String dialogTitle) {
        super();
        this.owner = owner;
        if(dialogTitle != null) setDialogTitle(dialogTitle);
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setApproveButtonText(LanguageManager.getString("CHOOSE_FOLDER"));
        setMultiSelectionEnabled(false);
    }

    public File getFolder() {
        int returnVal = showOpenDialog(owner);
        if(returnVal != JFileChooser.APPROVE_OPTION) return null;
        File file = getSelectedFile();
        if(file != null) {
            lastFolder = file;
            return file;
        }
        else return null;
    }
}
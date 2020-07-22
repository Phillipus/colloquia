package net.colloquia.gui.widgets;

import java.io.*;

import javax.swing.*;

import net.colloquia.io.*;

/**
* An implementation of JFileChooser that remembers the last directory.
*/
public class PFileChooser extends JFileChooser {
    /**
     * The initial default directory
    */
    protected static File lastFolder = new File(DataFiler.getDataFolder());

    public PFileChooser() {
        super(lastFolder);
    }

    public PFileChooser(File folder) {
        super(folder);
    }

    /**
    * Returns the selected file as a String and updates the local dir
    * @return The selected file as a String
    */
    public File getSelectedFileAndStore() {
        File file = getSelectedFile();
        if(file != null) {
            lastFolder = file.getParentFile();
            return file;
        }
        else return null;
    }

    /**
    * NOT YET IMPLEMENTED UNTIL JFILECHOOSER SUPPORTS MULTI_SELECT (1.3)
    * Returns the selected files as an array of Strings and updates the local dir
    * @return The selected files as an array of Strings
    */
    public File[] getSelectedFilesAndStore() {
        File[] files = getSelectedFiles();
        if(files != null) lastFolder = files[0].getParentFile();
        return files;
    }

    /**
    * Store the current directory.
    * @param dir The directory to set.
    */
    public void setStoredFolder(File folder) {
        lastFolder = folder;
    }

    public File getStoredFolder() {
        return lastFolder;
    }
}
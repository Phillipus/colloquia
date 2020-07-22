package net.colloquia.gui;

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;

/**
 * A text editor/viewer for notes, descriptions, objectives
 * But this one allows cached files
 */
public class ColloquiaTextEditor
extends HTMLEditor
implements FileListener
{
    protected ColloquiaComponent tc;
    protected boolean hottable = true;
    protected String fileName;

    final static boolean ASYNCHRONOUS = true;

	public ColloquiaTextEditor(boolean canEdit) {
    	super(canEdit);
        DataFiler.addFileListener(this);
    }

    public void loadComponentFile(ColloquiaComponent tc, int type, boolean canEdit) {
        this.tc = tc;
        String fileName = DataFiler.getTextFileName(tc, type, false);
        loadFile(fileName, canEdit);
        if(type == DataFiler.PERSONAL_NOTES) hottable = false;
    }

    public void loadFile(final String fileName, final boolean canEdit) {
        this.fileName = fileName;
        Document doc = getCachedHTMLFile(fileName);
        if(doc != null) getEditorPane().setDocument(doc);
        else {
            Runnable r = new Runnable() {
                public void run() {
                    ColloquiaTextEditor.super.loadFile(fileName, canEdit, ASYNCHRONOUS);
                }
            };
            SwingUtilities.invokeLater(r);
        }
    }

    public void setHottable(boolean set) {
    	this.hottable = set;
    }

    /**
    * An edit happened
    */
    protected void editHappened() {
        addCachedHTMLFile(fileName, editor.getDocument());
        if(tc != null && hottable) tc.setTimeStamp();
    }

    // ---------------------------------------------------------------------- \\
    // ---------------------- HTML NOTES CACHED/SAVING STUFF ---------------- \\
    // ---------------------------------------------------------------------- \\

    /**
     * List of cached HTML files
     * Format:- KEY String fileName, VALUE FileCache
     */
    private static Hashtable cachedHTMLFiles = new Hashtable();

    /**
    * Get a cached HTML file, return null if not found
    */
    private static Document getCachedHTMLFile(String fileName) {
        return fileName == null ? null : (Document)cachedHTMLFiles.get(fileName);
    }

    /**
     * Add a cached HTML file to the Hashtable
     */
    private static void addCachedHTMLFile(String fileName, Document doc) {
        if(!cachedHTMLFiles.containsKey(fileName)) {
            cachedHTMLFiles.put(fileName, doc);
        }
    }

    /**
    * Remove a dirty HTML file from the Hashtable
    */
    private static Document removeCachedHTMLFile(String fileName) {
        return (Document)cachedHTMLFiles.remove(fileName);
    }

    /**
     * Remove all dirty files from the Hashtable
     */
    private static void clearCachedHTMLFiles() {
        cachedHTMLFiles.clear();
    }


    public void saveHappened() {
        try {
            saveCachedHTMLFiles();
        }
        catch(Exception ex) {
            ErrorHandler.showWarning("ERR2", ex, "ERR");
        }
    }

    private static void saveCachedHTMLFiles() throws ColloquiaFileException {
        Enumeration e = cachedHTMLFiles.keys();
        while(e.hasMoreElements()) {
            String fileName = (String)e.nextElement();
            Document doc = getCachedHTMLFile(fileName);
            HTMLEditor.saveHTMLFile(fileName, doc);
            removeCachedHTMLFile(fileName);
        }
    }
}

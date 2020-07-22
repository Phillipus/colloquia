package net.colloquia.menu;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.tree.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

public class Action_ImportBookmarks
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    ImportThread thread;
	PMonitor monitor;

    public Action_ImportBookmarks() {
        super(LanguageManager.getString("ACTION_50") + "...", null);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
    	if(selComponent instanceof ResourceGroup) {
        	PFileChooser chooser = new PFileChooser();
	        chooser.setDialogTitle(LanguageManager.getString("24_3"));
        	File file = new File(chooser.getStoredFolder(), "bookmark.htm");
        	chooser.setSelectedFile(file);
    	    int returnVal = chooser.showOpenDialog(MainFrame.getInstance());
        	if(returnVal != PFileChooser.APPROVE_OPTION) return;
        	File fileName = chooser.getSelectedFileAndStore();

            if(thread != null) thread.waitFor();
            thread = new ImportThread(fileName);
            thread.start();
        }
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;

        // Resource Folder
        setEnabled(selComponent instanceof ResourceGroup);
    }

    class ImportThread
    extends WaitThread
    {
        File file;

        public ImportThread(File file) {
        	this.file = file;
        }

    	public void run() {
		    monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("ACTION_50"),
            	LanguageManager.getString("ACTION_50"), LanguageManager.getString("24_2"));

            Bookmarks bookMarks = new Bookmarks(file.getPath());
            Bookmarks.BookmarkDirectory rootNode = bookMarks.getRoot();

            // Count them
            int fileCount = 0;
        	Enumeration nodes = rootNode.preorderEnumeration();
        	while(nodes.hasMoreElements()) {
            	fileCount++;
            	nodes.nextElement();
            }

            monitor.init(LanguageManager.getString("24_2"), file.getName(), fileCount);

            addResourceFolder(rootNode, (ResourceGroup)selComponent);
        }
    }

    private void addResourceFolder(Bookmarks.BookmarkDirectory node, ResourceGroup parentGroup) {
    	String name = node.getName();

        // If we already have a Group, don't recreate it
        ResourceGroup rg = parentGroup.getResourceGroup(name);
        if(rg == null) {
        	rg = new ResourceGroup(name, null);
        	DataModel.addComponent(rg, parentGroup, false);
        }

        monitor.incProgress(1, true);

        Enumeration nodes = node.children();
        while(nodes.hasMoreElements()) {
            DefaultMutableTreeNode sub_node = (DefaultMutableTreeNode)nodes.nextElement();
            if(sub_node instanceof Bookmarks.BookmarkDirectory)
            	addResourceFolder((Bookmarks.BookmarkDirectory)sub_node, rg);
            else if(sub_node instanceof Bookmarks.BookmarkEntry)
            	addResource((Bookmarks.BookmarkEntry)sub_node, rg);
			if(monitor.isCanceled()) break;
        }
    }

    private void addResource(Bookmarks.BookmarkEntry entry, ResourceGroup parentGroup) {
        String name = entry.getName();
        URL url = entry.getLocation();
        String s = "";
        if(url != null) s = url.toString();
        Resource resource = parentGroup.getResource(name);
        if(resource == null) {
            resource = new Resource(name, null);
            DataModel.addComponent(resource, parentGroup, false);
        }
        resource.setURL(s, false);
        monitor.incProgress(1, true);
    }
}

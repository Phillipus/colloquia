package net.colloquia.menu;

import java.awt.event.*;
import java.io.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

public class Action_ImportFavourites
extends MenuAction
implements ComponentSelectionListener
{
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    ImportThread thread;
	PMonitor monitor;

    public Action_ImportFavourites() {
        super(LanguageManager.getString("ACTION_51") + "...", null);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
    	if(selComponent instanceof ResourceGroup) {
            PFolderChooser folderChooser = new PFolderChooser(MainFrame.getInstance(), LanguageManager.getString("24_1"));
            File folder = folderChooser.getFolder();
            if(folder == null) return;

            if(thread != null) thread.waitFor();
            thread = new ImportThread(folder);
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
        File folder;

        public ImportThread(File folder) {
        	this.folder = folder;
        }

    	public void run() {
		    monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("ACTION_51"),
            	LanguageManager.getString("ACTION_51"), LanguageManager.getString("24_2"));

            int fileCount = Utils.countFiles(folder);
            monitor.init(LanguageManager.getString("24_2"), folder.getName(), fileCount-1);

	        addResourceFolder(folder, (ResourceGroup)selComponent);
        }
    }

    private void addResourceFolder(File folder, ResourceGroup parentGroup) {
    	String name = folder.getName();

        // If we already have a Group, don't recreate it
        ResourceGroup rg = parentGroup.getResourceGroup(name);
        if(rg == null) {
        	rg = new ResourceGroup(name, null);
        	DataModel.addComponent(rg, parentGroup, false);
        }

        monitor.incProgress(1, true);

        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++) {
            File f = files[i];
        	if(files[i].isDirectory()) addResourceFolder(files[i], rg);
            else addResource(files[i], rg);
            if(monitor.isCanceled()) break;
        }
    }

    private void addResource(File file, ResourceGroup parentGroup) {
        String name = Utils.getFileName(file);
        String inLine = "";

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while(inLine != null) {
                inLine = in.readLine();
                if(inLine != null) {
		            if(monitor.isCanceled()) break;
                    inLine = inLine.toLowerCase();
                	int index = inLine.indexOf("url=");
                    if(index != -1) {
                        String url = inLine.substring(index+4);
                        Resource resource = parentGroup.getResource(name);
				        if(resource == null) {
                        	resource = new Resource(name, null);
	        				DataModel.addComponent(resource, parentGroup, false);
                        }
                        resource.setURL(url, false);
                        break;
                    }
                }
            }
            in.close();
        }
        catch (Exception ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
        }
        finally {
    	    monitor.incProgress(1, true);
        }
    }

}

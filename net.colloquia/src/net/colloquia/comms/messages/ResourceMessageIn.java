package net.colloquia.comms.messages;

import java.io.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
* A received Resource Component
*/
public class ResourceMessageIn
extends ComponentMessageIn
{

    /**
    * Constructor
    */
    protected ResourceMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }

    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        String entryName = "__resource__";
        String s = Utils.extractZipEntry(zipFile, entryName);
        if(s == null) return mInfo;

        Resource sentResource = new Resource("-", "");

        try {
            XMLStringReader reader = new XMLStringReader(s);
            reader.open();
            sentResource.unMarshallXML(reader);
            reader.close();
        }
        catch(XMLReadException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not file message", ex.getMessage());
        }

        // Do we already have this Resource?
        Resource oldResource = (Resource)DataModel.getComponent(sentResource.getGUID());
        if(oldResource != null) {
            oldResource.updateProperties(sentResource);
        }
        // New one
        else {
            // Is it for an Activity?
            String ID = mInfo.getActivityID();
            // No
            if(ID.equals("")) DataModel.addComponent(sentResource, DataModel.getResourceGroup(), false);
            // Yes
            else {
            	Activity A = (Activity)DataModel.getComponent(ID);
                if(A != null) DataModel.addComponent(sentResource, A, false);
            }
        }

        // Text Description file
        addTextFiles();

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}
}
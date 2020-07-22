package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.index.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
* A File class for a received Text Message
*/
public abstract class TextMessageIn
extends MessageIn
{

    /**
    * Constructor
    */
    protected TextMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }

    /**
    * Default behaviour - over-ride if desired or super and then over-ride
    */
    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        if(mInfo == null) return null;

        // We have to find an owning Activity otherwise it'll be an unmatched message
        Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        if(A == null || A.isInvite()) {
        	addToPending(deleteFile);
            return mInfo;
        }

        // We need to set the PersonID in the MessageInfo of the Person who sent it
        // If we don't have this it'll be an unmatched message
        Person person = DataModel.getPersonByEmailAddress(mInfo.getFrom());
        if(person == null) {
        	addToPending(deleteFile);
            return mInfo;
        }
        else mInfo.setPersonID(person.getGUID());

        // Success - I DON'T THINK WE NEED THIS AS THIS IS THE DEFAULT STATE
        mInfo.setState(MessageInfo.RCVD);

        // Save the html message
        saveHTMLMessage();
        // Save any attachments
        saveAttachments();
        // Add to target MessageIndex
        addMessageInfo();

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

        return mInfo;
    }

    /**
    * Will get and save the html file out of the Zip
    */
    protected void saveHTMLMessage() throws ColloquiaFileException {
        // Entry to look for
        String htmlMessageName = "__message__.html";

        // Get a File to save as
        String folder = mInfo.getFolderForMessage(true);
        String fileName = Utils.generateHTMLMessageFileName(folder);

        File file = Utils.extractZipEntry(zipFile, htmlMessageName, folder + fileName);
        if(file == null) return;

        // Update this filename into the MessageInfo file
        mInfo.setFileName(fileName);
    }

    /**
    * Will get and save any attachments out of the Zip
    */
    protected void saveAttachments() throws ColloquiaFileException {
        Vector attachments = mInfo.getAttachments();
		if(attachments.isEmpty()) return;

        String folder = DataFiler.getAttachmentsFolder(true);
        Hashtable newFiles = new Hashtable();

        for(int i = 0; i < attachments.size(); i++) {
            String attachmentName = (String)attachments.elementAt(i);

            // Check that the file doesn't already exist - if it does, rename
            String fileName = attachmentName;
            File checkFile = new File(folder + fileName);
            while(checkFile.exists()) {
                fileName = "_" + fileName;
                checkFile = new File(folder + fileName);
            }

            // Extract the zip entry and store the new fileName
            File theFile = Utils.extractZipEntry(zipFile, attachmentName, folder + fileName);
            if(theFile != null) newFiles.put(attachmentName, theFile.getPath());
        }

        // Now replace the file refs
        Enumeration e = newFiles.keys();
        while(e.hasMoreElements()) {
            String oldFileName = (String)e.nextElement();
            mInfo.removeAttachment(oldFileName);
            mInfo.addAttachment((String)newFiles.get(oldFileName));
        }
    }

    /*
    * If the message has been matched OK we need to put the updated MessageInfo
    * into the appropriate target MessageIndex
    */
    protected void addMessageInfo() {
        if(mInfo.getState() == MessageInfo.RCVD) {
            // Save in index
            MessageIndex index = MessageIndex.getMessageIndex(mInfo, true);
            index.addMessage(mInfo, false);
            try {
                index.save();
            }
            catch(XMLWriteException ex) {
                if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                ErrorHandler.showWarning("ERR6", ex, "ERR");
            }
        }
    }
}
package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;


/**
* A File class for a received Component Message
*/
public abstract class ComponentMessageIn
extends MessageIn
{

    /**
    * Constructor
    */
    protected ComponentMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }

    /*
    * Add text files
    * The mInfo file will contain a reference to them as Attachments in the zip
    * file.  We have to query the mInfo file, get the text file(s) out and resave
    * them with a suitable file name IF the fileDate stamp is later than ours.
    */
    protected void addTextFiles() throws ColloquiaFileException {
        Vector attachments = mInfo.getAttachments();

        for(int i = 0; i < attachments.size(); i++) {
            String attachmentName = (String)attachments.elementAt(i);

            // The attachment name will be of the format
            // component-guid.1   or  component-guid.2
            int dotPoint = attachmentName.lastIndexOf(".");
            if(dotPoint == -1) continue;

            // Get GUID
            String GUID = attachmentName.substring(0, dotPoint);

            // Get text type
            int textType = Integer.parseInt(attachmentName.substring(dotPoint + 1));

            // Get related Component from DataModel
            ColloquiaComponent tc = DataModel.getComponent(GUID);
            if(tc == null) continue;

            // Get target fileName
            String targetFileName = DataFiler.getTextFileName(tc, textType, true);

            // PENDING Java 2 - Because we can't set TimeStamp - see MessageIn.extractZipEntry()
            // The extracted file will be stamped NOW
            // Extract the zip entry to temp
            //String tempFileName =  tempFolder + attachmentName;
            //File tempFile = extractZipEntry(zipFile, attachmentName, tempFileName);
            // Make sure it's there
            //if(!tempFile.exists()) continue;
            // Does the text file already exist?
            //File targetFile = new File(targetFileName);
            // If it does, do we have a newer one?
            //if(targetFile.exists()) {
            //    long targetTime = targetFile.lastModified();
            //    long tempTime = tempFile.lastModified();
            //    if(tempTime > targetTime) extractZipEntry(zipFile, attachmentName, targetFileName);
            //}
            // Else add it
            //else extractZipEntry(zipFile, attachmentName, targetFileName);

            Utils.extractZipEntry(zipFile, attachmentName, targetFileName);

            // Delete temp file
            //tempFile.delete();
        }

    }

}
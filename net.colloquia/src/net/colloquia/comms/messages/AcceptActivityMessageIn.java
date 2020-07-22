package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * A received Activity Acceptance
 * The owner of an Activity will receive this message from one person if they accept
 */
public class AcceptActivityMessageIn
extends ComponentMessageIn
{

    /**
     * Constructor
     */
    protected AcceptActivityMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        String senderEmail = mInfo.getFrom();

        // Make sure I didn't send it to myself
        UserPrefs prefs = UserPrefs.getUserPrefs();
        if(prefs.isMyEmailAddress(senderEmail)) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Ascertain the Activity in question
        // It must be an existing Activity
        Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        if(A == null) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Now get the person who sent it
        String entryName = "__person__";
        String s = Utils.extractZipEntry(zipFile, entryName);
        if(s == null) return mInfo;

        Person sent_person = new Person("-", "");

        try {
            XMLStringReader reader = new XMLStringReader(s);
            reader.open();
            sent_person.unMarshallXML(reader);
            reader.close();
        }
        catch(XMLReadException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not file message", ex.getMessage());
        }

        // Do we already have this Person?
        Person person = DataModel.getPersonByEmailAddress(sent_person);

        // No! Then why have we got an acceptance?
        if(person == null) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Update person
        person.updateProperties(sent_person);

        A.setPersonAccepted(person, mInfo.isAccepted() ? Activity.ACCEPTED : Activity.DECLINED);
        if(!A.doesPersonHaveActivity(person)) A.setDateLastSentActivity(person, null);

        // Set mInfo Person GUID
        mInfo.setPersonID(person.getGUID());

        // Do we have a photograph?
        Vector attachments = mInfo.getAttachments();
        // Should only be one attachment
        if(attachments.size() > 0) {
            String photoName = (String)attachments.elementAt(0);
            String targetFileName = DataFiler.getPersonFolder(person.getGUID(), true) + photoName;
            Utils.extractZipEntry(zipFile, photoName, targetFileName);
            person.putProperty(Person.PHOTOGRAPH, targetFileName, false);
        }

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}
}
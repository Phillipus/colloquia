package net.colloquia.comms.messages;

import java.io.File;

import net.colloquia.comms.MessageInfo;
import net.colloquia.datamodel.DataModel;
import net.colloquia.datamodel.entities.Activity;
import net.colloquia.datamodel.entities.Person;
import net.colloquia.prefs.UserPrefs;


/**
 * A received Activity Re-activation
 */
public class ReactivateActivityMessageIn
extends ComponentMessageIn
{
    /**
    * Constructor
    */
    protected ReactivateActivityMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


    /**
    * File the message
    */
    public MessageInfo fileMessage(boolean deleteFile) {
        String senderEmail = mInfo.getFrom();

        // Make sure I didn't send it to myself
        UserPrefs prefs = UserPrefs.getUserPrefs();
        if(prefs.isMyEmailAddress(senderEmail)) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        if(A == null) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Came from Tutor - make Activity Live - It's Alive!!!
        if(!A.isMine()) A.setLive(true);

        // Came from Student
        else {
            // Find the Person
        	String emailFromAddress = mInfo.getFrom();
            Person person = DataModel.getPersonByEmailAddress(emailFromAddress);
            if(person != null) {
            	// Revive them
                A.setPersonRemoved(person, false);
            }
        }

        // Success
        mInfo.setState(MessageInfo.RCVD);

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}


}
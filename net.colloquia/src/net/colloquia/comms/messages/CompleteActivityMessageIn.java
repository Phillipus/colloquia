package net.colloquia.comms.messages;

import java.io.File;
import java.util.Vector;

import net.colloquia.comms.MessageInfo;
import net.colloquia.datamodel.DataModel;
import net.colloquia.datamodel.entities.Activity;
import net.colloquia.datamodel.entities.Person;
import net.colloquia.prefs.UserPrefs;


/**
 * A received Activity Completion
 */
public class CompleteActivityMessageIn
extends ComponentMessageIn
{
    /**
     * Constructor
     */
    protected CompleteActivityMessageIn(MessageInfo mInfo, File zipFile) {
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

        // Came from Tutor
        if(!A.isMine()) A.setCompleted();

        // Came from Student
        else {
            // Find the Person
        	String emailFromAddress = mInfo.getFrom();
            Person person = DataModel.getPersonByEmailAddress(emailFromAddress);
            if(person != null) {
            	// Cut them (grey them out) if not already
                A.setPersonRemoved(person, true);

                // The rule states that you can't be in a sub-Activity if Completed
                Vector v = A.getAllSubActivities();
                for(int i = 0; i < v.size(); i++) {
                    Activity subA = (Activity)v.elementAt(i);
                    subA.setPersonRemoved(person, true);
                }
            }
        }

        // Success
        mInfo.setState(MessageInfo.RCVD);

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}


}
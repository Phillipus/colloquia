package net.colloquia.comms.messages;

import java.util.*;
import java.util.zip.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * A File class for a sent Activity
 */
public class ActivityMessageOut
extends ComponentMessageOut
{
    boolean sendSubs;
    Vector activityComponents;

    /**
    * Constructor
    */
    protected ActivityMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
        activityComponents = new Vector();
        sendSubs = mInfo.getProperty(MessageInfo.SEND_SUBS).equalsIgnoreCase("true");
    }

    /**
    * Add content to Zip file containing:
    * 1. The Activity and its components (as XML)
    * 2. Description and Objectives files (as HTML)
    */
    protected void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        // Get the Activity
        String activityID = mInfo.getActivityID();
        Activity A = (Activity)DataModel.getComponent(activityID);
        // No Activity!
        if(A == null) return;

        // Get the components to send - include only people who have accepted the Activity
        getActivityComponents(A, activityComponents);

        // Add Me
        activityComponents.addElement(UserPrefs.getMe());

        // Marshall them all to XML
        StringBuffer sb = new StringBuffer();
        XMLStringWriter writer = new XMLStringWriter(sb);

        try {
            writer.writeln("<activity_update>");

            for(int i = 0; i < activityComponents.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)activityComponents.elementAt(i);
                SentComponent sc = tc.getSentComponent();
                if(sc != null) sc.write2XML(writer);
            }

            writer.writeln("</activity_update>");
        }
        catch(XMLWriteException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not addContentToZip", ex.getMessage());
        }

        Utils.addStringToZip(sb.toString(), "__activity__", zOut);

        addTextFiles(zOut, activityComponents);
	}

    protected void addTextFiles(ZipOutputStream zOut, Vector activityComponents) throws ColloquiaFileException {
        // Add the associated text files:-
        // Activity - Description, Objectives
        // Resource - Description
        // Person - (nothing)
        // Assignment - Description
        for(int i = 0; i < activityComponents.size(); i++) {
            String fileName, entryName;
            ColloquiaComponent tc = (ColloquiaComponent)activityComponents.elementAt(i);

            switch(tc.getType()) {
                case ColloquiaComponent.ACTIVITY:
                    fileName = DataFiler.getTextFileName(tc, DataFiler.DESCRIPTION, false);
                    entryName = tc.getGUID() + "." + DataFiler.DESCRIPTION;
                    if(DataFiler.fileExists(fileName)) {
                        addFileToZip(fileName, entryName, zOut);
                        sentmInfo.addAttachment(entryName);
                    }
                    fileName = DataFiler.getTextFileName(tc, DataFiler.OBJECTIVES, false);
                    entryName = tc.getGUID() + "." + DataFiler.OBJECTIVES;
                    if(DataFiler.fileExists(fileName)) {
                        addFileToZip(fileName, entryName, zOut);
                        sentmInfo.addAttachment(entryName);
                    }
                    break;

                case ColloquiaComponent.RESOURCE:
                case ColloquiaComponent.ASSIGNMENT:
                    fileName = DataFiler.getTextFileName(tc, DataFiler.DESCRIPTION, false);
                    entryName = tc.getGUID() + "." + DataFiler.DESCRIPTION;
                    if(DataFiler.fileExists(fileName)) {
                        addFileToZip(fileName, entryName, zOut);
                        sentmInfo.addAttachment(entryName);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
    * Gets an Activity's sub-components
    * Only Live and Mine Sub-Activities and their components will be added
    * People without e-mail addresses will not be added
    * People who are not acceptedStatus will not be added
    */
    protected void getActivityComponents(Activity A, Vector activityComponents) {
        activityComponents.addElement(A);

        Vector members = A.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);

            // If the member is a sub-Activity we only include it if it is Live and Mine
            // And user chose to send subs
            if(!sendSubs && (tc instanceof Activity)) continue;

            if(tc instanceof Activity) {
                Activity subA = (Activity)tc;
                if(!(subA.isLive() && subA.isMine())) continue;
                else getActivityComponents(subA, activityComponents);
            }

            // If the member is a Person we only include them if they have an e-mail address
            // And if they have accepted the Activity
            if(tc instanceof Person) {
                Person person = (Person)tc;
                if(person.getEmailAddress().trim().equals("")) continue;
                // Include only Accepted people
                if(A.getPersonAcceptedStatus(person) != Activity.ACCEPTED) continue;
            }

            if(!activityComponents.contains(tc)) activityComponents.addElement(tc);
        }
    }


    /**
    * Over-ride to set date last sent on this Activity and any sub_activities
    */
    public MessageInfo updateMessageInfo() {
        MessageInfo mInfo = super.updateMessageInfo();

        Date now = Utils.getNow();

        Enumeration e = activityComponents.elements();
        while(e.hasMoreElements()) {
            ColloquiaComponent tc = (ColloquiaComponent)e.nextElement();
            if(tc instanceof Activity) {
                Activity A = (Activity)tc;
                A.setNotHot();
                for(int i = 0; i < validSentAddresses.size(); i++) {
                    String email = (String)validSentAddresses.elementAt(i);
                    Person person = DataModel.getPersonByEmailAddress(email);
                    if(person != null && A.getPersonAcceptedStatus(person) == Activity.ACCEPTED) {
                    	A.setDateLastSentActivity(person, now);
                        A.setPersonSentActivity(person, true);
                    }
                }
            }
        }

        return mInfo;
    }
}
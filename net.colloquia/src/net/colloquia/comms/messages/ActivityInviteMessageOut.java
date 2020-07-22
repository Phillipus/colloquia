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
 * A File class for a sent Activity Invitation
 */
public class ActivityInviteMessageOut
extends ComponentMessageOut
{
    boolean sendSubs;
    Vector activityComponents;

    /**
     * Constructor
     */
    protected ActivityInviteMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
        activityComponents = new Vector();
        sendSubs = mInfo.getProperty(MessageInfo.SEND_SUBS).equalsIgnoreCase("true");
    }

    /**
     * Add content to Zip file containing:
     * 1. The Activity Invitation and its components (as XML)
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
            writer.writeln("<activity_invite>");

            for(int i = 0; i < activityComponents.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)activityComponents.elementAt(i);
                SentComponent sc;
                if(tc instanceof Activity) sc = new SentActivityInvite((Activity)tc);
                else sc = tc.getSentComponent();
                if(sc != null) sc.write2XML(writer);
            }

            writer.writeln("</activity_invite>");
        }
        catch(XMLWriteException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not addContentToZip", ex.getMessage());
        }

        Utils.addStringToZip(sb.toString(), "__activity__", zOut);

        addTextFiles(zOut, activityComponents);
	}

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
            // And if they have not declined the Activity
            if(tc instanceof Person) {
                Person person = (Person)tc;
                if(person.getEmailAddress().trim().equals("")) continue;
                // Include only Accepted people
                if(A.getPersonAcceptedStatus(person) == Activity.DECLINED) continue;
            }

            if(!activityComponents.contains(tc)) activityComponents.addElement(tc);
        }
    }

    /*
     * The path names are prepended with Activity GUID
     */
    protected void addTextFiles(ZipOutputStream zOut, Vector activityComponents) throws ColloquiaFileException {
        for(int i = 0; i < activityComponents.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)activityComponents.elementAt(i);
            if(tc instanceof Activity) addTextFiles((Activity)tc, zOut);
        }
    }


    protected void addTextFiles(Activity A, ZipOutputStream zOut) throws ColloquiaFileException {
    	String fileName, entryName;

        // Activity
        fileName = DataFiler.getTextFileName(A, DataFiler.DESCRIPTION, false);
        entryName = A.getGUID() + ":" + A.getGUID() + "." + DataFiler.DESCRIPTION;
        addTextFile(fileName, entryName, zOut);

        fileName = DataFiler.getTextFileName(A, DataFiler.OBJECTIVES, false);
        entryName = A.getGUID() + ":" + A.getGUID() + "." + DataFiler.OBJECTIVES;
        addTextFile(fileName, entryName, zOut);

        // sub-components
        Vector v = A.getMembers();

        for(int i = 0; i < v.size(); i++) {
        	ColloquiaComponent tc = (ColloquiaComponent)v.elementAt(i);
            if(tc instanceof Activity) addTextFiles((Activity)tc, zOut);
            else if(tc instanceof Resource || tc instanceof Assignment) {
                fileName = DataFiler.getTextFileName(tc, DataFiler.DESCRIPTION, false);
                entryName = A.getGUID() + ":" + tc.getGUID() + "." + DataFiler.DESCRIPTION;
                addTextFile(fileName, entryName, zOut);
            }
        }
    }

    private void addTextFile(String fileName, String entryName, ZipOutputStream zOut) throws ColloquiaFileException {
        if(DataFiler.fileExists(fileName)) {
            addFileToZip(fileName, entryName, zOut);
            sentmInfo.addAttachment(entryName);
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
                    if(person != null) {
                    	A.setDateLastSentActivity(person, now);
                    }
                }
            }
        }

        return mInfo;
    }
}

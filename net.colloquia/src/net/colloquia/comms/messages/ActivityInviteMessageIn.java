package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * A received Activity Invite
 */
public class ActivityInviteMessageIn
extends ActivityMessageIn
{
    /**
     * Constructor
     */
    protected ActivityInviteMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


    /**
     * File the message
     * 1. Get the e-mail address of the Sender
     * 2. Collect the Components sent in a Vector set of Entity objects
     * 3. Check whether I am there as an Entity.  If not, bail out.
     */
    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        // Who sent this?
        senderEmail = mInfo.getFrom();

        // Make sure I didn't send it to myself
        UserPrefs prefs = UserPrefs.getUserPrefs();
        if(prefs.isMyEmailAddress(senderEmail)) {
	        if(deleteFile) deleteZipFile();
        	return null;
        }

        // Collect the Components
		Vector entities = collectComponents();

        // I wasn't found!!! Maybe they used a wrong e-mail alias or something
        // So let's put it in pending
        if(!foundMe) {
        	addToPending(deleteFile);
            return mInfo;
        }

        // The topmost Activity's GUID is recorded in the mInfo
        String topActivityGUID =  mInfo.getActivityID();
        Entity topActivityEntity = getEntity(topActivityGUID, entities);
        if(topActivityEntity == null) return mInfo;   // Shouldn't happen!

        // Add Activities to tree, components to file
        addActivity(topActivityEntity, entities);

        // Add text files
        addTextFiles();

        // Success
        mInfo.setState(MessageInfo.RCVD);

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
    }

    protected void addActivity(Entity activityEntity, Vector entities) {
        if(activityEntity == null || !activityEntity.isActivity()) return;
        Activity A = (Activity)activityEntity.getComponent();
        if(A == null) return;

        // Can only Add/Update Activity and its members if:-
        // 1. The Submitter (owner) of the Activity is the person who sent this
        // AND
        // 2. I am a member of that Activity
        // BUT even if it's not OK we must check sub-Activities
        boolean OK = activityEntity.isSubmitter(senderEmail) && activityEntity.amMember();

        // Add/Update this Activity if OK
        if(OK) {
            // Ascertain parent group indirectly
            String parentGUID = A.getProperty(ColloquiaContainer.PARENT_GUID);
            ColloquiaContainer parentGroup = (ColloquiaContainer)DataModel.getComponent(parentGUID);

            // No Parent so use top Activity
            if(parentGroup == null) parentGroup = DataModel.getActivityGroup();

            // Is it a new Activity or an Update?
            Activity oldA = (Activity)DataModel.getComponent(A.getGUID());

            // Existing Activity
            if(oldA != null) {
                // If existing activity is an Invite, update it
                //if(oldA.isInvite()) oldA.updateProperties(A);
                oldA.updateProperties(A);
                // If existing activity is NOT an Invite why are we getting it?
                // This could happen if sender has removed me and then added me
                // again to become a new invitee - so we need to still do it

                // Compare existing parent with sent Parent in case of a Move
                ColloquiaContainer oldParentGroup = oldA.getParent();
                // Move
                if(parentGroup != oldParentGroup) {
                    DataModel.moveComponent(oldA, oldParentGroup, parentGroup, false);
	            	parentGroup.setComponentFree(oldA, false);
                }
            }

            // New Activity
            else {
            	DataModel.addComponent(A, parentGroup, false);
	            // Set it non-free as it is a Sent Activity
    	        parentGroup.setComponentFree(A, false);
            }
        }

        File file = DataFiler.getInviteFile(A.getGUID(), true);
        XMLFileWriter writer = new XMLFileWriter(file);

        // Write Activity members to a file
        try {
            writer.open();
            writer.writeln("<activity_invite>");

    	    Vector members = activityEntity.getMembers();
            for(int i = 0; i < members.size(); i++) {
                Member member = (Member)members.elementAt(i);
                Entity entity = getEntity(member.getGUID(), entities);
                if(entity == null) continue;

                // If it's an Activity recurse and add to tree
                if(entity.isActivity()) addActivity(entity, entities);

                // Else if OK then write to file
                else if(OK) {
                    ColloquiaComponent tc = entity.getComponent();
                    tc.write2XML(writer);
                }
            } // for

            writer.writeln("</activity_invite>");
            writer.close();
        }
        catch(Exception ex) {
            ErrorHandler.showWarning("ERR15", ex, "ERR");
        }
    }

    protected void addTextFiles() throws ColloquiaFileException {
        Vector attachments = mInfo.getAttachments();
        for(int i = 0; i < attachments.size(); i++) {
            String attachmentName = (String)attachments.elementAt(i);

            int sep = attachmentName.indexOf(":");
            if(sep == -1) continue;

            // Activity GUID
            String activityGUID = attachmentName.substring(0, sep);

            // FileName
            String fName = attachmentName.substring(sep + 1);

            String fileName = DataFiler.getInviteFolder(activityGUID, true) + DataFiler.fileSepChar + fName;
            Utils.extractZipEntry(zipFile, attachmentName, fileName);
        }
    }

}

package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.prefs.*;
import net.colloquia.xml.*;

public class SentActivityInvite
extends SentActivity
{
    public SentActivityInvite(Activity A) {
        super(A);
        // Set state to Invite
 	    setProperty(Activity.STATE, String.valueOf(Activity.INVITE));
        // Set to Pending
        setProperty(Activity.ACCEPT_DECLINE, String.valueOf(Activity.PENDING));
    }

    /*
    * Write the Activity's children
    * We only need to send the Member GUID ref
    */
    protected void writeMembers2XML(XMLWriter writer) throws XMLWriteException {
        Vector members = A.getMembers();

        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent member = (ColloquiaComponent)members.elementAt(i);
            String guid = member.getGUID();

            // If the member is a sub-Activity we only want to include it if it is Live and Mine
            if(member instanceof Activity) {
                Activity subA = (Activity)member;
                if(!(subA.isLive() && subA.isMine())) continue;
            }

            // If the member is a Person we only include them if they have an e-mail address
            // And if they have not declined the Activity
            else if(member instanceof Person) {
                Person person = (Person)member;
                String email = person.getEmailAddress().trim();
                if(email.equals("")) continue;
                // Don't include Declined people
                if(A.getPersonAcceptedStatus(person) == Activity.DECLINED) continue;
                //set GUID to be person's email address
                guid = email;
            }

            writer.writeln(MemberInfo.XMLStartTag);
            writer.write(new XMLTag(ColloquiaComponent.GUID, guid));
            writer.writeln(MemberInfo.XMLEndTag);
        }

        // Now write me as a member
        writer.writeln(MemberInfo.XMLStartTag);
        writer.write(new XMLTag(ColloquiaComponent.GUID, UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_ADDRESS)));
        writer.writeln(MemberInfo.XMLEndTag);
    }
}

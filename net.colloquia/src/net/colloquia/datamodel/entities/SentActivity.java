package net.colloquia.datamodel.entities;

import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.prefs.*;
import net.colloquia.xml.*;

public class SentActivity
extends SentComponent
{
    Activity A;

    public SentActivity(Activity A) {
        super(A, Activity.XMLStartTag, Activity.XMLEndTag);
        this.A = A;
        // Set submitter to my email
        setProperty(Activity.SUBMITTER, UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_ADDRESS));
        // Don't send hot
        removeProperty(Activity.HOT);
    }

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(startTag);

        Enumeration e = properties.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)properties.get(tag);
            writer.write(new XMLTag(tag, value));
        }

        writeMembers2XML(writer);

        writer.writeln(endTag);
    }

    /*
    * Write the Activity's children - this only goes one level deep
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
            // And if they have accepted the Activity
            else if(member instanceof Person) {
                Person person = (Person)member;
                String email = person.getEmailAddress().trim();
                if(email.equals("")) continue;
                // Include only Accepted people
                if(A.getPersonAcceptedStatus(person) != Activity.ACCEPTED) continue;
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
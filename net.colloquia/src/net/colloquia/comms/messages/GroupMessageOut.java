package net.colloquia.comms.messages;

import java.util.*;

import javax.mail.internet.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;



public class GroupMessageOut
extends TextMessageOut
{

    /**
    * Constructor
    */
    protected GroupMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Check the MessageInfo class and get the To Addresses - the members of the group
    * It will return only the valid e-mail addresses in the array
    */
    protected Vector parseToAddresses(MessageInfo mInfo, Vector invalidAddressStrings) {
        if(mInfo == null) return null;

        Vector v = new Vector();
        InternetAddress addr;

        // Get the Activity
        String activityID = mInfo.getActivityID();
        Activity A = (Activity)DataModel.getComponent(activityID);
        // No Activity!
        if(A == null) return null;

        // Get the Activity members (only Active ones)
        Vector people = A.getActivePeople();
        // No people!
        if(people.isEmpty()) return null;

        // Get their e-mail addresses
        for(int i = 0; i < people.size(); i++) {
            Person person = (Person)people.elementAt(i);
            String email = person.getEmailAddress();

            try {
                addr = parseAddress(email);
            }
            catch(AddressException ex) {
                invalidAddressStrings.addElement(email);
                continue;
            }

            v.addElement(addr);
        }

        // No addresses!
        if(v.isEmpty()) return null;

        return v;
    }

}

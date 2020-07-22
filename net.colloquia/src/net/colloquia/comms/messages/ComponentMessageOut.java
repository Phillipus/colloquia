package net.colloquia.comms.messages;

import java.util.*;

import javax.mail.internet.*;

import net.colloquia.comms.*;

/**
* A File class for a sent Component Message
*/
public abstract class ComponentMessageOut
extends MessageOut
{

    /**
    * Constructor
    */
    protected ComponentMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Check the MessageInfo class and get the To Addresses
    * Members will be those chosen at the wizard stage
    * It will return only the valid e-mail addresses in the array
    */
    protected Vector parseToAddresses(MessageInfo mInfo, Vector invalidAddressStrings) {
        if(mInfo == null) return null;

        Vector recipients = mInfo.getRecipients();
        // No recipients!
        if(recipients.isEmpty()) return null;

        Vector v = new Vector();
        InternetAddress addr;

        for(int i = 0; i < recipients.size(); i++) {
            String email = (String)recipients.elementAt(i);
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
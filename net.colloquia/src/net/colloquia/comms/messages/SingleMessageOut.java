package net.colloquia.comms.messages;

import java.util.*;

import javax.mail.internet.*;

import net.colloquia.comms.*;


public class SingleMessageOut
extends TextMessageOut
{

    /**
    * Constructor
    */
    protected SingleMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Check the MessageInfo class and get the To Address - A Single Address
    * For this simple type message, there is only one To: address
    */
    protected Vector parseToAddresses(MessageInfo mInfo, Vector invalidAddressStrings) {
        if(mInfo == null) return null;

        InternetAddress addr;

        try {
            addr = parseAddress(mInfo.getTo());
        }
        catch(AddressException ex) {
            invalidAddressStrings.addElement(mInfo.getTo());
            return null;
        }

        Vector v = new Vector();
        v.addElement(addr);
        return v;
    }


}
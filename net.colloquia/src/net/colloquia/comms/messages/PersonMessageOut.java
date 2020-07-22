package net.colloquia.comms.messages;

import java.util.zip.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * A File class for a sent Person (My Details)
 */
public class PersonMessageOut
extends ComponentMessageOut
{

    /**
    * Constructor
    */
    protected PersonMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Add content to Zip file containing:
    * 1. The Person (as XML)
    * 2. A photograph file (optional)
    */
    protected void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        Person me = UserPrefs.getMe();
        SentComponent sc = me.getSentComponent();

        // Marshall to XML
        StringBuffer sb = new StringBuffer();
        XMLStringWriter writer = new XMLStringWriter(sb);
        try {
            sc.write2XML(writer);
        }
        catch(XMLWriteException ex) {
            throw new ColloquiaFileException("Could not addContentToZip", ex.getMessage());
        }

        Utils.addStringToZip(sb.toString(), "__person__", zOut);

        // Attachment (could be photograph)
        addAttachmentsToZip(zOut);
    }


    /**
    * Over-ride to set date last sent
    */
    public MessageInfo updateMessageInfo() {
        MessageInfo mInfo = super.updateMessageInfo();

        // Update date when we sent My Details
        for(int i = 0; i < validSentAddresses.size(); i++) {
            String email = (String)validSentAddresses.elementAt(i);
            Person recipient = DataModel.getPersonByEmailAddress(email);
            if(recipient != null) recipient.setPropertyDate(Person.DATE_LAST_SENT_MYDETAILS, Utils.getNow());
        }

        return mInfo;
    }
}
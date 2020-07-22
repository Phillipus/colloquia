package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.colloquia.comms.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 * All the Information we need to send a Message and store the results of the Send attempt
 * Contains Everything for a Send - MessageInfo, Attachments, Zip, Files, etc.
 */
public abstract class MessageOut {
    protected MessageInfo mInfo;                // The source MessageInfo
    protected MessageInfo sentmInfo;            // The sent MessageInfo
    protected InternetAddress fromAddress;      // From me
    protected Vector toAddresses;    			// To them

    // Whether the message is OK to send - if false, the MessageManager won't send it
    protected boolean ok_to_send = true;

    // A Vector of String e-mail addresses that were parsed as bad BEFORE sending the message
    protected Vector invalidAddressStrings = new Vector();

    // These are the InternetAddresses that are valid/invalid
    protected Vector invalidAddresses = new Vector();          // Invalid Addresses
    protected Vector validUnsentAddresses = new Vector();      // ValidUnsent Addresses
    protected Vector validSentAddresses = new Vector();        // ValidSent Addresses

    protected Vector zipEntries = new Vector();

    /**
    * Factory method for returning the correct type of MessageOut
    * Depending on the contents of MessageInfo
    */
    public static MessageOut getMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        if(mInfo == null) return null;

        switch(mInfo.getMessageType()) {
            case MessageInfo.SINGLE_MESSAGE:
                return new SingleMessageOut(mInfo);

            case MessageInfo.GROUP_MESSAGE:
                return new GroupMessageOut(mInfo);

            case MessageInfo.ASSIGNMENT_MESSAGE:
                return new AssignmentMessageOut(mInfo);

            case MessageInfo.ACTIVITY:
                return new ActivityMessageOut(mInfo);

            case MessageInfo.ACTIVITY_INVITE:
                return new ActivityInviteMessageOut(mInfo);

            case MessageInfo.ACTIVITY_ACCEPT:
                return new AcceptActivityMessageOut(mInfo);

            case MessageInfo.ACTIVITY_COMPLETE:
                return new CompleteActivityMessageOut(mInfo);

            case MessageInfo.ACTIVITY_REACTIVATE:
                return new ReactivateActivityMessageOut(mInfo);

            case MessageInfo.PERSON:
                return new PersonMessageOut(mInfo);

            case MessageInfo.RESOURCE:
                return new ResourceMessageOut(mInfo);

            default:
                return null;
        }
    }

    /**
    * Constructor
    * If my address is invalid it throws up
    */
    protected MessageOut(MessageInfo mInfo) throws InvalidMessageException {
        this.mInfo = mInfo;

        // My Address
        try {
            fromAddress = parseAddress(UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_ADDRESS));
        }
        catch(AddressException ex) {
            ok_to_send = false;
            System.out.println("My e-mail address is bad: " + ex);
            throw new InvalidMessageException("Bad From Address", "Message error: Bad From Address");
        }

        // To Address(es)
        toAddresses = parseToAddresses(mInfo, invalidAddressStrings);
        if(toAddresses == null) ok_to_send = false;

        // Make Sent MessageInfo
        sentmInfo = createSentMessageInfo();
    }


    /**
    * Make a target MessageInfo based on our source MessageInfo
    * This is what the recipient will store at their end
    * Over-ride this if you need to set special stuff
    */
    protected MessageInfo createSentMessageInfo() {
        // Make a copy
        MessageInfo sentmInfo = mInfo.copy(false);

        // Now set some stuff
        sentmInfo.setState(MessageInfo.RCVD);
        sentmInfo.setDateSent(Utils.getNow());
        sentmInfo.setSeenFlag(false);
        sentmInfo.setFrom(fromAddress.getAddress());
        sentmInfo.setTo("ME");

        // BACKWARD COMPAT.
        sentmInfo.setFromFlag(false);
        // BACKWARD COMPAT.

        // Blank out some stuff
        sentmInfo.setPersonID("");     // Don't need this anymore
        sentmInfo.setFileName("");     // Don't need this anymore

        return sentmInfo;
    }

    /**
    * This should be called once the message has been sent
    * It is called from OutBox
    * We analyse what the valid and invalid addresses are and set stuff on the MessageInfo class
    * It is here that we can update message status etc.
    */
    public MessageInfo updateMessageInfo() {
        // Was the message OK to send?
        // If not, caller should then look at getInvalidAddresses()
        // Did we not manage to send the message to anyone?
        if(ok_to_send == false || validSentAddresses.isEmpty()) return mInfo;

        // OK
        mInfo.setState(MessageInfo.SENT);
        mInfo.setDateSent(Utils.getNow());

        return mInfo;
    }

    /**
    * These are the addresses that are valid/invalid
    * They can be added to on successful/failed message sending
    * This is called from MessageManager.send()
    */
    public void addSentAddresses(Address[] invalid, Address[] validUnsent, Address[] validSent) {
        if(invalid != null) {
            for(int i = 0; i < invalid.length; i++) {
                String a = invalid[i].toString();
                if(!invalidAddresses.contains(a)) invalidAddresses.addElement(a);
            }
        }

        if(validUnsent != null) {
            for(int i = 0; i < validUnsent.length; i++) {
                String a = validUnsent[i].toString();
                if(!validUnsentAddresses.contains(a)) validUnsentAddresses.addElement(a);
            }
        }

        if(validSent != null) {
            for(int i = 0; i < validSent.length; i++) {
                String a = validSent[i].toString();
                if(!validSentAddresses.contains(a)) validSentAddresses.addElement(a);
            }
        }
    }

    public Vector getInvalidAddresses() { return invalidAddresses; }
    public Vector getValidUnsentAddresses() { return validUnsentAddresses; }
    public Vector getValidSentAddresses() { return validSentAddresses; }

    public Vector getAllInvalidAddresses() {
        Vector v = new Vector();

        for(int i = 0; i < invalidAddresses.size(); i++) {
            v.addElement(invalidAddresses.elementAt(i).toString());
        }

        for(int i = 0; i < invalidAddressStrings.size(); i++) {
            v.addElement(invalidAddressStrings.elementAt(i).toString());
        }

        return v;
    }


    /**
    * Create a Zip file containing the sent item
    * messageID is the unique javamail message id assigned by MessageManager
    */
    public File createZipFile(String messageID) throws ColloquiaFileException {
        // Add MessageID
        mInfo.setMessageID(messageID);
        sentmInfo.setMessageID(messageID);

        // Create a fileName based on the current time
        String fileName = String.valueOf(System.currentTimeMillis()) + ".cqm";
        File zipFile = new File(DataFiler.getTempFolder(true) + fileName);

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(zipFile));
            ZipOutputStream zOut = new ZipOutputStream(out);

            // Add content
            addContentToZip(zOut);

            // Add MessageInfo File (last! because addAttachmentsToZip() will modify it)
            StringBuffer sb = new StringBuffer();
            XMLStringWriter writer = new XMLStringWriter(sb);
            sentmInfo.write2XML(writer);
            Utils.addStringToZip(sb.toString(), "__messageinfo__", zOut);

            zOut.flush();
            zOut.close();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("MessageOut: Could not create zip file", ex.getMessage());
        }

        return zipFile;
    }

    protected abstract void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException;


    // =========================================================================
    // ============================= HELPERS ===================================
    // =========================================================================

    /**
    * Adds any Attachments to the Zip file
    */
    protected void addAttachmentsToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        Vector attachments = mInfo.getAttachments();
        for(int i = 0; i < attachments.size(); i++) {
            String fileName = (String)attachments.elementAt(i);
            File file = new File(fileName);
            if(!file.exists()) continue;
            // Add fileName without path
            addFileToZip(fileName, file.getName(), zOut);
            sentmInfo.addAttachment(file.getName());
        }
    }


    protected boolean addFileToZip(String fileName, String entryName, ZipOutputStream zOut)
    throws ColloquiaFileException {
        // Already in
        if(zipEntries.contains(entryName.toLowerCase())) return false;

        if(Utils.addFileToZip(fileName, entryName, zOut)) {
	        zipEntries.addElement(entryName.toLowerCase());
    	    return true;
        }
        else return false;
	}

    /**
    * Returns true if this message is good to send
    */
    public boolean isOKtoSend() {
        return ok_to_send;
    }

    public String getSubject() {
        return sentmInfo.getSubject();
    }

    public InternetAddress getFromAddress() {
        return fromAddress;
    }

    public InternetAddress[] getToAddresses() {
        InternetAddress[] a = new InternetAddress[toAddresses.size()];
        toAddresses.copyInto(a);
        return a;
    }

    public void removeToAddress(String address) {
    	toAddresses.removeElement(address);
    }

    public Vector getInvalidAddressStrings() {
        return invalidAddressStrings;
    }

    /**
    * Check the MessageInfo class and get the To Address(es)
    * Put invalid ones in invalidAddressStrings
    */
    protected abstract Vector parseToAddresses(MessageInfo mInfo, Vector invalidAddressStrings);

    /**
    * Parses an address, returns InternetAddress
    */
    protected InternetAddress parseAddress(String email) throws AddressException {
        if(email == null) throw new AddressException("Null email address");
        if(email.indexOf("@") == -1) throw new AddressException("Malformed email address");
        return new InternetAddress(email);
    }
}
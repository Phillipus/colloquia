package net.colloquia.comms;

import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;

/**
* Information about a message and the object it links to
* Can also be used as a template to convey information and matching
*/
public class MessageInfo
implements XML
{
    // Message Types
    public static final int SINGLE_MESSAGE = 1;
    public static final int GROUP_MESSAGE = 2;
    public static final int ACTIVITY = 3;
    public static final int ASSIGNMENT_MESSAGE = 4;
    public static final int ASSIGNMENT = 5;
    public static final int PERSON = 6;
    public static final int RESOURCE = 7;
    public static final int ACTIVITY_INVITE = 8;
    public static final int ACTIVITY_ACCEPT = 9;
    public static final int ACTIVITY_COMPLETE = 10;
    public static final int ACTIVITY_REACTIVATE = 11;

    // Message State
    public static final int DRAFT = 0;
    public static final int PENDING = 1;
    public static final int SENT = 2;
    public static final int RCVD = 3;
    public static final int UNMATCHED = 4;
    public static final int RESEND = 5;

    // Keys to properties
    public static final String MESSAGE_TYPE = "type";
    public static final String SUBJECT = "subject";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String FILENAME = "filename";
    public static final String ACTIVITY_ID = "activity_id";
    public static final String ACTIVITY_NAME = "activity_name";
    public static final String PERSON_ID = "person_id";
    public static final String COMPONENT_ID = "component_id";
    public static final String ASSIGNMENT_GRADE = "assignment_grade";
    public static final String STATE = "state";
    public static final String DATE_SENT = "date_sent";
    public static final String DATE_RCVD = "date_rcvd";
    public static final String SEEN_FLAG = "seen_flag";
    public static final String ATTACHMENT = "attachment";
    public static final String RECIPIENT = "recipient";
    public static final String MESSAGE_ID = "message_id";
    public static final String ACCEPTED = "accepted";
    public static final String SEND_SUBS = "send_subs";


    private Hashtable properties = new Hashtable();

    public Hashtable getProperties() { return properties; }
    public void setProperties(Hashtable set) { properties = set; }

    public void putProperty(String key, String value) {
        if(key == null || value == null) return;
        if(key.equals("")) return;
        key = key.toLowerCase();

        if(key.equals(ATTACHMENT)) addAttachment(value); // Special case
        else if(key.equals(RECIPIENT))  addRecipient(value);  // Special case
        else properties.put(key, value);
    }

    public String getProperty(String key) {
        String value = (String)properties.get(key.toLowerCase());
        return value == null ? "" : value;
    }

    public void removeProperty(String key) {
        if(key != null) properties.remove(key.toLowerCase());
    }

    /**
    * Constructor for default values
    */
    public MessageInfo() {
        this(0);
    }

    public MessageInfo(int type) {
        setMessageType(type);
        setFrom("ME");
        setState(DRAFT);
        setSeenFlag(true);
        setFromFlag(true);   // Me
    }

    // =========================== ATTACHMENTS ==============================
    // Attachments are set differently to the String-String key-value property
    // They are set in their own Vector

    private Vector attachments = new Vector();

    public void addAttachment(String fileName) {
        if(!attachments.contains(fileName)) attachments.addElement(fileName);
    }

    public void removeAttachment(String fileName) {
        attachments.removeElement(fileName);
    }

    public Vector getAttachments() {
        return attachments;
    }

    public void setAttachments(Vector set) {
        attachments = set;
    }

    public boolean hasAttachment() {
        return attachments.size() > 0;
    }

    // =========================== RECIPIENTS ==============================
    // Recipients are set differently to the String-String key-value property
    // They are set in their own Vector

    private Vector recipients = new Vector();

    public void addRecipient(String email) {
        if(!recipients.contains(email)) recipients.addElement(email);
    }

    public void removeRecipient(String email) {
        recipients.removeElement(email);
    }

    public Vector getRecipients() {
        return recipients;
    }

    public void setRecipients(Vector set) {
        recipients = set;
    }

    //======================== Convenience methods ===========================

    // The Type of Message
    public void setMessageType(int type) {
        putProperty(MESSAGE_TYPE, String.valueOf(type));
    }

    public int getMessageType() {
        String type = getProperty(MESSAGE_TYPE);
        return Integer.parseInt(type);
    }

    // The MessagID
    public void setMessageID(String ID) {
        putProperty(MESSAGE_ID, ID);
    }

    public String getMessageID() {
        return getProperty(MESSAGE_ID);
    }

    // The Subject of the Message
    public void setSubject(String subject) {
        putProperty(SUBJECT, subject);
    }

    public String getSubject() {
        return getProperty(SUBJECT);
    }

    // From
    public void setFrom(String from) {
        putProperty(FROM, from);
    }

    public String getFrom() {
        return getProperty(FROM);
    }

    // To
    public void setTo(String to) {
        putProperty(TO, to);
    }

    public String getTo() {
        return getProperty(TO);
    }

    // The fileName of the Message (the corresponding HTML File or zipfile)
    public void setFileName(String fileName) {
        putProperty(FILENAME, fileName);
    }

    public String getFileName() {
        return getProperty(FILENAME);
    }

    // The ACTIVITY it relates to
    public void setActivityID(String ID) {
        putProperty(ACTIVITY_ID, ID);
    }

    public String getActivityID() {
        return getProperty(ACTIVITY_ID);
    }

    public void setActivityName(String name) {
        putProperty(ACTIVITY_NAME, name);
    }

    public String getActivityName() {
        return getProperty(ACTIVITY_NAME);
    }

    public void setComponentID(String ID) {
        putProperty(COMPONENT_ID, ID);
    }

    public String getComponentID() {
        return getProperty(COMPONENT_ID);
    }

    // The Person it relates to
    public void setPersonID(String personID) {
        putProperty(PERSON_ID, personID);
    }

    public String getPersonID() {
        return getProperty(PERSON_ID);
    }

    public void setAssignmentGrade(String grade) {
        putProperty(ASSIGNMENT_GRADE, grade);
    }

    public String getAssignmentGrade() {
        return getProperty(ASSIGNMENT_GRADE);
    }

    // Whether the message is Draft, Pending or sent
    public void setState(int state) {
        putProperty(STATE, String.valueOf(state));
    }

    public int getState() {
        String state = getProperty(STATE);
        return Integer.parseInt(state);
    }

    public void setDateRcvd(Date date) {
        putProperty(DATE_RCVD, String.valueOf(date.getTime()));
    }

    public Date getDateRcvd() {
        String val = getProperty(DATE_RCVD);
        if(val.equals("")) return null;
        else return new Date(Long.parseLong(val));
    }

    public void setDateSent(Date date) {
        putProperty(DATE_SENT, String.valueOf(date.getTime()));
    }

    public Date getDateSent() {
        String val = getProperty(DATE_SENT);
        if(val.equals("")) return null;
        else return new Date(Long.parseLong(val));
    }

    // Whether the message has been read
    public void setSeenFlag(boolean seen) {
        putProperty(SEEN_FLAG, seen ? "true" : "false");
    }

    public boolean isSeen() {
        String val = getProperty(SEEN_FLAG);
        return val.toLowerCase().equals("true");
    }

    //==============================================================
    /*
    * This in for backward compat.  We don't really need a From flag
    * Once everybody is on version 1.2.2 we can remove this
    */
    // Whether the message is from me (true) or them (false)

    public static final String FROM_FLAG = "from_flag";

    public void setFromFlag(boolean who) {
        putProperty(FROM_FLAG, who ? "true" : "false");
    }
    //==============================================================

    public boolean isFromMe() {
        return getFrom().toLowerCase().equals("me");
    }

    public void setAccepted(boolean set) {
        putProperty(ACCEPTED, set ? "true" : "false");
    }

    public boolean isAccepted() {
        String val = getProperty(ACCEPTED);
        return val.toLowerCase().equals("true");
    }

    public void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);

        // Properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            writer.write(new XMLTag(tag, value));
        }

        // Attachments
        for(int j = 0; j < attachments.size(); j++) {
            String file = (String)attachments.elementAt(j);
            writer.write(new XMLTag(ATTACHMENT, file));
        }

        // Recipients
        for(int j = 0; j < recipients.size(); j++) {
            String email = (String)recipients.elementAt(j);
            writer.write(new XMLTag(RECIPIENT, email));
        }

        writer.writeln(XMLEndTag);
    }

    public void write2Element(Element element) {

	}

    public void unMarshallXML(XMLReader reader) throws XMLReadException {
        String line;
        XMLTag xmlTag;
        while((line = reader.readLine()) != null) {
            xmlTag = XMLTag.getXMLTag(line);
            if(xmlTag != null) putProperty(xmlTag.tag, xmlTag.value);
        }
    }

    public static String XMLStartTag = "<message_info>";
    public static String XMLEndTag = "</message_info>";

    /**
    * Return a clone copy of this - this is used to send to the Recipient
    * Or as a Resend in which case we need the Attachments
    * We don't clone the Recipients
    */
    public MessageInfo copy(boolean includeAttachments) {
        MessageInfo mInfo = new MessageInfo();
        mInfo.setProperties((Hashtable)getProperties().clone());
        if(includeAttachments) mInfo.setAttachments(getAttachments());
        return mInfo;
    }

    /*
    * Return an Icon to display for this type of Message
    */
    public ImageIcon getIcon() {
        return getIcon(getMessageType());
    }

    public static ImageIcon getIcon(int messageType) {
        ImageIcon icon;

        switch(messageType) {
            case SINGLE_MESSAGE:
                icon = Utils.getIcon(ColloquiaConstants.iconSingleMessage);
                break;
            case GROUP_MESSAGE:
                icon = Utils.getIcon(ColloquiaConstants.iconGroupMessage);
                break;
            case ACTIVITY_REACTIVATE:
            case ACTIVITY:
                icon = Utils.getIcon(ColloquiaConstants.iconActivityLiveFolder);
                break;
            case ACTIVITY_INVITE:
                icon = Utils.getIcon(ColloquiaConstants.iconActivityFuture);
                break;
            case ACTIVITY_ACCEPT:
                icon = Utils.getIcon(ColloquiaConstants.iconActivityFuture);
                break;
            case ACTIVITY_COMPLETE:
                icon = Utils.getIcon(ColloquiaConstants.iconActivityCompletedFolder);
                break;
            case ASSIGNMENT_MESSAGE:
                icon = Utils.getIcon(ColloquiaConstants.iconAssignment);
                break;
            case PERSON:
                icon = Utils.getIcon(ColloquiaConstants.iconPerson);
                break;
            case RESOURCE:
                icon = Utils.getIcon(ColloquiaConstants.iconResource);
                break;
            default:
                icon = Utils.getIcon(ColloquiaConstants.iconMessage);
                break;
        }

        return icon;
    }

    /**
    * Returns which folder this message should reside in
    */
    public String getFolderForMessage(boolean checkFolder) {
        String folder;

        // Which directory?
        switch(getMessageType()) {
            case ASSIGNMENT_MESSAGE:
                folder = DataFiler.getAssignmentMailFolder(getActivityID(), checkFolder);
                break;
            case SINGLE_MESSAGE:
            case GROUP_MESSAGE:
                folder = DataFiler.getActivityMailFolder(getActivityID(), checkFolder);
                break;
            case ACTIVITY:   // This must be the outbox
                folder = DataFiler.getMailFolder(checkFolder);
                break;
            default:
                folder = DataFiler.getMailFolder(checkFolder);
        }

        return folder;
    }

    /**
    * Get the full message fileName referenced in a message
    */
    public String getFullFileName() {
        return getFolderForMessage(false) + getFileName();
    }

    /**
    * Returns true if fileName, ActivityID, MessageType and Rcvd Date match.
    * Thus the two messages point to the same thing
    * Single Message matches on html fileName - ActivityID - MessageType
    * Group Message matches on html fileName - ActivityID - MessageType
    * Assignment Message matches on html fileName - ActivityID - MessageType
    * Activity (and other components) matches on  ActivityID - MessageType
    */
    public boolean matches(MessageInfo mInfo) {
        String fileName1 = mInfo.getFileName();
        String fileName2 = getFileName();
        String ID1 = mInfo.getActivityID();
        String ID2 = getActivityID();
        int type1 =  mInfo.getMessageType();
        int type2 =  getMessageType();
        Date date1 = mInfo.getDateRcvd();
        Date date2 = getDateRcvd();

        boolean match = fileName1.equals(fileName2);
        match = match && ID1.equals(ID2);
        match = match && type1 == type2;

        // Recvd date will be null if not rcvd message
        if(date1 != null && date2 != null) match = match && date1.equals(date2);

        return match;
    }
}
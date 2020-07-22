package net.colloquia.comms.messages;

import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.colloquia.comms.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
* A File class representing a message that has been received via e-mail
*/
public abstract class MessageIn {
    protected File zipFile;
    protected MessageInfo mInfo;

    /**
    * Factory method for returning the correct type of MessageIn
    * Depending on the contents of Message - called from MessageManager
    * Throws MailFailureException if nasty
    */
    public static MessageIn getMessageIn(Message message, PMonitor monitor) throws MailFailureException {
        if(message == null) throw new MailFailureException("Null Message exception", "getMessageIn");
      	File _zipFile = saveMimeMessage(message, DataFiler.getTempFolder(true), monitor);
        return getMessageIn(_zipFile);
    }

    /**
    * Factory method for returning the correct type of MessageIn
    * Depending on the contents of File (MessageInfo in fact)
    * This is for an existing zip File - called from Inbox and PendingBox
    */
    public static MessageIn getMessageIn(File zipFile) {
        if(zipFile == null || !zipFile.exists()) {
            System.out.println("MessageIn.getMessageIn() - zipFile is null");
            return null;
        }

        MessageInfo mInfo;

        try {
            mInfo = extractMessageInfo(zipFile);
        }
        catch(ColloquiaFileException ex) {
            ErrorHandler.showWarning("ERR11", ex, "ERR");
            return null;
        }

        if(mInfo == null) return null;

        // Set time stamp to NOW if it hasn't been done
        if(mInfo.getDateRcvd() == null) mInfo.setDateRcvd(Utils.getNow());

        switch(mInfo.getMessageType()) {
            case MessageInfo.SINGLE_MESSAGE:
                return new SingleMessageIn(mInfo, zipFile);

            case MessageInfo.GROUP_MESSAGE:
                return new GroupMessageIn(mInfo, zipFile);

            case MessageInfo.ASSIGNMENT_MESSAGE:
                return new AssignmentMessageIn(mInfo, zipFile);

            case MessageInfo.ACTIVITY:
                return new ActivityMessageIn(mInfo, zipFile);

            case MessageInfo.ACTIVITY_INVITE:
                return new ActivityInviteMessageIn(mInfo, zipFile);

            case MessageInfo.ACTIVITY_ACCEPT:
            	return new AcceptActivityMessageIn(mInfo, zipFile);

            case MessageInfo.ACTIVITY_COMPLETE:
            	return new CompleteActivityMessageIn(mInfo, zipFile);

            case MessageInfo.ACTIVITY_REACTIVATE:
            	return new ReactivateActivityMessageIn(mInfo, zipFile);

            case MessageInfo.PERSON:
                return new PersonMessageIn(mInfo, zipFile);

            case MessageInfo.RESOURCE:
                return new ResourceMessageIn(mInfo, zipFile);

            default:
                return null;
        }
    }


    /**
    * Default Constructor for siblings
    */
    protected MessageIn(MessageInfo mInfo, File zipFile) {
        this.mInfo = mInfo;
        this.zipFile = zipFile;
    }

    /**
    * Do your thing and try to file the message, possibly deleting the file afterwards
    * Return the updates MessageInfo
    */
    public abstract MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException;

    protected void deleteZipFile() {
        if(zipFile != null) zipFile.delete();
    }

    /**
    * Copy the zip file to the Pending folder if not matched
    * Delete original file if set - however, original file might be target file
    * so don't delete it
    * Set mInfo to UNMATCHED
    */
    protected void addToPending(boolean deleteFile) {
        mInfo.setState(MessageInfo.UNMATCHED);

        // Copy zip to Pending if it's not already there
        if(zipFile != null) {
        	File file = new File(DataFiler.getPendingMailFolder(true) + zipFile.getName());
            // Don't add again
            if(!zipFile.equals(file)) {
                try {
        	        DataFiler.copyFile(zipFile, file);
                    if(deleteFile) deleteZipFile();
                    mInfo.setFileName(file.getName());
                }
                catch(IOException ex) {
                    ErrorHandler.showWarning("ERR7", ex, "ERR");
                }
            }
        }
    }

    /**
    * Gets the MessageInfo out of the Zip file
    * Returns null if not found or weirdness happens
    */
    private static MessageInfo extractMessageInfo(File zipFile) throws ColloquiaFileException {
        if(zipFile == null || !zipFile.exists()) return null;

        String infoName = "__messageinfo__";
        MessageInfo mInfo = null;

        try {
            // Extract it
            String xmlString = Utils.extractZipEntry(zipFile, infoName);
            // Got it?
            if(xmlString == null) return null;

            // Now we have to read it in again, line by line
            XMLStringReader reader = new XMLStringReader(xmlString);
            reader.open();
            mInfo = new MessageInfo();
            mInfo.unMarshallXML(reader);
            reader.close();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not extract MessageInfo", ex.getMessage());
        }

        return mInfo;
    }


    /**
    * Gets the Zip file out of the Message and saves it to Temp folder
    * Throws up if shit happens
    */
    private static File saveMimeMessage(Message message, String destFolder, PMonitor monitor) throws MailFailureException {
        Object content;
        int numBodyParts;
        BodyPart bodypart;
        String fileName;
        Object input;
        boolean aborted = false;

        // Get the Content
        try {
        	content = message.getContent();
        }
        catch(Exception ex) {
            throw new MailFailureException("Could not get mail content: " + ex, "saveMimeMessage");
        }

        // The content SHOULD be a MimeMultiPart
        if(!(content instanceof MimeMultipart)) throw new MailFailureException("Could not cast mail content", "MimeMultipart");
        // Cast it
        MimeMultipart mp = (MimeMultipart)content;

        // Get number of body parts - there should be two
        try {
        	numBodyParts = mp.getCount();
        }
        catch(MessagingException mex) {
        	throw new MailFailureException("Could not get mail bodyparts: " + mex, "mp.getCount()");
        }

        // Got to have two parts!
        if(numBodyParts != 2) throw new MailFailureException("Wrong number of bodyparts", "numBodyParts");

        // First body part (0) is the Helper String message - ignore it
        // The second body part is the Zip file (we hope!)
        try {
        	bodypart = mp.getBodyPart(1);
        }
        catch(MessagingException mex) {
        	throw new MailFailureException("Could not get bodypart: " + mex, "mp.getBodyPart(1)");
        }

        // BodyPart SHOULD be MimeBodyPart
        if(!(bodypart instanceof MimeBodyPart)) throw new MailFailureException("Not MimeBodyPart", "MimeBodyPart");

        // Cast it
        MimeBodyPart mbp = (MimeBodyPart)bodypart;

        // Make a file for the zip file
        try {
        	fileName =  mbp.getFileName();
        }
        catch(MessagingException mex) {
        	throw new MailFailureException("Could not get fileName: " + mex, "");
        }

        File file = new File(destFolder + fileName);

        // Get content of MimeBodypart
        try {
        	input = mbp.getContent();
        }
        catch(Exception ex) {
            throw new MailFailureException("Could not get message input: " + ex, "mbp.getContent()");
        }

        // Get size of MimeBodypart
        int sizeMessage;
        try {
        	sizeMessage = mbp.getSize();
        }
        catch(Exception ex) {
        	sizeMessage = 100;
        }

        // Got to be an InputStream
        if(!(input instanceof InputStream)) throw new MailFailureException("Not InputStream", "InputStream");

        InputStream is = (InputStream)input;
        final int bufSize = 2048;
        byte buf[] = new byte[bufSize];

        int bytesRead;
        int totalBytesRead = 0;

        try {
            BufferedInputStream in = new BufferedInputStream(is, bufSize);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            //sizeMessage = is.available();  // Doesn't work anymore!
            monitor.init(sizeMessage);
            //System.out.println("Size is: " + sizeMessage);

            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                totalBytesRead += bytesRead;
	            monitor.incProgress(bytesRead, false);
                if(monitor.isCanceled()) {
                    aborted = true;
                    break;
                }
            }

            out.flush();
            out.close();
        }
        catch(Exception ex) {
            if(file != null) file.delete();
            throw new MailFailureException("Could not save message file: " + ex, "");
        }

        //System.out.println("Bytes read: " + totalBytesRead);
        monitor.incProgress(sizeMessage - totalBytesRead, false);

        if(aborted) {
            // Delete half-downloaded file
            if(file != null) file.delete();
            // Make file null so that returned value won't give error
            file = null;
        }

        return file;
    }
}

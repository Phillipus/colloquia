package net.colloquia.comms;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.mail.internet.*;

import net.colloquia.*;
import net.colloquia.comms.messages.*;
import net.colloquia.comms.tables.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;

public final class MessageManager {
    private static boolean debug = false;
    private static String store_protocol;
    private static String userName;
    private static String inServer;
    private static String passwordIn;
    private static String passwordOut;
    private static String outServer;
    private static String serverFolder;

    /**
     * Whether to use Authenticated SMTP
     */
    private static boolean useAuthSMTP;

    private static SendListener sendListener;

    // This is the message that a recipient will see if they get this message in
    // Their regular e-mail client
    private final static String infoMessage =
                LanguageManager.getString("16_28") + "\n\n" +
                LanguageManager.getString("16_29") + "\n\n" +
                LanguageManager.getString("16_30") + " " + ColloquiaConstants.WEB_PAGE;

    // Progress monitor
    private static PMonitor monitor;

    // =====================================================================
    // ======================== INITIALISE THE SESSION======================
    // =====================================================================

    public static Session getIncomingSession() {
        StatusWindow.printTrace(LanguageManager.getString("16_34"));

        UserPrefs prefs = UserPrefs.getUserPrefs();
        store_protocol = prefs.getProperty(UserPrefs.EMAIL_PROTOCOL);
        userName = prefs.getProperty(UserPrefs.EMAIL_IN_USER_NAME);
        inServer = prefs.getProperty(UserPrefs.EMAIL_IN_SERVER);
        serverFolder = prefs.getProperty(UserPrefs.EMAIL_FOLDER);

        StatusWindow.printTrace(LanguageManager.getString("16_35") + " " + store_protocol);
        StatusWindow.printTrace(LanguageManager.getString("16_36") + " " + userName);
        StatusWindow.printTrace(LanguageManager.getString("16_37") + " " + inServer);
        StatusWindow.printTrace(LanguageManager.getString("16_38") + " " + serverFolder);

        // Create some properties
        Properties props = System.getProperties();
        if(debug) props.put("mail.debug", "true");

        // Create a session
        StatusWindow.printTrace(LanguageManager.getString("16_40"));
        Session session = Session.getInstance(props, null);
        session.setDebug(debug);
        StatusWindow.printTrace(LanguageManager.getString("16_41"));
        return session;
    }

    public static Session getOutgoingSession() {
        StatusWindow.printTrace(LanguageManager.getString("16_34"));

        UserPrefs prefs = UserPrefs.getUserPrefs();
        userName = prefs.getProperty(UserPrefs.EMAIL_IN_USER_NAME);
        outServer = prefs.getProperty(UserPrefs.EMAIL_ACTIVE_OUT_SERVER);

        StatusWindow.printTrace(LanguageManager.getString("16_36") + " " + userName);
        StatusWindow.printTrace(LanguageManager.getString("16_39") + " " + outServer);

        // Create some properties
        Properties props = System.getProperties();
        props.put("mail.smtp.sendpartial", "true");  // Send messages if some of the addresses are invalid
        //props.put("mail.host", outServer);
        //props.put("mail.smtp.host", outServer);

        useAuthSMTP = prefs.getBooleanProperty(UserPrefs.EMAIL_AUTHSMTP);
        props.put("mail.smtp.auth", useAuthSMTP ? "true" : "false");

        if(debug) props.put("mail.debug", "true");

        //SMTPAuthenticator m = new SMTPAuthenticator();

        // Create a session
        StatusWindow.printTrace(LanguageManager.getString("16_40"));
        Session session = Session.getInstance(props, null);
        session.setDebug(debug);
        StatusWindow.printTrace(LanguageManager.getString("16_41"));
        return session;
    }


    private static class SMTPAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication(){
            return new PasswordAuthentication("username", "password");
        }
    }


    // =====================================================================
    // ======================== GET MESSAGES ===============================
    // =====================================================================

    /**
    * Gets any new  messages
    * @return a Vector of MessageFile classes of the zip files
    * or null if no messages
    * It is up to the caller to close the session to purge deleted messages
    */
    public static void getMessages(Frame owner) throws MailFailureException {
        Message[] allMessages;
        String msgStatus;
        Folder folder = null;
        Store store;

        // Return null if no password
        passwordIn = getInPassword(owner);
        if(passwordIn == null) return;

        // New Progress Monitor
        monitor = new PMonitor(owner,
            LanguageManager.getString("16_31"),
            LanguageManager.getString("16_32"),
            " ");

        // Initialise
        Session session = getIncomingSession();

        // Get a Store object "imap" or "pop3"
        try {
            StatusWindow.printTrace(LanguageManager.getString("16_42"));
            store = session.getStore(store_protocol);
            StatusWindow.printTrace(LanguageManager.getString("16_43"));
        } catch(NoSuchProviderException ex) {
            passwordIn = null;
            monitor.close();
            throw new MailFailureException(LanguageManager.getString("16_44"),
                    LanguageManager.getString("16_45") + " " + ex);
        }

        // Connect to Store
        try {
            monitor.setMessage(LanguageManager.getString("16_33"));
            connectStore(store);
        }
        catch(MessagingException ex) {
            monitor.close();
            passwordIn = null;
            throw new MailFailureException(LanguageManager.getString("16_46"),
                LanguageManager.getString("16_47") + " " + ex);
        }


        // Not connected
        if(!store.isConnected() || monitor.isCanceled()) {
            passwordIn = null;
            monitor.close();
            throw new MailFailureException(LanguageManager.getString("16_48"),
                LanguageManager.getString("16_48"));
        }

        // Open the server Folder
        try {
            msgStatus = LanguageManager.getString("16_49"); StatusWindow.printTrace(msgStatus);
            monitor.setMessage(msgStatus);

            folder = store.getFolder(serverFolder);

            // Check validity of folder
            if(folder == null || !folder.exists()) {
                passwordIn = null;
                monitor.close();
                throw new MailFailureException(LanguageManager.getString("16_50"),
                    LanguageManager.getString("16_51"));
            }

            StatusWindow.printTrace(LanguageManager.getString("16_52") + " " + folder);
            StatusWindow.printTrace(LanguageManager.getString("16_53"));

            if(folder.isOpen()) folder.close(false);

            /* Try to open the folder in read-write mode.  If we fail, tell the
            user and open in Read-only mode.  We won't be able to purge messages
            though */

            try {
            	folder.open(Folder.READ_WRITE);
            }
            catch(ReadOnlyFolderException rex) {
            	ErrorHandler.showWarning("16_96", rex, "ERR");
                folder.open(Folder.READ_ONLY);
            }

            StatusWindow.printTrace(LanguageManager.getString("16_54"));
        }
        catch(MessagingException ex) {
            passwordIn = null;
            monitor.close();
            closeSession(folder, false);
            throw new MailFailureException(LanguageManager.getString("16_55"),
                LanguageManager.getString("16_51") + ": " + ex);
        }

        // Count all messages in this folder
        msgStatus = LanguageManager.getString("16_56");
        StatusWindow.printTrace(msgStatus);
        monitor.setMessage(msgStatus);

        int totalMessages = countTotalMessages(folder);

        // If no messages, return
        if(totalMessages == 0  || monitor.isCanceled()) {
            StatusWindow.printTrace(LanguageManager.getString("16_57"));
            monitor.close();
            closeSession(folder, false);
            return;
        }
        else {
            StatusWindow.printTrace(LanguageManager.getString("16_58") + " " +
                totalMessages + " " + LanguageManager.getString("16_15"));
        }

        // Get the messages as a bunch
        try {
            msgStatus = LanguageManager.getString("16_32");
            StatusWindow.printTrace(msgStatus);
            monitor.setMessage(msgStatus);

            allMessages = folder.getMessages();
            StatusWindow.printTrace(allMessages.length + " " + LanguageManager.getString("16_59"));
        }
        catch(MessagingException ex) {
            passwordIn = null;
            monitor.close();
            closeSession(folder, false);
            throw new MailFailureException(LanguageManager.getString("16_60"),
                LanguageManager.getString("16_61") + " " + ex);
        }

        // Go thru the messages and get our Messages
        Vector colloquiaMessages = new Vector();

        // Get them adding them to our Vector
        for(int i = 0; i < allMessages.length; i++) {
            if(isColloquiaMessage(allMessages[i])) colloquiaMessages.addElement(allMessages[i]);
        }

        // If no Colloquia Messages return
        if(colloquiaMessages.size() == 0 || monitor.isCanceled()) {
            monitor.close();
			closeSession(folder, false);
            return;
        }

        // Otherwise, get them
        StatusWindow.printTrace(LanguageManager.getString("16_58") + " " +
            colloquiaMessages.size() + " " + LanguageManager.getString("16_15"));

        // Whether to delete off server
		boolean purge = UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.EMAIL_LEAVE_ON_SERVER);

        // = GET THE MESSAGES =
        for(int i = 0; i < colloquiaMessages.size(); i++) {
            Message message = (Message)colloquiaMessages.elementAt(i);

            msgStatus = LanguageManager.getString("16_62") + " " + (i+1) + " / " + colloquiaMessages.size();
            StatusWindow.printTrace(msgStatus);
            monitor.setNote(msgStatus);
            if(monitor.isCanceled()) break;

            try {
                // Save Message to Disk via MessageIn class
                MessageIn messageIn = MessageIn.getMessageIn(message, monitor);
                // If saved
                if(messageIn != null) {
                    // File it to its destination
                    MessageInfo mInfo = messageIn.fileMessage(true);
                    // Now put it in the Inbox
                    Inbox.getInstance().addDeliveredMessage(mInfo);
                    // Mark as deleted
                    if(purge) markMessageDeleted(message);
                }
                else {
                    System.out.println("Message was null in MessageManager");
                }
            }
            catch(Exception ex) {
                System.out.println("Could not get message: " + ex);
            }
        }

        monitor.close();
		closeSession(folder, true);
    }


    // =====================================================================
    // ======================== GET SERVER FOLDERS =========================
    // =====================================================================

    /**
    * Returns a list of folders on server
    * returns null if a problem occurs
    */
    public static Folder[] getFolders(Frame owner) throws MailFailureException {
        Folder[] folders = null;
        Store store = null;
        Folder folder;

        // Return null if no password
        passwordIn = getInPassword(owner);
        if(passwordIn == null) return null;

        // Initialise
        StatusWindow.printTrace(LanguageManager.getString("16_49"));
        Session session = getIncomingSession();

        // Get a Store object "imap" or "pop3"
        try {
            StatusWindow.printTrace(LanguageManager.getString("16_42"));
            store = session.getStore(store_protocol);
            StatusWindow.printTrace(LanguageManager.getString("16_43"));
        }
        catch(NoSuchProviderException ex) {
            throw new MailFailureException(LanguageManager.getString("16_44"),
                LanguageManager.getString("16_45") + " " + ex);
        }

        // Connect to Store
        try {
            connectStore(store);
        }
        catch(MessagingException ex) {
            passwordIn = null;
            throw new MailFailureException(LanguageManager.getString("16_46"),
                LanguageManager.getString("16_47") + " " + ex);
        }

        // Not connected
        if(!store.isConnected()) {
            passwordIn = null;
            throw new MailFailureException(LanguageManager.getString("16_48"),
                LanguageManager.getString("16_48"));
        }

        // Open the default Folder
        try {
            StatusWindow.printTrace(LanguageManager.getString("16_65"));
            folder = store.getDefaultFolder();
            // Check validity of folder
            if(folder == null || !folder.exists()) {
                throw new MailFailureException(LanguageManager.getString("16_66"),
                    LanguageManager.getString("16_51"));
            }
            else StatusWindow.printTrace(LanguageManager.getString("16_52"));
            StatusWindow.printTrace(LanguageManager.getString("16_67"));
            folders = folder.list();
            StatusWindow.printTrace(LanguageManager.getString("16_68"));
	        //System.out.println("sep: " + folder.getSeparator());
        }
        catch(MessagingException ex) {
            throw new MailFailureException(LanguageManager.getString("16_55"),
                LanguageManager.getString("16_51") + ": " + ex);
        }

        closeSession(folder, false);
        return folders;
    }


    // =====================================================================
    // ======================== SEND MESSAGES ===============================
    // =====================================================================

    /**
    * Sends a bunch of Messages - msgs is a Vector of MessageInfo classes
    * Returns a corresponding Vector of MessageOut classes updated with send information
    */
    public static Vector sendMessages(Frame owner, Vector msgs) {
        MessageOut messageOut = null;
        Vector msgOuts = new Vector();
        boolean result = true;
        Transport transport = null;
        String msgStatus;

        if(msgs == null || msgs.isEmpty()) return msgOuts;

        // Initialise some stuff
        Session session = getOutgoingSession();

        // If using Authenticated SMTP ask for pwd
        if(useAuthSMTP) {
            passwordOut = getOutPassword(owner);
            if(passwordOut == null) return msgOuts;
        }

        // Set up Progress Monitor
        monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("16_69"), " ", " ");

        // Try to connect
        try {
            msgStatus = LanguageManager.getString("16_70");
            monitor.setMessage(msgStatus); StatusWindow.printTrace(msgStatus);

            transport = session.getTransport("smtp");

	        // Register our SendListener for ConnectionEvents and TransportEvents
            sendListener = new SendListener();
	        transport.addConnectionListener(sendListener);
	        transport.addTransportListener(sendListener);

            // Use this for an authenticated smtp server
            transport.connect(outServer, userName, passwordOut);
            //else transport.connect();
        }
        catch(Exception ex) {
            if(ex instanceof NoSuchProviderException) {}
            StatusWindow.printTrace(LanguageManager.getString("16_71") + ": " + ex);
            ErrorHandler.showWarning("16_72", ex, "EMAIL");
            closeTransport(transport);
            monitor.close();
            return msgOuts;
        }

        // Init Progress Monitor
        monitor.init(LanguageManager.getString("16_73"), LanguageManager.getString("16_74"), msgs.size() - 1);

        // Create MessageOuts
        for(int i = 0; i < msgs.size(); i++) {
            MessageInfo mInfo = (MessageInfo)msgs.elementAt(i);

            try {
                messageOut = MessageOut.getMessageOut(mInfo);
            }
            catch(InvalidMessageException ex) {
                System.out.println(ex);
                // My address is invalid
                if(ex.getMessage().equals("Bad From Address")) {
                    closeTransport(transport);
                    monitor.close();
                    ErrorHandler.showWarning("16_75", ex, "EMAIL");
                    return msgOuts;
                }
            }

            // OK - so add it to the Vector
            if(messageOut != null) msgOuts.addElement(messageOut);

            // ProgressMonitor
            monitor.incProgress(1, false);
            if(monitor.isCanceled()) {
                closeTransport(transport);
                return msgOuts;
            }
        }


        // ProgressMonitor
        monitor.init(msgs.size() - 1);

        // Send the message(s)
        for(int i = 0; i < msgOuts.size(); i++) {
            msgStatus = LanguageManager.getString("16_76") + " " + (i + 1);
            StatusWindow.printTrace(msgStatus);
            monitor.setNote(msgStatus);

            // Get the the MessageOut
            messageOut = (MessageOut)msgOuts.elementAt(i);

            // Send it!
            if(messageOut.isOKtoSend()) {
                result = sendMessage(messageOut, transport, session);
                if(result == false) ErrorHandler.showWarning("16_71", null, "EMAIL");
            }

            // Update monitor
            monitor.incProgress(1, true);

            if(monitor.isCanceled() || result == false ) {
                closeTransport(transport);
                monitor.close();
                return msgOuts;
            }
        }

        closeTransport(transport);
        monitor.close();
        return msgOuts;
    }

    /**
    * Send a message from a MessageOut class
    * If we return false, a serious exception has been thrown, so we better cancel!
    */
    private static boolean sendMessage(MessageOut messageOut, Transport transport, Session session) {
        if(messageOut == null) return true;
        String messageID;
        File zipFile;

        // Create a mime message
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(messageOut.getFromAddress());
            msg.setRecipients(Message.RecipientType.TO, messageOut.getToAddresses());
            msg.setSubject("COLLOQUIA - " + messageOut.getSubject());
            msg.setSentDate(Utils.getNow());
            // Our unique identifier
            msg.addHeader(ColloquiaConstants.ColloquiaMessageVersion, ColloquiaConstants.msgVersion);

            // Get a unique message ID
            msg.saveChanges();
            String[] header = msg.getHeader("Message-ID");
            if(header != null) messageID = header[0];
            else messageID = "";
        }
        catch(Exception ex) {
             StatusWindow.printTrace(LanguageManager.getString("16_77") + ": " + ex);
             return true;
        }

        // Create the Multipart and attach the Info message and the Zip file
        Multipart multiPart = new MimeMultipart();

        try {
            // Info Message
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(infoMessage, "text/plain");
            multiPart.addBodyPart(mbp);

            // Zip File
            zipFile = messageOut.createZipFile(messageID);
            FileDataSource fds = new FileDataSource(zipFile);
            mbp = new MimeBodyPart();
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(zipFile.getName());
            multiPart.addBodyPart(mbp);

            // Add the Multipart to the message
            msg.setContent(multiPart);

            // IT'S VERY IMPORTANT TO SAVE CHANGES OTHERWISE ANY ATTACHMENTS
            // (THE ZIP FILE) WILL NOT GET SAVED IN THE MESSAGE!!!!!!!
            msg.saveChanges();

            // Restore original MessageID (because saveChanges() makes a new one)
            msg.setHeader("Message-ID", messageID);
        }
        catch(Exception ex) {
             StatusWindow.printTrace(LanguageManager.getString("16_78") + ": " + ex);
             return true;
        }

        // Send the message
        try {
            StatusWindow.printTrace(LanguageManager.getString("16_79"));

            // Set the MessageOut in the SendListener
            sendListener.setMessageOut(messageOut);

            // Use this method so the TransportEvent gets fired
            transport.sendMessage(msg, msg.getAllRecipients());

	        // Give the EventQueue enough time to fire its events
	        try {Thread.sleep(20);} catch(InterruptedException e) {}

            StatusWindow.printTrace(LanguageManager.getString("16_80"));
        }

        // Failed
        catch(MessagingException mex) {
	        // Give the EventQueue enough time to fire its events
            try {Thread.sleep(20);} catch(InterruptedException e) {}

            // Deal with all the possible send failures
            Exception ex = mex;
            do {
                if(ex instanceof SendFailedException) {
                    SendFailedException sfex = (SendFailedException)ex;
                    Address[] invalid = sfex.getInvalidAddresses();
                    Address[] validUnsent = sfex.getValidUnsentAddresses();
                    Address[] validSent = sfex.getValidSentAddresses();
                    messageOut.addSentAddresses(invalid, validUnsent, validSent);
                    return true;
                }

                // Serious!
                if(ex instanceof AuthenticationFailedException) {
                    StatusWindow.printTrace(LanguageManager.getString("16_81") + ": " + ex);
                    return false;
                }
    	    } while((ex =((MessagingException)ex).getNextException()) != null);
            zipFile.delete();
        }

        zipFile.delete();
        return true;
    }

    /**
    * This class will listen to send connections and Transport events
    */
    private static class SendListener implements ConnectionListener, TransportListener {
        MessageOut messageOut;

        public void setMessageOut(MessageOut messageOut) {
            this.messageOut = messageOut;
        }


        // Connection events
        public void opened(ConnectionEvent e) {
            StatusWindow.printTrace(LanguageManager.getString("16_82"));
        }

        public void disconnected(ConnectionEvent e) {
            StatusWindow.printTrace(LanguageManager.getString("16_83"));
        }

        public void closed(ConnectionEvent e) {
        }


        // Transport events
        public void messageDelivered(TransportEvent e) {
            Address[] invalid = e.getInvalidAddresses();
            Address[] validUnsent = e.getValidUnsentAddresses();
            Address[] validSent = e.getValidSentAddresses();
            messageOut.addSentAddresses(invalid, validUnsent, validSent);
            if(monitor != null) monitor.setNote(LanguageManager.getString("16_84"));
            StatusWindow.printTrace(LanguageManager.getString("16_84"));
        }

        /**
        * Message was not delivered
        */
        public void messageNotDelivered(TransportEvent e) {
            Address[] invalid = e.getInvalidAddresses();
            Address[] validUnsent = e.getValidUnsentAddresses();
            Address[] validSent = e.getValidSentAddresses();
            messageOut.addSentAddresses(invalid, validUnsent, validSent);
            if(monitor != null) monitor.setNote(LanguageManager.getString("16_85"));
            StatusWindow.printTrace(LanguageManager.getString("16_85"));
            printNaughtyAddresses(invalid, validUnsent, validSent);
        }

        public void messagePartiallyDelivered(TransportEvent e) {
            // SMTPTransport doesn't partially deliver msgs
        }

        private void printNaughtyAddresses(Address[] invalid, Address[] validUnsent, Address[] validSent) {
            if(invalid != null) {
                StatusWindow.printTrace(LanguageManager.getString("16_86"));
                for(int i = 0; i < invalid.length; i++) {
                    StatusWindow.printTrace(invalid[i].toString());
                }
            }

            if(validUnsent != null) {
                StatusWindow.printTrace(LanguageManager.getString("16_87"));
                for(int i = 0; i < validUnsent.length; i++) {
                    StatusWindow.printTrace(validUnsent[i].toString());
                }
            }

            if(validSent != null) {
                StatusWindow.printTrace(LanguageManager.getString("16_88"));
                for(int i = 0; i < validSent.length; i++) {
                    StatusWindow.printTrace(validSent[i].toString());
                }
            }
        }


   }


    // =====================================================================
    // =========================== HELPERS =================================
    // =====================================================================

    /**
     * Connect to the Store - will need password authentification
     */
    private static void connectStore(Store store) throws MessagingException {
        StatusWindow.printTrace(LanguageManager.getString("16_89"));
        store.connect(inServer, userName, passwordIn);
        StatusWindow.printTrace(LanguageManager.getString("16_82"));
    }

    /**
     * Get the In password for getting Messages
     */
    private static String getInPassword(Frame owner) {
        if(passwordIn == null) {
            PasswordDialog dialog = new PasswordDialog(owner,
                        LanguageManager.getString("16_90"), LanguageManager.getString("16_91"));
            dialog.show();
            return dialog.getValue();
        }
        else return passwordIn;
    }

    /**
     * Get the Out password for getting Messages
     */
    private static String getOutPassword(Frame owner) {
        if(passwordOut == null) {
            PasswordDialog dialog = new PasswordDialog(owner,
                        LanguageManager.getString("16_90"), LanguageManager.getString("16_91"));
            dialog.show();
            return dialog.getValue();
        }
        else return passwordOut;
    }

    /**
     * Clear the In and Out Passwords
     */
    public static void clearPasswords() {
    	passwordIn = null;
        passwordOut = null;
    }

    /*
    * Delete any marked messages on server and close session
    */
    public static void closeSession(Folder folder, boolean purge) {
        try {
            StatusWindow.printTrace(LanguageManager.getString("16_95"));
            // A true value will expunge marked messages
            if(folder != null && folder.isOpen()) folder.close(purge);
            //if(store != null && store.isConnected()) store.close();
        } catch (MessagingException ex) {
        	System.out.println("Error on closeSession: " + ex);
        }
    }

    /**
    * Mark a message as deleted
    */
    private static void markMessageDeleted(Message msg) {
        try {
            if(msg != null) msg.setFlag(Flags.Flag.DELETED, true);
        }
        catch (MessagingException ex) {
        	System.out.println("Could not mark message as deleted: " + ex);
        }
    }

    private static void closeTransport(Transport transport) {
        try {
            if(transport != null) transport.close();
        } catch(MessagingException ex) {
        	System.out.println("Error on closeTransport: " + ex);
        }
    }

    private static int countTotalMessages(Folder folder) {
        int count = 0;
        try {
            count = folder.getMessageCount();
        } catch (MessagingException ex) {}
        return count;
    }

    private static int countNewMessages(Folder folder) {
        int count = 0;
        try {
            count = folder.getNewMessageCount();
        } catch (MessagingException ex) {}
        return count;
    }

    /**
    * Check header field of Message to see if it's a Colloquia message
    */
    private static boolean isColloquiaMessage(Message m) {
        try {
            String ID[] = m.getHeader(ColloquiaConstants.ColloquiaMessageVersion);
            if(ID == null) return false;
            else return ID[0].equals(ColloquiaConstants.msgVersion);
        } catch (MessagingException ex) {
            return false;
        }
    }
}




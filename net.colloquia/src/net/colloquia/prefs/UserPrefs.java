package net.colloquia.prefs;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

/**
 * User Prefs
 */
public class UserPrefs
extends XMLProperties
{
    private static UserPrefs userPrefs;

    public static String XMLStartTag = "<user_prefs>";
    public static String XMLEndTag = "</user_prefs>";

    private String userName;

    private Vector aliases;
    private Vector outServers;

    public UserPrefs(String userName) {
        this.userName = userName;

        aliases = new Vector();
        outServers = new Vector();

        // Defaults
        putProperty(APP_WIDTH, Utils.getScreenWidth() - 60);
        putProperty(APP_HEIGHT, Utils.getScreenHeight() - 60);
        putProperty(APP_X, 5);
        putProperty(APP_Y, 5);
        putProperty(APP_MAINSPLITTER_POS, 200);
        putProperty(EMAIL_FOLDER, "INBOX");
        putProperty(EMAIL_PROTOCOL, "imap");
        putProperty(EMAIL_LEAVE_ON_SERVER, true);     // Actually the parity is reversed
        putProperty(BACKUP, true);
        putProperty(MESSAGE_QUOTE, true);
        putProperty(DOWNLOAD_LEVEL, 0);
        putProperty(APP_LOOK_FEEL, UIManager.getSystemLookAndFeelClassName());
        putProperty(UNPACK_ZIPS, true);
    }

    /**
    * Over-ride this so we can put certain things in System properties
    */
    public void putProperty(String key, String value) {
        super.putProperty(key, value);

        if(key.equals(PROXY_HOST)) System.getProperties().put(key, value);
        if(key.equals(PROXY_PORT)) System.getProperties().put(key, value);
    }

    public String getUserName() {
        return userName;
    }

    /**
    * Get currently loaded user prefs
    */
    public static UserPrefs getUserPrefs() {
        return userPrefs;
    }

    /**
    * Set currently loaded user prefs
    */
    public static void setUserPrefs(UserPrefs userPrefs) {
        UserPrefs.userPrefs = userPrefs;
    }


    /**
    * Load a UserPrefs file and return it but don't set this to it
    */
    public static UserPrefs loadUserPrefs(String userName) {
        UserPrefs userPrefs = new UserPrefs(userName);

        FileInputStream fIn = null;
		String fileName = DataFiler.getUserPrefsFileName(userName);
        File file = new File(fileName);
        if(!file.exists()) return userPrefs;         // Nothing there

        XMLFileReader reader = new XMLFileReader(file);

        try {
            reader.open();
            userPrefs.unMarshallXML(reader);
            reader.close();
        }
        catch(XMLReadException ex) {
            ErrorHandler.showWarning("ERR18", ex, "ERR");
        	return userPrefs;
        }

        return userPrefs;
    }

    /**
    * Save Preferences
    */
    public static void saveUserPrefs(UserPrefs userPrefs) {
        // App position and size
        if(MainFrame.getInstance() != null) {
            int windowState = 0;
            windowState = MainFrame.getInstance().getExtendedState();
            userPrefs.putProperty(APP_STATE, windowState);

            userPrefs.putProperty(APP_MAINSPLITTER_POS, MainFrame.getInstance().mainSplitter.getDividerLocation());

            // Un-maximised bounds - only save if not maximized
            if(windowState == JFrame.NORMAL) {
                Rectangle r = MainFrame.getInstance().getBounds();
                userPrefs.putProperty(APP_X, r.x);
                userPrefs.putProperty(APP_Y, r.y);
                userPrefs.putProperty(APP_WIDTH, r.width);
                userPrefs.putProperty(APP_HEIGHT, r.height);
            }
        }

        String fileName = DataFiler.getUserPrefsFileName(userPrefs.getUserName());
        File file = new File(fileName);
        XMLFileWriter xmlWriter = new XMLFileWriter(file);

        try {
            xmlWriter.open();
            userPrefs.write2XML(xmlWriter);
            xmlWriter.close();
        }
        catch(XMLWriteException ex) {
            ErrorHandler.showWarning("ERR1", ex, "ERR");
        }

    }


    protected void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);

        Enumeration e = keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)get(tag);
            writer.write(new XMLTag(tag, value));
        }

        // Aliases
        writer.writeln("<aliases>");
        for(int i = 0; i < aliases.size(); i++) {
            String eMail = (String)aliases.elementAt(i);
            writer.write(new XMLTag(Person.ALIAS, eMail));
        }
        writer.writeln("</aliases>");

        // OutServers
        writer.writeln("<email_out_servers>");
        for(int i = 0; i < outServers.size(); i++) {
            String server = (String)outServers.elementAt(i);
            writer.write(new XMLTag(EMAIL_OUT_SERVER, server));
        }
        writer.writeln("</email_out_servers>");

        // Close
        writer.writeln(XMLEndTag);
    }


    protected void unMarshallXML(XMLReader reader) throws XMLReadException {
        String line;
        XMLTag xmlTag;
        while((line = reader.readLine()) != null) {
            xmlTag = XMLTag.getXMLTag(line);
            if(xmlTag != null) {
                if(xmlTag.tag.equals(Person.ALIAS)) aliases.addElement(xmlTag.value);
                else if(xmlTag.tag.equals(EMAIL_OUT_SERVER)) outServers.addElement(xmlTag.value);
            	else putProperty(xmlTag.tag, xmlTag.value);
            }
        }
    }


    /**
    * Get my details from UserPrefs and make them into a Person class
    */
    public static Person getMe() {
        UserPrefs userPrefs = getUserPrefs();

        Person me = new Person("-", "-");
        me.setName(userPrefs.getProperty(GIVEN_NAME) + " " + userPrefs.getProperty(FAMILY_NAME), false);
        me.setGUID(userPrefs.getProperty(EMAIL_ADDRESS));
        me.putProperty(Person.GIVEN_NAME, userPrefs.getProperty(GIVEN_NAME), false);
        me.putProperty(Person.FAMILY_NAME, userPrefs.getProperty(FAMILY_NAME), false);
        me.putProperty(Person.ADDRESS, userPrefs.getProperty(ADDRESS), false);
        me.putProperty(Person.PHONE_NUMBER, userPrefs.getProperty(PHONE), false);
        me.putProperty(Person.PHOTOGRAPH, userPrefs.getProperty(PHOTOGRAPH), false);
        me.putProperty(Person.URL, userPrefs.getProperty(HOME_PAGE), false);
        me.putProperty(Person.ID, userPrefs.getProperty(ID), false);
        me.setEmailAddress(userPrefs.getProperty(EMAIL_ADDRESS), false);
        me.setSubmitter(userPrefs.getProperty(EMAIL_ADDRESS), false);

        // e-mail aliases
        Vector v = userPrefs.getAliases();
        for(int i = 0; i < v.size(); i++) {
        	String s = (String)v.elementAt(i);
            me.putProperty(Person.ALIAS + (i + 1), s.trim(), false);
        }

        return me;
    }


    //==========================================================================
    //============================ PROPERTIES ==================================
    //==========================================================================

    public static String FAMILY_NAME = "family_name";
    public static String GIVEN_NAME = "given_name";
    public static String ADDRESS = "address";
    public static String HOME_PAGE = "home_page";
    public static String PHONE = "phone";
    public static String PHOTOGRAPH = "photograph";
    public static String ID = "id";

    public static String APP_LOOK_FEEL = "app_look_feel";
    public static String APP_X = "app_x";
    public static String APP_Y = "app_y";
    public static String APP_WIDTH = "app_width";
    public static String APP_HEIGHT = "app_height";
    public static String APP_STATE = "app_state";
    public static String APP_MAINSPLITTER_POS = "app_mainsplitter_pos";

    public static String EMAIL_IN_USER_NAME = "email_user_name";
    public static String EMAIL_OUT_USER_NAME = "email_out_user_name";
    public static String EMAIL_OUT_SERVER = "email_out_server";
    public static String EMAIL_ACTIVE_OUT_SERVER = "email_active_out_server";
    public static String EMAIL_IN_SERVER = "email_in_server";
    public static String EMAIL_FOLDER = "email_folder";
    public static String EMAIL_PROTOCOL = "email_protocol";
    public static String EMAIL_AUTHSMTP = "email_authsmtp";
    public static String EMAIL_ADDRESS = "email_address";
    public static String EMAIL_LEAVE_ON_SERVER = "email_leave_on_server";
    public static String EMAIL_IMPORT_FOLDER = "email_import_folder";

    public static String BROWSER_NOW = "browser_now";
    public static String DOWNLOAD_LEVEL = "download_level";
    public static String PROXY_HOST = "http.proxy.host";
    public static String PROXY_PORT = "http.proxy.port";

    public static String DATA_ROOT = "data_root";

    public static String MESSAGE_QUOTE = "message_quote";
    public static String BACKUP = "backup";
    public static String BACKUP_FOLDER = "backup_folder";

    public static String SAVE_NAG_MINS = "save_nag_mins";
    public static String STATUS_MESSAGES = "status_messages";

    public static String FTP_REMOTE = "ftp_remote";
    public static String FTP_FOLDER = "ftp_folder";
    public static String FTP_USERNAME = "ftp_username";

    public static String UNPACK_ZIPS = "unpack_zips";


    //==========================================================================
    //============================ ACCESSORS ===================================
    //==========================================================================

    public void setAliases(Vector set) { aliases = set; }

    public Vector getAliases() {
        return (Vector)aliases.clone();
    }

    public Vector getAllEmailAddresses() {
        Vector v = getAliases();
        v.addElement(getProperty(EMAIL_ADDRESS));
        return v;
    }

    public boolean isMyEmailAddress(String eMail) {
        if(eMail == null) return false;
        String emailAddress = getProperty(EMAIL_ADDRESS);
        if(eMail.equalsIgnoreCase(emailAddress)) return true;
        for(int i = 0; i < aliases.size(); i++) {
            String s = (String)aliases.elementAt(i);
            if(eMail.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    public void setOutServers(Vector set) { outServers = set; }

    public Vector getOutServers() {
        return (Vector)outServers.clone();
    }
}

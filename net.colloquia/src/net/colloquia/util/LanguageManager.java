package net.colloquia.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import net.colloquia.io.DataFiler;

/**
 * 
 * LanguageManager  - handles String bundles
 *
 * @author Phillip Beauvoir
 * @version $Id: LanguageManager.java,v 1.1 2006/04/19 14:17:52 phillipus Exp $
 */
public class LanguageManager {
    public static String DEFAULT_LANGUAGE = "English";
    
    public static String STRINGBUNDLE_LOCATION = "net.colloquia.resources.Strings";

    private static String[] langs = {
        "Cymraeg", "cy", "GB",
    	"English", "en", "GB",
        "Espanol", "es", "ES",
        "Italiano", "it", "IT"
    };

    private static String currentLanguage;

    private static ResourceBundle messages;
    private static Properties userMessages;

    public static String getCurrentLanguage() {
        if(currentLanguage == null) setLanguage(DEFAULT_LANGUAGE);
        return currentLanguage;
    }

    private static Locale getLocale(String language) {
    	for(int i = 0; i < langs.length; i+=3) {
        	if(language.equals(langs[i])) return new Locale(langs[i+1], langs[i+2]);
        }

        // Default
        return Locale.UK;
    }

    public static void setLanguage(String language) {
        currentLanguage = language;

        // Try this first
        userMessages = loadUserStrings(language);
        if(userMessages != null) {
            messages = null;
        	return;
        }

        try {
	        Locale currentLocale = getLocale(language);
            messages = ResourceBundle.getBundle(STRINGBUNDLE_LOCATION, currentLocale);
            userMessages = null;
        } catch (MissingResourceException mre) {
            System.err.println(STRINGBUNDLE_LOCATION + ".properties not found");
            System.exit(1);
        }
    }

    public static String getString(String key) {
        if(userMessages != null) {
        	String s = userMessages.getProperty(key);
        	return s == null ? "no string" : s;
        }

        if(messages == null) {
            System.out.println("No String Bundle");
            return "";
        }

        try {
            return messages.getString(key);
        } catch (MissingResourceException mre) {
            System.out.println("Missing ResourceBundle String: " + key);
            return "";
        }
    }

    public static Vector getLanguageNames() {
		Vector languages = new Vector();

    	for(int i = 0; i < langs.length; i+=3) {
            languages.addElement(langs[i]);
        }

        // Look for user defined strings in Colloquia install folder
        String installFolder = System.getProperty("user.dir");
        installFolder = DataFiler.addFileSepChar(installFolder);
        File fileFolder = new File(installFolder);
        String[] files = fileFolder.list();
        for(int i = 0; i < files.length; i++) {
            if(files[i].toLowerCase().endsWith(".strings")) {
                String userString = files[i].substring(0, files[i].indexOf('.'));
                languages.addElement(userString);
            }
        }

        return languages;
    }

    private static Properties loadUserStrings(String language) {
        String fileName = System.getProperty("user.dir");
        fileName = DataFiler.addFileSepChar(fileName) + language + ".strings";
        File file = new File(fileName);
        if(!file.exists()) return null;

    	Properties p = new Properties();
        try {
        	BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        	p.load(in);
        }
        catch(Exception ex) {
        	System.out.println("loadUserStrings: " + ex);
            return null;
        }
        return p;
    }
}


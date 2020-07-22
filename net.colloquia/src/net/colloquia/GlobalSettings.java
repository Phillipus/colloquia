package net.colloquia;

import java.io.*;

import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

public final class GlobalSettings {

	public static String LAST_USER = "last_user";
	public static String LANGUAGE = "language";
	public static String ONLINE = "online";

    public static String getGlobalSettingsFile() {
        return DataFiler.getColloquiaFolder() + "colloquia.cqs";
    }

    public static void save(String lastUser, String language, boolean online) {
        try {
            File file = new File(getGlobalSettingsFile());
	        XMLFileWriter writer= new XMLFileWriter(file);
        	writer.open();
        	writer.writeln("<colloquia_settings>");

			writer.write(new XMLTag(LAST_USER, lastUser));
			writer.write(new XMLTag(LANGUAGE, language));
			writer.write(new XMLTag(ONLINE, String.valueOf(online)));

        	writer.writeln("</colloquia_settings>");
        	writer.close();
        } catch(XMLWriteException ex) {
            System.out.println("saveSettings error: " + ex);
        }
    }

    public static String getLastUser() {
		String val = XMLUtils.getXMLKey(LAST_USER, getGlobalSettingsFile());
        return val == null ? "default" : val;
    }

    public static String getLanguage() {
		String val = XMLUtils.getXMLKey(LANGUAGE, getGlobalSettingsFile());
        return val == null ? LanguageManager.DEFAULT_LANGUAGE : val;
    }

    public static boolean isOnline() {
        String val = XMLUtils.getXMLKey(ONLINE, getGlobalSettingsFile());
    	return val == null ? false : val.equalsIgnoreCase("true");
    }
}

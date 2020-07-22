package net.colloquia.util;

import java.io.*;
import java.net.*;
import java.util.*;

import net.colloquia.io.*;

public final class URLUtils  {
    private URLUtils() {}

    /**
    * Takes a possibly dodgy url String and tries to make a good one
    * Returns null if can't manage it!
    */
    public static URL normalizeAddress(String urlString) {
        if(urlString == null) return null;

        URL url = makeURL(urlString);

        // Did it work? If not, try adding http://
        if(url == null) url = makeURL("http://" + urlString);

        return url;
    }

    public static boolean isFTP(URL url) {
    	return url.getProtocol().equals("ftp");
    }

    /**
    * Quick way to see if the file is a QT Movie
    */
    public static boolean isQuickTimeFile(URL url) {
        if(url == null) return false;
        String s = url.toString().toLowerCase();
        FileType[] fileTypes = FileType.getFileTypes();
        if(fileTypes == null) return false;
        for(int i = 0; i < fileTypes.length; i++) {
        	if(fileTypes[i].isMediaFile() && s.endsWith(fileTypes[i].getExtension())) return true;
        }
        return false;
    }

    /**
    * Determine whether we can display this in the browser
    */
    public static boolean isExternalFile(URL url) {
        if(url == null) return false;

        String type = URLUtils.getContentType(url);
        if(type != null) {
            type = type.toLowerCase();
			if(type.equals("text/html")) return false;
            if(type.startsWith("application")) return true;
            if(type.startsWith("audio")) return true;
            if(type.startsWith("video")) return true;
        }

    	String s = url.toString().toLowerCase();
        FileType[] fileTypes = FileType.getFileTypes();
        if(fileTypes == null) return false;
        for(int i = 0; i < fileTypes.length; i++) {
        	if(s.endsWith(fileTypes[i].getExtension())) return true;
        }
        return false;
    }

    /** Utility to turn a string into a URL
        @param s the string to convert
        @return converted URL or null if not successful
    */
    public static URL makeURL(String s) {
        if(s == null) return null;
        URL u = null;

        try {
             u = new URL(s);
        }
        catch(MalformedURLException exc) {
            return null;
        }

        return u;
    }

    /** Utility to turn a string into a URL
        @param s the string to convert
        @return converted URL or null if not successful
    */
    public static URL makeLocalURL(String fileName) {
        if(fileName == null) return null;
        URL u = null;

        try {
             u = new URL("file", "", fileName);
        }
        catch(MalformedURLException exc) {
            return null;
        }

        return u;
    }

    /**
    * Returns true if the URL is an application (like a exe, pdf, zip etc.)
    */
    public static boolean isApplication(URL url) {
        String contentType = getContentType(url);
        if(contentType == null) return false;
        return contentType.startsWith("application");
    }

    /**
    * Returns true if the URL is of unknown type
    */
    public static boolean isUnknown(URL url) {
        String contentType = getContentType(url);
        if(contentType == null) return false;
        return contentType.startsWith("content/unknown");
    }

    /**
    * Gets the content type of a URL or null if not known
    * Examples are "text/html", "application/pdf", "content/unknown"
    */
    public static String getContentType(URL url) {
        URLConnection uc = getURLConnection(url);
        if(uc != null) return uc.getContentType();
        else return null;
    }

    public static String guessContentTypeFromName(URL url) {
        URLConnection uc = getURLConnection(url);
        if(uc != null) {
            try {
            	InputStream is = uc.getInputStream();
        		return URLConnection.guessContentTypeFromStream(is);
            }
            catch(IOException ex) {
                //System.out.println(ex);
            	return null;
            }
    	}
        else return null;
    }

    /**
    * Gets the last modified date of the document (may be null)
    */
    public static Date getLastModified(String loc) {
        URLConnection uc = getURLConnection(loc);
        if(uc != null) return new Date(uc.getLastModified());
        else return null;
    }

    /**
    * Returns true if loc2 date modified is newer than loc1
    */
    public static boolean isNewer(String loc1, String loc2) {
        Date date1 = getLastModified(loc1);
        Date date2 = getLastModified(loc2);

        if(date1 == null || date2 == null) return false;

        System.out.println("local:" + date1);
        System.out.println("net:" + date2);

        return date2.after(date1);
    }

    /**
    * Gets the URLConnection for a URL or null
    */
    public static URLConnection getURLConnection(String loc) {
        URL url = makeURL(loc);
		return getURLConnection(url);
    }

    public static URLConnection getURLConnection(URL url) {
        if(url == null) return null;

        URLConnection uc = null;

        try {
            uc = url.openConnection();
        }
        catch(IOException ex) {
            System.out.println("No Connection to: " + url.toString());
        }

        return uc;
    }

    /**
     * Make a suitable jar file name
     * ClueBrowser 4.0 uses the jar: protocol
     * Not in ClueBrowser 4.11
    */
    public static String makeJarName(String fileName) {
        // ClueBrowser 4.0
        //fileName = "jar:file:/" + fileName + "!/index.html";
        // ClueBrowser 4.11
        fileName = "file:/" + fileName + "!/index.html";
        return fileName;
    }

}

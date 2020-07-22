package net.colloquia.util;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.io.*;

public final class Utils  {
    private static boolean createdUKTimeZone = false;

    private Utils() {}

	public static final int MACINTOSH = 0;
	public static final int WINDOWS = 1;
    public static final int UNIX = 2;

    public static final int NT = 3;

    // Determine the OS
    public static int getOS() {
		String osName = System.getProperty("os.name");
        osName = osName.toLowerCase();
		if(osName.startsWith("mac os")) return MACINTOSH;
        if(osName.equalsIgnoreCase("windows nt")) return NT;
		if(osName.startsWith("windows")) return WINDOWS;
        else return UNIX;
    }

    public static String getJavaVersion() {
        return System.getProperties().getProperty("java.version");
    }

    /**
    * Parse date to a short date string
    */
    public static String parseDate(Date date) {
        if(date == null) {
            return "";
        }
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return df.format(date);
    }

    public static Date getNow() {
        /*
        // This is a kludge because Daylight Savings aren't taken into account in UK
        // Bug Id 4225280 / 4252829
        if(!createdUKTimeZone && TimeZone.getDefault().getID().equals("GMT")) {
            SimpleTimeZone stz = new SimpleTimeZone(0, "UK_DST");
            stz.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2*60*60*1000);
            stz.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2*60*60*1000);
            TimeZone.setDefault(stz);
            createdUKTimeZone = true;
        }
        */
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }


    /**
     * Return the extension portion of the file's name or null
     */
    public static String getExtension(File f) {
	    if(f != null) {
	        String filename = f.getName();
            return getExtension(filename);
	    }
	    return null;
    }

    public static String getExtension(String f) {
        int i = f.lastIndexOf('.');
	    if(i > 0 && i < f.length() - 1) return f.substring(i + 1);
	    return null;
    }

    /**
    @return the name part of a file name excluding the extension
    */
    public static String getFileName(File f) {
	    if(f != null) {
	        String filename = f.getName();
            return getFileName(filename);
	    }
	    return null;
    }

    public static String getFileName(String f) {
        int i = f.lastIndexOf('.');
	    if(i > 0 && i < f.length() - 1) return f.substring(0, i);
	    else return f;
    }

    /**
    * Gets a resource image icon
    */

    //private static Hashtable iconCache = new Hashtable();
    
	public static ImageIcon getIcon(String iconName) {
         if(!iconName.endsWith(".gif") && !iconName.endsWith(".jpg")) iconName += ".gif";
         ImageIcon imageIcon = null;

         try {
            String fullIconName = ColloquiaConstants.iconPath + iconName;
            URL url = Utils.class.getResource(fullIconName);
            if(url == null) return null;  // Some icons don't exist yet
            imageIcon = new ImageIcon(url);
         }
         catch(Exception ex) {
            System.out.println(ex);
            //System.out.println("Could not get icon: " + iconName);
            return null;
         }
         return imageIcon;
    }

    /**
    * Gets a resource image
    */
    public static Image getImage(String imageName) {
        ImageIcon imageIcon = getIcon(imageName);
        if(imageIcon != null) return imageIcon.getImage();
        else return null;
    }

    /**
    * Gets a photo image
    */
    public static Image getPhotoImage(String imagePath) {
        if(imagePath == null || imagePath.length() == 0) return null;

        URL url;

        // Do we have a URL or not?
        try {
             url = new URL(imagePath);
        }
        catch(MalformedURLException exc) {
            // No, it's a file path
            return Toolkit.getDefaultToolkit().getImage(imagePath);
        }
        // Yes, it's a URL
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    /**
    Returns number of String lines in file
    */
    public static int countLines(File file) {
        int lines = 0;
        String inLine = "";

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while(inLine != null) {
                inLine = in.readLine();
                lines++;
            }
            in.close();
        } catch (Exception ex) {
            System.out.println("Count Lines error: " + ex);
            return lines;
        }

        return lines;
    }

	/*
    * Counts the total files & folders in given folder
    * The count includes folder
    */
    public static int countFiles(File folder) {
        int count = 1;
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++) {
            File f = files[i];
        	if(files[i].isDirectory()) count += countFiles(files[i]);
            else count++;
        }
        return count;
    }

    /**
    * Centres a window on the screen given the size of window.
    * It doesn't show the window.
    */
    public static void centreWindow(Window window, int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;
        window.setSize(width, height);
        window.setLocation(screenWidth - width/2, screenHeight - height/2);
    }

    /**
    * Returns the width of the screen
    */
    public static int getScreenWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
    }

    /**
    * Returns the height of the screen
    */
    public static int getScreenHeight() {
        return Toolkit.getDefaultToolkit().getScreenSize().height;
    }


    /**
    * Checks that <b>str</b> is not an empty or null String.  If it is, the
    * string is converted to a " " string.  This is used in Export routines
    * to ensure at least something is saved in a tab-delimited file.
    * @param str The string to be checked.
    * @return The original string if it was OK or " " if it was empty or null.
    */
    public static String spaceString(String str) {
        if(str == null || str.length() == 0) str = " ";
        return str;
    }

    /**
    * Checks that <b>str</b> - a tab delimited string - does not contain a
    * null token/field.  If it does, a " " token is inserted.
    * This is used in Import/Open routines.
    * @param str The tab delimited string to be checked.
    * @return The original string if it was OK or a new string with replaced " "
    * tokens.
    */
    public static String padInLine(String str) {
        int index;

        while((index = str.indexOf("\t\t")) != -1) {
            str = str.substring(0, index + 1) + " " + str.substring(index + 1);
        }

        return str;
    }

    /**
    * Checks a String for CR characters and replaces occurences of \r or \r\n with \n
    * A Macintosh inserts the former which does not show
    * as a CR on a PC when text is displayed in insertString() in a DefaultStyledDocument.
    * @return the modified String
    */
    public static String changeCR(String text) {
        int index = 0;

        while(true) {
            index = text.indexOf('\r', index);
            if(index == -1) break;
            else {
                index++;
                if(index >= text.length()) break;
                // If next char is not \n, add one
                if(text.charAt(index) != '\n') {
                    text = text.substring(0, index - 1) + "\n" + text.substring(index);
                }
                // Else, just remove the \r
                else {
                    text = text.substring(0, index - 1) + text.substring(index);
                }
            }
        }

        return text;
    }

    /**
    * @return an int greater than 0 if A > B
    */
    public static int compare(Object A, Object B) {
        if(A instanceof Date && B instanceof Date)
            return compareDate((Date)A, (Date)B);
        else if(A instanceof String && B instanceof String)
            return compareString((String)A, (String)B);
        else return 0;
    }

    /**
    * Compares 2 dates
    */
    private static int compareDate(Date A, Date B) {
        return A.before(B) ? -1 : A.after(B) ? 1 : 0;
    }

    /**
    * Compares 2 strings
    * returns an int greater than 0 if a > b
    */
    private static int compareString(String A, String B) {
        return A.toLowerCase().compareTo(B.toLowerCase());
    }

    /*
    * Sort a Vector
    */
    public static void sortVector(Vector members, boolean ascending) {
        Object o1, o2;
        for(int i = 0; i < members.size(); i++) {
            for(int j = 0; j < members.size() - 1; j++) {
                o1 = members.elementAt(j);
                o2 = members.elementAt(j + 1);
                int comparison = Utils.compare(o1.toString(), o2.toString());
                if((ascending && comparison > 0) || (!ascending && comparison < 0)) {
                    members.removeElementAt(j);
                    members.insertElementAt(o1, j + 1);
                }
            }
        }
    }

    /**
    * A utility function that layers on top of the LookAndFeel's
    * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
    * is supported. Returns false if the LookAndFeel is not supported
    * and/or if there is any kind of error checking if the LookAndFeel
    * is supported.
    * The L&F menu will use this method to detemine whether the various
    * L&F options should be active or inactive.
    */
     public static boolean isAvailableLookAndFeel(String classname) {
         try { // Try to create a L&F given a String
             Class lnfClass = Class.forName(classname);
             LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
             return newLAF.isSupportedLookAndFeel();
         } catch(Exception e) {
             return false;
         }
     }

    /**
    * Generates a unique filename for a text html e-mail message
    * DOES NOT return folder in path
    */
    public static String generateHTMLMessageFileName(String folder) {
        Random rand = new Random(new Date().getTime());
        int num;
        String fileName, tmpName;

        // Loop while generated number already exists on file
        do {
            num = rand.nextInt();
            fileName = String.valueOf(num);
            // Remove minus sign
            if(fileName.indexOf("-") != -1) fileName = fileName.substring(1);
            // Add extension
            fileName += ".html";
            tmpName = folder + fileName;
        } while(DataFiler.fileExists(tmpName));

        return fileName;
    }

    /**
    * Returns a class from a jar file that exists in the Colloquia install folder
    */
	public static Class getClassFromJar(String className, String jarName) {
        try {
            String dir = System.getProperty("user.dir");
            URL url = new URL("file:/" + dir + "/" + jarName);
            ClassLoader loader = new URLClassLoader(new URL[] { url });
            return loader.loadClass(className);
        }
        catch(Exception ex) {
            if(ColloquiaConstants.DEBUG) System.out.println("Could not create class: "  + className + " " + ex);
            return null;
        }
    }

    /**
    * Returns a class instance from a jar file that exists in the Colloquia install folder
    */
    public static Object getClassInstanceFromJar(String className, String jarName) {
        Object o = null;

        try {
	    	Class c = getClassFromJar(className, jarName);
        	if(c != null) o = c.newInstance();
        }
        catch(Exception ex) {
            if(ColloquiaConstants.DEBUG) System.out.println("Could not create class instance: "  + className + " " + ex);
            return null;
        }
        return o;
    }


    public static boolean isQuickTimeInstalled() {
		String s = findLibrary("QTJava");
        if(s == null) return false;
		Class c = getClassFromJar("quicktime.QTSession", "QTJava.zip");
        if(c == null) return false;
        return true;
    }

    public static void playQuickTimeFile(URL url) {
        if(!Utils.isQuickTimeInstalled()) {
            ErrorHandler.showWarning(MainFrame.getInstance(), "QT_3", null, "QT");
        }
        else {
            QTimePlayer qtp = QTimePlayer.getInstance();
            if(qtp != null) qtp.playFile(url.toString());
        }
    }

    /**
     * Returns the absolute path name of a native library.
     * Or null
	*/
    public static String findLibrary(String libName) {
        String s = System.mapLibraryName(libName);
		String libPath = System.getProperties().getProperty("java.library.path");
		String sep = System.getProperties().getProperty("path.separator");
        StringTokenizer t = new StringTokenizer(libPath, sep);
        while(t.hasMoreTokens()) {
            String f = DataFiler.addFileSepChar(t.nextToken()) + s;
        	if(DataFiler.fileExists(f)) return f;
        }
        return null;
    }

     /**
    * Gets a local resource
    */
	public static URL getResourceURL(String fileName) {
         return new Object().getClass().getResource(fileName);
    }

    /**
    * Adds a file to the Zip file
    */
    public static boolean addFileToZip(String fileName, String entryName, ZipOutputStream zOut)
    throws ColloquiaFileException {
        return addFileToZip(new File(fileName), entryName, zOut);
    }

    /**
    * Adds a file to the Zip file
    */
    public static boolean addFileToZip(File file, String entryName, ZipOutputStream zOut)
    throws ColloquiaFileException {
        if(file == null) return false;
        if(!file.exists()) return false;

        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];

        try {
            ZipEntry zipEntry = new ZipEntry(entryName);
            // Set time stamp to file's
            zipEntry.setTime(file.lastModified());
            zOut.putNextEntry(zipEntry);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), bufSize);
            while((bytesRead = in.read(buf)) != -1) {
                zOut.write(buf, 0, bytesRead);
            }
            zOut.closeEntry();
            in.close();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not add file to zip", ex.getMessage());
        }
        return true;
    }

    /**
    * Adds a String as a fiel entry to the Zip file
    */
    public static boolean addStringToZip(String text, String entryName, ZipOutputStream zOut)
    throws ColloquiaFileException {
        try {
            int i;
            BufferedReader reader = new BufferedReader(new StringReader(text));
            ZipEntry zipEntry = new ZipEntry(entryName);
            zOut.putNextEntry(zipEntry);
            while((i = reader.read()) != -1) {
                zOut.write(i);
            }
            zOut.closeEntry();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not add String to zip", ex.getMessage());
        }
        return true;
    }

    /**
    * Extracts a named entry out of the zip file and returns the entry as a String
    * Returns null if weirdness happens
    */
    public static String extractZipEntry(File zipFile, String entryName) throws ColloquiaFileException {
        if(zipFile == null || !zipFile.exists()) return null;

        ZipEntry zipEntry;
        ZipInputStream zIn;
        int bit;
        boolean foundEntry = false;
        StringBuffer sb;

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile));
            zIn = new ZipInputStream(in);

            // Get zip entry
            while((zipEntry = zIn.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                if(zipEntryName.equalsIgnoreCase(entryName)) {
                    foundEntry = true;
                    break;
                }
                zIn.closeEntry();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw new ColloquiaFileException("MessageIn: Could not extract zip entry", ex.getMessage());
        }

        // If we didn't get it return
        if(foundEntry == false) {
            try{
                zIn.close();
            }
            catch(IOException ex) {}
            return null;
        }

        sb = new StringBuffer();

        // Extract it
        try {
            while((bit = zIn.read()) != -1) {
                sb.append((char)bit);
            }

            zIn.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            throw new ColloquiaFileException("MessageIn: Could not put zip entry to String", ex.getMessage());
        }


        return sb.toString();
    }


    /**
    * Extracts a named entry out of the zip file to the specified fileName
    * Returns the File ref if OK or null if weirdness happens
    */
    public static File extractZipEntry(File zipFile, String entryName, String outFileName)
    throws ColloquiaFileException {
        if(zipFile == null || !zipFile.exists()) return null;

        ZipInputStream zIn;
        ZipEntry zipEntry;
        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];
        boolean foundEntry = false;
        File outFile = null;

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile), bufSize);
            zIn = new ZipInputStream(in);

            while((zipEntry = zIn.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                if(zipEntryName.equalsIgnoreCase(entryName)) {
                    foundEntry = true;
                    break;
                }
                zIn.closeEntry();
            }
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not extract zip entry", ex.getMessage());
        }

        // If we didn't get it return
        if(foundEntry == false) return null;

        // Extract it and save to fullFileName
        try {
            outFile = new File(outFileName);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile), bufSize);

            while((bytesRead = zIn.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.flush();
            out.close();
            zIn.close();

            // Restore time stamp
	        outFile.setLastModified(zipEntry.getTime());
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not save zip entry", ex.getMessage());
        }

        return outFile;
    }

    /**
    * Extracts all files out of a zip file entry names to a Vector
    * Returns an array of String names or null if none
    */
    public static String[] getZipFileEntryNames(File zipFile)
    throws ColloquiaFileException {
        if(zipFile == null || !zipFile.exists()) return null;
        ZipInputStream zIn = null;
        ZipEntry zipEntry;
        final int bufSize = 16000;
        Vector fileList = new Vector();

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipFile), bufSize);
            zIn = new ZipInputStream(in);
            while((zipEntry = zIn.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                fileList.addElement(zipEntryName);
                zIn.closeEntry();
            }
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not locate zip entry", ex.getMessage());
        }
        finally {
        	try {
            	if(zIn != null) zIn.close();
            }
            catch(IOException ex) { }
        }

        String[] names = new String[fileList.size()];
        fileList.copyInto(names);
        return names;
	}

    /**
    * Extracts all entries out of the zip file to the specified folder
    * Returns an array of File types extracted
    * If there is a magic entry called "startfile" that contains the name of one
    * of the files then this will be the first file in the list.
    */
    public static File[] unpackZip(File zipFile, String folder) throws ColloquiaFileException {
    	String[] names = getZipFileEntryNames(zipFile);
        if(names == null) return null;

        Vector fileList = new Vector();

        String startfile = extractZipEntry(zipFile, "startfile");
        if(startfile != null) {
            startfile = startfile.trim();
            File file = extractZipEntry(zipFile, startfile, folder + startfile);
            if(file != null) fileList.addElement(file);
        }

        for(int i = 0; i < names.length; i++) {
        	//System.out.println(folder + names[i]);
            // Don't extract startfile magic entry
            if(names[i].equalsIgnoreCase("startfile")) continue;
            // Already extracted start file
            if(startfile != null && names[i].equalsIgnoreCase(startfile)) continue;
            File file = extractZipEntry(zipFile, names[i], folder + names[i]);
            if(file != null) fileList.addElement(file);
        }

        File[] files = new File[fileList.size()];
        fileList.copyInto(files);

        return files;
    }
}


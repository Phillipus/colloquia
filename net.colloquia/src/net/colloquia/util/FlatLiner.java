package net.colloquia.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.views.*;


public final class FlatLiner
implements Runnable
{
    private String saveFolder;
    private URL topURL;            				// The very first page
    private int depthMax = 1;                   // The search depth
    private String jarName;                     // The Jar file name
    private ColloquiaComponent tc;                 // The associated tc

    private String quote = "\"";

    private PMonitor monitor;

    /** The list of all Files we have downloaded */
    private Vector files = new Vector();

    /** The thread which will be spun */
    private Thread thread;

    private boolean ok = true;
    private boolean jarSuccess = true;


    public FlatLiner(URL url, ColloquiaComponent tc) {
        saveFolder = DataFiler.getWebPagesFolder(true);
        depthMax = UserPrefs.getUserPrefs().getIntegerProperty(UserPrefs.DOWNLOAD_LEVEL) + 1;
        topURL = url;
        this.tc = tc;

        // Set up the ProgressMonitor
        monitor = new PMonitor(MainFrame.getInstance(), "Flatliner",
            LanguageManager.getString("15_1"), LanguageManager.getString("15_2"));

        // Start the thread (calls run())
        thread = new Thread(this);
        thread.start();
    }


    public void run() {
        if(ColloquiaConstants.DEBUG) System.out.println("Downloading page: " + topURL);

        PageInfo pageInfo = new PageInfo(topURL, 1);

        jarName = getValidFileName(pageInfo.urlFull) + ".jar";

        downloadHTMLPage(pageInfo);

        // Put in jar
        putInJar();

        // Close monitor progress - in case it hasn't finished
        monitor.close();

        // Thank-you
        if(ok) JOptionPane.showMessageDialog(MainFrame.getInstance(),
            LanguageManager.getString("15_3"), LanguageManager.getString("15_4"),
            JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(MainFrame.getInstance(),
            LanguageManager.getString("15_10"), LanguageManager.getString("15_4"),
            JOptionPane.INFORMATION_MESSAGE);
    }


    /**
    * Put all the files in a jar file
    */
    private void putInJar() {
        ZipEntry zipEntry;
        BufferedInputStream bin;
        FileOutputStream fout = null;
        ZipOutputStream zout = null;
        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];

        monitor.setNote(LanguageManager.getString("15_5"));

        try {
            String fullName = saveFolder + jarName;
            if(DataFiler.fileExists(fullName)) DataFiler.deleteFile(fullName);

            fout = new FileOutputStream(fullName);
            zout = new ZipOutputStream(fout);

            for(int i = 0; i < files.size(); i++) {
                String fileName = saveFolder + files.elementAt(i);
                if(!DataFiler.fileExists(fileName)) continue;
                bin = new BufferedInputStream(new FileInputStream(fileName), bufSize);
                zipEntry = new ZipEntry((String)files.elementAt(i));
                zout.putNextEntry(zipEntry);
                while((bytesRead = bin.read(buf)) != -1) zout.write(buf, 0, bytesRead);
                zout.closeEntry();
                bin.close();
            }

            zout.flush();
            zout.close();
            fout.close();

            deleteFiles();

        } catch(Exception ex) {
            StatusWindow.printTrace("Could not put files in Jar!");
            StatusWindow.printTrace("Exception: " + ex);
            ok = false;
            jarSuccess = false;
            try {
                if(zout != null) zout.close();
                if(fout != null) fout.close();
            }
            catch(Exception ex1) { }
            deleteFiles();
            return;
        }

        // Set local file to name of this jar
        // Java 2 uses Jar protocol
        jarName = URLUtils.makeJarName(saveFolder + jarName);
        tc.setLocalFile(jarName, false);

        ViewPanel.getInstance().repaintCurrentView();
    }

    private void deleteFiles() {
        // Delete files
        for(int i = 0; i < files.size(); i++) {
            String fileName = saveFolder + files.elementAt(i);
            DataFiler.deleteFile(fileName);
        }
    }

    /**
    * Go thru an HTML Page and download all the referenced files
    */
    private void downloadPageRefs(PageInfo pageInfo) {
        Enumeration refs = pageInfo.getRefs().keys();
        while(refs.hasMoreElements()) {
            String loc = (String)refs.nextElement();
            // Make sure it's a complete name
            loc = pageInfo.getFileURL(loc);
            // If it's an HTML file
            if(isHTMLFile(loc)) {
                URL url = URLUtils.normalizeAddress(loc);
                if(url != null) {
                	PageInfo newPage = new PageInfo(url, pageInfo.depth + 1);
                	downloadHTMLPage(newPage);
                }
            }
            // Else get file
            else {
                FileInfo newFile = new FileInfo(loc, pageInfo.depth);
                downloadFile(newFile);
            }
            if(monitor.isCanceled()) return;
        }
    }


    /**
    * Download an HTML page
    */
    private void downloadHTMLPage(PageInfo pageInfo) {
        String f, fileName;
        FileOutputStream out = null;

        // Make a nice file name
        if(pageInfo.urlFull.equals(topURL.toString())) f = "index.html";
        else {
            f = getValidFileName(pageInfo.urlFull);
            if(!isHTMLFile(f)) f += ".html";
        }

        fileName = saveFolder + f;

        // Store local filename
        pageInfo.localFileName = fileName;

        // Have we already saved it?
        if(DataFiler.fileExists(fileName)) return;

        try {
            URLConnection conn = URLUtils.getURLConnection(pageInfo.urlFull);
            if(conn == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // This goes AFTER conn.getInputStream()
            int length = conn.getContentLength();
            monitor.init(LanguageManager.getString("15_1") + " (" +
                LanguageManager.getString("15_6") + " " + pageInfo.depth + ")", pageInfo.urlFull, length);

            if(ColloquiaConstants.DEBUG) System.out.println("Sucking " + pageInfo.urlFull + "....");

            // Read in the page as a stream and save to file
            int i;
            out = new FileOutputStream(fileName);
            while((i = reader.read()) != -1) {
                if(monitor.isCanceled()) {
                    out.flush();
                    out.close();
                    reader.close();
                    return;
                }
                out.write(i);
                monitor.incProgress(1, false);
            }

            out.flush();
            out.close();
            reader.close();
        }
        catch(Exception ex) {
            if(ColloquiaConstants.DEBUG) {
                System.out.println("**** Could not download page: " + pageInfo.urlFull + " ****");
                System.out.println("Exception: " + ex);
            }
            ok = false;
            try {
                if(out != null) out.close();
            }
            catch (Exception ex1) {}
            return;
        }

        //if(Constants.DEBUG) System.out.println("sucked!");

        // Now sort out the page refs
        getPageRefs(pageInfo);
        downloadPageRefs(pageInfo);
        changePageRefs(pageInfo);

        // Add to our big list
        files.addElement(f);
    }


    /**
    * Download a binary file
    */
    private void downloadFile(FileInfo fileInfo) {
        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        String url = fileInfo.location;

        // Make a nice save file name
        String fileSave = getValidFileName(url);

        // If the file is an anchor ref remove the anchor
        // Do this here so that the ref remains
        int index = fileSave.lastIndexOf('#');
        if(index != -1) fileSave = fileSave.substring(0, index);

        // If we already have the file, don't download
        if(files.contains(fileSave)) return;

        // Store in the scheme of things
        files.addElement(fileSave);

        fileSave = saveFolder + fileSave;

        try {
            // Connect to file
            URLConnection conn = URLUtils.getURLConnection(url);
            if(conn == null) return;

            in = new BufferedInputStream(conn.getInputStream(), bufSize);
            out = new BufferedOutputStream(new FileOutputStream(fileSave), bufSize);

            // This goes AFTER conn.getInputStream()
            int length = conn.getContentLength();

            monitor.init(LanguageManager.getString("15_1") + " (" + LanguageManager.getString("15_6")
                + " " + fileInfo.depth + ")", url, length);

            if(ColloquiaConstants.DEBUG) System.out.println("Sucking " + url + "....");

            // Read/Write bytes
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                monitor.incProgress(bytesRead, false);
            }

            out.flush();
            out.close();
            in.close();
        }
        catch(IOException ex) {
            if(ColloquiaConstants.DEBUG) {
                System.out.println("**** Could not download file: " + url + " ****");
                System.out.println("Exception: " + ex);
            }
            ok = false;
            try {
                if(out != null) out.close();
                if(in != null) in.close();
                //DataFiler.deleteFile(fileSave);
            }
            catch(IOException ex1) {  }
        }
    }


    /**
    * Parse the html file and change the original file refs to our new file refs
    */
    private void changePageRefs(PageInfo pageInfo) {
        String inText = "";
        String orgFile = "";
        String localFile;
        String temp;
        Enumeration keys;
        int index;
        BufferedReader in = null;
        BufferedWriter out = null;

        String filePage = pageInfo.localFileName;

        try {
            //if(Constants.DEBUG) System.out.println("Changing file refs...");
            monitor.setNote(LanguageManager.getString("15_7"));

            // Create a temp file
            String tempFile = filePage + "$";

            // In & Out Writers
            in = new BufferedReader(new FileReader(filePage));
            if(in == null) return;
            out = new BufferedWriter(new FileWriter(tempFile));
            if(out == null) return;

            // Read in each line of the html file
            while((inText = in.readLine()) != null) {
                // Go through the fileRefs table
                keys = pageInfo.getRefs().keys();
                while(keys.hasMoreElements()) {
                    // Original file name
                    orgFile = (String)keys.nextElement();
                    // Local file name
                    localFile = (String)pageInfo.getRefs().get(orgFile);
                    // Temp string
                    temp = "";

                    while(true) {
                        // Is original ref name in this line/part-line?
                        index = indexOf(inText, orgFile);
                        // No, bail out
                        if(index == -1) {
                            inText = temp + inText;
                            break;
                        }

                        // Yes, store this part of the line
                        temp += inText.substring(0, index) + localFile;
                        // and get the next part of the line
                        inText = inText.substring(index + orgFile.length());

                        // Did the user press cancel?
                        if(monitor.isCanceled()) break;
                    }
                }

                // Write the line to file
                out.write(inText);
                out.newLine();
            }

            // Flush the files
            out.flush();
            out.close();
            in.close();

            // Delete old file & rename temp file to old file
            File f1 = new File(filePage);
            f1.delete();
            File f2 = new File(tempFile);
            f2.renameTo(f1);

            //if(Constants.DEBUG) System.out.println("done");

        } catch(Exception e) {
            if(ColloquiaConstants.DEBUG) {
                System.out.println("**** Could not change file refs on page: " + pageInfo.urlFull + " ****");
                System.out.println("Line: " + inText);
                System.out.println("Ref: " + orgFile);
                System.out.println("Exception: " + e);
            }
            ok = false;
            try {
                if(in != null) in.close();
                if(out != null) out.close();
            }
            catch(Exception e1) { }
        }
    }


    /**
    * Searches line for the first occurrence of ref checking that it
    * is a whole String, not a sub-string.
    * Returns position of start of text if found or -1 if not found
    */
    private int indexOf(String line, String ref) {
        char startChar, endChar;

        // Find first occurrence
        int index = line.indexOf(ref);
        // Definitely not here!
        if(index == -1) return -1;

        // Now check that we have the full thing by ensuring that the ref is
        // surrounded by legal characters
        if(index == 0) startChar = line.charAt(0);
        else startChar = line.charAt(index - 1);

        endChar = line.charAt(index + ref.length());

        if(isSeparator(startChar) && isSeparator(endChar)) return index;
        else return -1;
    }

    /**
    * Returns true if c is a separating character for a html ref
    */
    private boolean isSeparator(char c) {
        return c == ' ' || c == '\"' || c == '=' || c == '>' ||  c == '<' ||
            c == '\'' || c == '\t' || c == '\n';
    }


    /**
    * Information on a page and its refs and our new refs
    */
    private class PageInfo {
        public URL url;
        public String urlFull = "";         // the url html page to go to       http://www.mypage.com/food/z.html
        public String urlRoot = "";         // the url location root            http://www.mypage.com/
        public String urlFolder = "";       // the url location folder (if any) food/
        public String htmlFile = "";        // the url html page to go to       z.html
        public int depth;                   // The search depth

        public PageInfo(URL url, int depth) {
            if(url == null) {
                if(ColloquiaConstants.DEBUG) {
                    System.out.println("null url in PageInfo");
                }
                return;
            }

            // Depth
            this.depth = depth;

            // Full url
            urlFull = url.toString();

            // Get Root
            urlRoot = url.getProtocol() + "://" + url.getHost() + "/";

            // Get end reference
            String getFile = url.getFile();

            // If no end reference then no folder(s) or html ref
            if(getFile.equals("/")) {
                urlFolder = "";
                htmlFile = "";
            }

            // There is an end reference
            else {
                // Locate the last '/'
                int index = getFile.lastIndexOf("/");
                if(index != -1) {
                    // Get folder(s)
                    urlFolder = getFile.substring(0, index + 1);
                    // Strip off leading '/'
                    while(urlFolder.startsWith("/")) urlFolder = urlFolder.substring(1);
                    // Get html file
                    htmlFile = getFile.substring(index + 1);
                }
            }
        }

        // Local File Name
        public String localFileName = "";

        /**
        * Return a full URL for a file on this page
        */
        public String getFileURL(String file) {
            // If file starts with 'http' then use that as a file ref
            if(file.startsWith("http")) return file;
            // Else if file starts with '/' then we go off the root url
            else if(file.startsWith("/")) {
                // remove any leading '/'
                while(file.startsWith("/")) file = file.substring(1);
                return urlRoot + file;
            }
            // Else use the full url + relative folder url + file
            else return urlRoot + urlFolder + file;
        }

        // References
        private Hashtable refs = new Hashtable();

        public void addRef(String ref) {
            // Don't add 'mailto:' or 'ftp:' or 'news:' refs
            if(isMailToRef(ref) || isFTPRef(ref) || isNewsRef(ref)) return;
            // Else put ref as it is in the page against our fileName
            String localName = getValidFileName(getFileURL(ref));
            refs.put(ref, localName);
        }

        public Hashtable getRefs() {
            return refs;
        }
    }


    /**
    * Information on a file that has been downloaded
    */
    private class FileInfo {
        public String location;
        public int depth;

        public FileInfo(String location, int depth) {
            this.location = location;
            this.depth = depth;
        }

    }


    /**
    * Makes a valid file name from a URL
    * An example page might be mypage.html?647747 which does not work as a file name
    * so we strip off any trailing/leading stuff
    * Returns the modified fileName
    */
    private String getValidFileName(String fileName) {
        fileName = fileName.toLowerCase();

        // Curtail any trailing Question marks
        int index = fileName.indexOf("?");
        if(index != -1) fileName = fileName.substring(0, index);

        // Get rid of leading http:
        index = fileName.indexOf("http:");
        if(index != -1) fileName = fileName.substring(index + 5);

        // Get rid of leading file:
        index = fileName.indexOf("file:");
        if(index != -1) fileName = fileName.substring(index + 5);

        // Get rid of trailing '/'
        while(fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length() - 1);

        // Get rid of leading '/'
        while(fileName.startsWith("/")) fileName = fileName.substring(1);

        // Replace '/' with '.'
        fileName = fileName.replace('/', '.');

        // Replace ':' with '~'
        fileName = fileName.replace(':', '~');

        // Replace '%' with "~"
        fileName = fileName.replace('%', '~');

        // Replace ',' with '~'
        fileName = fileName.replace(',', '~');

        // .com is no good for PCs
        if(fileName.endsWith(".com")) fileName += ".html";

        return fileName;
    }


    /**
    * Returns true if file ends with html or htm
    */
    private boolean isHTMLFile(String file) {
        if(file == null) return false;
        return file.toLowerCase().endsWith(".htm") || file.toLowerCase().endsWith(".html");
    }

    /**
    * Returns true if the ref is a mailto: ref
    */
    private boolean isMailToRef(String ref) {
        if(ref == null) return false;
        return ref.toLowerCase().startsWith("mailto:");
    }

    /**
    * Returns true if the ref is a ftp: ref
    */
    private boolean isFTPRef(String ref) {
        if(ref == null) return false;
        return ref.toLowerCase().startsWith("ftp:");
    }

    /**
    * Returns true if the ref is a news: ref
    */
    private boolean isNewsRef(String ref) {
        if(ref == null) return false;
        return ref.toLowerCase().startsWith("news:");
    }

    /**
    * Returns true if the file is an image file
    */
    private boolean isImageFile(String file) {
        if(file == null) return false;
        return file.toLowerCase().endsWith(".gif") || file.toLowerCase().endsWith(".jpg");
    }

    /**
    * Go thru an HTML page and parse all the links
    */
    private void getPageRefs(PageInfo pageInfo) {
        String[] tag = {"SRC", "BACKGROUND", "HREF"};
        String inText = "";
        int i, p = 0, index, start, end;
        boolean eol; // End of Line

        try {
            BufferedReader in = new BufferedReader(new FileReader(pageInfo.localFileName));
            if(in == null) return;

            // Read in each line of the html file
            while((inText = in.readLine()) != null) {

                for(p = 0; p < tag.length; p++) {

                    // If tag = HREF only get if depth is OK
                    if(tag[p].equals("HREF") && pageInfo.depth >= depthMax) continue;

                    index = 0;
                    eol = false;
                    while(index < inText.length()) {
                        // Is tag in this line? Both upper and lower case
                        i = inText.indexOf(tag[p], index);
                        if(i == -1) i = inText.indexOf(tag[p].toLowerCase(), index);
                        if(i == -1) break; // Bail out
                        else index = i;

                        index = index + tag[p].length();

                        // Check for a '='
                        while(inText.charAt(index) != '=') {
                            index++;
                            if(index >= inText.length()) {
                                eol = true;
                                break;
                            }
                        }
                        if(eol) break;

                        // Move index on until a start character is found
                        while(isSeparator(inText.charAt(index))) {
                            index++;
                            if(index >= inText.length()) {
                                eol = true;
                                break;
                            }
                        }
                        if(eol) break;
                        start = index;

                        // Move on until the next separator char is found
                        while(!isSeparator(inText.charAt(index))) {
                            index ++;
                            if(index >= inText.length()) {
                                eol = true;
                                break;
                            }
                        }
                        if(eol) break;
                        end = index;

                        pageInfo.addRef(inText.substring(start, end));

                        // Did the user press cancel?
                        if(monitor.isCanceled()) break;
                    }
                } // for
            } // while

            // Close
            in.close();
        } catch(Exception e) {
            if(ColloquiaConstants.DEBUG) {
                System.out.println("**** Could not get tag: " + tag[p] + " ****");
                System.out.println("File: " + pageInfo.localFileName);
                System.out.println("Line: " + inText);
                System.out.println("Exception: " + e);
            }
        }
    }


    /**
    * OLD METHOD - NOT USED
    * Go thru an HTML page and parse all the links
    */
    private void ________getPageRefs(PageInfo pageInfo) {
        Element elem;
        Object attr;
        AttributeSet attSet;
        SimpleAttributeSet simpleAttSet;

        // Stuff the page into an HTMLEditorKit document
        EditorKit kit = new HTMLEditorKit();
        Document doc = kit.createDefaultDocument();
        // The Document class does not yet handle charsets properly.
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

        try {
            // Read the page into the HTMLDocument
            BufferedReader in = new BufferedReader(new FileReader(pageInfo.localFileName));
            if(in == null) return;
            kit.read(in, doc, 0);
            in.close();

            // Iterate through the elements of the HTML document
            ElementIterator it = new ElementIterator(doc);
            while((elem = it.next()) != null) {
                attSet = elem.getAttributes();

                // Get any SRC elements
                attr = attSet.getAttribute(HTML.Attribute.SRC);
                if(attr != null) pageInfo.addRef(attr.toString());

                // Get any HREF elements if depth is OK
                if(pageInfo.depth < depthMax) {
                    simpleAttSet = (SimpleAttributeSet)attSet.getAttribute(HTML.Tag.A);
                    if(simpleAttSet != null) {
                        attr = simpleAttSet.getAttribute(HTML.Attribute.HREF);
                        if(attr != null) pageInfo.addRef(attr.toString());
                    }
                }

                // Background
                attr = attSet.getAttribute(HTML.Attribute.BACKGROUND);
                if(attr != null) pageInfo.addRef(attr.toString());

            } // while
        } catch(Exception ex) {
            System.out.println("Could not get files on page: " + ex);
        }
    }

}




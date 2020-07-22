package net.colloquia.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;



public class Backup {

    // ---------------------------------------------------------------------- \\
    // --------------------------- BACKUP ALL DATA -------------------------- \\
    // ---------------------------------------------------------------------- \\
    public static final String MAGIC_ENTRY = "Toomol Magic Backup";

    public static void backupData() throws ColloquiaFileException {
        // Create a fileName based on the current time
        String fileName;
        int index = 0;

        //Date now = Utils.getNow();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        Vector v = getUserFiles();

        do {
            index++;
            fileName = DataFiler.getBackupFolder() + year + "-" + month +
                "-" + day + "_" + "bak" + index + ".cqb";
        } while(DataFiler.fileExists(fileName));

        PMonitor monitor= new PMonitor(MainFrame.getInstance(),
                                        LanguageManager.getString("ACTION_25"),
                                        LanguageManager.getString("ACTION_24"),
                                        " ");
        monitor.init(v.size());

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
            ZipOutputStream zOut = new ZipOutputStream(out);
            // Magic String
            addStringToZip(MAGIC_ENTRY, MAGIC_ENTRY, zOut);
            // Files
            for(int i = 0; i < v.size(); i++) {
                String fName = (String)v.elementAt(i);
                addFileToZip(fName, zOut);
                if(monitor.isCanceled()) break;
                monitor.incProgress(1, true);
            }
            zOut.flush();
            zOut.close();
        }
        catch(Exception ex) {
            monitor.close();
            throw new ColloquiaFileException("Zip Error in Backup", ex.getMessage());
        }

        monitor.close();
    }

    private static void addFileToZip(String fileName, ZipOutputStream zOut) throws IOException, FileNotFoundException {
        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];
        String entryName;

        File file = new File(fileName);

        // Strip off leading user folder
        int i = fileName.indexOf(DataFiler.getDataFolder());
        if(i == -1) entryName = fileName;
        else entryName = fileName.substring(DataFiler.getDataFolder().length());
        ZipEntry zipEntry = new ZipEntry(entryName);
        // Set time stamp to file's
        zipEntry.setTime(file.lastModified());
        zOut.putNextEntry(zipEntry);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), bufSize);
        while((bytesRead = in.read(buf)) != -1) {
            zOut.write(buf, 0, bytesRead);
        }
        zOut.closeEntry();
    }

    /**
    * Adds a String to a Zip file
    */
    public static void addStringToZip(String text, String entryName, ZipOutputStream zOut) throws IOException {
        int i;
        BufferedReader reader = new BufferedReader(new StringReader(text));
        ZipEntry zipEntry = new ZipEntry(entryName);
        zOut.putNextEntry(zipEntry);
        while((i = reader.read()) != -1) {
            zOut.write(i);
        }
        zOut.closeEntry();
    }

    /**
    * This gathers all user files with full path names into a Vector v
    * This allows us to count them as well
    */
    private static Vector getUserFiles() {
        Vector v = new Vector();

        // People
        File folder = new File(DataFiler.getPeopleRootFolder(false));
        addFiles(folder, v);
        // Resources
        folder = new File(DataFiler.getResourceRootFolder(false));
        addFiles(folder, v);
        // Resource Groups
        folder = new File(DataFiler.getResourceGroupFolder(false));
        addFiles(folder, v);
        // Assignments
        folder = new File(DataFiler.getAssignmentsRootFolder(false));
        addFiles(folder, v);
        // Activities
        folder = new File(DataFiler.getActivityRootFolder(false));
        addFiles(folder, v);
        // Tree
        folder = new File(DataFiler.getTreeFolder(false));
        addFiles(folder, v);
        // Attachments
        folder = new File(DataFiler.getAttachmentsFolder(false));
        addFiles(folder, v);
        // Mail
        folder = new File(DataFiler.getMailFolder(false));
        addFiles(folder, v);
        // Invites
        folder = new File(DataFiler.getInviteRootFolder(false));
        addFiles(folder, v);

        return v;
    }

    private static void addFiles(File folder, Vector v) {
        if(folder == null || !folder.exists()) return;

        // Don't back up deleted stuff
        if(folder.getPath().endsWith("!")) return;

        String fileName = DataFiler.addFileSepChar(folder.getPath());
        String[] files = folder.list();
        for(int i = 0; i < files.length; i++) {
            String name = fileName + files[i];
            File f = new File(name);
            if(f.isDirectory()) addFiles(f, v);
            else v.addElement(name);
        }
    }
}






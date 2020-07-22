package net.colloquia.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

public class Restore {

    // ---------------------------------------------------------------------- \\
    // --------------------------- RESTORE ALL DATA -------------------------- \\
    // ---------------------------------------------------------------------- \\

    public static boolean restoreData() {
        JFileChooser chooser = new JFileChooser(DataFiler.getBackupFolder());
        chooser.setDialogTitle(LanguageManager.getString("ACTION_27"));
        ColloquiaFileFilter filter = new ColloquiaFileFilter();
        filter.addExtension("cqb");
        filter.setDescription(LanguageManager.getString("ACTION_29"));
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());
        if(returnVal != JFileChooser.APPROVE_OPTION) return false;
        String zipfileName = chooser.getSelectedFile().getPath();
        boolean success = true;

        // Check it's valid
        BackupInfo bInfo = getBackupInfo(zipfileName);

        if(bInfo == null) {
            ErrorHandler.showWarning("ACTION_28", null, "ACTION_27");
            return false;
        }

        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            LanguageManager.getString("ACTION_32") + "      " + bInfo.backupTime  + "\n" +
            LanguageManager.getString("ACTION_33") + " " + bInfo.fileLength  + "\n\n" +
            LanguageManager.getString("ACTION_31") + "\n\n" +
                LanguageManager.getString("ARE_YOU_SURE"),
            LanguageManager.getString("ACTION_27"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return false;

        PMonitor monitor= new PMonitor(MainFrame.getInstance(),
                                        LanguageManager.getString("ACTION_27"),
                                        LanguageManager.getString("ACTION_34"),
                                        " ");
        monitor.init(bInfo.numEntries);

        ZipEntry zipEntry;
        int bytesRead;
        final int bufSize = 16000;
        byte buf[] = new byte[bufSize];

        try {
            String userFolderName = DataFiler.getDataFolder();
            File userFolder = new File(userFolderName);

            // Wipe - NO!!
            // deleteUserFiles(userFolderName);

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(zipfileName), bufSize);
            ZipInputStream zIn = new ZipInputStream(in);

            while((zipEntry = zIn.getNextEntry()) != null) {
                monitor.incProgress(1, true);

                String zipEntryName = zipEntry.getName();

                // Not the Magic Entry
                if(zipEntryName.equalsIgnoreCase(Backup.MAGIC_ENTRY)) {
                    zIn.closeEntry();
                    continue;
                }

                String fullName = userFolderName + zipEntryName;
                File outFile = new File(fullName);

                // If the file exists, can we write to it?
                // A read only file will throw an exception
                if(outFile.exists() && !outFile.canWrite()) {
                	zIn.closeEntry();
                    System.out.println("Did not write to read-only file: " + fullName);
                    continue;
                }

                // Create folder if not existing
                String name = outFile.getName();
                String folder = fullName.substring(0, fullName.indexOf(name));
                DataFiler.checkFolder(folder);

                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile), bufSize);

                while((bytesRead = zIn.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }

                out.flush();
                out.close();
                zIn.closeEntry();

                // Restore Time Stamp
	            outFile.setLastModified(zipEntry.getTime());

				// User pressed cancel
                if(monitor.isCanceled()) {
                    success = false;
                	break;
                }
            }

            zIn.close();
            monitor.close();
        }
        catch(Exception ex) {
            monitor.close();
            System.out.println("Invalid backup: " + ex);
            ErrorHandler.showWarning("ACTION_28", null, "ACTION_27");
            return false;
        }

        // And reload
        DataFiler.reloadData();
        return success;
    }

    /**
    * Validates that it is a Colloquia Magic File
    * Returns details of zip file
    * Null if anvalid
    */
    private static BackupInfo getBackupInfo(String fileName) {
        ZipEntry zipEntry;
        BackupInfo bInfo = null;
        int numEntries = 0;

        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);

        try {
            File file = new File(fileName);
            if(!file.exists()) return null;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            ZipInputStream zIn = new ZipInputStream(in);

            while((zipEntry = zIn.getNextEntry()) != null) {
                numEntries++;
                String zipEntryName = zipEntry.getName();
                if(zipEntryName.equalsIgnoreCase(Backup.MAGIC_ENTRY)) {
                    bInfo = new BackupInfo(file, zipEntry);
                }
                zIn.closeEntry();
            }

            zIn.close();
        }
        catch(Exception ex) {
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
            System.out.println("Error on Zip: " + ex);
            return null;
        }

        if(bInfo != null) bInfo.numEntries = numEntries;

        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        return bInfo;
    }

    private static class BackupInfo {
        String fileLength = "";
        String backupTime;
        int numEntries = 0;

        public BackupInfo(File file, ZipEntry magicEntry) {
            fileLength += (file.length() / 1024) + "KB";
            backupTime = Utils.parseDate(new Date(magicEntry.getTime()));
        }
    }

}




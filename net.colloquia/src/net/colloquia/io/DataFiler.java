package net.colloquia.io;

import java.io.*;
import java.util.*;

import net.colloquia.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.views.tree.*;
import net.colloquia.xml.*;


/**
 * Filing functionality for notes, description files, etc.
 * Also includes methods for obtaining certain information from files.
 */
public class DataFiler {
    // File path separator
    public static char fileSepChar = File.separatorChar;

    // The fileName for the main data
    public static final String dataFileName = "data.tml";
    public static final String dataFileName2 = "colloquia.xml";
    // The fileName for the tree state info
    public static final String treeStateFileName = "data.tree";

    // Main data folders
    public static final String peopleFolder = "people";
    public static final String resourcesFolder = "resources";
    public static final String assignmentsFolder = "assignments";
    public static final String activitiesFolder = "las";
    public static final String treeFolder = "tree";
    public static final String tempFolder = "temp";
    public static final String webPageFolder = "web-pages";
    public static final String attachmentsFolder = "attachments";
    public static final String mailFolder = "mail";
    public static final String pendingFolder = "pending";
    public static final String backupFolder = "backup";
    public static final String inviteFolder = "invites";
    public static final String resourceGroupFolder = "resource-groups";


    /**
    * Load the Tree Data
    */
    public static void loadTreeData(String fileName) {
        File file = new File(fileName);

        // File not found so default to new tree and return
        if(!file.exists()) {
            DataModel.newData();
            return;
        }

        /*
        // If version 1.2 file use old version else use new version
        String version = "1.3";
        String dataType = XMLUtils.getXMLKey("datatype", fileName);
        if(dataType != null && dataType.equalsIgnoreCase("MAINDATA")) {
	        // Get Version
    	    String ver = XMLUtils.getXMLKey("version", fileName);
        	if(ver != null && ver.equalsIgnoreCase("1.2")) version = "1.2";
        }
        */

        Open.open(file);
        //Open2.open(file);
    }

    /**
    * Reload everything given a new Data Folder
    * PENDING - this should be a listener event
    */
    public static void reloadData() {
        // Load Tree Data
        loadTreeData(getDataFileName(false));
        // Message Centre
        MessageWindow.getInstance().reloadMessages();
    }

    /**
    * Save the tree file and all associated data files.
    */
    public static synchronized void saveAll() throws ColloquiaFileException {
		//System.out.println("saving");
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);

        // Save Tree State Info
        try {
            TreeState.save();
        }
        catch(XMLWriteException ex) {
            // Not so serious
            System.out.println("Could not save tree state: " + ex);
        }

        // Save DataModel
        try {
            DataModel.save();
            // Too soon for this version!
            //DataModel.save2();
        }
        catch(Exception ex) {
            throw new ColloquiaFileException("Could not save data", ex.getMessage());
        }
        finally {
            // Save UserPrefs (so we can save the window position)
            UserPrefs.saveUserPrefs(UserPrefs.getUserPrefs());
            fireSaveHappened();
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        }
    }

    // Text types
    public static final int PERSONAL_NOTES = 0;   // Personal Notes
    public static final int DESCRIPTION = 1;      // Description
    public static final int OBJECTIVES = 2;       // Objectives

    public static final String[] textFileNames = {
        "notes.html",
        "description.html",
        "objs.html"
    };

    /**
    * Constructs a suitable filename for the Texts of an entity
    * type is PERSONAL_NOTES, DECRIPTION, or OBJECTIVES
    */
    public static String getTextFileName(ColloquiaComponent tc, int type, boolean checkFolder) {
        if(tc == null) {
            return "";
        }
        String folder = getComponentFolder(tc, checkFolder);
        return folder + textFileNames[type];
    }

    /**
    * Get Invite html text file
    * type is PERSONAL_NOTES, DECRIPTION, or OBJECTIVES
    */
    public static String getInviteTextFileName(String activityGUID, String GUID, int type, boolean checkFolder) {
        return getInviteFolder(activityGUID, checkFolder) + GUID + "." + type;
    }

    /**
    * Get Invite xml file
    */
    public static File getInviteFile(String activityGUID, boolean checkFolder) {
        return new File(getInviteFolder(activityGUID, checkFolder) + "invite.cqi");
    }

    /**
    * Returns data folder for a component
    */
    public static String getComponentFolder(ColloquiaComponent tc, boolean checkFolder) {
        String GUID = tc.getGUID();
        String folder = "";

        switch(tc.getType()) {
            case ColloquiaComponent.ACTIVITY:
                if(((Activity)tc).isInvite()) folder = getInviteFolder(GUID, checkFolder);
                else folder = getActivityFolder(GUID, checkFolder);
                break;
            case ColloquiaComponent.PERSON:
                folder = getPersonFolder(GUID, checkFolder);
                break;
            case ColloquiaComponent.RESOURCE:
                folder = getResourceFolder(GUID, checkFolder);
                break;
            case ColloquiaComponent.ASSIGNMENT:
                folder = getAssignmentsFolder(GUID, checkFolder);
                break;
            case ColloquiaComponent.RESOURCE_GROUP:
                folder = getResourceGroupFolder(GUID, checkFolder);
                break;
        }

        return folder;
    }


    /*
    * If delete/cut a component rename its data folder with a ! on the end
    */
    public static void cutFolder(ColloquiaComponent tc) {
        if(tc.getInstanceCount() <= 1) {
            File oldFolder = new File(getComponentFolder(tc, false));
            File newFolder = new File(oldFolder.getPath() + "!");
            oldFolder.renameTo(newFolder);
            if(tc instanceof ColloquiaContainer) {
                Vector members = ((ColloquiaContainer)tc).getMembers();
                for(int i = 0 ; i < members.size(); i++) {
                    ColloquiaComponent child = (ColloquiaComponent)members.elementAt(i);
                    cutFolder(child);
                }
            }
        }
    }

    /*
    * If undo/paste a component rename its data folder back again
    */
    public static void uncutFolder(ColloquiaComponent tc) {
        File oldFolder = new File(getComponentFolder(tc, false));
        File newFolder = new File(oldFolder.getPath() + "!");
        if(newFolder.exists()) newFolder.renameTo(oldFolder);
        if(tc instanceof ColloquiaContainer) {
            Vector members = ((ColloquiaContainer)tc).getMembers();
            for(int i = 0 ; i < members.size(); i++) {
                ColloquiaComponent child = (ColloquiaComponent)members.elementAt(i);
                uncutFolder(child);
            }
        }
    }

    //==========================================================================
    //==========================================================================
    //==========================================================================


    /**
    * Delete all user files
    */
    public static void deleteUserFiles(String dataFolder) {
        dataFolder = addFileSepChar(dataFolder);
        // Delete individual folders - NOT THE TOP LEVEL DATA FOLDER -
        // it might be c:\ or something!
        deleteFolder(dataFolder + assignmentsFolder);
        deleteFolder(dataFolder + activitiesFolder);
        deleteFolder(dataFolder + peopleFolder);
        deleteFolder(dataFolder + resourcesFolder);
        deleteFolder(dataFolder + tempFolder);
        deleteFolder(dataFolder + treeFolder);
        deleteFolder(dataFolder + mailFolder);
        deleteFolder(dataFolder + webPageFolder);
        deleteFolder(dataFolder + resourceGroupFolder);
        deleteFolder(dataFolder + inviteFolder);
        // Then deleteFile on top dataFolder (NOT deleteFolder in case it was the root!)
        if(isFolderEmpty(dataFolder)) deleteFile(dataFolder);
    }

    /**
    * Goes thru the folders and deletes any folders with ! on the end
    */
    public static void cleanFolders() {
        // ASSIGNMENTS FOLDER
        cleanFolder(getAssignmentsRootFolder(false));
        // RESOURCES
        cleanFolder(getResourceRootFolder(false));
        // PEOPLE
        cleanFolder(getPeopleRootFolder(false));
        // ACTIVITIES
        cleanFolder(getActivityRootFolder(false));
        // INVITES
        cleanFolder(getInviteRootFolder(false));
        // RESOURCE GROUPS
        cleanFolder(getResourceGroupFolder(false));
    }

    /**
    * Goes thru the folders and deletes any stuff that that doesn't relate to the
    * loaded data
    */
    private static void cleanFolder(String folderName) {
        // List sub-folders
        File folder = new File(folderName);
        if(!folder.exists()) return;

        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i++) {
            File f = files[i];
            if(f.getPath().endsWith("!")) deleteFolder(f.getPath());
        }
    }

    /**
    * Delete all empty folders
    */
    public static void deleteEmptyFolders(String folder) {
        folder = addFileSepChar(folder);
        File fileFolder = new File(folder);
        if(!fileFolder.exists() || !fileFolder.isDirectory()) return;

        String[] files = fileFolder.list();
        for(int i = 0; i < files.length; i++) {
            deleteEmptyFolders(folder + files[i]);
        }

        // Now check again
        files = fileFolder.list();
        if(files.length == 0) fileFolder.delete();
    }

    /**
    * Delete all files/sub-folders in a folder and the folder itself
    * THIS IS VERY DANGEROUS - BE CAREFUL!!!
    */
    public static void deleteFolder(String folder) {
        folder = addFileSepChar(folder);
        File fileFolder = new File(folder);

        if(fileFolder.exists() && fileFolder.isDirectory()) {
            String[] files = fileFolder.list();
            for(int i = 0; i < files.length; i++) {
                File delFile = new File(folder + files[i]);
                if(delFile.isDirectory()) deleteFolder(folder + files[i]);
                else delFile.delete();
            }

            fileFolder.delete();
        }
    }


    /**
    * Copies a file
    */
    public static boolean copyFile(File srcFile, File destFile) throws IOException {
        if(srcFile == null || destFile == null) return false;
        if(!srcFile.exists()) return false;
        int bufSize = 1024;
        byte[] buf = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile), bufSize);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile), bufSize);
        int size;
        while((size = bis.read(buf)) != -1) {
            bos.write(buf, 0, size);
        }
        bos.flush();
        bos.close();
        bis.close();
        return true;
    }

    public static boolean copyFile(String srcFile, String destFile) throws IOException {
        if(srcFile == null || destFile == null) return false;
    	return copyFile(new File(srcFile), new File(destFile));
    }

    public static boolean moveFile(String srcFile, String destFolder) throws IOException {
        if(srcFile == null || destFolder == null) return false;
        File src = new File(srcFile);
        if(src == null) return false;
        File dest = new File(addFileSepChar(destFolder) + src.getName());
    	boolean result = copyFile(src, dest);
        if(result) src.delete();
        return result;
    }


    /**
    Delete a file on the Disk
    */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if(file != null) file.delete();
    }

    /**
    @returns true if the file exists
    */
    public static boolean fileExists(String fileName) {
        if(fileName == null) return false;
        File file = new File(fileName);
        return file.exists();
    }

    /**
    *
    */
    public static boolean isFolderEmpty(String folder) {
        File file = new File(folder);
        if(!file.exists()) return true;
        if(!file.isDirectory()) return true;
        String[] contents = file.list();
        if(contents == null) return true;
        return contents.length == 0;
    }

    /**
    * Checks for a valid filename
    * The file SHOULD NOT EXIST!! BECAUSE OF TEMP FILE & DELETE
    */
    public static boolean isValidFileName(String fileName) {
        File file = new File(fileName);
        if(file.exists()) return true;

        try {
            FileWriter out = new FileWriter(file);
            out.close();
            file.delete();
        } catch(Exception ex) {
            return false;
        }

        return true;
    }

    /**
    * Add a file separator char to the end of a file name/folder if it hasn't got one
    */
    public static String addFileSepChar(String file) {
        if(file == null || file.equals("")) file = "" + File.separatorChar;
        else if(file.lastIndexOf(File.separatorChar) != file.length() - 1)
            file += File.separatorChar;
        return file;
    }

    /**
    * Remove a file separator char at the end of a file name/folder
    */
    public static String removeFileSepChar(String file) {
        if(file == null) file = "";
        if(file.lastIndexOf(File.separatorChar) == file.length() - 1)
            file = file.substring(0, file.length() - 1);
        return file;
    }

    public static void renameFile(String oldFileName, String newFileName) {
        File oldFile = new File(oldFileName);
        File newFile = new File(newFileName);
        if(oldFile.exists() && !newFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }

    // ---------------------------------------------------------------------- \\
    // ---------------------- FOLDERS / DATA LOCATIONS ---------------------- \\
    // ---------------------------------------------------------------------- \\

    private static String userHome;

    // User's home dir
    public static String getUserHome() {
        if(userHome == null) {
            userHome = System.getProperty("user.home");
        }
        return addFileSepChar(userHome);
    }

    public static void setUserHome(String set) {
        userHome = set;
    }

    // Colloquia folder
    public static String getColloquiaFolder() {
        String folder = getUserHome() + "Colloquia" +  fileSepChar;
        checkFolder(folder);
        return folder;
    }


    // User's Colloquia Data Folder as stored in UserPrefs
    // Check for valid data folder & backward compatibility
    // Returns correct folder
    public static String getDataFolder() {
        UserPrefs prefs = UserPrefs.getUserPrefs();

        String folder = prefs.getProperty(UserPrefs.DATA_ROOT);

        // Not set
        if(folder == null || folder.length() == 0) {
            folder = getDefaultDataFolder(prefs.getUserName());
            prefs.putProperty(UserPrefs.DATA_ROOT, folder);
        }

        // Something there - check for old ll_data folder
        else {
            String oldStyleFolder = addFileSepChar(folder) + "ll_data" + fileSepChar;
            File check = new File(oldStyleFolder);
            if(check.exists() && check.isDirectory()) {
                folder = oldStyleFolder;
                prefs.putProperty(UserPrefs.DATA_ROOT, folder);
            }
        }

        // Check it's there
        checkFolder(folder);
        return addFileSepChar(folder);
    }

    public static String getDefaultDataFolder(String userName) {
        return getColloquiaFolder() + userName + ".cqd" + fileSepChar;
    }

    public static String getBackupFolder() {
        UserPrefs prefs = UserPrefs.getUserPrefs();

        String folder = prefs.getProperty(UserPrefs.BACKUP_FOLDER);

        // Not set
        if(folder == null || folder.length() == 0) {
            folder = getDefaultBackupFolder(prefs.getUserName());
            prefs.putProperty(UserPrefs.BACKUP_FOLDER, folder);
        }

        // Check it's there
        checkFolder(folder);
        return addFileSepChar(folder);
    }

    public static String getDefaultBackupFolder(String userName) {
        String folder = getColloquiaFolder() + backupFolder + fileSepChar + userName + fileSepChar;
        checkFolder(folder);
        return folder;
    }

    // ======================== USER PREFS =====================================

    public static String getUserPrefsFileName(String userName) {
        return getColloquiaFolder() + userName + ".cqp";
    }


    /*
    * Delete's a user profile
    * If deleteFiles = true delete data
    * This is called from the logon window
    */
    public static void deleteProfile(String userName, boolean deleteFiles) {
        // Get user prefs/profile file
        String fileName = getUserPrefsFileName(userName);
        if(!fileExists(fileName)) return;

        if(deleteFiles) {
            UserPrefs prefs = UserPrefs.loadUserPrefs(userName);
            String dataFolder = prefs.getProperty(UserPrefs.DATA_ROOT);
            if(dataFolder == null || dataFolder.length() == 0)
                dataFolder = getDefaultDataFolder(userName);
            deleteUserFiles(dataFolder);
        }

        // Delete Profile
        deleteFile(fileName);
    }


    public static void renameProfile(String oldUserName, String newUserName) {
        String oldFileName = getUserPrefsFileName(oldUserName);
        if(!fileExists(oldFileName)) return;
        String newFileName = getUserPrefsFileName(newUserName);
        if(fileExists(newFileName)) return;
        renameFile(oldFileName, newFileName);
    }

    //==========================================================================

    public static String getDataFileName(boolean checkFolder) {
        return getTreeFolder(checkFolder) + dataFileName;
    }

    public static String getTreeStateFileName(boolean checkFolder) {
        return getTreeFolder(checkFolder) + treeStateFileName;
    }

    public static String getAssignmentsRootFolder(boolean checkFolder) {
        String folder = getDataFolder() + assignmentsFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getAssignmentsFolder(String GUID, boolean checkFolder) {
        String folder = getAssignmentsRootFolder(checkFolder) + GUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getActivityRootFolder(boolean checkFolder) {
        String folder = getDataFolder() + activitiesFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getActivityFolder(String GUID, boolean checkFolder) {
        String folder = getActivityRootFolder(checkFolder) + GUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    // Activity MESSAGES FOLDER
    public static String getActivityMailFolder(String activityGUID, boolean checkFolder) {
        String folder = getActivityFolder(activityGUID, checkFolder) + "mail" + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    // Mail to do with an Assignment - for a tutor and a student
    public static String getAssignmentMailFolder(String activityGUID, boolean checkFolder) {
        String folder = getActivityFolder(activityGUID, checkFolder) + "assignment" + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getPeopleRootFolder(boolean checkFolder) {
        String folder = getDataFolder() + peopleFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getResourceRootFolder(boolean checkFolder) {
        String folder = getDataFolder() + resourcesFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getResourceFolder(String GUID, boolean checkFolder) {
        String folder = getResourceRootFolder(checkFolder) + GUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getResourceGroupFolder(boolean checkFolder) {
        String folder = getDataFolder() + resourceGroupFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getResourceGroupFolder(String GUID, boolean checkFolder) {
        String folder = getResourceGroupFolder(checkFolder) + GUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getTempFolder(boolean checkFolder) {
        String folder = getDataFolder() + tempFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getTreeFolder(boolean checkFolder) {
        String folder = getDataFolder() + treeFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getMailFolder(boolean checkFolder) {
        String folder = getDataFolder() + mailFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    // Unmatched Messages
    public static String getPendingMailFolder(boolean checkFolder) {
        String folder = getMailFolder(checkFolder) + pendingFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    // Get Attachments Folder
    public static String getAttachmentsFolder(boolean checkFolder) {
        String folder = getDataFolder() + attachmentsFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    /**
    * The default folder for local file web pages
    */
    public static String getWebPagesFolder(boolean checkFolder) {
        String folder = getDataFolder() + webPageFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    /**
    * Gets the person's personal folder
    */
    public static String getPersonFolder(String personGUID, boolean checkFolder) {
        String folder = getPeopleRootFolder(checkFolder) + personGUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    /*
    * Where invites are stored
    */
    public static String getInviteRootFolder(boolean checkFolder) {
        String folder = getDataFolder() + inviteFolder + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    public static String getInviteFolder(String activityGUID, boolean checkFolder) {
        String folder = getInviteRootFolder(checkFolder) + activityGUID + fileSepChar;
        if(checkFolder) checkFolder(folder);
        return folder;
    }

    /**
    * Checks that a folder exists, creates it if it doesn't
    */
    public static void checkFolder(String folder) {
        checkFolder(new File(folder));
    }

    public static void checkFolder(File folder) {
        if(!folder.exists()) folder.mkdirs();
    }

    /**
    * Generates a unique filename
    * DOES return folder in path
    */
    public static String generateTmpFileName(String folder) {
        Random rand = new Random(new Date().getTime());
        int num;
        String tmpName;

        // Loop while generated number already exists on file
        do {
            num = rand.nextInt();
            tmpName = String.valueOf(num);
            // Remove minus sign
            if(tmpName.indexOf("-") != -1) tmpName = tmpName.substring(1);
            // Add ~
            tmpName = folder+ "~" + tmpName;
        } while(DataFiler.fileExists(tmpName));

        return tmpName;
    }

    //============================== LISTENERS =================================

    // Data Model change listeners
    private static Vector listeners = new Vector();

    public static synchronized void addFileListener(FileListener listener) {
        if(!listeners.contains(listener)) listeners.addElement(listener);
    }

    public static synchronized void removeFileListener(FileListener listener) {
        listeners.removeElement(listener);
    }

    /**
    * Tell our listeners that we have added a new element
    */
    public static void fireSaveHappened() {
        for(int i = 0; i < listeners.size(); i++) {
            FileListener listener = (FileListener)listeners.elementAt(i);
            listener.saveHappened();
        }
    }
}



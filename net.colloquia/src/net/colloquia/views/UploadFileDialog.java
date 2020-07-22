package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.comms.ftp.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;


public class UploadFileDialog
extends JDialog
implements FTPListener
{
    ColloquiaComponent tc;
    File localFile;
    UserPrefs userPrefs;

	PMonitor monitor;
    JTextField tfRemote, tfFolder, tfUserName, tfPassword;

	public UploadFileDialog(ColloquiaComponent tc) {
        super(MainFrame.getInstance(), LanguageManager.getString("ACTION_1"), true);
        setResizable(false);
    	this.tc = tc;
        String msg;
        userPrefs = UserPrefs.getUserPrefs();

        // Get Local File
        String file = normalizeLocalFileName(tc);
        localFile = new File(file);
        boolean OK = localFile.exists();

        // Add OK Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
  	    btnOK.addActionListener(new btnOKClick());
        btnOK.setEnabled(OK);
        JButton btnCancel = new JButton(LanguageManager.getString("CANCEL"));
        buttonPanel.add(btnCancel);
  	    btnCancel.addActionListener(new btnCancelClick());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Add rubric
        JTextPane messagePanel = new JTextPane();
        messagePanel.setOpaque(false);
        messagePanel.setEditable(false);
        messagePanel.setFont(new Font("SansSerif", Font.BOLD, 12));
        messagePanel.setMargin(new Insets(10, 10, 0, 10));
        if(OK) msg = LanguageManager.getString("22_2");
        else msg = LanguageManager.getString("22_3");
        messagePanel.setText(msg);
        getContentPane().add(messagePanel, BorderLayout.NORTH);

        // Add fields
        if(OK) {
            JPanel infoPanel = new JPanel();
            infoPanel.setBorder(new EmptyBorder(10, 50, 80, 80));
            infoPanel.setLayout(new GridLayout(4, 2, 5, 5));

            infoPanel.add(new PLabel(LanguageManager.getString("22_4")));
            tfRemote = new JTextField();
            tfRemote.setText(userPrefs.getProperty(UserPrefs.FTP_REMOTE));
            infoPanel.add(tfRemote);

            infoPanel.add(new PLabel(LanguageManager.getString("22_5")));
            tfFolder = new JTextField();
            tfFolder.setText(userPrefs.getProperty(UserPrefs.FTP_FOLDER));
            infoPanel.add(tfFolder);

            infoPanel.add(new PLabel(LanguageManager.getString("22_6")));
            tfUserName = new JTextField();
            tfUserName.setText(userPrefs.getProperty(UserPrefs.FTP_USERNAME));
            infoPanel.add(tfUserName);

            infoPanel.add(new PLabel(LanguageManager.getString("22_7")));
            tfPassword = new JPasswordField();
            infoPanel.add(tfPassword);

            getContentPane().add(infoPanel, BorderLayout.CENTER);
        }

        setSize(480, 350);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }


    private class btnOKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            final String remote = tfRemote.getText().trim();
            final String folder = tfFolder.getText().trim();
            final String userName = tfUserName.getText().trim();
            final String pwd = tfPassword.getText();

            userPrefs.putProperty(UserPrefs.FTP_REMOTE, remote);
            userPrefs.putProperty(UserPrefs.FTP_FOLDER, folder);
            userPrefs.putProperty(UserPrefs.FTP_USERNAME, userName);

            // Put on thread so that Progress monitor displays
            Thread doRun = new Thread() {
                public void run() {
		            uploadFile(localFile, localFile.getName(), remote, folder, userName, pwd);
                }
            };
            doRun.start();

        	dispose();
        }
    }

    private class btnCancelClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    protected void uploadFile(File srcFile, String targetFileName, String remoteAddress, String remoteFolder,
    	String userName, String password) {

	    monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("ACTION_1"),
            	LanguageManager.getString("ACTION_1"), LanguageManager.getString("22_1"));

        FTPClient client = null;
        try {
            monitor.init(LanguageManager.getString("22_8"), srcFile.getName(), (int)srcFile.length());

            client = new FTPClient(remoteAddress);
            client.addFTPListener(this);

            monitor.setNote(LanguageManager.getString("22_9"));
            client.login(userName, password);

            if(!remoteFolder.equals("")) client.chdir(remoteFolder);

            client.setType(FTPTransferType.BINARY);
            client.put(srcFile.getPath(), targetFileName);

            client.quit();
        }
        catch(Exception ex) {
            monitor.close();
            ErrorHandler.showWarning("ERR19", ex, "ERR");
            return;
        }

        monitor.close();
        if(remoteFolder.equals("")) remoteFolder = "/";
        if(!remoteFolder.startsWith("/")) remoteFolder = "/" + remoteFolder;
        if(!remoteFolder.endsWith("/")) remoteFolder += "/";
        tc.setURL("ftp://" + remoteAddress + remoteFolder + targetFileName, true);
		ViewPanel.getInstance().repaintCurrentView();
    }

    public boolean bytesPut(int bytes) {
    	monitor.incProgress(bytes, true);
        return monitor.isCanceled();
    }

    public boolean bytesGot(int bytes) {
    	return false;
    }

    public void commandSent(String cmd) {
    	monitor.setNote(cmd);
    }

    protected String normalizeLocalFileName(ColloquiaComponent tc) {
		String s = tc.getLocalFile().trim();
        while(s.startsWith("file:")) s = s.substring(5);
        while(s.startsWith("/")) s = s.substring(1);
        return s;
    }
}
package net.colloquia.views.browser;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.menu.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

import com.netclue.browser.*;
import com.netclue.html.event.*;
import com.netclue.xml.gui.event.*;

/**
 * Core and abstract functionality of a Browser Panel
 */
public abstract class BrowserPanel
extends JPanel
implements HyperlinkListener, HyperlinkFilter, DocListener
{
    protected ColloquiaBrowser browser;
    //protected ProgressSign progressSign;
    protected StatusBar statusBar;
    protected PTextField txtField;
    protected ColloquiaToolBar toolBar;

    protected TextBoxAction action_TextBox;
    protected BackAction action_Back;
    protected ReloadAction action_Reload;
    protected ForwardAction action_Forward;
    protected StopAction action_Stop;
    //protected PrintAction action_Print;

    static String msgLoad = LanguageManager.getString("3_1");
    static String msgBad = LanguageManager.getString("3_2") + ": ";

    private String passWord;
    private String userName;

    public static final int ACTION_NONE = 0;
    public static final int ACTION_DISPLAY = 1;
    public static final int ACTION_EXTERNAL = 2;
    public static final int ACTION_QUICKTIME = 3;

    protected BrowserPanel() {
        setLayout(new BorderLayout());

        statusBar = new StatusBar();

        browser = new ColloquiaBrowser();
    	//progressSign = createProgressSign(browser);

        toolBar = initToolBar();
        add(toolBar, BorderLayout.NORTH);

	    // ClueBrowser
        browser.setDefaultStatus("Done");
        browser.addHyperlinkListener(this);
        browser.setHyperlinkFilter(this);
        browser.addDocListener(this);
        browser.setStatusLabel(statusBar);

        add(browser, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
	}

    /*
    protected ProgressSign createProgressSign(ColloquiaBrowser browser) {
    	ProgressSign ps = new ProgressSign();

        try {
        	MediaTracker tracker = new MediaTracker(this);
            Image images[] = new Image[15];
            for(int i = 0 ; i < images.length; i++) {
            	images[i] = Utils.getImage("PSign" + i + ".jpg");
                tracker.addImage(images[i], i);
            }
            tracker.waitForAll();
            ps.setImages(images);
            browser.setProgressSign(ps);
        }
        catch(Exception ex) {
        	System.err.println("Could not make ProgressSign");
        }

        return ps;
    }
    */

    protected void setCurrentLocation(final String urlString) {
        Thread thread = new Thread() {
            public void run() {
                statusBar.clearText();

                // No URL String
                if(urlString == null || urlString.length() == 0) {
                    clearContent();
                    return;
                }

                URL url = URLUtils.normalizeAddress(urlString);
                if(url == null) {
                    statusBar.setText(msgBad + urlString);
                    return;
                }

                int action = determineAction(url);

                switch(action) {
                    case ACTION_NONE:
                        break;

                    case ACTION_EXTERNAL:
                        downloadFileThread(url);
                        break;

                    case ACTION_QUICKTIME:
                        Utils.playQuickTimeFile(url);
                        break;

                    case ACTION_DISPLAY:
                        displayThread(url);
                        break;

                    default:
                        break;
                }
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    protected void displayThread(final URL url) {
        Thread thread = new Thread() {
            public void run() {
                browser.setCurrentLocation(url.toString());
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    protected void downloadFileThread(final URL url) {
        Thread thread = new Thread() {
            public void run() {
                downloadFile(url);
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    // ClueBrowser
	public void hyperlinkTriggered(HyperlinkEvent e) {
    	//System.out.println("hyperlinkTriggered: " + e.getURL().toString());
    }

    // ClueBrowser
    public boolean isFiltered(HyperlinkEvent e) {
    	URL url = e.getURL();
        int action = determineAction(url);
        switch(action) {
        	case ACTION_EXTERNAL:
                downloadFileThread(url);
                return true;

        	case ACTION_QUICKTIME:
                Utils.playQuickTimeFile(url);
                return true;

        	case ACTION_DISPLAY:
                return false;

            default:
            	return false;
        }
    }

    // ClueBrowser
    public void handleDocumentEvent(DocEvent e) {
    	switch(e.getType()) {
        	case DocEvent.DOCUMENT_DONE:
                txtField.setText(browser.getCurrentLocation());
            	break;
        	case DocEvent.DOCUMENT_LOAD_ERROR:
                System.out.println(e.getMessage());
            	break;
        	case DocEvent.DOCUMENT_RENDER_ERROR:
                System.out.println(e.getMessage());
            	break;
        }
    }

    public void dispose() {
        browser = null;
        statusBar = null;
        txtField = null;
        toolBar = null;
    }

    private ColloquiaToolBar initToolBar() {
        ColloquiaToolBar toolBar = new ColloquiaToolBar();

        toolBar.add(action_Back = new BackAction());
        toolBar.add(action_Reload = new ReloadAction());
        toolBar.add(action_Forward = new ForwardAction());
        toolBar.add(action_Stop = new StopAction());
        //toolBar.add(action_Print = new PrintAction());

        toolBar.addSeparator();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        txtField = new PTextField();
        action_TextBox = new TextBoxAction();
        txtField.addActionListener(action_TextBox);
        txtField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        //txtField.setBackground(ColloquiaConstants.color3);
        toolBar.add(panel);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel(LanguageManager.getString("ADDRESS") + ": "), BorderLayout.WEST);
        panel.add(txtField, BorderLayout.CENTER);
        //panel.add(progressSign, BorderLayout.EAST);

        return toolBar;
    }

    public void clearContent() {
        browser.clear();
        txtField.setText("");
    }

    protected class StopAction extends MenuAction {
        public StopAction() {
            super(LanguageManager.getString("STOP"), ColloquiaConstants.iconStop);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT25"));
        }

        public void actionPerformed(ActionEvent e) {
            browser.stop();
        }
    }

    protected class PrintAction extends MenuAction {
        public PrintAction() {
            super(LanguageManager.getString("PRINT"), ColloquiaConstants.iconPrint);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT23"));
        }

        public void actionPerformed(ActionEvent e) {
            browser.print();
        }
    }

    protected class BackAction extends MenuAction {
        public BackAction() {
            super(LanguageManager.getString("BACK"), ColloquiaConstants.iconBack);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT1"));
        }

        public void actionPerformed(ActionEvent e) {
            browser.back();
        }
    }

    protected class ForwardAction extends MenuAction {
        public ForwardAction() {
            super(LanguageManager.getString("FORWARD"), ColloquiaConstants.iconForward);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT2"));
        }

        public void actionPerformed(ActionEvent e) {
            browser.forward();
            txtField.setText(browser.getCurrentLocation());
        }
    }

    protected class ReloadAction extends MenuAction {
        public ReloadAction() {
            super(LanguageManager.getString("RELOAD"), ColloquiaConstants.iconReload);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT24"));
        }

        public void actionPerformed(ActionEvent e) {
            browser.reload();
        }
    }


    /**
    * Goto the URL in the text field
    */
    protected class TextBoxAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            textBoxHappened();
        }
    }

    protected void textBoxHappened() {
        setCurrentLocation(txtField.getText());
    }


    public void setCurrentLocation(URL url) {
        setCurrentLocation(url.toString());
    }

    public String getCurrentLocation() {
        return browser.getCurrentLocation();
    }

    public Dimension getMinimumSize() { return new Dimension(0, 0); }


    /**
    * Check url is ftp and requires password and user name
    * Uses Java2
    */
 	protected URL getFTPURL(URL url) {
        if(url == null) return null;

        URL newURL;

        if(url.getProtocol().equals("ftp") && url.getUserInfo() == null) {
            try {
                // Get User name & Password
                LogonDialog dialog = new LogonDialog(MainFrame.getInstance(),
                	LanguageManager.getString("3_9"), LanguageManager.getString("3_11"));
                dialog.setUserName(userName);
                dialog.setPassword(passWord);
                int val = dialog.show();
                if(val == -1) return null;  // Cancel

                userName = dialog.getUserName().trim();
                passWord = dialog.getPassword().trim();

                String host = url.getHost();
                if(!userName.equals("") && !passWord.equals("")) host = userName + ":" + passWord + "@" + url.getHost();

            	// Don't use this method - it puts [] brackets around host name in Java 1.4
                //newURL = new URL(url.getProtocol(), host, url.getPort(),
                //                url.getFile().equals("") ? "/" : url.getFile());

                newURL = new URL("ftp://" + host + (url.getFile().equals("") ? "/" : url.getFile()));
            }
            catch(MalformedURLException ex) {
            	return null;
            }
            return newURL;
        }

        else return url;
	}

    /**
    * Download  given URL to a File
    * @return File if successful, null if aborted
    */
    protected File downloadFile(URL url) {
        if(url == null) return null;

        // Ask for a folder to save to

        /*
        PFolderChooser chooser = new PFolderChooser(MainFrame.getInstance(), LanguageManager.getString("CHOOSE_FOLDER"));
        File folder = chooser.getFolder();
        if(folder == null) return null;
        File f = new File(url.getFile());
        File file = new File(folder, f.getName());
        */

        // Because of bug #4357012 we can't use this
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(LanguageManager.getString("SAVE"));
        File tmp = new File(chooser.getStoredFolder(), url.getFile());
        chooser.setSelectedFile(tmp);
        int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return null;
        File file = chooser.getSelectedFileAndStore();

        url = getFTPURL(url);
        if(url == null) return null;

        int bytesRead;
        final int bufSize = 2048;
        byte buf[] = new byte[bufSize];
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

	    PMonitor monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("15_1"),
            	LanguageManager.getString("15_1"), LanguageManager.getString("15_2"));

        boolean aborted = false;

        try {
            // Connect to file
            URLConnection conn = URLUtils.getURLConnection(url);
            if(conn == null) return null;

            in = new BufferedInputStream(conn.getInputStream(), bufSize);
            out = new BufferedOutputStream(new FileOutputStream(file), bufSize);

            // This goes AFTER conn.getInputStream()
            int size = conn.getContentLength();
            monitor.init(LanguageManager.getString("15_1"), url.getFile(), size);

            // Read/Write bytes
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                monitor.incProgress(bytesRead, true);
                if(monitor.isCanceled()) {
                    aborted = true;
                    break;
                }
            }

            out.flush();
            out.close();
            in.close();
        }
        catch(IOException ex) {
            aborted = true;
            monitor.close();
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            ErrorHandler.showWarning("ERR4", ex, "ERR");
            try {
                if(out != null) out.close();
                if(in != null) in.close();
            }
            catch(IOException ex1) {
                if(ColloquiaConstants.DEBUG) ex1.printStackTrace();
            }
        }

        monitor.close();

        if(aborted && file != null) {
            file.delete();
            file = null;
        }

        return file;
    }


    /**
    * Can't get this to work
    */
    /*
    protected String __downloadFile(URL url) {
        LogonDialog dialog = new LogonDialog(MainFrame.getInstance(),
            LanguageManager.getString("3_9"), LanguageManager.getString("3_11"));
        dialog.setUserName(userName);
        dialog.setPassword(passWord);
        int val = dialog.show();
        if(val == -1) return null;  // Cancel

        userName = dialog.getUserName();
        if(userName.equals("")) userName = "anonymous";

        passWord = dialog.getPassword();
        if(passWord.equals("")) passWord = "guest@here.com";

        // Ask for a file name
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(LanguageManager.getString("SAVE_TO_FILE"));

        File file = new File(chooser.getStoredFolder(), url.getFile());
        chooser.setSelectedFile(file);

        int returnVal = chooser.showSaveDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return null;
        String localFile = chooser.getSelectedFileAndStore().toString();

	    PMonitor monitor = new PMonitor(MainFrame.getInstance(), LanguageManager.getString("15_1"),
            	LanguageManager.getString("15_1"), LanguageManager.getString("15_2"));

        FTPClient client = null;
        try {
            String host = url.getHost();
            String remoteFile = url.getFile();

            client = new FTPClient(host);
            client.addFTPListener(new DownloadListener(monitor));

            monitor.setNote(LanguageManager.getString("22_9"));
            client.login(userName, passWord);
            client.setType(FTPTransferType.BINARY);

            monitor.init(LanguageManager.getString("22_8"), url.getFile(), client.getFileSize(remoteFile));

            client.get(localFile, remoteFile);

            client.quit();
        }
        catch(Exception ex) {
            monitor.close();
            ErrorHandler.showWarning("ERR4", ex, "ERR");
            return null;
        }

        monitor.close();
        return localFile;
    }

    private class DownloadListener
    implements FTPListener
    {
        PMonitor monitor;

    	public DownloadListener(PMonitor monitor) {
        	this.monitor = monitor;
        }

        public boolean bytesPut(int bytes) {
            return false;
        }

        public boolean bytesGot(int bytes) {
            monitor.incProgress(bytes, true);
            return monitor.isCanceled();
        }

        public void commandSent(String cmd) {
            monitor.setNote(cmd);
        }
    }
    */

    /**
    * Determine what action to take
    */
    protected int determineAction(URL url) {
        statusBar.setText(msgLoad + ": " + url.toString());
        if(url == null) return ACTION_DISPLAY;
        // In this order please
        if(URLUtils.isFTP(url)) return ACTION_EXTERNAL;
        if(URLUtils.isQuickTimeFile(url)) return ACTION_QUICKTIME;
        if(URLUtils.isExternalFile(url)) return ACTION_EXTERNAL;
        return ACTION_DISPLAY;
    }

}




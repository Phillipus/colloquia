package net.colloquia.comms.editors;

import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.io.*;
import net.colloquia.util.*;


/**
 * Views an e-mail (Read only)
 */
public class EMailViewer
extends JPanel
implements HyperlinkListener
{
    protected MessageInfo mInfo;
    protected JEditorPane viewer;

    public EMailViewer() {
        setLayout(new BorderLayout());
        viewer = new JEditorPane();
        viewer.setEditable(false);
        viewer.addHyperlinkListener(this);
        HTMLEditorKit kit = new HTMLEditorKit();
        viewer.setEditorKit(kit);
        kit.setDefaultCursor(ColloquiaConstants.textCursor);
        add(new JScrollPane(viewer));
    }

    /**
     * Displays the message referenced in mInfo.<br>
     * If mInfo is null the display area is cleared
     * @param mInfo the Message to display
     */
    public void showMessage(MessageInfo mInfo) {
        this.mInfo = mInfo;

        if(mInfo == null) clear();
        else {
            String fileName = mInfo.getFullFileName();
            if(DataFiler.fileExists(fileName)) {
		        URL url = URLUtils.makeLocalURL(fileName);
                try {
					if(url != null) viewer.setPage(url);
                    else clear();
                }
                catch(Exception ex) {
					if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                    System.out.println("Could not display message: " + ex);
                }
            }
            else clear();
        }
    }

    public void printMessage() {
        try {
            if(mInfo == null) return;

            Font headerFont = new Font("SansSerif", Font.BOLD, 12);
            Font textFont = new Font("SansSerif", Font.PLAIN, 10);
            Printer p = new Printer("Colloquia");

            String activityName = mInfo.getActivityName();
            String subject = mInfo.getSubject();
            String fromAddress = mInfo.getFrom();
            String toAddress = mInfo.getTo();
            String dateSent = Utils.parseDate(mInfo.getDateSent());

            p.setFont(headerFont);
            p.printText(LanguageManager.getString("0") + ":\t\t" + activityName + "\n");
            p.printText(LanguageManager.getString("16_1") + ":\t\t" + subject + "\n");
            p.printText(LanguageManager.getString("16_8") + ":\t\t" + fromAddress + "\n");
            p.printText(LanguageManager.getString("16_26") + ":\t\t\t" + toAddress + "\n");
            p.printText(LanguageManager.getString("16_2") + ":\t\t" + dateSent + "\n");
            p.drawLine();
            p.printText("\n");

            p.setFont(textFont);
            Document doc = viewer.getDocument();
            p.printText(doc.getText(0, doc.getLength()));

            p.close();
        }
        catch(Exception ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
        }
    }

    public void clear() {
    	viewer.setDocument(viewer.getEditorKit().createDefaultDocument());
    }

    /**
     * Listen to hyperlinks and launch the URL in external browser
     * @param e The Hyperlink event
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                BrowserLauncher.openURL(e.getURL().toString());
            }
            catch(Exception ex) {
                if(ColloquiaConstants.DEBUG) ex.printStackTrace();
                System.out.println("BrowserLauncher error: " + e.getURL());
            }
        }
    }
}



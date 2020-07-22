package net.colloquia.comms.tables;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.util.*;

/**
 * The Window that holds the Inbox, Outbox and Holding area for messages
 */
public class MessageWindow extends JFrame {
    private static final MessageWindow instance = new MessageWindow();
    JTabbedPane tabPane;

    private MessageWindow() {
        setIconImage(Utils.getIcon(ColloquiaConstants.iconMessage).getImage());
        setTitle(ColloquiaConstants.APP_NAME + " - " + LanguageManager.getString("MESSAGING"));

        getContentPane().setLayout(new BorderLayout());

        tabPane = new JTabbedPane();
        tabPane.addTab(LanguageManager.getString("UNREAD"), Inbox.getInstance());
        tabPane.addTab(LanguageManager.getString("READ"), Readbox.getInstance());
        tabPane.addTab(LanguageManager.getString("UNSENT"), Outbox.getInstance());
        tabPane.addTab(LanguageManager.getString("SENT"), Sentbox.getInstance());
        tabPane.addTab(LanguageManager.getString("UNMATCHED"), Pendingbox.getInstance());
        getContentPane().add(tabPane);
        //setSize(620, 470);
        Utils.centreWindow(this, 620, 470);
    }

    public static MessageWindow getInstance() {
        return instance;
    }

    /**
    * Refresh Messages
    */
    public void reloadMessages() {
        Inbox.getInstance().reloadMessages();
        Readbox.getInstance().reloadMessages();
        Outbox.getInstance().reloadMessages();
        Sentbox.getInstance().reloadMessages();
        Pendingbox.getInstance().reloadMessages();
    }

    public void showWindow() {
        setVisible(true);
        if((MainFrame.getInstance().getExtendedState() & JFrame.ICONIFIED) == 1) {
            MainFrame.getInstance().setState(JFrame.NORMAL);
        }
        requestFocus();
    }

    public void selectTab(int tab) {
    	tabPane.setSelectedIndex(tab);
    }
}

package net.colloquia.comms.tables;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * Panel to display a list of e-mail messages for a single person
 */
public class SingleMessageTablePanel
extends MessageTablePanel
{
    private Activity A;
    private Person person;

    public SingleMessageTablePanel(EMailViewer emailViewer) {
        super(emailViewer);

        model = new SingleMessageTableModel();
        table.setModel(model);
        model.addTableModelListener(this);

        table.createDefaultColumnsFromModel();
        tableHeader = table.getTableHeader();
        tableHeader.setResizingAllowed(true);

        // Set the default cell renderer for any type object
        tableRenderer = new SingleMessageTableRenderer();
        table.setDefaultRenderer(Object.class, tableRenderer);

        // Set the renderer for the column header
        TableColumnModel tcm = tableHeader.getColumnModel();
        TableColumn current;
        headerRenderer = new MessageTableHeaderRenderer(0, model);
        for(Enumeration cols = tcm.getColumns(); cols.hasMoreElements();) {
            current = (TableColumn)cols.nextElement();
            // Assign our custom header-renderer to each column
            current.setHeaderRenderer(headerRenderer);
        }

        // Columns
        tcm.getColumn(0).setPreferredWidth(25);
        tcm.getColumn(0).setMaxWidth(25);
        tcm.getColumn(1).setPreferredWidth(18);
        tcm.getColumn(1).setMaxWidth(18);
        tcm.getColumn(2).setPreferredWidth(18);
        tcm.getColumn(2).setMaxWidth(18);
        tcm.getColumn(3).setPreferredWidth(290);
        //tcm.getColumn(4).setPreferredWidth(50);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        // Listen to mouse-clicks on the table header
        headerClickHandler = new MessageTableClickHandler(table);
        tableHeader.addMouseListener(headerClickHandler);
    }

    /**
    * Load a new set of messages when selected
    */
    public void loadMessages(Person person, Activity A) {
        if(person == null || A == null) return;
        this.A = A;
        this.person = person;

        // New title of person's name
        //String text = "<html><font color=black>" + LanguageManager.getString("16_27") + " <font color=red>" +
        //     person.getName() + "<font color=black> " + LanguageManager.getString("ABOUT2") + " <font color=red>" + A.getName() + "</html>";
        String text = LanguageManager.getString("16_27") + " " + person.getName() + " " +
            LanguageManager.getString("ABOUT2") + " " + A.getName();
        title.setText(text);
        // Display
        ((SingleMessageTableModel)model).loadMessageIndex(A, person);
        selectMessage(lastMessageSelected);
    }

    /**
    * Compose a new message to this person
    */
    protected void composeNewMessage() {
        new SingleMessageEditor(A, person);
    }

    /**
    * Reply to a message to this person
    * Essentially, it's a new message with a Re: and maybe the original message
    */
    protected void replyToMessage() {
        MessageInfo mInfo = getSelectedMessage();
        if(mInfo == null) return;

        // New editor
        new SingleMessageEditor(A, person, mInfo);
    }

    protected void resendMessage() {
        MessageInfo mInfo = getSelectedMessage();
        if(mInfo == null) return;

        // Check we wanna do this!
        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            LanguageManager.getString("16_114"),
            LanguageManager.getString("16_113"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        // Make a copy and add to index
        MessageInfo mInfoCopy = mInfo.copy(true);
        mInfoCopy.setState(MessageInfo.RESEND);

        // Send to the Outbox
        Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfoCopy, true);
    }
}



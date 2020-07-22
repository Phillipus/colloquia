package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.editors.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * Panel to display a list of e-mail messages relating to an Assignment
 */
public class AssignmentMessageTablePanel
extends MessageTablePanel
{
    private Activity A;
    private Person person;
    private PTextField gradeBox;
    private KeyPressListener keyPressListener;

    public AssignmentMessageTablePanel(EMailViewer emailViewer) {
        super(emailViewer);

        model = new AssignmentMessageTableModel();
        table.setModel(model);
        model.addTableModelListener(this);

        table.createDefaultColumnsFromModel();
        tableHeader = table.getTableHeader();
        tableHeader.setResizingAllowed(true);

        // Set the default cell renderer for any type object
        tableRenderer = new AssignmentMessageTableRenderer();
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

        // Add a grade box
        toolBar.add(new JToolBar.Separator());
        toolBar.add(new PLabel(LanguageManager.getString("12_9") + ":"));
        toolBar.add(new JToolBar.Separator());
        gradeBox = new PTextField(1);
        toolBar.add(gradeBox);
        toolBar.add(new JToolBar.Separator());

        // Add a key listener for the grade box
        keyPressListener = new KeyPressListener();
    }

    /**
    * Load a new set of messages when selected for an Assignment
    * Person will be either the Tutor or the Student
    * For a tutor view there might not be an Assignment
    */
    public void loadMessages(Person person, Activity A) {
        if(person == null || A == null) return;
        this.A = A;
        this.person = person;

        // New title
        //String text = "<html><font color=red>" + A.getName() +
        //              "<font color=black> " + LanguageManager.getString("2") + "</html>";
        String text = A.getName() + " " + LanguageManager.getString("2");
        title.setText(text);

        // Grade box
        // Tutor
        if(A.isMine()) {
            gradeBox.setEditable(true);
            gradeBox.addKeyListener(keyPressListener);
            gradeBox.setText(A.getPersonAssignmentGrade(person));
        }
        // Student
        else {
            gradeBox.setEditable(false);
            gradeBox.removeKeyListener(keyPressListener);
            Assignment assignment = A.getAssignment();
            gradeBox.setText(A.getAssignmentGrade(assignment));
        }

        // Display
        ((AssignmentMessageTableModel)model).loadMessageIndex(A, person);
        selectMessage(lastMessageSelected);
    }

    /**
    * Compose a new message either to the Student or the Tutor
    */
    protected void composeNewMessage() {
        new AssignmentMessageEditor(A, person);
    }

    /**
    * Reply to a message to this person about the Assignment
    * Essentially, it's a new message with a Re: and maybe the original message
    */
    protected void replyToMessage() {
        MessageInfo mInfo = getSelectedMessage();
        if(mInfo == null) return;

        // New editor
        new AssignmentMessageEditor(A, person, mInfo);
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

    /**
    * A key listener for the grade box - only valid for the tutor
    */
    protected class KeyPressListener extends KeyAdapter {
        public void keyReleased(KeyEvent e) {
            if(A.isMine()) {
                A.setPersonAssignmentGrade(person, gradeBox.getText());
                // Update tree
                ColloquiaTree.getInstance().updateSelectedNode();
            }
        }
    }

}


package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

public class Activity_PropertiesView
extends JDialog
{
    Activity A;
    JTabbedPane tabPane;
    JCheckBox cbPeople, cbResources, cbAssignment;
    JButton reActivate;

    public Activity_PropertiesView(Activity A) {
        super(MainFrame.getInstance(), LanguageManager.getString("ACTION_43"), true);
        setResizable(false);
        this.A = A;

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        getContentPane().add(tabPane, BorderLayout.CENTER);

        // Add OK Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
  	    btnOK.addActionListener(new btnOKClick());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Add info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 60, 0, 60));
        infoPanel.setLayout(new GridLayout(12, 2, 5, 5));
        tabPane.addTab(LanguageManager.getString("GENERAL"), null, infoPanel);

        // Name
        infoPanel.add(new PLabel(LanguageManager.getString("5_2")));
        infoPanel.add(new PLabel(A.getName()));
        // Date Created
        infoPanel.add(new PLabel(LanguageManager.getString("5_3")));
        String date = Utils.parseDate(A.getPropertyDate(ColloquiaComponent.DATE_CREATED));
        infoPanel.add(new PLabel(date));
        // Date Modified
        infoPanel.add(new PLabel(LanguageManager.getString("5_4")));
        date = Utils.parseDate(A.getPropertyDate(ColloquiaComponent.DATE_MODIFIED));
        infoPanel.add(new PLabel(date));
        // Creator
        infoPanel.add(new PLabel(LanguageManager.getString("21_16")));
        String creator = A.getSubmitter();
        Person person = DataModel.getPersonByEmailAddress(creator);
        creator = person == null ? creator : person.getName();
        infoPanel.add(new PLabel(creator));

        // Inherits people
        JCheckBox inheritsPeople = new JCheckBox(LanguageManager.getString("8_8"));
        inheritsPeople.setSelected(A.inheritsPeople());
        inheritsPeople.setEnabled(A.isMine());
        inheritsPeople.addActionListener(new InheritsPeople());
		infoPanel.add(inheritsPeople);

        // Accept Resources
        JCheckBox acceptResources = new JCheckBox(LanguageManager.getString("21_14"));
        acceptResources.setSelected(A.acceptsResources());
        acceptResources.setEnabled(A.isMine());
        acceptResources.addActionListener(new AcceptsResources());
		infoPanel.add(acceptResources);

        // Re-Activate
        reActivate = new JButton(LanguageManager.getString("21_15"));
        reActivate.setEnabled(A.canBeMadeLive());
        reActivate.addActionListener(new MakeLive());
		infoPanel.add(reActivate);

        // Visibility
        infoPanel.add(new PLabel(""));
        infoPanel.add(new PLabel(""));
        infoPanel.add(new PLabel(""));
        infoPanel.add(new PLabel(LanguageManager.getString("21_10"), Color.black));
        infoPanel.add(new PLabel(""));

        cbPeople = new JCheckBox(LanguageManager.getString("21_11"));
        cbPeople.addActionListener(new VisiblePeopleListener());
        cbPeople.setSelected(ColloquiaTree.getInstance().isPeopleVisible(A));
        infoPanel.add(cbPeople);
        infoPanel.add(new PLabel(""));

        cbResources = new JCheckBox(LanguageManager.getString("21_12"));
        cbResources.addActionListener(new VisibleResourcesListener());
        cbResources.setSelected(ColloquiaTree.getInstance().isResourcesVisible(A));
        infoPanel.add(cbResources);
        infoPanel.add(new PLabel(""));

        cbAssignment = new JCheckBox(LanguageManager.getString("21_13"));
        cbAssignment.addActionListener(new VisibleAssignmentListener());
        cbAssignment.setSelected(ColloquiaTree.getInstance().isAssignmentVisible(A));
        infoPanel.add(cbAssignment);
        infoPanel.add(new PLabel(""));

        // Blanks
        for(int i = 0; i < 2; i++) infoPanel.add(new PLabel(""));

        if(A.isMine())  {
            // Updates panel
            JTable updateTable = new JTable(new UpdatePeopleTableModel(A, true));
	        updateTable.setDefaultRenderer(Object.class, new ActivityPeopleTableRenderer(A, A.getActivePeople()));
            JScrollPane sp1 = new JScrollPane(updateTable);
            sp1.setColumnHeaderView(updateTable.getTableHeader());
            tabPane.addTab(LanguageManager.getString("21_1"), null, sp1);

            // Invites panel
            JTable inviteTable = new JTable(new UpdatePeopleTableModel(A, false));
	        inviteTable.setDefaultRenderer(Object.class, new ActivityPeopleTableRenderer(A, A.getPendingPeople()));
            JScrollPane sp2 = new JScrollPane(inviteTable);
            sp2.setColumnHeaderView(inviteTable.getTableHeader());
            tabPane.addTab(LanguageManager.getString("21_2"), null, sp2);

            // People status
            JTable peopleTable = new JTable(new PeopleTableModel(A));
	        peopleTable.setDefaultRenderer(Object.class, new ActivityPeopleTableRenderer(A, A.getAllPeople()));
            JScrollPane sp3 = new JScrollPane(peopleTable);
            sp3.setColumnHeaderView(peopleTable.getTableHeader());
            tabPane.addTab(LanguageManager.getString("21_4"), null, sp3);
        }

        // Centre it in relation to our main frame
        setSize(500, 400);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    private class VisiblePeopleListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            ColloquiaTree.getInstance().displayPeople(A, cbPeople.isSelected());
            ColloquiaTree.getInstance().reselectCurrentNode();
        }
    }

    private class VisibleResourcesListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            ColloquiaTree.getInstance().displayResources(A, cbResources.isSelected());
            ColloquiaTree.getInstance().reselectCurrentNode();
        }
    }

    private class VisibleAssignmentListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            ColloquiaTree.getInstance().displayAssignment(A, cbAssignment.isSelected());
            ColloquiaTree.getInstance().reselectCurrentNode();
        }
    }

    /**
    * Make Live again
    */
    private class MakeLive extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String msg;
            if(A.isMine()) msg = LanguageManager.getString("17_54");
            else msg = LanguageManager.getString("17_55");

            int result = JOptionPane.showConfirmDialog
                (MainFrame.getInstance(),
                msg,
                LanguageManager.getString("17_56"),
                JOptionPane.YES_NO_OPTION);
            if(result != JOptionPane.YES_OPTION) return;

            A.setLive(true);
	        reActivate.setEnabled(false);

	        // And update menu items
    	    MainFrame.getInstance().mainMenu.refreshAll();

            // Now send a message out again
            MessageInfo mInfo;

            // My Activity
            if(A.isMine()) {
                mInfo = makeTutorMessageInfo(A);
            }

            else {
                mInfo = makeStudentMessageInfo(A);
            }

            if(mInfo != null) Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, true);
        }
    }

    /**
    * Make a MessageInfo class to send to the Students for re-activate
    */
    protected MessageInfo makeTutorMessageInfo(Activity A) {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_REACTIVATE);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("17_57"));
        mInfo.setActivityID(A.getGUID());
        mInfo.setActivityName(A.getName());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(LanguageManager.getString("17_23"));

        Vector people = A.getActivePeople();

        // No people!
        if(people.size() == 0) return null;

        for(int i = 0; i < people.size(); i++) {
            Person person = (Person)people.elementAt(i);
            mInfo.addRecipient(person.getEmailAddress());
        }

        return mInfo;
    }

    /**
    * Make a MessageInfo class to send to the Tutor for re-activate
    */
    protected MessageInfo makeStudentMessageInfo(Activity A) {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_REACTIVATE);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("17_57"));
        mInfo.setActivityID(A.getGUID());
        mInfo.setActivityName(A.getName());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(A.getSubmitter());
        mInfo.addRecipient(A.getSubmitter());

        return mInfo;
    }

    /**
    * Inherits People
    */
    private class InheritsPeople extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
        	A.setInheritsPeople(!A.inheritsPeople(), false);
        }
    }

    /**
    * Accepts Resources
    */
    private class AcceptsResources extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
        	A.setAcceptsResources(!A.acceptsResources(), true);
        }
    }

    /** Close */
    private class btnOKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }


    /**
    * The Table Model for UpdatingPeople
    */
    protected class UpdatePeopleTableModel extends AbstractTableModel {
        Vector people;
        Activity A;

        public UpdatePeopleTableModel(Activity A, boolean update) {
            this.A = A;
            if(update) people = A.getActivePeople();
            else people = A.getPendingPeople();
        }

        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            Person person = (Person)people.elementAt(rowIndex);
            switch(columnIndex) {
                case 0:
                    return person.getName();
                case 1:
                    return person.getEmailAddress();
                case 2:
                    Date date = A.getDateLastSentActivity(person);
                    if(date == null) return "";
                    else return DateFormat.getInstance().format(date);
            }
            return "";
        }

        // getColumnCount
        public int getColumnCount() {
            return 3;
        }

        // getRowCount
        public int getRowCount() {
            return people.size();
        }

        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return LanguageManager.getString("NAME");
                case 1:
                    return LanguageManager.getString("EMAIL");
                case 2:
                    return LanguageManager.getString("21_3");
            }
            return "";
        }
    }

    /**
    * The Table Model for Status of People
    */
    protected class PeopleTableModel extends AbstractTableModel {
        Vector people;
        Activity A;

        public PeopleTableModel(Activity A) {
            this.A = A;
            people = A.getAllPeople();
        }

        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            Person person = (Person)people.elementAt(rowIndex);
            switch(columnIndex) {
                case 0:
                    return person.getName();
                case 1:
                    return person.getEmailAddress();
                case 2:
                	int status = A.getPersonAcceptedStatus(person);
                    if(status == Activity.ACCEPTED) {
                        if(A.doesPersonHaveActivity(person)) return LanguageManager.getString("21_5");
                        else return LanguageManager.getString("21_17");
                    }
                    else if(status == Activity.PENDING) return LanguageManager.getString("21_6");
                    else if(status == Activity.DECLINED) return LanguageManager.getString("21_7");
                    else return "?";
                case 3:
                	return new Boolean(!A.isPersonRemoved(person));
            }
            return "";
        }

        // getColumnCount
        public int getColumnCount() {
            return 4;
        }

        // getRowCount
        public int getRowCount() {
            return people.size();
        }

        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return LanguageManager.getString("NAME");
                case 1:
                    return LanguageManager.getString("EMAIL");
                case 2:
                	return LanguageManager.getString("21_8");
                case 3:
                    return LanguageManager.getString("21_9");
            }
            return "";
        }

        public Class getColumnClass(int columnIndex) {
        	Class retVal = String.class;
            if(columnIndex == 3) retVal = Boolean.class;
            return retVal;
        }

	    public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 3;
    	}

	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        	if(columnIndex == 3) {
	            Person person = (Person)people.elementAt(rowIndex);
                if(person != null) {
                	A.setPersonRemoved(person, !A.isPersonRemoved(person));
                	fireTableRowsUpdated(rowIndex, rowIndex);
                	ColloquiaTree.getInstance().repaint();
                }
            }
    	}
    }

}

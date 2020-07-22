package net.colloquia.comms.wizard;

import java.awt.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;


/**
 * The main dialog wizard for sending an Activity
 */
public class SendActivityWizard
extends SendWizard
{
    UserPrefs prefs;

    String myEmailAddress;
    String myGivenName;
    String myFamilyName;

    Page1 page1;
    Page2 page2;
    Page3 page3;
    Page4 page4;

    Vector topUpdatePeople;
    Vector topInvitePeople;
    Vector topUpdatePeopleSelected;
    Vector topInvitePeopleSelected;

    Vector subUpdatePeople;
    Vector subInvitePeople;
    Vector subUpdatePeopleSelected;
    Vector subInvitePeopleSelected;

    Activity topActivity;

    static int UPDATE = 1;
    static int INVITE = 2;
    static int BOTH = 3;

    public SendActivityWizard(Activity A) {
        topActivity = A;

        // Get sender's details
        prefs = UserPrefs.getUserPrefs();
        myEmailAddress = prefs.getProperty(UserPrefs.EMAIL_ADDRESS);
        myGivenName = prefs.getProperty(UserPrefs.GIVEN_NAME);
        myFamilyName = prefs.getProperty(UserPrefs.FAMILY_NAME);

        init();

        setPanel(page1);
        setVisible(true);
	}

    protected void init() {
        //setColor(new Color(223, 140, 120));
        setColor(ColloquiaConstants.color2);
        setHeading(LanguageManager.getString("17_7"));

        // Get the top-level people of this Activity
        topUpdatePeople = getTopUpdatePeople(topActivity);
        removeMe(topUpdatePeople);
        topInvitePeople = getTopInvitePeople(topActivity);
        removeMe(topInvitePeople);

        // Get People who are in sub-Activities but not in the top Activity
        subUpdatePeople = new Vector();
        getSubUpdatePeople(topActivity, subUpdatePeople);
        removeMe(subUpdatePeople);

        subInvitePeople = new Vector();
        getSubInvitePeople(topActivity, subInvitePeople);
        removeMe(subInvitePeople);

        // Now set up the pages
        page1 = new Page1();
        page2 = new Page2();
        page3 = new Page3();
        page4 = new Page4();
    }



    protected void buttonCancelClicked(WizardPage currentPage) {
        dispose();
    }


    /**
    * Hit the << Prev button in the dialog box
    */
    protected void buttonPreviousClicked(WizardPage currentPage){
        if(currentPage == null) return;
        switch(currentPage.getPageNum()) {
            case 2:
                setSize(530, 340);
                setPanel(page1);
                validate();
                break;
            case 3:
                setPanel(page2);
                break;
            case 4:
                setPanel(page3);
                break;
        }
    }


    /**
    * Hit the Next >> button in the dialog box
    */
    protected void buttonNextClicked(WizardPage currentPage) {
        if(currentPage == null) return;
        switch(currentPage.getPageNum()) {
            case 1:
                // Check Personal details
                if(page1.checkSenderDetails()) {
                    page2.set();
	               	setPanel(page2);
                    if(page1.getType() == BOTH) {
                		setSize(530, 600);
                		validate();
                        setLocationRelativeTo(MainFrame.getInstance());
                    }
                }
                break;
            case 2:
                // Get/Check top level people selected
                if(checkTopPeople()) {
                	page3.set();
                	setPanel(page3);
                }
                break;
            case 3:
                if(checkSubPeople()) setPanel(page4);
                break;
        }
    }

    /**
    * Hit the Finish button and send the messages
    */
    protected void buttonFinishClicked(WizardPage currentPage) {
        sendActivity(true);
    }

    /**
    * Hit the Outbox button
    */
    protected void buttonOutboxClicked(WizardPage currentPage) {
        sendActivity(false);
    }


    /**
    * Send the Activity to the Outbox
    */
    protected void sendActivity(boolean sendNow) {
        // Save some stuff that the user might have changed
        prefs.putProperty(UserPrefs.EMAIL_ADDRESS, page1.tfmyEmailAddress.getText());
        prefs.putProperty(UserPrefs.GIVEN_NAME, page1.tfmyGivenName.getText());
        prefs.putProperty(UserPrefs.FAMILY_NAME, page1.tfmyFamilyName.getText());

        // Check sender
        if(!page1.checkSenderDetails()) return;

        // Check top people
        if(!checkTopPeople()) return;

        // Check Sub people - do it anyway so we have a Vector
        boolean b = checkSubPeople();
        if(page1.sendSubs() && !b) return;

        // Put into OUTBOX FIRST!!!!!

        // UPDATE
        if(page1.getType() == UPDATE || page1.getType() == BOTH) {
            if(topUpdatePeopleSelected.size() + subUpdatePeopleSelected.size() > 0) {
	        	MessageInfo mInfo = makeUpdateMessageInfo();
    	    	Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, false);
            }
        }

        // INVITE
        if(page1.getType() == INVITE || page1.getType() == BOTH) {
            if(topInvitePeopleSelected.size() + subInvitePeopleSelected.size() > 0) {
	        	MessageInfo mInfo = makeInviteMessageInfo();
    	    	Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, false);
        	}
        }

        // CLose window first
        dispose();

        // Wait until all messages are in OUTBOX before sending
        if(sendNow) Outbox.getInstance().sendAllMail(MainFrame.getInstance());
    }

    /**
    * Make a MessageInfo class to send to the Outbox
    */
    protected MessageInfo makeUpdateMessageInfo() {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("0"));
        mInfo.setActivityID(topActivity.getGUID());
        mInfo.setActivityName(topActivity.getName());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(LanguageManager.getString("17_23"));
        if(page1.sendSubs()) mInfo.putProperty(MessageInfo.SEND_SUBS, "true");

        for(int i = 0; i < topUpdatePeopleSelected.size(); i++) {
            Person person = (Person)topUpdatePeopleSelected.elementAt(i);
            mInfo.addRecipient(person.getEmailAddress());
        }

        if(page1.sendSubs()) {
            for(int i = 0; i < subUpdatePeopleSelected.size(); i++) {
                Person person = (Person)subUpdatePeopleSelected.elementAt(i);
                mInfo.addRecipient(person.getEmailAddress());
            }
        }

        return mInfo;
    }

    /**
    * Make a MessageInfo class to send to the Outbox
    */
    protected MessageInfo makeInviteMessageInfo() {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_INVITE);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("14"));
        mInfo.setActivityID(topActivity.getGUID());
        mInfo.setActivityName(topActivity.getName());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(LanguageManager.getString("17_23"));
        if(page1.sendSubs()) mInfo.putProperty(MessageInfo.SEND_SUBS, "true");

        for(int i = 0; i < topInvitePeopleSelected.size(); i++) {
            Person person = (Person)topInvitePeopleSelected.elementAt(i);
            mInfo.addRecipient(person.getEmailAddress());
        }

        if(page1.sendSubs()) {
            for(int i = 0; i < subInvitePeopleSelected.size(); i++) {
                Person person = (Person)subInvitePeopleSelected.elementAt(i);
                mInfo.addRecipient(person.getEmailAddress());
            }
        }

        return mInfo;
    }

    /**
    * Returns a Vector of People who are members of the top Activity
    * These are people who have accepted
    */
    protected Vector getTopUpdatePeople(Activity A) {
        return A.getActivePeople();
    }

    /**
    * Returns a Vector of People who are members of the top Activity
    * These are people who are Pending
    */
    protected Vector getTopInvitePeople(Activity A) {
        return A.getPendingPeople();
    }

    /**
    * Returns a Vector of People who are members of sub-Activities but not the top Activity
    * These are people who have accepted
    */
    protected void getSubUpdatePeople(Activity A, Vector v) {
        Vector liveActivities = A.getLiveActivities();

        for(int i = 0; i < liveActivities.size(); i++) {
            Activity subA = (Activity)liveActivities.elementAt(i);
            Vector people = subA.getActivePeople();
            for(int j = 0; j < people.size(); j++) {
                Person person = (Person)people.elementAt(j);
                if(!topUpdatePeople.contains(person) && !v.contains(person))
                    v.addElement(person);
            }
            getSubUpdatePeople(subA, v);
        }
    }


    /**
    * Returns a Vector of People who are members of sub-Activities but not the top Activity
    * These are people who are Pending
    */
    protected void getSubInvitePeople(Activity A, Vector v) {
        Vector liveActivities = A.getLiveActivities();

        for(int i = 0; i < liveActivities.size(); i++) {
            Activity subA = (Activity)liveActivities.elementAt(i);
            Vector people = subA.getPendingPeople();
            for(int j = 0; j < people.size(); j++) {
                Person person = (Person)people.elementAt(j);
                if(!topInvitePeople.contains(person) && !v.contains(person)) v.addElement(person);
            }
            getSubInvitePeople(subA, v);
        }
    }

    /**
    * Check top people are valid
    */
    protected boolean checkTopPeople() {
        topUpdatePeopleSelected = page2.getTopUpdatePeopleSelected();
        topInvitePeopleSelected = page2.getTopInvitePeopleSelected();

        // Check we got at least one
        int num = topUpdatePeopleSelected.size() + topInvitePeopleSelected.size();
        if(num == 0) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            LanguageManager.getString("17_5"),
                            LanguageManager.getString("ERROR"),
                            JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check for e-mail addresses
        Vector v = new Vector();
        for(int i = 0; i < topUpdatePeopleSelected.size(); i++) {
            v.addElement(topUpdatePeopleSelected.elementAt(i));
        }
        for(int i = 0; i < topInvitePeopleSelected.size(); i++) {
            v.addElement(topInvitePeopleSelected.elementAt(i));
        }
        if(!checkPeopleEmail(v)) return false;

        return true;
    }

    /**
    * Check sub people are valid
    */
    protected boolean checkSubPeople() {
        // Get/Check orphans
        subUpdatePeopleSelected = page3.getSubUpdatePeopleSelected();
        subInvitePeopleSelected = page3.getSubInvitePeopleSelected();

        // Check for e-mail addresses
        Vector v = new Vector();
        for(int i = 0; i < subUpdatePeopleSelected.size(); i++) {
            v.addElement(subUpdatePeopleSelected.elementAt(i));
        }
        for(int i = 0; i < subInvitePeopleSelected.size(); i++) {
            v.addElement(subInvitePeopleSelected.elementAt(i));
        }
        if(!checkPeopleEmail(v)) return false;

        return true;
    }

    /**
    * Page 1 - The sender's details
    */
    class Page1 extends SendWizardPage {
        PTextField tfmyEmailAddress;
        PTextField tfmyGivenName;
        PTextField tfmyFamilyName;
        ButtonGroup bg;
    	MinkyRadioButton rb1, rb2, rb3;
        PCheckBox cbSubs;

        public Page1() {
            JPanel dPanel = new JPanel();
            dPanel.setOpaque(false);
            dPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);

            tfmyEmailAddress = new PTextField(myEmailAddress, 20);
            tfmyGivenName = new PTextField(myGivenName, 20);
            tfmyFamilyName = new PTextField(myFamilyName, 20);

            c.gridx = 0; c.gridy = 0;
            dPanel.add(new PLabel(LanguageManager.getString("17_8") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyEmailAddress, c);

            c.gridx = 0; c.gridy = 1;
            dPanel.add(new PLabel(LanguageManager.getString("17_9") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyGivenName, c);

            c.gridx = 0; c.gridy = 2;
            dPanel.add(new PLabel(LanguageManager.getString("17_10") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyFamilyName, c);

            add(dPanel);

            JPanel rbPanel = new JPanel();
            rbPanel.setOpaque(false);

        	rb1 = new MinkyRadioButton(LanguageManager.getString("17_48"));
        	rb2 = new MinkyRadioButton(LanguageManager.getString("17_49"));
        	rb3 = new MinkyRadioButton(LanguageManager.getString("17_50"));

            rb1.setEnabled(!topUpdatePeople.isEmpty());
            rb2.setEnabled(!topInvitePeople.isEmpty());
            rb3.setEnabled(!topUpdatePeople.isEmpty() && !topInvitePeople.isEmpty());

            rb1.setSelected(rb1.isEnabled());
            rb2.setSelected(rb2.isEnabled());

            bg = new ButtonGroup();
            bg.add(rb1);
            bg.add(rb2);
            bg.add(rb3);

        	rbPanel.add(rb1);
        	rbPanel.add(rb2);
        	rbPanel.add(rb3);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);
    		bottomPanel.add(rbPanel, BorderLayout.NORTH);

	        cbSubs = new PCheckBox(LanguageManager.getString("17_17"), topActivity.hasActivities());
            cbSubs.setEnabled(topActivity.hasActivities());
            JPanel cbPanel = new JPanel();
            cbPanel.setOpaque(false);
            cbPanel.add(cbSubs);
            bottomPanel.add(cbPanel, BorderLayout.CENTER);

            add(bottomPanel);
        }

        public int getType() {
        	return rb1.isSelected() ? 1 : rb2.isSelected() ? 2 : 3;
        }

        public boolean sendSubs() {
        	return cbSubs.isSelected();
        }

        /**
        * Make sure that the sender has filled in stuff
        */
        public boolean checkSenderDetails() {
            String errMsg = LanguageManager.getString("17_11") + ":\n";
            boolean correct = true;

            // Check my e-mail address
            if(tfmyEmailAddress.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_8") + ".\n";
                correct = false;
            }

            // Check my Given Name
            if(tfmyGivenName.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_9") + ".\n";
                correct = false;
            }

            // Check my Family Name
            if(tfmyFamilyName.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_10") + ".\n";
                correct = false;
            }

            if(!correct) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    errMsg,
                    LanguageManager.getString("ERROR"),
                    JOptionPane.ERROR_MESSAGE);
            }

            return correct;
        }

        public int getPageNum() {
            return 1;
        }

        public String getMessage() {
            return LanguageManager.getString("17_12") + LanguageManager.getString("17_13");
        }

        public boolean getBtnPreviousState() {
            return false;
        }

        public boolean getBtnNextState() {
            return true;
        }

        public boolean getBtnFinishState() {
            return false;
        }

        public boolean getBtnOutboxState() {
            return false;
        }

        public boolean getBtnCancelState() {
            return true;
        }
    } // END Page1 class


    /**
    * Page 2 - Top-level people
    */
    class Page2 extends SendWizardPage {
        JTable updateTable, inviteTable;
        int numPeople;
        JPanel panel1, panel2;

        public Page2() {
            panel1 = new JPanel(new BorderLayout());
            panel2 = new JPanel(new BorderLayout());
            panel1.setOpaque(false);
            panel2.setOpaque(false);
        	panel1.setBorder(new EmptyBorder(0, 20, 10, 20));
        	panel2.setBorder(new EmptyBorder(0, 20, 0, 20));
            panel1.add(new PLabel(LanguageManager.getString("17_14"), Color.black), BorderLayout.NORTH);
            panel2.add(new PLabel(LanguageManager.getString("17_15"), Color.black), BorderLayout.NORTH);

            numPeople = topUpdatePeople.size() + topInvitePeople.size();

            // UPDATE
            updateTable = new JTable(new TopPeopleTableModel(topUpdatePeople));
	        updateTable.setDefaultRenderer(Object.class, new ActivityPeopleTableRenderer(topActivity, topUpdatePeople));
            // Select all if there are any
            int num = topUpdatePeople.size();
            if(num > 0) updateTable.setRowSelectionInterval(0, num - 1);
            JScrollPane sp1 = new JScrollPane(updateTable);
            sp1.setColumnHeaderView(updateTable.getTableHeader());
            panel1.add(sp1, BorderLayout.CENTER);

            // INVITES
            inviteTable = new JTable(new TopPeopleTableModel(topInvitePeople));
	        inviteTable.setDefaultRenderer(Object.class, new ActivityPeopleTableRenderer(topActivity, topInvitePeople));
            // Select all if there are any
            num = topInvitePeople.size();
            if(num > 0) inviteTable.setRowSelectionInterval(0, num - 1);
            JScrollPane sp2 = new JScrollPane(inviteTable);
            sp2.setColumnHeaderView(inviteTable.getTableHeader());
            panel2.add(sp2, BorderLayout.CENTER);
        }

        public void set() {
        	removeAll();

            if(page1.getType() == BOTH) {
            	setLayout(new GridLayout(2, 1));
	            add(panel1);
    	        add(panel2);
            }

            else {
            	setLayout(new GridLayout(1, 1));
                if(page1.getType() == UPDATE) add(panel1);
                else add(panel2);
            }
        }

        /**
        * Get the top-level invited people selection
        */
        public Vector getTopInvitePeopleSelected() {
            Vector v = new Vector();

            if(page1.getType() == BOTH || page1.getType() == INVITE) {
                int[] selected = inviteTable.getSelectedRows();
                for(int i = 0; i < selected.length; i++) {
                    v.addElement(topInvitePeople.elementAt(selected[i]));
                }
            }

            return v;
        }

        /**
        * Get the top-level invited people selection
        */
        public Vector getTopUpdatePeopleSelected() {
            Vector v = new Vector();

            if(page1.getType() == BOTH || page1.getType() == UPDATE) {
                int[] selected = updateTable.getSelectedRows();
                for(int i = 0; i < selected.length; i++) {
                    v.addElement(topUpdatePeople.elementAt(selected[i]));
                }
			}

            return v;
        }

        /**
        * The Table Model for top-level people selection
        */
        protected class TopPeopleTableModel extends AbstractTableModel {
        	Vector topPeople;

        	public TopPeopleTableModel(Vector topPeople) {
            	this.topPeople = topPeople;
            }

            // getValueAt
            public Object getValueAt(int rowIndex, int columnIndex) {
                Person person = (Person)topPeople.elementAt(rowIndex);
                switch(columnIndex) {
                    case 0:
                        return person.getName();
                    case 1:
                        return person.getEmailAddress();
                    case 2:
                        Date date = topActivity.getDateLastSentActivity(person);
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
                return topPeople.size();
            }

            public String getColumnName(int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        return LanguageManager.getString("NAME");
                    case 1:
                        return LanguageManager.getString("EMAIL");
                    case 2:
                        return LanguageManager.getString("17_16");
                }
                return "";
            }
        }

        public int getPageNum() {
            return 2;
        }

        public String getMessage() {
            return LanguageManager.getString("17_18") + LanguageManager.getString("17_20");
        }

        public boolean getBtnPreviousState() {
            return true;
        }

        public boolean getBtnNextState() {
            if(!page1.sendSubs()) return false;
            else return numPeople > 0;
        }

        public boolean getBtnFinishState() {
            return numPeople > 0;
        }

        public boolean getBtnOutboxState() {
            return numPeople > 0;
        }

        public boolean getBtnCancelState() {
            return true;
        }
    } // END Page2


    /**
    * Page 3 - Possible sub-People in lower level Activities
    */
    class Page3 extends SendWizardPage {
        JTable updateTable, inviteTable;
        JPanel panel1, panel2;

        public Page3() {
            panel1 = new JPanel(new BorderLayout());
            panel2 = new JPanel(new BorderLayout());
            panel1.setOpaque(false);
            panel2.setOpaque(false);
        	panel1.setBorder(new EmptyBorder(0, 20, 10, 20));
        	panel2.setBorder(new EmptyBorder(0, 20, 0, 20));
            panel1.add(new PLabel(LanguageManager.getString("17_14"), Color.black), BorderLayout.NORTH);
            panel2.add(new PLabel(LanguageManager.getString("17_15"), Color.black), BorderLayout.NORTH);

            // UPDATE
            updateTable = new JTable(new SubPeopleTableModel(subUpdatePeople));
	        updateTable.setDefaultRenderer(Object.class, new ComponentTableRenderer(Person.class));
            // Select all if there are any
            int num = subUpdatePeople.size();
            if(num > 0) updateTable.setRowSelectionInterval(0, num - 1);
            JScrollPane sp1 = new JScrollPane(updateTable);
            sp1.setColumnHeaderView(updateTable.getTableHeader());
            panel1.add(sp1, BorderLayout.CENTER);

            // INVITES
            inviteTable = new JTable(new SubPeopleTableModel(subInvitePeople));
	        inviteTable.setDefaultRenderer(Object.class, new ComponentTableRenderer(Person.class));
            // Select all if there are any
            num = subInvitePeople.size();
            if(num > 0) inviteTable.setRowSelectionInterval(0, num - 1);
            JScrollPane sp2 = new JScrollPane(inviteTable);
            sp2.setColumnHeaderView(inviteTable.getTableHeader());
            panel2.add(sp2, BorderLayout.CENTER);
        }

        public void set() {
        	removeAll();

            if(page1.getType() == BOTH) {
            	setLayout(new GridLayout(2, 1));
	            add(panel1);
    	        add(panel2);
            }

            else {
            	setLayout(new GridLayout(1, 1));
                if(page1.getType() == UPDATE) add(panel1);
                else add(panel2);
            }
        }

        /**
        * Get the orphan selection for updates
        */
        public Vector getSubUpdatePeopleSelected() {
            Vector v = new Vector();

            if(page1.getType() == BOTH || page1.getType() == UPDATE) {
                int[] selected = updateTable.getSelectedRows();
                for(int i = 0; i < selected.length; i++) {
                    v.addElement(subUpdatePeople.elementAt(selected[i]));
                }
			}

            return v;
        }

        /**
        * Get the orphan selection for invites
        */
        public Vector getSubInvitePeopleSelected() {
            Vector v = new Vector();

            if(page1.getType() == BOTH || page1.getType() == INVITE) {
                int[] selected = inviteTable.getSelectedRows();
                for(int i = 0; i < selected.length; i++) {
                    v.addElement(subInvitePeople.elementAt(selected[i]));
                }
            }

            return v;
        }

        /**
        * The Table Model for orphan selection
        */
        protected class SubPeopleTableModel extends AbstractTableModel {
        	Vector subPeople;

        	public SubPeopleTableModel(Vector subPeople) {
            	this.subPeople = subPeople;
            }

            // getValueAt
            public Object getValueAt(int rowIndex, int columnIndex) {
                Person person = (Person)subPeople.elementAt(rowIndex);
                switch(columnIndex) {
                    case 0:
                        return person.getName();
                    case 1:
                        return person.getEmailAddress();
                }
                return "";
            }

            // getColumnCount
            public int getColumnCount() {
                return 2;
            }

            // getRowCount
            public int getRowCount() {
                return subPeople.size();
            }

            public String getColumnName(int columnIndex) {
                switch(columnIndex) {
                    case 0:
                        return LanguageManager.getString("NAME");
                    case 1:
                        return LanguageManager.getString("EMAIL");
                }
                return "";
            }
        }

        public int getPageNum() {
            return 3;
        }

        public String getMessage() {
            return LanguageManager.getString("17_21") + "  " + LanguageManager.getString("17_20");
        }

        public boolean getBtnPreviousState() {
            return true;
        }

        public boolean getBtnNextState() {
            return true;
        }

        public boolean getBtnFinishState() {
            return true;
        }

        public boolean getBtnOutboxState() {
            return true;
        }

        public boolean getBtnCancelState() {
            return true;
        }
    } // END Page3


    /**
    * Page 4 - Send the Activity
    */
    class Page4 extends SendWizardPage {
        public int getPageNum() {
            return 4;
        }

        public String getMessage() {
            return LanguageManager.getString("17_22");
        }

        public boolean getBtnPreviousState() {
            return true;
        }

        public boolean getBtnNextState() {
            return false;
        }

        public boolean getBtnFinishState() {
            return true;
        }

        public boolean getBtnOutboxState() {
            return true;
        }

        public boolean getBtnCancelState() {
            return true;
        }

    }

}

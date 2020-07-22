package net.colloquia.comms.wizard;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;


/**
* The main dialog wizard for sending a Resource
*/
public class SendResourceWizard
extends SendWizard
{
    private Page1 page1;
    private Page2 page2;

    private Resource resource;
    private ColloquiaContainer parentGroup;
    private Activity A;

    public SendResourceWizard(Resource resource, ColloquiaContainer parentGroup) {
        this.resource = resource;
        this.parentGroup = parentGroup;
        if(parentGroup instanceof Activity) A = (Activity)parentGroup;

        setHeading(LanguageManager.getString("17_29"));
        setColor(ColloquiaConstants.color2);

        page1 = new Page1();
        page2 = new Page2();

        setPanel(page1);
        setVisible(true);
    }

    /**
    * Page 1 - The list of people to send to
    */
    private class Page1 extends SendWizardPage {
        JTable table;
        Vector people;

        public Page1() {
            people = getPeopleToSendTo();
            int numPeople = people.size();

            setLayout(new BorderLayout());

            // Add table
            table = new JTable(new PeopleTableModel());
            JScrollPane sp = new JScrollPane(table);
            sp.setColumnHeaderView(table.getTableHeader());
            add(sp, BorderLayout.CENTER);

            // Select all if there are any
            if(numPeople > 0) table.setRowSelectionInterval(0, numPeople - 1);
        }

        /**
        * Get the people selected
        */
        public Vector getPeopleSelected() {
            Vector v = new Vector();
            int[] selected = table.getSelectedRows();

            for(int i = 0; i < selected.length; i++) {
                v.addElement(people.elementAt(selected[i]));
            }

            return v;
        }

        /**
        * The Table Model for people selection
        * Only people in Live Activities
        */
        private class PeopleTableModel extends AbstractTableModel {
            String columnNames[] = {
                LanguageManager.getString("7")
            };

            // getValueAt
            public Object getValueAt(int rowIndex, int columnIndex) {
                Person person = (Person)people.elementAt(rowIndex);
                switch(columnIndex) {
                    case 0:
                        return person.getProperty(Person.NAME);
                    default:
                        return "";
                }
            }

            // getColumnCount
            public int getColumnCount() {
                return columnNames.length;
            }

            // getRowCount
            public int getRowCount() {
                return people.size();
            }

            public String getColumnName(int columnIndex) {
                return columnNames[columnIndex];
            }
        }

        public int getPageNum() {
            return 1;
        }

        public String getMessage() {
            return LanguageManager.getString("17_30")  + LanguageManager.getString("17_31")
                + "  " + LanguageManager.getString("17_32");
        }

        public boolean getBtnPreviousState() {
            return false;
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

    }

    private class Page2 extends SendWizardPage {
        public int getPageNum() {
            return 2;
        }

        public String getMessage() {
            return LanguageManager.getString("17_33");
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

    /**
    * Return a Vector of People to Send the Resource to:
    *
    * If the parent group is a top level Resource Group, user can send to everyone
    * where this Resource occurs in an Activity that is mine (update)
    *
    * If the parent group is an Activity that is mine, again no problem.
    * If not mine but accepts Resources, user can send to owner of that Activity
    *
    * Only Active people are selected
    */
    private Vector getPeopleToSendTo() {
        Vector v = new Vector();
        if(resource == null || parentGroup == null) return v;

        	/*
        	Resource in one Activity
           	If the Activity is Mine, Return Active members
           	Else, submitter if accepts Resources
        	*/
        if(A != null) {
            if(A.isMine()) return A.getActivePeople();
            else if(A.acceptsResources()) {
            	Person person = DataModel.getPersonByEmailAddress(A.getSubmitter());
                if(person != null) v.addElement(person);
            }
        }

        	/*
        	Resource in ResourceGroup
        	Gather the Activities it is in and get these people
        	*/
        else {
            Vector activities = resource.getActivities();
            for(int i = 0; i < activities.size(); i++) {
                Activity Act = (Activity)activities.elementAt(i);
                if(Act.isMine()) {
                	Vector people = Act.getActivePeople();
                	for(int j = 0; j < people.size(); j++) {
                    	Person person = (Person)people.elementAt(j);
                    	if(!v.contains(person)) v.addElement(person);
                	}
                }
            }
        }

        return v;
    }


    /**
    * Hit the << Prev button in the dialog box
    */
    protected void buttonPreviousClicked(WizardPage currentPage){
        if(currentPage == null) return;
        switch(currentPage.getPageNum()) {
            case 2:
                setPanel(page1);
                break;
        }
    }

    protected void buttonNextClicked(WizardPage currentPage) {
        if(currentPage == null) return;
        switch(currentPage.getPageNum()) {
            case 1:
                setPanel(page2);
                break;
        }
    }

    protected void buttonOutboxClicked(WizardPage currentPage) {
        sendResource(false);
    }

    protected void buttonFinishClicked(WizardPage currentPage) {
        sendResource(true);
    }

    protected void buttonCancelClicked(WizardPage currentPage) {
        dispose();
    }

    /**
    * Send the Resource to the Outbox
    */
    private void sendResource(boolean sendNow) {
        // Do all the stages in btnNextClick here to account for user pressing
        // Finish button before reaching end of wizard sequence
        if(!checkPeopleSelected()) return;

        // Make a MessageInfo Class for the Outbox
        MessageInfo mInfo = makeMessageInfo();

        // Send to the Outbox
        Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, sendNow);

        dispose();
    }

    /**
    * Check that ther are people selected with e-mail addresses
    */
    private boolean checkPeopleSelected() {
        Vector peopleSelected = page1.getPeopleSelected();
        if(!checkSelectedPeopleCount(peopleSelected)) return false;
        if(!checkPeopleEmail(peopleSelected)) return false;
        return true;
    }

    /**
    * Make a MessageInfo class to send to the Outbox
    */
    private MessageInfo makeMessageInfo() {
        MessageInfo mInfo = new MessageInfo(MessageInfo.RESOURCE);

        // Set some properties
        mInfo.setSubject(resource.getName());
        mInfo.setComponentID(resource.getGUID());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(LanguageManager.getString("17_23"));
        mInfo.setPersonID("ME");
        if(A != null) {
        	mInfo.setActivityID(A.getGUID());
            mInfo.setActivityName(A.getName());
        }
        else mInfo.setActivityName(LanguageManager.getString("12_7"));

        // Recipients
        Vector peopleSelected = page1.getPeopleSelected();
        for(int i = 0; i < peopleSelected.size(); i++) {
            Person person = (Person)peopleSelected.elementAt(i);
            mInfo.addRecipient(person.getEmailAddress());
        }

        return mInfo;
    }
}
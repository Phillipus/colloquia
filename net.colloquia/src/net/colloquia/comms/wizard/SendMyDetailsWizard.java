package net.colloquia.comms.wizard;

import java.awt.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;


/**
 * The main dialog wizard for sending My Details
 */
public class SendMyDetailsWizard
extends SendWizard
{
    private Page1 page1;
    private Page2 page2;
    Activity currentActivity;

    public SendMyDetailsWizard(Activity currentActivity) {
        this.currentActivity = currentActivity;
        setHeading(LanguageManager.getString("17_24"));
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
            setLayout(new BorderLayout());

            // If we have selected an Activity on the Tree get the people in that
            // otherwise get all people in Live Activities
            // PENDING - we need to give the option to send to Completed People as well
            if(currentActivity == null) {
	            people = DataModel.getPeopleInLiveActivities();
            }
            else {
            	people = currentActivity.getActivePeople();
            }

            int numPeople = people.size();

            // Add table
            table = new JTable(new PeopleTableModel());
        	table.setDefaultRenderer(Object.class, new ColloquiaTableRenderer());
            JScrollPane sp = new JScrollPane(table);
            sp.setColumnHeaderView(table.getTableHeader());
            add(sp, BorderLayout.CENTER);

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
                LanguageManager.getString("7"),
                LanguageManager.getString("17_16")
            };

            // getValueAt
            public Object getValueAt(int rowIndex, int columnIndex) {
                Person person = (Person)people.elementAt(rowIndex);
                switch(columnIndex) {
                    case 0:
                        return person.getProperty(Person.NAME);
                    case 1:
                        Date date = person.getPropertyDate(Person.DATE_LAST_SENT_MYDETAILS);
                        if(date == null) return "";
                        else return DateFormat.getInstance().format(date);
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
            return LanguageManager.getString("17_25") + LanguageManager.getString("17_26") +
                LanguageManager.getString("17_27");
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
            return LanguageManager.getString("17_28");
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
    * Hit the << Prev button in the dialog box
    */
    protected void buttonPreviousClicked(WizardPage currentPage){
        if(currentPage == null) return;
        switch(currentPage.getPageNum()) {
            case 2:
                setPanel(page1);
                break;
            case 3:
                setPanel(page2);
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
        sendMyDetails(false);
    }

    protected void buttonFinishClicked(WizardPage currentPage) {
        sendMyDetails(true);
    }

    protected void buttonCancelClicked(WizardPage currentPage) {
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
    * Send the Message to the Outbox
    */
    private void sendMyDetails(boolean sendNow) {
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
    * Make a MessageInfo class to send to the Outbox
    */
    private MessageInfo makeMessageInfo() {
        MessageInfo mInfo = new MessageInfo(MessageInfo.PERSON);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("MY_DETAILS"));
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(LanguageManager.getString("17_23"));
        mInfo.setPersonID("ME");
        mInfo.setActivityName(currentActivity == null ? LanguageManager.getString("12_7") : currentActivity.getName());

        // Recipients
        Vector peopleSelected = page1.getPeopleSelected();
        for(int i = 0; i < peopleSelected.size(); i++) {
            Person person = (Person)peopleSelected.elementAt(i);
            mInfo.addRecipient(person.getEmailAddress());
        }

        // Photograph as attachment
        // PENDING - check if allowed to send
        String fileName = UserPrefs.getUserPrefs().getProperty(UserPrefs.PHOTOGRAPH);
        if(!fileName.equals("")) mInfo.addAttachment(fileName);

        return mInfo;
    }
}

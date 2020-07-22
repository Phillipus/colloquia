package net.colloquia.comms.wizard;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;


/**
* The abstract class wizard for sending an Entity
*/
public abstract class SendWizard
extends Wizard
{
    protected JButton btnOutbox;

    protected SendWizard() {
        super();
        setSize(530, 340);
        setLocationRelativeTo(MainFrame.getInstance());
    }

    /**
    * Over-ride definition of a wizard page
    */
    abstract class SendWizardPage extends Wizard.WizardPage {
        public abstract boolean getBtnOutboxState();
    }

    /**
    * Set the wizard panel
    */
    protected void setPanel(SendWizardPage page) {
        super.setPanel(page);
        btnOutbox.setEnabled(page.getBtnOutboxState());
    }


    /**
    * Construct the bottom button panel
    */
    protected void addButtons() {
        // Super!!
        super.addButtons();
        // Add OutBox button
        btnOutbox = new JButton(LanguageManager.getString("OUTBOX"));
        buttonPanel.add(btnOutbox, 2);
        btnOutbox.addActionListener(this);
        // Over-ride the Finish button to be "Send Now"
        btnFinish.setText(LanguageManager.getString("17_4"));
    }


    /**
    * Click a button listener over-ride
    */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(btnOutbox)) buttonOutboxClicked(currentPage);
        else super.actionPerformed(e);
    }

    protected abstract void buttonOutboxClicked(WizardPage currentPage);

    /**
    * Check that there is a selection of people to send to
    */
    protected boolean checkSelectedPeopleCount(Vector peopleSelected) {
        if(peopleSelected.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                            LanguageManager.getString("17_5"),
                            LanguageManager.getString("ERROR"),
                            JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else return true;
    }

    /**
    * Check that there are people with e-mail addresses
    */
    protected boolean checkPeopleEmail(Vector people) {
        Person person;
        String msg = LanguageManager.getString("17_6");
        boolean nogo = false;

        for(int i = 0; i < people.size(); i++) {
            person = (Person)people.elementAt(i);
            if(person.getEmailAddress().equals("")) {
                msg += "\n" + person.getName();
                nogo = true;
            }
        }

        // And show a suitable message
        if(nogo) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), msg,
                LanguageManager.getString("ERROR"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
    * Just in case I added Me as a Person, remove me
    */
    protected void removeMe(Vector people) {
        // Check I haven't added myself
        UserPrefs prefs = UserPrefs.getUserPrefs();
        for(int i = people.size() - 1; i >= 0; i--) {
            Person person = (Person)people.elementAt(i);
            if(prefs.isMyEmailAddress(person.getEmailAddress())) people.removeElement(person);
        }
    }
}

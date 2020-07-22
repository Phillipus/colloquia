package net.colloquia.menu;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * Import a tab delimited file of people
 * 6 Fields:-
 * 0 Family Name
 * 1 Given Name
 * 2 Address
 * 3 Phone
 * 4 E-mail address
 * 5 ID
 *
 */
public class Action_ImportPeople
extends MenuAction
implements Runnable, ComponentSelectionListener
{
    private Thread thread;
    private File fileName;
    private PMonitor monitor;

    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    private static int NUM_FIELDS = 6;

    public static final int FAMILY_NAME = 0;
    public static final int GIVEN_NAME = 1;
    public static final int ADDRESS = 2;
    public static final int PHONE_NUMBER = 3;
    public static final int EMAIL = 4;
    public static final int ID = 5;

    static String name = LanguageManager.getString("ACTION_10");

    public Action_ImportPeople() {
        super(name + "...", ColloquiaConstants.iconPerson);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }

    /**
     * Triggered from the Menu.  Calls {@link #importPeople}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        importPeople();
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
    }

    /**
     * Starts the thread to import people.
     */
    private void importPeople() {
        // Get the file name
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(LanguageManager.getString("ACTION_19"));
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return;
        fileName = chooser.getSelectedFileAndStore();

        // Set up the progress monitor
        monitor = new PMonitor(MainFrame.getInstance(), name,
                                LanguageManager.getString("ACTION_12"),
                                LanguageManager.getString("7"));
        monitor.init(Utils.countLines(fileName) - 1);

        // Start the thread (calls run())
        thread = new Thread(this);
        thread.start();
    }

    /**
     * This is the entry point for the thread.
     */
    public void run() {
        BufferedReader in;
        String inLine;
        StringTokenizer t;
        String[] fields = new String[NUM_FIELDS];
        Person person;

        try {
            in = new BufferedReader(new FileReader(fileName));

            while((inLine = in.readLine()) != null) {
                // Update progress monitor
                monitor.incProgress(1, true);
                if(monitor.isCanceled()) break;

                // Check for null fields
                inLine = Utils.padInLine(inLine);

                t = new StringTokenizer(inLine, "\t");

                // Blank line
                if(t.countTokens() == 0) continue;

                // Blank fields
                for(int i = 0; i < NUM_FIELDS; i++) fields[i] = "";

                try {
                    // Get the line data
                    for(int i = 0; i < NUM_FIELDS; i++) {
                        fields[i] = t.nextToken().trim();
                    }
                }
                catch (NoSuchElementException ex) {
                } // Ignore extra/missing elements

                // At least one field
                if(fields[FAMILY_NAME] == null || fields[FAMILY_NAME].equals("")) continue;

                // Default name
                String name = fields[FAMILY_NAME] + ", " + fields[GIVEN_NAME];

                monitor.setNote(name);

                // Do we have this person already?
                person = getPerson(fields);

                // Make new person
                if(person == null) person = new Person(name, null);

                // Update fields
                person.putProperty(Person.FAMILY_NAME, fields[FAMILY_NAME], true);
                person.putProperty(Person.GIVEN_NAME, fields[GIVEN_NAME], true);
                person.putProperty(Person.ADDRESS, fields[ADDRESS], true);
                person.putProperty(Person.PHONE_NUMBER, fields[PHONE_NUMBER], true);
                person.putProperty(Person.EMAIL, fields[EMAIL], true);
                person.putProperty(Person.ID, fields[ID], true);

                DataModel.addComponent(person, DataModel.getPeopleGroup(), false);
            }

            in.close();
        } catch (Exception ex) {
            monitor.close();
            ErrorHandler.showWarning("ACTION_17", ex, "ACTION_16");
            return;
        }

        ViewPanel.getInstance().repaintCurrentView();

        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                    LanguageManager.getString("ACTION_18"),
                                    LanguageManager.getString("ACTION_10"),
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Check whether this person exists in our data
     * Returns the person if they do
     */
    private Person getPerson(String[] fields) {
        Vector people = DataModel.getAllPeople();
        for(int i = 0; i < people.size(); i++) {
            Person person = (Person)people.elementAt(i);

            // Check e-mail address first (if it's not blank)
            if(!fields[EMAIL].equals("")) {
                if(person.getEmailAddress().equalsIgnoreCase(fields[EMAIL])) return person;
            }

            // Check ID (if it's not blank)
            if(!fields[ID].equals("")) {
                if(person.getProperty(Person.ID).equalsIgnoreCase(fields[ID])) return person;
            }

            // Else, check on Family Name & Given Name & Address
            if(fields[FAMILY_NAME].equalsIgnoreCase(person.getProperty(Person.FAMILY_NAME)) &&
                   fields[GIVEN_NAME].equalsIgnoreCase(person.getProperty(Person.GIVEN_NAME)) &&
                   fields[ADDRESS].equalsIgnoreCase(person.getProperty(Person.ADDRESS)))
                   return person;
        }

        return null;
    }
}

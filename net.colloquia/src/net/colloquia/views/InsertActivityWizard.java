package net.colloquia.views;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;


/**
 * The main dialog wizard for inserting an Activity
 */
public class InsertActivityWizard
extends Wizard
{
    private ColloquiaContainer parentGroup;
    private boolean isSubActivity;
    private boolean isTemplate;

    private Page1 page1;
    private Page2 page2;
    private Page3 page3;
    private Page4 page4;

    public InsertActivityWizard(ColloquiaContainer parentGroup) {
        this.parentGroup = parentGroup;

        // Is this going to be a sub-Activity?
        isSubActivity = parentGroup instanceof Activity;

        // Is Parent a Template?
        if(isSubActivity) isTemplate = ((Activity)parentGroup).isTemplate();
        else isTemplate = parentGroup instanceof TemplateGroup;

        setHeading(LanguageManager.getString("NEW") + " " + LanguageManager.getString("0"));

        page1 = new Page1();
        page2 = new Page2();
        page3 = new Page3();
        page4 = new Page4();

        setPanel(page1);

        setSize(400, 300);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    /**
    * Page 1 -
    */
    private class Page1 extends WizardPage {
        private JTextField tField;
        private JCheckBox cbInheritPeople;
        private JCheckBox cbAcceptResources;

        public Page1() {
            PLabel label = new PLabel(LanguageManager.getString("NAME") + ":");
            add(label);
            tField = new JTextField(20);
            add(tField);

            cbInheritPeople = new JCheckBox(LanguageManager.getString("18_3"));
            cbInheritPeople.setEnabled(isSubActivity);
            add(cbInheritPeople);

            cbAcceptResources = new JCheckBox(LanguageManager.getString("21_14"));
            add(cbAcceptResources);

            tField.requestFocus();
        }

        public boolean inheritsPeople() {
            return cbInheritPeople.isSelected() && isSubActivity;
        }

        public boolean acceptsResources() {
            return cbAcceptResources.isSelected();
        }

        public String getName() {
            return tField.getText().trim();
        }

        public int getPageNum() {
            return 1;
        }

        public String getMessage() {
            return LanguageManager.getString("18_1") + LanguageManager.getString("18_2");
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

        public boolean getBtnCancelState() {
            return true;
        }
    }


    /**
    * Page 2 - People
    */
    private class Page2 extends WizardPage {
        private SelectionTree peopleTree;

        public Page2() {
            setLayout(new BorderLayout());
            peopleTree = new SelectionTree(DataModel.getPeopleGroup());

            if(!isTemplate) {
                add(new JScrollPane(peopleTree), BorderLayout.CENTER);
            }
        }

        public Vector getSelectedComponents() {
            return peopleTree.getSelectedComponents();
        }

        public int getPageNum() {
            return 2;
        }

        public String getMessage() {
            if(isTemplate) return LanguageManager.getString("18_8") + "\n\n" +
                LanguageManager.getString("18_9");
            else return LanguageManager.getString("18_5") + "\n\n" +
                LanguageManager.getString("18_9");
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

        public boolean getBtnCancelState() {
            return true;
        }

    }

    /**
    * Page 3 - Resources
    */
    private class Page3 extends WizardPage {
        private SelectionTree resourceTree;

        public Page3() {
            setLayout(new BorderLayout());
            resourceTree = new SelectionTree(DataModel.getResourceGroup());
            add(new JScrollPane(resourceTree), BorderLayout.CENTER);
        }

        public Vector getSelectedComponents() {
            return resourceTree.getSelectedComponents();
        }

        public int getPageNum() {
            return 3;
        }

        public String getMessage() {
            return LanguageManager.getString("18_6");
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

        public boolean getBtnCancelState() {
            return true;
        }

    }


    /**
    * Page 4 - Assignment
    */
    private class Page4 extends WizardPage {
        private JTextField tField;

        public Page4() {
            PLabel label = new PLabel(LanguageManager.getString("NAME") + ":");
            add(label);
            tField = new JTextField(20);
            add(tField);
            tField.requestFocus();
        }

        public String getName() {
            return tField.getText().trim();
        }

        public int getPageNum() {
            return 4;
        }

        public String getMessage() {
            return LanguageManager.getString("18_7");
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

        public boolean getBtnCancelState() {
            return true;
        }

    }


    /**
    *  Simply add the new Activity from page 1
    */
    private Activity addActivity() {
        String name = page1.getName();
        if(name == null || name.length() == 0) return null;

        boolean inheritsPeople = page1.inheritsPeople();
        boolean acceptsResources = page1.acceptsResources();

        // Add the new Activity
        Activity A = new Activity(name, null);
        A.setInheritsPeople(inheritsPeople, false);
        A.setAcceptsResources(acceptsResources, false);
        A.setTimeStamp();
        DataModel.addComponent(A, parentGroup, true);

        // The new Activity inherits its parent's People
        // We have to do this AFTER adding the Activity
        if(inheritsPeople) {
            Activity parentA = (Activity)A.getParent();
            Vector people = parentA.getAllPeople();
            for(int i = 0; i < people.size(); i++) {
                Person person = (Person)people.elementAt(i);
                DataModel.addComponent(person, A, false);
            }
        }

        return A;
    }

    /**
    * Add any selected Components
    */
    private void addSelectedComponents(Activity A, Vector components) {
        if(A == null || components == null) return;

        for(int i = 0; i < components.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
            // Don't add groups
            if(!(tc instanceof ColloquiaContainer)) {
                DataModel.addComponent(tc, A, false);
            }
        }
    }

    /**
    * Add an Assignment
    */
    private void addAssignment(Activity A, String name) {
        if(A == null || name.length() == 0) return;
        Assignment assignment = new Assignment(name, null);
        DataModel.addComponent(assignment, A, false);
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
                setPanel(page1);
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
                String name = page1.getName();
                if(name.length() > 0) setPanel(page2);
                else Toolkit.getDefaultToolkit().beep();
                break;
            case 2:
                setPanel(page3);
                break;
            case 3:
                setPanel(page4);
                break;
        }
    }

    /**
    * Hit the Finish button
    * What to do will depend on what page we're on when we hit it
    */
    protected void buttonFinishClicked(WizardPage currentPage) {
        if(currentPage == null) return;

        // Add Activity
        Activity A = addActivity();

        switch(currentPage.getPageNum()) {
            case 4:
                addAssignment(A, page4.getName());
                // Fall thru
            case 3:
                addSelectedComponents(A, page3.getSelectedComponents());
                // Fall thru
            case 2:
                addSelectedComponents(A, page2.getSelectedComponents());
                break;
        }

        if(A != null) ComponentTransferManager.insertedActivity(getClass(), A, parentGroup);
        dispose();
    }
}
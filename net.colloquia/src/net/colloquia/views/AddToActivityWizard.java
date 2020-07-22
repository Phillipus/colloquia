package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;


/**
 */
public class AddToActivityWizard
extends JDialog
{
    private Activity A;
    private SelectionTree peopleTree;
    private SelectionTree resourceTree;
    protected JButton btnOK;
    protected JButton btnCancel;
    private JCheckBox cbInheritPeople;
    private boolean addPeople;

    public AddToActivityWizard(Activity A) {
        super(MainFrame.getInstance(), true);
        setTitle(LanguageManager.getString("ACTION_20") + " - " + A.getName());

        this.A = A;

        addPeople = !A.isTemplate() && A.isMine();

        JPanel treePanel = new JPanel();
        JPanel peoplePanel = null;

        // people
        if(addPeople) {
            treePanel.setLayout(new GridLayout(1, 2));
            peoplePanel = createPeopleTree();
        }
        // No people
        else {
            treePanel.setLayout(new GridLayout(1, 1));
        }

        JPanel resourcesPanel = createResourceTree();

        if(peoplePanel != null) treePanel.add(peoplePanel);
        treePanel.add(resourcesPanel);

        getContentPane().add(treePanel, BorderLayout.CENTER);
        getContentPane().add(constructButtonPanel(), BorderLayout.SOUTH);

        setSize(600, 470);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    private JPanel createPeopleTree() {
        JPanel panel = new JPanel(new BorderLayout());
        peopleTree = new SelectionTree(DataModel.getPeopleGroup());
        panel.add(new JScrollPane(peopleTree), BorderLayout.CENTER);
        return panel;
    }


    private JPanel createResourceTree() {
        JPanel panel = new JPanel(new BorderLayout());
        resourceTree = new SelectionTree(DataModel.getResourceGroup());
        panel.add(new JScrollPane(resourceTree), BorderLayout.CENTER);
        return panel;
    }

    /**
    * Construct the bottom button panel
    */
    private JPanel constructButtonPanel() {
        ButtonClickListener btnClicker = new ButtonClickListener();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
        btnOK.addActionListener(btnClicker);
        getRootPane().setDefaultButton(btnOK);

        btnCancel = new JButton(LanguageManager.getString("CANCEL"));
        buttonPanel.add(btnCancel);
        btnCancel.addActionListener(btnClicker);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 20, 0, 20));

        if(addPeople) {
            cbInheritPeople = new JCheckBox(LanguageManager.getString("20_1"));
            panel.add(cbInheritPeople);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void buttonCancelClicked() {
        dispose();
    }

    /**
    * Put on a thread so that the dialog can close
    */
    private void buttonOKClicked() {
        Runnable doRun = new Runnable() {
            public void run() {
                Vector v = new Vector();
                Vector people = new Vector();
                if(peopleTree != null) people = peopleTree.getSelectedComponents();
                Vector resources = resourceTree.getSelectedComponents();

                for(int i = 0; i < people.size(); i++) {
                    ColloquiaComponent tc = (ColloquiaComponent)people.elementAt(i);
                    // Don't add groups or existing ones
                    if(!(tc instanceof ColloquiaContainer) && !A.hasMember(tc)) {
                        v.addElement(tc);
                    }
                }

                for(int i = 0; i < resources.size(); i++) {
                    ColloquiaComponent tc = (ColloquiaComponent)resources.elementAt(i);
                    // Don't add groups or existing ones
                    if(!(tc instanceof ColloquiaContainer) && !A.hasMember(tc)) {
                        v.addElement(tc);
                    }
                }

                if(!v.isEmpty()) {
                    boolean inheritPeople = cbInheritPeople == null ? false : cbInheritPeople.isSelected();
                    ComponentTransferManager.insertComponents(A, v, inheritPeople);
                }
            }
        };

        SwingUtilities.invokeLater(doRun);
        dispose();
    }

    /**
    * Click a button listener
    */
    private class ButtonClickListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(btnCancel)) buttonCancelClicked();
            else if(e.getSource().equals(btnOK)) buttonOKClicked();
        }
    }
}

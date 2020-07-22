package net.colloquia.views.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

/**
 * A Panel to hold the Tree and the Button Panel
 */
public class TreePanel extends JPanel {
    private static final TreePanel instance = new TreePanel(); // This

    private ColloquiaToolBar buttonPanel;
    public PToggleButton peopleButton, resourceButton, assignmentButton;
    public PToggleButton completedButton;

    // Fields for toggle buttons
    public static final int PEOPLE = 0;
    public static final int RESOURCES = 1;
    public static final int ASSIGNMENTS = 2;
    public static final int COMPLETED = 3;
    //public static final int FUTURE = 4;
    public static final int LIVE = 5;

    private TreePanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(ColloquiaTree.getInstance()), BorderLayout.CENTER);
        buttonPanel = new ColloquiaToolBar(ColloquiaConstants.color1);
        add(buttonPanel, BorderLayout.NORTH);
        addButtons(buttonPanel);
    }

    public static TreePanel getInstance() {
        return instance;
    }

    private void addButtons(JToolBar buttonPanel) {
        peopleButton = new PToggleButton(PEOPLE);
        buttonPanel.add(peopleButton);
        resourceButton = new PToggleButton(RESOURCES);
        buttonPanel.add(resourceButton);
        assignmentButton = new PToggleButton(ASSIGNMENTS);
        buttonPanel.add(assignmentButton);
        buttonPanel.addSeparator();
        completedButton = new PToggleButton(COMPLETED);
        buttonPanel.add(completedButton);
    }


    /**
    * A Dinky Pinky little ToggleButton that will show/hide nodes in the TreeModel
    */
    private class PToggleButton extends JToggleButton {
        private int type;
        private int state;

        public PToggleButton(int type) {

            switch(type) {
                case PEOPLE:
                    setIcon(Utils.getIcon(ColloquiaConstants.iconHidePeople));
                    setSelectedIcon(Utils.getIcon(ColloquiaConstants.iconHidePeople + "c"));
                    setRolloverIcon(Utils.getIcon(ColloquiaConstants.iconHidePeople + "r"));
                    setToolTipText(LanguageManager.getString("12_11"));
                    break;
                case RESOURCES:
                    setIcon(Utils.getIcon(ColloquiaConstants.iconHideResources));
                    setSelectedIcon(Utils.getIcon(ColloquiaConstants.iconHideResources + "c"));
                    setRolloverIcon(Utils.getIcon(ColloquiaConstants.iconHideResources + "r"));
                    setToolTipText(LanguageManager.getString("12_12"));
                    break;
                case ASSIGNMENTS:
                    setIcon(Utils.getIcon(ColloquiaConstants.iconHideAssignments));
                    setSelectedIcon(Utils.getIcon(ColloquiaConstants.iconHideAssignments + "c"));
                    setRolloverIcon(Utils.getIcon(ColloquiaConstants.iconHideAssignments + "r"));
                    setToolTipText(LanguageManager.getString("12_13"));
                    break;
                case COMPLETED:
                    setIcon(Utils.getIcon(ColloquiaConstants.iconHideCompleted));
                    setSelectedIcon(Utils.getIcon(ColloquiaConstants.iconHideCompleted + "c"));
                    setRolloverIcon(Utils.getIcon(ColloquiaConstants.iconHideCompleted + "r"));
                    setToolTipText(LanguageManager.getString("12_15"));
                    break;
            }

            this.type = type;
            setBorder(new javax.swing.border.EmptyBorder(2, 2, 2, 2));
            setContentAreaFilled(false);
            setFocusPainted(false);

            addMouseListener(new MouseA());
            addActionListener(new Popper());
        }

        public int getType() {
            return type;
        }

        private class MouseA extends MouseAdapter {
            public void mouseEntered(MouseEvent e) {
                if(MainFrame.getInstance() != null)
                    MainFrame.getInstance().statusBar.setText(getToolTipText());
            }

            public void mouseExited(MouseEvent e) {
                if(MainFrame.getInstance() != null)
                    MainFrame.getInstance().statusBar.clearText();
            }
        }

        private class Popper extends AbstractAction {
            ColloquiaTree tree;
            boolean show;
            Runner thread;

            public void actionPerformed(ActionEvent e) {
                thread = new Runner();
                SwingUtilities.invokeLater(thread);
            }

            private class Runner implements Runnable {
                public void run() {
                    MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
                    tree = ColloquiaTree.getInstance();
                    getState();

                    // Get ALL activities
                    Vector activities = DataModel.getAllActivities();

                    for(int i = 0; i < activities.size(); i++) {
                        Activity A = (Activity)activities.elementAt(i);

                        switch(type) {
                            case TreePanel.COMPLETED:
                                if(A.isCompleted()) setActivity(A);
                                break;
                            case TreePanel.PEOPLE:
                                setChildren(A, TreePanel.PEOPLE);
                                break;
                            case TreePanel.RESOURCES:
                                setChildren(A, TreePanel.RESOURCES);
                                break;
                            case TreePanel.ASSIGNMENTS:
                                setChildren(A, TreePanel.ASSIGNMENTS);
                                break;
                            default:
                                return;
                        }
                    }

                    MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
                }
            }

            private void setActivity(Activity A) {
                if(show) {
                    tree.addComponent(A, A.getParent());
                    if(tree.peopleShown) setChildren(A, TreePanel.PEOPLE);
                    if(tree.resourcesShown) setChildren(A, TreePanel.RESOURCES);
                    if(tree.assignmentsShown) setChildren(A, TreePanel.ASSIGNMENTS);
                }
                else tree.removeComponent(A, A.getParent());
            }


            private void setChildren(Activity A, int type) {
                Vector members = null;
                switch(type) {
                    case TreePanel.PEOPLE:
                        members = tree.isPeopleVisible(A) ? A.getAllPeople() : null;
                        break;
                    case TreePanel.RESOURCES:
                        members = tree.isResourcesVisible(A) ? A.getResources() : null;
                        break;
                    case TreePanel.ASSIGNMENTS:
                        members = tree.isAssignmentVisible(A) ? A.getAssignments() : null;
                        break;
                    default:
                        return;
                }

                if(members != null) for(int i = 0; i < members.size(); i++) {
                    ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
                    if(show) tree.addComponent(tc, A);
                    else tree.removeComponent(tc, A);
                }
            }

            private void getState() {
                switch(type) {
                    case TreePanel.PEOPLE:
                        show = (tree.peopleShown = !tree.peopleShown);
                        break;

                    case TreePanel.RESOURCES:
                        show = (tree.resourcesShown = !tree.resourcesShown);
                        break;

                    case TreePanel.ASSIGNMENTS:
                        show = (tree.assignmentsShown = !tree.assignmentsShown);
                        break;

                    case TreePanel.COMPLETED:
                        show = (tree.completedShown = !tree.completedShown);
                        break;

                    case TreePanel.LIVE:
                        show = (tree.liveShown = !tree.liveShown);
                        break;

                    default:
                        show = true;
                }
            }
        }

    }
}





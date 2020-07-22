package net.colloquia.menu;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

public class Action_Completed
extends MenuAction
implements MainMenuItem, ComponentSelectionListener
{
    protected ColloquiaComponent selComponent;
    protected Activity currentActivity;

    public Action_Completed() {
        super(LanguageManager.getString("BUT12"), ColloquiaConstants.iconActivityCompleted,
            MainFrame.getInstance().statusBar);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
        setButtonText(LanguageManager.getString("BUT12"));
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.currentActivity = currentActivity;
        refresh();
    }

    public void refresh() {
        if(selComponent == currentActivity) setEnabled(currentActivity.canBeMadeCompleted());
        else setEnabled(false);
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        if(selComponent == null) return;
        if(!(selComponent instanceof Activity)) return;
        Activity A = (Activity)selComponent;

        // Ask first
        String msg;
        if(A.isMine()) msg = LanguageManager.getString("17_52");
        else msg = LanguageManager.getString("17_53");

        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            msg,
            LanguageManager.getString("17_51"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        A.setCompleted();

        MessageInfo mInfo;

        // My Activity
        if(A.isMine()) {
        	mInfo = makeTutorMessageInfo(A);
        }

        else {
        	mInfo = makeStudentMessageInfo(A);
        }

        if(mInfo != null) Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, true);

        // Must repaint tree
        ColloquiaTree.getInstance().reselectCurrentNode();
        ColloquiaTree.getInstance().repaint();
        // And update menu items
        MainFrame.getInstance().mainMenu.refreshAll();
    }

    /**
     * Make a MessageInfo class to send to the Students
     */
    protected MessageInfo makeTutorMessageInfo(Activity A) {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_COMPLETE);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("17_51"));
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
     * Make a MessageInfo class to send to the Tutor
     */
    protected MessageInfo makeStudentMessageInfo(Activity A) {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_COMPLETE);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("17_51"));
        mInfo.setActivityID(A.getGUID());
        mInfo.setActivityName(A.getName());
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(A.getSubmitter());
        mInfo.addRecipient(A.getSubmitter());

        return mInfo;
    }
}
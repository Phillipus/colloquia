package net.colloquia.comms.editors;

import java.awt.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;

public class AssignmentMessageEditor
extends EMailEditor
{
    protected Activity A;
    protected Person person;

    /**
     * Constructor for brand new message
     */
    public AssignmentMessageEditor(Activity A, Person person) {
        super();

        this.A = A;
        this.person = person;

        mInfo = new MessageInfo(MessageInfo.ASSIGNMENT_MESSAGE);
        mInfo.setActivityID(A.getGUID());
        mInfo.setActivityName(A.getName());
        mInfo.setPersonID(person.getGUID());
        mInfo.setTo(person.getEmailAddress());

        init();

        subjectBox.grabFocus();
    }

    /**
     * Constructor for brand new message and replying to Message mInfo
     */
    public AssignmentMessageEditor(Activity A, Person person, MessageInfo mInfo) {
        this(A, person);
        // Set Re: subject
        setReSubject(mInfo.getSubject());
        // Quote original message?
        if(UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.MESSAGE_QUOTE)) setQuoteMessage(mInfo);
    }

    /**
     * Constructor for editing existing message
     */
    protected AssignmentMessageEditor(MessageInfo mInfo) {
        super();
        A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        person = (Person)DataModel.getComponent(mInfo.getPersonID());
        loadMessage(mInfo);
    }

    protected String getFrameTitle() {
        return LanguageManager.getString("14_4") + " " + person.getName() + " ("
            + person.getEmailAddress() + ")";
    }

    protected Image getFrameIcon() {
        return MessageInfo.getIcon(MessageInfo.ASSIGNMENT_MESSAGE).getImage();
    }

}

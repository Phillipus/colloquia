package net.colloquia.comms.editors;

import java.awt.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;


public class GroupMessageEditor
extends EMailEditor
{
    protected Activity A;

    /**
     * Constructor for brand new message
     */
    public GroupMessageEditor(Activity A) {
        super();

        this.A = A;

        mInfo = new MessageInfo(MessageInfo.GROUP_MESSAGE);
        mInfo.setTo(LanguageManager.getString("13"));
        mInfo.setActivityID(A.getGUID());
        mInfo.setActivityName(A.getName());

        init();

        subjectBox.grabFocus();
    }

    /**
     * Constructor for brand new message and replying to a Message mInfo
     */
    public GroupMessageEditor(Activity A, MessageInfo mInfo) {
        this(A);
        // Set Re: subject
        setReSubject(mInfo.getSubject());
        // Quote original message?
        if(UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.MESSAGE_QUOTE)) setQuoteMessage(mInfo);
    }

    /**
     * Constructor for editing existing message
     */
    protected GroupMessageEditor(MessageInfo mInfo) {
        super();
        A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        loadMessage(mInfo);
    }

    protected String getFrameTitle() {
        return LanguageManager.getString("14_3") + " " + A.getName();
    }

    protected Image getFrameIcon() {
        return MessageInfo.getIcon(MessageInfo.GROUP_MESSAGE).getImage();
    }

}

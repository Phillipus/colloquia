package net.colloquia.datamodel.entities;

import net.colloquia.prefs.*;

public class SentAssignment
extends SentComponent
{
    public SentAssignment(Assignment ass) {
        super(ass, Assignment.XMLStartTag, Assignment.XMLEndTag);
        // Set Submitter to my e-mail address if this is mine
        if(ass.isMine()) setProperty(Assignment.SUBMITTER, UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_ADDRESS));
    }
}
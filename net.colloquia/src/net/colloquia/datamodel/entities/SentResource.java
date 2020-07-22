package net.colloquia.datamodel.entities;

import net.colloquia.prefs.*;

/**
*/
public class SentResource
extends SentComponent
{
    public SentResource(Resource resource) {
        super(resource, Resource.XMLStartTag, Resource.XMLEndTag);
        // Set Submitter to my e-mail address if this is mine
        if(resource.isMine()) setProperty(Resource.SUBMITTER, UserPrefs.getUserPrefs().getProperty(UserPrefs.EMAIL_ADDRESS));
    }
}
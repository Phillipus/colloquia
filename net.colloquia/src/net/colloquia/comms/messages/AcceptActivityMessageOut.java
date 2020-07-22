package net.colloquia.comms.messages;

import java.util.zip.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
 */
public class AcceptActivityMessageOut
extends ComponentMessageOut
{
    /**
     * Constructor
     */
    protected AcceptActivityMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
     * Add content to Zip file containing:
     * 1. The Person (as XML)
     * 2. A photograph file (optional) IF person has accepted
     */
    protected void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        Person me = UserPrefs.getMe();
        SentComponent sc = me.getSentComponent();

        // Marshall to XML
        StringBuffer sb = new StringBuffer();
        XMLStringWriter writer = new XMLStringWriter(sb);
        try {
            sc.write2XML(writer);
        }
        catch(XMLWriteException ex) {
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
            throw new ColloquiaFileException("Could not addContentToZip", ex.getMessage());
        }

        Utils.addStringToZip(sb.toString(), "__person__", zOut);

        // Attachment (could be photograph)
        addAttachmentsToZip(zOut);
    }

    /**
     * Over-ride to set Activity State
     */
    public MessageInfo updateMessageInfo() {
        MessageInfo mInfo = super.updateMessageInfo();
        Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        if(A != null) {
        	A.setAcceptedState(mInfo.isAccepted() ? Activity.ACCEPTED : Activity.DECLINED);
        }
        return mInfo;
    }
}

package net.colloquia.comms.messages;

import java.util.zip.*;

import net.colloquia.comms.*;
import net.colloquia.io.*;

/**
* A File class for a sent Text Message
*/
public abstract class TextMessageOut
extends MessageOut
{

    /**
    * Constructor
    */
    protected TextMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Add content to Zip file containing:
    * 1. The Message text (as HTML) this will be in MessageInfo.getFileName()
    * 2. Any attachment files
    */
    protected void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        addMessageTextToZip(zOut);
        addAttachmentsToZip(zOut);
    }

    /**
    * Adds an HTML Message text to the zip file
    */
    protected void addMessageTextToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        String fileName = mInfo.getFullFileName();
        if(fileName.equals("")) return;                    // This shouldn't happen!
        addFileToZip(fileName, "__message__.html", zOut);
    }

}
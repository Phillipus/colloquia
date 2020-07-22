package net.colloquia.comms.messages;

import java.util.zip.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
* A File class for a sent Resource
*/
public class ResourceMessageOut
extends ComponentMessageOut
{

    /**
    * Constructor
    */
    protected ResourceMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * Add content to Zip file containing:
    * 1. The Resource (as XML)
    * 2. Description file (as HTML)
    */
    protected void addContentToZip(ZipOutputStream zOut) throws ColloquiaFileException {
        String ID = mInfo.getComponentID();
        Resource resource = (Resource)DataModel.getComponent(ID);
        if(resource == null) return;

        SentComponent sc = resource.getSentComponent();

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

        Utils.addStringToZip(sb.toString(), "__resource__", zOut);

        // Add description file
        String fileName = DataFiler.getTextFileName(resource, DataFiler.DESCRIPTION, false);
        String entryName = resource.getGUID() + "." + DataFiler.DESCRIPTION;
        if(DataFiler.fileExists(fileName)) {
            addFileToZip(fileName, entryName, zOut);
            sentmInfo.addAttachment(entryName);
        }
    }
}

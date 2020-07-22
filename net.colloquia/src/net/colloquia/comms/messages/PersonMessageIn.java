package net.colloquia.comms.messages;

import java.io.*;
import java.util.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;


/**
* A received Person Component (My Details)
*/
public class PersonMessageIn
extends ComponentMessageIn
{

    /**
    * Constructor
    */
    protected PersonMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
        String entryName = "__person__";
        String s = Utils.extractZipEntry(zipFile, entryName);
        if(s == null) return mInfo;

        Person sentPerson = new Person("-", "");

        try {
            XMLStringReader reader = new XMLStringReader(s);
            reader.open();
            sentPerson.unMarshallXML(reader);
            reader.close();
        }
        catch(XMLReadException ex) {
            throw new ColloquiaFileException("Could not file message", ex.getMessage());
        }

        // Do we already have this Person?
        Person oldPerson = DataModel.getPersonByEmailAddress(sentPerson);
        if(oldPerson != null) {
            oldPerson.updateProperties(sentPerson);
            sentPerson = oldPerson;
        }

        // New Person
        // PENDING - If we don't have this Person and we are receiving a Person's
        // details then surely there's a mistake and maybe we shouldn't add this Person?
        else {
            sentPerson.setGUID(ColloquiaComponent.generateGUID());
            DataModel.addComponent(sentPerson, DataModel.getPeopleGroup(), false);
        }

        // Set mInfo Person GUID
        mInfo.setPersonID(sentPerson.getGUID());

        // Do we have a photograph?
        Vector attachments = mInfo.getAttachments();
        // Should only be one attachment
        if(attachments.size() > 0) {
            String photoName = (String)attachments.elementAt(0);
            String targetFileName = DataFiler.getPersonFolder(sentPerson.getGUID(), true) + photoName;
            Utils.extractZipEntry(zipFile, photoName, targetFileName);
            sentPerson.putProperty(Person.PHOTOGRAPH, targetFileName, false);
        }

        // Delete the zip file if set
        if(deleteFile) deleteZipFile();

    	return mInfo;
	}
}
package net.colloquia.datamodel.entities;

/**
*/
public class SentPerson
extends SentComponent
{
    public SentPerson(Person person) {
        super(person, Person.XMLStartTag, Person.XMLEndTag);

        // Set GUID to be person's email address
        setProperty(Person.GUID, person.getEmailAddress());

        // Blank out some (local) properties
        // Date Last Sent my details
        removeProperty(Person.DATE_LAST_SENT_MYDETAILS);
    }
}

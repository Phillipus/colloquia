package net.colloquia.comms.messages;

import java.util.zip.ZipOutputStream;

import net.colloquia.comms.InvalidMessageException;
import net.colloquia.comms.MessageInfo;


/**
*
*/
public class ReactivateActivityMessageOut
extends ComponentMessageOut
{

    protected ReactivateActivityMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    protected void addContentToZip(ZipOutputStream zOut) {
	}
}
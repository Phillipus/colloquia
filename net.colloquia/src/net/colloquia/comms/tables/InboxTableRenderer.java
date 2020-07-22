package net.colloquia.comms.tables;

import javax.swing.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

public class InboxTableRenderer
extends MessageTableRenderer
{

    protected int getHorizontalAlignment(int column) {
        switch(column) {
        	case 0:
        	case 1:
		        return SwingConstants.CENTER;
            default:
	            return SwingConstants.LEFT;
        }
    }

    protected Icon getIcon(int column) {
        switch(column) {
            // Message Icon
        	case 0:
        	    return mInfo.getIcon();

            // Attachment for Text Message
        	case 1:
                switch(mInfo.getMessageType()) {
                    case MessageInfo.ASSIGNMENT_MESSAGE:
                    case MessageInfo.SINGLE_MESSAGE:
                    case MessageInfo.GROUP_MESSAGE:
                        if(mInfo.hasAttachment()) return iconAttach;
                        else return null;
                    default:
                        return null;
                }

            default:
				return null;
        }
    }

    protected final String getText(int column, MessageInfo mInfo) {
        String text, from;
        Person person;

        switch(column) {
            // Type
            case 0:
            case 1:
                text = null;
                break;
            // From
            case 2:
                from = mInfo.getFrom();
                person = DataModel.getPersonByEmailAddress(from);
                if(person != null) text = person.getName() + " (" + from + ")";
                else text = from;
                break;
            // Activity
            case 3:
                text = mInfo.getActivityName();
                break;
            // Subject
            case 4:
                text = mInfo.getSubject();
                break;
            // Sent
            case 5:
                text = Utils.parseDate(mInfo.getDateSent());
                break;
            default:
                text = "";
        }
        return text;
    }
}
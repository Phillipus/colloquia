package net.colloquia.comms.tables;

import javax.swing.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

public class GroupMessageTableRenderer
extends MessageTableRenderer
{

    protected int getHorizontalAlignment(int column) {
        switch(column) {
        	case 0:
        	case 1:
        	case 2:
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

            // Mark
        	case 2:
                if(!mInfo.isSeen()) return iconMark;
                else return null;

            default:
				return null;
        }
    }

    protected final String getText(int column, MessageInfo mInfo) {
        String text = "";
        String from = "";
        Person person;

        switch(column) {
            // Icon
            case 0:
            // Attachment
            case 1:
            // Mark
            case 2:
                text = "";
                break;
            // From
            case 3:
                from = mInfo.getFrom();
                person = DataModel.getPersonByEmailAddress(from);
                if(person != null) text = person.getName() + " (" + from + ")";
                else text = from;
                break;
            // Subject
            case 4:
                text = mInfo.getSubject();
                break;
            // Sent
            case 5:
                switch(mInfo.getState()) {
                    case MessageInfo.UNMATCHED:
                    case MessageInfo.RCVD:
                    case MessageInfo.SENT:
                        text = Utils.parseDate(mInfo.getDateSent());
                        break;
                    case MessageInfo.DRAFT:
                        text = draft;
                        break;
                    case MessageInfo.PENDING:
                        text = pending;
                        break;
                }
                break;
            default:
                text = "";
        }
        return text;
    }
}
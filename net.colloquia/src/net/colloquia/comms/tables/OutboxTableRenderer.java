package net.colloquia.comms.tables;

import javax.swing.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;

public class OutboxTableRenderer
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
        String text, to;

        switch(column) {
            // Type
            case 0:
            case 1:
                text = null;
                break;
            // To
            case 2:
                to = mInfo.getTo();
                Person person = DataModel.getPersonByEmailAddress(to);
                if(person != null) text = person.getName() + " (" + to + ")";
                else text = to;
                break;
            // Activity
            case 3:
                text = mInfo.getActivityName();
                break;
            // Subject
            case 4:
                text = mInfo.getSubject();
                break;
            default:
                text = "";
        }
        return text;
    }
}
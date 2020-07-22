package net.colloquia.comms.tables;

import javax.swing.*;

import net.colloquia.comms.*;
import net.colloquia.util.*;

public class AssignmentMessageTableRenderer
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

        switch(column) {
            // Icon
            case 0:
            case 1:
            case 2:
                text = "";
                break;
            // Subject
            case 3:
                text = mInfo.getSubject();
                break;
            // Sent
            case 4:
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
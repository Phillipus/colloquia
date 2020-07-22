package net.colloquia.comms.tables;

import javax.swing.*;

/**
* Renders the table header for e-mail messages
*/
public class MessageBoxTableHeaderRenderer
extends MessageTableHeaderRenderer
{

    public MessageBoxTableHeaderRenderer(int initialColumn, MessageTableModel model) {
    	super(initialColumn, model);
	}

    protected Icon getIcon(int column) {
        switch(column) {
            case 0:
                return typeIcon;
            case 1:
            	return attachIcon;
            default:
                if(index != null) {
                	if(column == index.getSortedColumn()) return index.isAscending() ? ascendingIcon : descendingIcon;
                    else return null;
                }
                else return null;
        }
    }
}
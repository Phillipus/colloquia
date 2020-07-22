package net.colloquia.comms.tables;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.util.*;

/**
 * Abstract class - renders the cells for message tables.
 * Sub-classed for the Inbox, OutBox, Single Message, Group Message
 */
public abstract class MessageTableRenderer
extends JLabel
implements TableCellRenderer
{
    protected String text;
    protected MessageInfo mInfo;
    protected int type;

    private Color foreColor;

    protected static String draft = LanguageManager.getString("16_3");
    protected static String pending = LanguageManager.getString("16_4");

	ImageIcon iconAttach = Utils.getIcon(ColloquiaConstants.iconMessageAttachment);
	ImageIcon iconMark = Utils.getIcon(ColloquiaConstants.iconMessageMark);

    public MessageTableRenderer() {
    	setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus,
					    int row, int column) {

        if(value == null) return this;

        // A message
        mInfo = (MessageInfo)value;

        // Font
        switch(mInfo.getState()) {
            case MessageInfo.RCVD:
                setFont(mInfo.isSeen() ? ColloquiaConstants.plainFont11 : ColloquiaConstants.boldFont11);
                break;

            case MessageInfo.SENT:
                setFont(ColloquiaConstants.plainFont11);
                break;

            case MessageInfo.UNMATCHED:
            case MessageInfo.PENDING:
            case MessageInfo.DRAFT:
                setFont(ColloquiaConstants.italicFont11);
                break;

            default:
                setFont(ColloquiaConstants.plainFont11);
        }

        // Colour
        if(isSelected) foreColor = Color.white;
        else {
            if(mInfo.isFromMe()) foreColor = Color.black;  // me
            else foreColor = Color.blue;                   // them
        }

        setForeground(foreColor);
        setBackground(isSelected ? ColloquiaConstants.color1 : Color.white);
        setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

        // Convert virtual column to real column
        int col = table.convertColumnIndexToModel(column);

        setHorizontalAlignment(getHorizontalAlignment(col));
        setIcon(getIcon(col));
        setText(getText(col, mInfo));
        return this;
    }

    protected abstract int getHorizontalAlignment(int column);
    protected abstract Icon getIcon(int column);
    protected abstract String getText(int column, MessageInfo mInfo);
}

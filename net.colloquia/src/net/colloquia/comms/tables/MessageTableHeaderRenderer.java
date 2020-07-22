package net.colloquia.comms.tables;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.index.*;
import net.colloquia.util.*;

/**
* Renders the table header for e-mail messages
*/
public class MessageTableHeaderRenderer
extends JLabel
implements TableCellRenderer
{
    protected Font plainFont = new Font("SansSerif", Font.PLAIN, 12);
    protected Color textColor = Color.black;
    protected ImageIcon ascendingIcon, descendingIcon, typeIcon, attachIcon, markIcon;
    protected MessageTableModel model;
    protected MessageIndex index;
    private int col;
    private int columnToSort;

    public MessageTableHeaderRenderer(int initialColumn, MessageTableModel model) {
        ascendingIcon = Utils.getIcon(ColloquiaConstants.iconAscendingSort);
        descendingIcon = Utils.getIcon(ColloquiaConstants.iconDescendingSort);
        typeIcon = Utils.getIcon(ColloquiaConstants.iconMessage);
        attachIcon = Utils.getIcon(ColloquiaConstants.iconMessageAttachment);
        markIcon = Utils.getIcon(ColloquiaConstants.iconMessageMark);
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setOpaque(true);
        setForeground(textColor);
        columnToSort = initialColumn;
        this.model = model;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus,
					    int row, int column) {

        setText(value.toString());
        index = model.getMessageIndex();
        col = table.convertColumnIndexToModel(column);
        setIcon(getIcon(col));
        return this;
    }

    protected Icon getIcon(int column) {
        switch(column) {
            case 0:
                return typeIcon;
            case 1:
            	return attachIcon;
            case 2:
            	return markIcon;
            default:
                if(index != null) {
                	if(column == index.getSortedColumn()) return index.isAscending() ? ascendingIcon : descendingIcon;
                    else return null;
                }
                else return null;
        }
    }
}

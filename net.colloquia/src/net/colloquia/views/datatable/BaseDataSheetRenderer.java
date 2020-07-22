package net.colloquia.views.datatable;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;

/**
* Renders the cells for data sheet tables.
*/
public final class BaseDataSheetRenderer
extends JLabel
implements TableCellRenderer
{
    public BaseDataSheetRenderer() {
    	setOpaque(true);
        setFont(ColloquiaConstants.plainFont11);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus, int row, int column) {

        BaseTableModel model = (BaseTableModel)table.getModel();

        setToolTipText(model.getToolTipText(row, column));

        String text = value == null ? "" : value.toString();
        setText(text);

        if(column == 0) setHorizontalAlignment(SwingConstants.CENTER);
        else setHorizontalAlignment(SwingConstants.LEFT);

        setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

        // Render read-only cell
        if(!model.isCellEditable(row, column)) {
            setBackground(isSelected ? ColloquiaConstants.color1 : ColloquiaConstants.color3);
            setForeground(isSelected ? Color.white : Color.black);

            // Shall we highlight the cell as a button?
            if(model.isBorderHighlighted(row, column)) setBorder(ColloquiaConstants.hiBorder);
        }

        // Render editable cell
        else {
            setForeground(isSelected ? Color.white : Color.black);
            setBackground(isSelected ? ColloquiaConstants.color1 : Color.white);
        }

        return this;
    }

    // Over-ride (why?)
    public void requestFocus() {}

}

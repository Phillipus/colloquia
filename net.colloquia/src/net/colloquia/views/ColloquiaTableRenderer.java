package net.colloquia.views;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;


public class ColloquiaTableRenderer
implements TableCellRenderer
{
    protected JLabel label;

    public ColloquiaTableRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        label.setFont(ColloquiaConstants.plainFont11);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus, int row, int column) {

        TableModel model = table.getModel();

        label.setText(value == null ? "" : value.toString());
        label.setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

        // Render read-only cell
        if(!model.isCellEditable(row, column)) {
            label.setBackground(isSelected ? ColloquiaConstants.color1 : ColloquiaConstants.color3);
            label.setForeground(isSelected ? Color.white : Color.black);
        }

        // Render editable cell
        else {
            label.setForeground(isSelected ? Color.white : Color.black);
            label.setBackground(isSelected ? ColloquiaConstants.color1 : Color.white);
        }

        return label;
    }
}

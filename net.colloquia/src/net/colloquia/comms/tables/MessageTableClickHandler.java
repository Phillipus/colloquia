package net.colloquia.comms.tables;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;


/**
* Handles mouse-clicks on the tableheader for sorting
*/
public class MessageTableClickHandler
extends MouseAdapter
{
    private JTable table;
    private MessageTableModel model;
    private JTableHeader tableHeader;

    public MessageTableClickHandler(JTable table) {
        this.table = table;
        this.model = (MessageTableModel)table.getModel();
        tableHeader = table.getTableHeader();
    }

    public void mouseClicked(MouseEvent e) {
        // Click on header
        if(e.getComponent() == tableHeader) {
            // Get the clicked column
            Point point = e.getPoint();
            int column = table.convertColumnIndexToModel(tableHeader.columnAtPoint(point));

            // Don't sort column 0, 1
            if(column <= 1) return;

            // Clear any selected rows so we don't confuse people
            //table.clearSelection();

            tableHeader.setCursor(ColloquiaConstants.waitCursor);
            model.sortIndex(column);
            tableHeader.setCursor(ColloquiaConstants.defaultCursor);

            // Repaint the table-header to reflect changes
            tableHeader.repaint();
        }
    }
}

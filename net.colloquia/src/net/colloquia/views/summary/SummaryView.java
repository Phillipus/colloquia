package net.colloquia.views.summary;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
* A table view that shows a summary of members of a Group
*/
public abstract class SummaryView extends JPanel {
    protected JTable table;
    protected AbstractTableModel model;
    protected JTableHeader tableHeader;
    protected ClickHandler clickHandler;
    protected int sortedColumn = 0;
    protected boolean ascending = true;
    protected TableHeaderRenderer headerRenderer;
    protected String[] columnNames;
    protected String[] propertyKeyNames;

    protected ColloquiaContainer group;
    protected Vector members = new Vector();

    protected SummaryView() {
        model = new DefaultSummaryModel();

        init();

        table = new JTable(model);

        table.setDefaultRenderer(Object.class, new ColloquiaTableRenderer());

        setLayout(new BorderLayout());

        tableHeader = table.getTableHeader();
        tableHeader.setResizingAllowed(true);

        // Set the renderer for the column headers
        headerRenderer = new TableHeaderRenderer();
        TableColumnModel tcm = tableHeader.getColumnModel();
        for(Enumeration cols = tcm.getColumns(); cols.hasMoreElements();) {
            TableColumn tCol = (TableColumn)cols.nextElement();
            // Assign our custom header-renderer to each column
            tCol.setHeaderRenderer(headerRenderer);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        // Listen to mouse-clicks on the table
        clickHandler = new ClickHandler();
        table.addMouseListener(clickHandler);
        tableHeader.addMouseListener(clickHandler);
    }

    protected abstract void init();

    protected void setGroup(ColloquiaContainer group) {
        this.group = group;
        ascending = true;
        //sortedColumn = -1;
    }

    protected class ClickHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            int clicks = e.getClickCount();

            // Double-Click on row in table - jump to component
            if(source == table && clicks == 2) jump(table.getSelectedRow());

            // Click on column header
            else if(source == tableHeader) {
                // Get the clicked column
                Point point = e.getPoint();
                int column = table.convertColumnIndexToModel(tableHeader.columnAtPoint(point));
                sort(column);
            }
        }
    }

    /**
    * Renders the table header
    */
    protected class TableHeaderRenderer
    extends JLabel
    implements TableCellRenderer
    {
        ImageIcon iconAscendingSort = Utils.getIcon(ColloquiaConstants.iconAscendingSort);
        ImageIcon iconDescendingSort = Utils.getIcon(ColloquiaConstants.iconDescendingSort);

        public TableHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setVerticalTextPosition(SwingConstants.CENTER);
            setBorder(new BevelBorder(BevelBorder.RAISED));
            setOpaque(true);
            setForeground(Color.black);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {

            setText(value.toString());
            int col = table.convertColumnIndexToModel(column);

            if(col == sortedColumn)
                setIcon(isAscending() ? iconAscendingSort : iconDescendingSort);
            else setIcon(null);

            return this;
        }

    }

    protected class DefaultSummaryModel extends AbstractTableModel {
        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            if(rowIndex >= members.size()) return "";
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(rowIndex);
            return tc.getProperty(propertyKeyNames[columnIndex]);
        }

        // getRowCount
        public int getRowCount() {
            return members.size();
        }

        // getColumnCount
        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
    }

    /**
    * Jump to the node
    */
    protected void jump(int row) {
        if(row == -1) return;
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(row);
        ColloquiaTree.getInstance().selectNode(tc, group);
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
    }

    protected void sort(int column) {
        // If it's already the current sorted column then just reverse the sorting order
        if(column == sortedColumn) ascending = !ascending;
        else {
            ascending = true;
            sortedColumn = column;
        }

        tableHeader.repaint();

        table.setCursor(ColloquiaConstants.waitCursor);

        DataModel.sortComponents(members, group, propertyKeyNames[column], ascending);

        table.setCursor(ColloquiaConstants.defaultCursor);
        table.repaint();
    }

    protected boolean isAscending() {
        return ascending;
    }

    public Dimension getMinimumSize() { return new Dimension(0, 0); }

}

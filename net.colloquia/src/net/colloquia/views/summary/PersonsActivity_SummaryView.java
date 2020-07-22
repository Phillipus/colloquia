package net.colloquia.views.summary;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.tree.*;

/**
 * A table view that shows what Activities a person is a member of
 * Used in a Person View
 * It's a passive view with no sorting
 */
public class PersonsActivity_SummaryView
extends JPanel
{
    private JTable table;
    private AbstractTableModel model;
    private JTableHeader tableHeader;
    protected String[] columnNames;
    private ClickHandler clickHandler;
    private Vector members = new Vector();

    private Renderer renderer;
    private Person person;

    public PersonsActivity_SummaryView() {
        columnNames = new String[] {
            LanguageManager.getString("0"),
            LanguageManager.getString("8_4"),
            LanguageManager.getString("12_9")
        };

        model = new ActivitySummaryModel();
        table = new JTable(model);

        setLayout(new BorderLayout());

        tableHeader = table.getTableHeader();
        tableHeader.setResizingAllowed(true);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(tableHeader);
        add(sp, BorderLayout.CENTER);

        // Set the default cell renderer for any type object
        renderer = new Renderer();
        table.setDefaultRenderer(Object.class, renderer);

        // Listen to mouse-clicks on the table
        clickHandler = new ClickHandler();
        table.addMouseListener(clickHandler);
    }

    public void setPerson(Person person) {
        this.person = person;
        members = person.getActivities();
        model.fireTableDataChanged();
    }

    protected class ClickHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            int clicks = e.getClickCount();
            // Double-Click on row in table - jump to component
            if(source == table && clicks == 2) jump(table.getSelectedRow());
        }
    }

    /**
    * Jump to the node
    */
    protected void jump(int row) {
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        Activity A = (Activity)members.elementAt(row);
        ColloquiaTree.getInstance().selectNode(person, A);
        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
    }

    private class ActivitySummaryModel extends AbstractTableModel {
        public Object getValueAt(int rowIndex, int columnIndex) {
            if(rowIndex < members.size()) {
                return (Activity)members.elementAt(rowIndex);
            }
            else return null;
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

    static String na = LanguageManager.getString("12_7");

    /**
    * Renders the cells for the summary tables
    */
    private class Renderer
    extends JLabel
    implements TableCellRenderer
    {
        private String text;
        private Activity A;
        private Color foreColor;

        public Renderer() {
            // Set opaque - important
            setOpaque(true);
            setFont(ColloquiaConstants.plainFont11);
        }
       public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {

            text = "";

            if(value == null) return this;

            A = (Activity)value;

            switch(column) {
                // Activity Name
                case 0:
                    // If this Activity has a parent Activity add indent
                    int indent = A.getNumberParentActivities();
                    for(int i = 0; i < indent; i++) {
                        if(i == indent - 1) text += "\\__";
                        else text += "     ";
                    }
                    text += A.getName();
                    break;
                // Date due (only if the Activity is mine)
                case 1:
                    if(!A.isMine()) text = na;
                    else {
                        Assignment ass = A.getAssignment();
                        if(ass != null) text = ass.getProperty(Assignment.DUE_DATE);
                        else text = "";
                    }
                    break;
                // Grade (only if the Activity is mine)
                case 2:
                    if(!A.isMine()) text = na;
                    else text = A.getPersonAssignmentGrade(person);
                    break;
            }

            setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

            if(isSelected) foreColor = Color.white;
            else {
                if(A.isLive()) foreColor = ColloquiaConstants.liveColor;
                else if(A.isCompleted()) foreColor = ColloquiaConstants.completedColor;
                else foreColor = Color.black;
            }
            setForeground(foreColor);

            setBackground(isSelected ? ColloquiaConstants.color1 : Color.white);
            setText(text);
            return this;
        }
    }
}



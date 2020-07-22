package net.colloquia.views;

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
import net.colloquia.views.tree.*;

public class HotInfo
extends JDialog
{
    JTabbedPane tabPane;
   	Vector acts;
    JTable updateTable;

    public HotInfo() {
        super(MainFrame.getInstance(), LanguageManager.getString("ACTION_3"), true);

		//DataModel.setActivitiesHot();

        setResizable(false);

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        getContentPane().add(tabPane, BorderLayout.CENTER);

        // Add OK Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
  	    btnOK.addActionListener(new btnOKClick());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Add info Panel
        //JPanel infoPanel = new JPanel();
        //infoPanel.setBorder(new EmptyBorder(10, 60, 0, 60));
        //infoPanel.setLayout(new GridLayout(12, 2, 5, 5));
        //tabPane.addTab(LanguageManager.getString("GENERAL"), null, infoPanel);

        // Updates panel
        JPanel updatePanel = new JPanel(new BorderLayout());
        updatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextPane messagePanel = new JTextPane();
        messagePanel.setOpaque(false);
        messagePanel.setEditable(false);
        messagePanel.setFont(new Font("SansSerif", Font.BOLD, 12));
        messagePanel.setMargin(new Insets(10, 10, 10, 10));
        messagePanel.setText(LanguageManager.getString("23_2"));
        updatePanel.add(messagePanel, BorderLayout.NORTH);

        updateTable = new JTable(new UpdateTableModel());
        updateTable.setOpaque(false);
        updateTable.setDefaultRenderer(Object.class, new ComponentTableRenderer(Activity.class));
        // Listen to mouse-clicks on the table
        updateTable.addMouseListener(new ClickHandler());
        JScrollPane sp1 = new JScrollPane(updateTable);
        sp1.setColumnHeaderView(updateTable.getTableHeader());
        updatePanel.add(sp1, BorderLayout.CENTER);

        tabPane.addTab(LanguageManager.getString("21_1"), null, updatePanel);

        // Centre it in relation to our main frame
        setSize(400, 300);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    /** Close */
    private class btnOKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }


    /**
    * The Table Model for Status of Activities
    */
    protected class UpdateTableModel extends AbstractTableModel {
        public UpdateTableModel() {
        	acts = DataModel.getHotActivities();
        }

        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            Activity A = (Activity)acts.elementAt(rowIndex);
            switch(columnIndex) {
                case 0:
                    return A.getName();
            }
            return "";
        }

        // getColumnCount
        public int getColumnCount() {
            return 1;
        }

        // getRowCount
        public int getRowCount() {
            return acts.size();
        }

        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return LanguageManager.getString("NAME");
            }
            return "";
        }

    }

    protected class ClickHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            int clicks = e.getClickCount();
            // Double-Click on row in table - jump to component
            if(source == updateTable && clicks == 2) jump(updateTable.getSelectedRow());
        }
    }

    protected void jump(int row) {
        if(row == -1) return;
        setCursor(ColloquiaConstants.waitCursor);
        Activity A = (Activity)acts.elementAt(row);
        ColloquiaTree.getInstance().selectNodeByObject(A);
        setCursor(ColloquiaConstants.defaultCursor);
        dispose();
    }
}

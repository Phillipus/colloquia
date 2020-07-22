package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;


/**
 * Dialog for adding/removing attachments to a message
 */
public class AttachmentsDialog
extends JDialog
implements ListSelectionListener, TableModelListener
{
    MessageInfo mInfo;
    JTable table;
    JButton btnOK, btnView, btnAdd, btnRemove;
    AttachmentsTableModel model;

    public AttachmentsDialog(Frame owner, MessageInfo mInfo, boolean editable) {
        super(owner, true);
        this.mInfo = mInfo;

        model = new AttachmentsTableModel();
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(table.getTableHeader());
        getContentPane().add(sp, BorderLayout.CENTER);

        // Selection model and selection listener
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsm.addListSelectionListener(this);

        // Columns
		TableColumnModel tcm = table.getTableHeader().getColumnModel();
        tcm.getColumn(0).setPreferredWidth(18);
        tcm.getColumn(0).setMaxWidth(18);
        tcm.getColumn(1).setPreferredWidth(140);

        table.setDefaultRenderer(Object.class, new AttachmentsTableRenderer());

        // Listen for double-clicks on the table
        table.addMouseListener(new MouseAdapter() {
            // Double-click on row
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) launchAttachment();
            }
        });

        JPanel buttonPanel = new JPanel();

		btnView = new JButton(LanguageManager.getString("VIEW"));
        btnView.setEnabled(false);
        btnView.addActionListener(new Action_ViewAttachment());
        buttonPanel.add(btnView);

        if(editable) {
            btnAdd = new JButton(LanguageManager.getString("ADD"));
            btnAdd.addActionListener(new Action_AddAttachment());
            buttonPanel.add(btnAdd);

            btnRemove = new JButton(LanguageManager.getString("REMOVE"));
    	    btnRemove.setEnabled(false);
	        btnRemove.addActionListener(new Action_RemoveAttachment());
            buttonPanel.add(btnRemove);
        }

		btnOK = new JButton(LanguageManager.getString("OK"));
        btnOK.addActionListener(new Action_OK());
        buttonPanel.add(btnOK);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setTitle(LanguageManager.getString("16_5"));
        setSize(460, 340);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    private void launchAttachment() {
        int row = table.getSelectedRow();
        if(row != -1) {
        	String fileName = (String)mInfo.getAttachments().elementAt(row);
            try {
            	AppLauncher.run(fileName);
        	}
            catch(Exception ex) {}
        }
    }

    private class Action_ViewAttachment extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
        	launchAttachment();
        }
    }

    private class Action_OK extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
        	dispose();
        }
    }

    private class Action_AddAttachment extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            File fileName = getFile();
            if(fileName != null) {
                mInfo.addAttachment(fileName.toString());
                ((AbstractTableModel)table.getModel()).fireTableDataChanged();
            }
        }
    }

    private class Action_RemoveAttachment extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            int rows[] = table.getSelectedRows();
            if(rows.length == 0) return;

            for(int i = rows.length - 1; i >= 0; i--) {
                mInfo.getAttachments().removeElementAt(rows[i]);
                ((AbstractTableModel)table.getModel()).fireTableRowsDeleted(rows[i], rows[i]);
            }

            updateView();
        }
    }

    private File getFile() {
        PFileChooser chooser = new PFileChooser();
        String add = LanguageManager.getString("ADD");
        chooser.setApproveButtonText(add);
        chooser.setDialogTitle(add);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal != PFileChooser.APPROVE_OPTION) return null;
        else return chooser.getSelectedFileAndStore();
    }

    /**
    * A row has been selected
    */
    public void valueChanged(ListSelectionEvent e) {
        updateView();
    }

    /**
    * This is triggered for the TableModelListener and has to be included for
    * when the underlying data changes (new People view, new Tree).
    */
    public void tableChanged(TableModelEvent e) {
        updateView();
    }

    /**
    * Update the buttons and display the message
    */
    protected void updateView() {
        String f = getSelectedFile();
        btnView.setEnabled(f != null);
        if(btnRemove != null) btnRemove.setEnabled(f != null);
    }

    /**
    Get the selected file or null if none selected
    */
    protected String getSelectedFile() {
        int row = table.getSelectedRow();
        int numRows = model.getRowCount();

        // No row selected
        if(row == -1 || numRows == 0) return null;
        // Get the File
        else return (String)model.getValueAt(row, 0);
    }


    private class AttachmentsTableModel extends AbstractTableModel {

        // getValueAt
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 1:
                    // A shortened visual version
                    String fileName = (String)mInfo.getAttachments().elementAt(rowIndex);
                    File file = new File(fileName);
                    return file.getName();
                default:
                    return "";
            }
        }

        // getColumnCount
        public int getColumnCount() {
            return 2;
        }

        // getRowCount
        public int getRowCount() {
            return mInfo.getAttachments().size();
        }

        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return " ";
                case 1:
                    return LanguageManager.getString("FILE");
                default:
                    return "";
            }
        }

    }

    private class AttachmentsTableRenderer
    extends ColloquiaTableRenderer
    {
        protected String text;
        protected MessageInfo mInfo;
        protected int type;

        ImageIcon iconAttach = Utils.getIcon(ColloquiaConstants.iconMessageAttachment);

        public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {

            // Convert virtual column to real column
            int col = table.convertColumnIndexToModel(column);

            // Centre and set icon in first column, but not the others
            if(col == 0) {
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setIcon(iconAttach);
            }
            else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setIcon(null);
            }

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
package net.colloquia.views.datatable;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

/**
 * A Panel that holds the table for displaying metadata for an object.
 */
public abstract class BaseDataSheet
extends JPanel
{
    protected ColloquiaComponent tc;
    protected JTable table;
    protected BaseTableModel tableModel;
    private ClickHandler clickHandler;

    public BaseDataSheet(BaseTableModel tableModel) {
        this.tableModel = tableModel;
        table = new JTable(tableModel);

        setLayout(new BorderLayout());

        JTableHeader th = table.getTableHeader();
        th.setResizingAllowed(true);
        th.setReorderingAllowed(false);

        // Set the default cell renderer for any type object
        BaseDataSheetRenderer cellRenderer = new BaseDataSheetRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);

        // Columns
        TableColumnModel tcm = th.getColumnModel();
        tcm.getColumn(0).setMaxWidth(15);
        tcm.getColumn(0).setPreferredWidth(15);
        tcm.getColumn(1).setMaxWidth(130);
        tcm.getColumn(1).setPreferredWidth(130);

        JScrollPane sp = new JScrollPane(table);
        sp.setColumnHeaderView(table.getTableHeader());
        add(sp, BorderLayout.CENTER);

        // Listen to mouse-clicks on the table
        clickHandler = new ClickHandler();
        table.addMouseListener(clickHandler);
    }

    /**
    * Set the tc here and in the table model
    */
    public void setComponent(ColloquiaComponent tc) {
        // If we are in mid table-edit stop the editing before updating new data
        if(table.isEditing()) table.getCellEditor().stopCellEditing();
        this.tc = tc;
        tableModel.setComponent(tc);
    }

    /**
    * Trap clicks on the table so we can set photo, local file, and so on
    */
    private class ClickHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            Component source = e.getComponent();
            int clicks = e.getClickCount();

            // Click on table
            if(source == table && clicks == 1 && table.getSelectedColumn() == 0) {
		        String key = tc.getTableRowKey(table.getSelectedRow());
                tableClicked(key);
            }
        }
    }

    // Over-ride
    protected void tableClicked(String key) {
        if(key.equalsIgnoreCase(ColloquiaComponent.LOCAL_FILE)) setLocalFile();
        if(key.equalsIgnoreCase(ColloquiaComponent.URL)) setExternalBrowser();
    }

    /**
    * Set the URL of a local file.
    */
    private void setLocalFile() {
        MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);

        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(LanguageManager.getString("7_1"));
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());

        MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);

        if(returnVal != PFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFileAndStore();
        if(file == null) return;

        String fileName = file.toString();
        String tmp = fileName.toLowerCase();
        // If it's a jar file, prefix and suffix
        if(tmp.endsWith(".jar")) fileName = URLUtils.makeJarName(fileName);
        else if(tmp.endsWith(".htm") || tmp.endsWith(".html")) fileName = "file:/" + fileName;

        tc.setLocalFile(fileName, false);
        tableModel.fireTableDataChanged();
    }

    protected void setExternalBrowser() {
        tc.setExternalBrowser(!tc.isExternalBrowser(), true);
        tableModel.fireTableDataChanged();
    }

    public Dimension getPreferredSize() { return new Dimension(200, 220); }
    public Dimension getMinimumSize() { return new Dimension(0, 0);}
}


package net.colloquia.prefs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import uk.ac.reload.dweezil.gui.layout.*;

public class PrefsBrowserPanel
extends PrefsPanel
{
    FileTypesTable fileTypesTable;
    PCheckBox cbBrowserNow;
    PComboBox cbDownloadLevels;
    PCheckBox cbUnpackZips;
    PTextField tfProxyHost;
    PTextField tfProxyPort;

    public PrefsBrowserPanel(JFrame owner) {
        super(owner, new XYLayout());
        setBackground(ColloquiaConstants.color2);
        setBorder(new EmptyBorder(2, 10, 0, 0));

        fileTypesTable = new FileTypesTable();

        // General settings
        PLabel label = new PLabel(LanguageManager.getString("4_9"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, 0, 200, LABEL_HEIGHT));

        // Browser Now Checkbox
        cbBrowserNow = new PCheckBox(LanguageManager.getString("4_11"), false);
        add(cbBrowserNow, new XYConstraints(0, 25, 350, CHECKBOX_HEIGHT));

        // Download levels Combobox
        String ld = LanguageManager.getString("15_9");
        String[] items = new String[] { "1 " + ld, "2 " + ld, "3 " + ld, "4 " + ld, "5 " + ld };
        cbDownloadLevels = new PComboBox(items);
        add(cbDownloadLevels, new XYConstraints(0, 50, 150, COMBOBOX_HEIGHT));
        label = new PLabel(LanguageManager.getString("4_49"));
        add(label, new XYConstraints(160, 54, 350, LABEL_HEIGHT));

        // Unpack zips
        cbUnpackZips = new PCheckBox(LanguageManager.getString("4_43"), false);
        add(cbUnpackZips, new XYConstraints(0, 80, 350, CHECKBOX_HEIGHT));

        // Proxy
        label = new PLabel("Proxy Host");
        add(label, new XYConstraints(0, 110, 200, LABEL_HEIGHT));
        tfProxyHost = new PTextField();
        add(tfProxyHost, new XYConstraints(90, 108, 200, TEXTBOX_HEIGHT));

        label = new PLabel("Proxy Port");
        add(label, new XYConstraints(310, 110, 200, LABEL_HEIGHT));
        tfProxyPort = new PTextField();
        add(tfProxyPort, new XYConstraints(390, 108, 100, TEXTBOX_HEIGHT));

        addStrut(144);

        // BUTTONS
        DinkyButton addButton = new DinkyButton(new Action_Add(fileTypesTable), LanguageManager.getString("4_54"));
        add(addButton, new XYConstraints(0, 170, 20, 20));
        DinkyButton removeButton = new DinkyButton(new Action_Remove(fileTypesTable), LanguageManager.getString("4_55"));
        add(removeButton, new XYConstraints(25, 170, 20, 20));
        label = new PLabel(LanguageManager.getString("4_31"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(55, 173, 350, LABEL_HEIGHT));

        // FILE TYPES TABLE
        JScrollPane sp = new JScrollPane(fileTypesTable);
        sp.setColumnHeaderView(fileTypesTable.getTableHeader());
        add(sp, new XYConstraints(0, 193, 670, 200));
    }

    /**
    * Update UserPrefs according to the controls' settings
    */
    public void updateUserPrefs(UserPrefs prefs) {
		prefs.putProperty(UserPrefs.BROWSER_NOW, cbBrowserNow.isSelected());
		prefs.putProperty(UserPrefs.DOWNLOAD_LEVEL, cbDownloadLevels.getSelectedIndex());
		prefs.putProperty(UserPrefs.UNPACK_ZIPS, cbUnpackZips.isSelected());
		prefs.putProperty(UserPrefs.PROXY_HOST, tfProxyHost.getText().trim());
		prefs.putProperty(UserPrefs.PROXY_PORT, tfProxyPort.getText().trim());
        FileType.save(fileTypesTable.getFileTypes());
    }

    /**
    * Set the controls' settings from those found in UserPrefs
    */
    public void setSettings(UserPrefs prefs) {
        cbBrowserNow.setSelected(prefs.getBooleanProperty(UserPrefs.BROWSER_NOW));
        cbDownloadLevels.setSelectedIndex(prefs.getIntegerProperty(UserPrefs.DOWNLOAD_LEVEL));
        cbUnpackZips.setSelected(prefs.getBooleanProperty(UserPrefs.UNPACK_ZIPS));
        tfProxyHost.setText(prefs.getProperty(UserPrefs.PROXY_HOST));
        tfProxyPort.setText(prefs.getProperty(UserPrefs.PROXY_PORT));
        fileTypesTable.load(FileType.getFileTypes());
    }

    class FileTypesTable extends JTable {
        FileTypesModel ftModel;

        public FileTypesTable() {
            ftModel = new FileTypesModel();
            setModel(ftModel);
            setDefaultRenderer(Object.class, new ColloquiaTableRenderer());
            JTableHeader th = getTableHeader();
            th.setResizingAllowed(true);
            th.setReorderingAllowed(false);
            TableColumnModel tcm = th.getColumnModel();
            tcm.getColumn(0).setMaxWidth(100);
            tcm.getColumn(0).setPreferredWidth(100);
            tcm.getColumn(2).setMaxWidth(100);
            tcm.getColumn(2).setPreferredWidth(100);
        }

        public void load(FileType[] fileTypes) {
            ftModel.load(fileTypes);
        }

        public FileType[] getFileTypes() {
            return ftModel.getFileTypes();
        }
    }

    class FileTypesModel extends AbstractTableModel {
        FileType[] fileTypes;

        String[] columnNames = {
            LanguageManager.getString("4_52"),
            LanguageManager.getString("4_53"),
            LanguageManager.getString("QT")
        };

        public FileTypesModel() {}

        public FileType[] getFileTypes() {
            return fileTypes;
        }

        public void load(FileType[] fileTypes) {
            this.fileTypes = fileTypes;
            fireTableDataChanged();
        }

        public void addFileType(FileType fileType) {
            if(fileTypes == null) {
                fileTypes = new FileType[] { new FileType("", "", false) };
            }
            else {
                FileType ft[] = new FileType[fileTypes.length + 1];
                System.arraycopy(fileTypes, 0, ft, 0, fileTypes.length);
                ft[fileTypes.length] = fileType;
                fileTypes = ft;
            }
            fireTableDataChanged();
        }

        public void deleteFileTypes(int[] rows) {
            if(fileTypes == null) return;
            if(rows.length == 0) return;

            Vector v = new Vector();

            for(int i = 0; i < fileTypes.length; i++) {
                v.addElement(fileTypes[i]);
            }

            for(int i = 0; i < rows.length; i++) {
                v.removeElement(fileTypes[rows[i]]);
            }

            fileTypes = new FileType[v.size()];
            v.copyInto(fileTypes);
            fireTableDataChanged();
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(aValue == null || fileTypes == null) return;

            switch(columnIndex) {
                case 0:
                    fileTypes[rowIndex].setExtension((String)aValue);
                    break;
                case 1:
                    fileTypes[rowIndex].setDescription((String)aValue);
                    break;
                case 2:
                    fileTypes[rowIndex].setIsMedia(!fileTypes[rowIndex].isMediaFile());
                    break;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if(fileTypes == null) return "";

            switch(columnIndex) {
                case 0:
                    return fileTypes[rowIndex].getExtension();
                case 1:
                    return fileTypes[rowIndex].getDescription();
                case 2:
                    return new Boolean(fileTypes[rowIndex].isMediaFile());
                default:
                    return "";
            }
        }

        public int getRowCount() {
            return fileTypes != null ? fileTypes.length : 0;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        public Class getColumnClass(int columnIndex) {
            Class retVal = String.class;
            if(columnIndex == 2) retVal = Boolean.class;
            return retVal;
        }

    }

    class Action_Add extends AbstractAction {
        FileTypesTable table;

        public Action_Add(FileTypesTable table) {
            super("+");
            this.table = table;
        }

        public void actionPerformed(ActionEvent e) {
            FileTypesModel ftModel = (FileTypesModel)table.getModel();
            ftModel.addFileType(new FileType("", "", false));
        }

    }

    class Action_Remove extends AbstractAction {
        FileTypesTable table;

        public Action_Remove(FileTypesTable table) {
            super("-");
            this.table = table;
        }

        public void actionPerformed(ActionEvent e) {
            FileTypesModel ftModel = (FileTypesModel)table.getModel();
            ftModel.deleteFileTypes(table.getSelectedRows());
        }

    }

    class DinkyButton extends JButton {
        Dimension size;
        Insets insets;

        public DinkyButton(Action a, String toolTipText) {
            super(a);
            size = new Dimension(20, 20);
            insets = new Insets(1, 1, 1, 1);
            setToolTipText(toolTipText);
        }

        public Dimension getPreferredSize() {
            return size;
        }

        public Insets getInsets() {
            return insets;
        }
    }

}
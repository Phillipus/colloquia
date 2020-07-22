package net.colloquia.views.entities;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.datatable.*;
import net.colloquia.xml.*;


public class ActivityInviteView
extends ColloquiaView
{
    protected ActivityDataSheet ds;
    protected JTabbedPane tabPane;

    protected ColloquiaTextEditor descriptionViewer;
    protected ColloquiaTextEditor objectivesViewer;
    protected ColloquiaTextEditor assignmentViewer;
    protected ColloquiaTextEditor resource_descriptionViewer;

    protected JTable peopleTable;
    protected PeopleTableModel peopleTableModel;
    protected JTable resourceTable;
    protected ResourceTableModel resourceTableModel;

    protected Vector people;
    protected Vector resources;
    protected Assignment assignment;

    private static final ActivityInviteView instance = new ActivityInviteView();

    protected ActivityInviteView() {
        setLayout(new BorderLayout());

        ds = new ActivityDataSheet();
        descriptionViewer = new ColloquiaTextEditor(false);
        objectivesViewer = new ColloquiaTextEditor(false);
        assignmentViewer = new ColloquiaTextEditor(false);
		resource_descriptionViewer = new ColloquiaTextEditor(false);

        tabPane = new JTabbedPane();
        tabPane.setFont(ColloquiaConstants.plainFont11);
        add(tabPane);

        // Metadata / Description / Objectives
        PSplitPane split1 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        PSplitPane split2 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);

        // DataSheet
        split1.setTopComponent(ds);
        JPanel p1 = new JPanel(new BorderLayout());
        PLabel l1 = new PLabel(" " + LanguageManager.getString("12_3"));
        p1.setBackground(ColloquiaConstants.color3);
        p1.add(l1, BorderLayout.NORTH);
        // Description
        p1.add(descriptionViewer, BorderLayout.CENTER);
        split1.setBottomComponent(p1);

        // Objectives
        split2.setTopComponent(split1);
        JPanel p2 = new JPanel(new BorderLayout());
        PLabel l2 = new PLabel(" " + LanguageManager.getString("12_4"));
        p2.setBackground(ColloquiaConstants.color3);
        p2.add(l2, BorderLayout.NORTH);
        p2.add(objectivesViewer, BorderLayout.CENTER);
        split2.setBottomComponent(p2);

        tabPane.addTab(LanguageManager.getString("12_3"), null, split2);
        split1.setDividerLocation(120);
        split2.setDividerLocation(320);

        // People
        peopleTableModel = new PeopleTableModel();
        peopleTable = new JTable(peopleTableModel);
        peopleTable.setDefaultRenderer(Object.class, new ColloquiaTableRenderer());
        JScrollPane sp1 = new JScrollPane(peopleTable);
        sp1.setColumnHeaderView(peopleTable.getTableHeader());
        tabPane.addTab(LanguageManager.getString("7"), null, sp1);

        // Resources
        resourceTableModel = new ResourceTableModel();
        resourceTable = new JTable(resourceTableModel);
        resourceTable.setDefaultRenderer(Object.class, new ColloquiaTableRenderer());
        JScrollPane sp2 = new JScrollPane(resourceTable);
        sp2.setColumnHeaderView(resourceTable.getTableHeader());
        JPanel panel = new JPanel(new BorderLayout());
        PSplitPane split3 = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        split3.setTopComponent(sp2);
        split3.setBottomComponent(resource_descriptionViewer);
        tabPane.addTab(LanguageManager.getString("9"), null, split3);

        // Assignment
        tabPane.addTab(LanguageManager.getString("2"), null, assignmentViewer);

        // Selection model and selection listener
        ListSelectionModel lsm = resourceTable.getSelectionModel();
        lsm.addListSelectionListener(resourceTableModel);
    }

    public static ActivityInviteView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof Activity) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;
        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");

        // Update data Sheet
        ds.setComponent(tc);

        // Description
        String fileName = DataFiler.getInviteTextFileName(tc.getGUID(), tc.getGUID(), DataFiler.DESCRIPTION, false);
        descriptionViewer.loadFile(fileName, false);

        // Objectives
        fileName = DataFiler.getInviteTextFileName(tc.getGUID(), tc.getGUID(), DataFiler.OBJECTIVES, false);
        objectivesViewer.loadFile(fileName, false);

        // Read in summary XML sheet
        readInviteFile((Activity)tc);

        // Resources
        resourceTableModel.fireTableDataChanged();

        // Assignment
        if(assignment != null) {
	        fileName = DataFiler.getInviteTextFileName(tc.getGUID(), assignment.getGUID(), DataFiler.DESCRIPTION, false);
        	assignmentViewer.loadFile(fileName, false);
        }
        else {
        	assignmentViewer.clear();
        }

        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }

    /**
    * Reads in the invite xml file and parses it
    */
    private void readInviteFile(Activity A) {
        String line, uline, endTag = "**";
		ColloquiaComponent tc = null;

        // Clear
        people = new Vector();
        resources = new Vector();
        assignment = null;

        File file = DataFiler.getInviteFile(A.getGUID(), false);
        if(!file.exists()) return;

        XMLFileReader reader = new XMLFileReader(file);

        try {
        	reader.open();

            while((line = reader.readLine()) != null) {
                // Get lower case line
                uline = line.toLowerCase();

                // Do we have a tag?
                XMLTag xmlTag = XMLTag.getXMLTag(line);
                if(xmlTag != null) {
                    if(tc != null) tc.putProperty(xmlTag.tag, xmlTag.value, false);
                    continue;
                }

                // Person start tag
                if(uline.indexOf(Person.XMLStartTag) != -1) {
                    tc = new Person("-", "");
                    endTag = Person.XMLEndTag;
                }

                // Resource start tag
                else if(uline.indexOf(Resource.XMLStartTag) != -1) {
                    tc = new Resource("-", "");
                    endTag = Resource.XMLEndTag;
                }

                // Assignment start tag
                else if(uline.indexOf(Assignment.XMLStartTag) != -1) {
                    tc = new Assignment("-", "");
                    endTag = Assignment.XMLEndTag;
                }

                // End of new Component
                else if((uline.indexOf(endTag) != -1) && (tc != null)) {
                    if(tc instanceof Person) people.addElement(tc);
                    else if(tc instanceof Resource) resources.addElement(tc);
                    else if(tc instanceof Assignment) assignment = (Assignment)tc;
                    tc = null;
                }
            }

            reader.close();
        }
        catch(XMLReadException ex) {
        	ErrorHandler.showWarning("ERR16", ex, "ERR");
        }
    }


    /**
    * People table
    */
    protected class PeopleTableModel extends AbstractTableModel {
        //fireTableDataChanged();

        String[] columnNames = new String[] {
        	LanguageManager.getString("NAME"),
        	LanguageManager.getString("10_1"),
        	LanguageManager.getString("10_2")
        };

        public Object getValueAt(int rowIndex, int columnIndex) {
            Person person = (Person)people.elementAt(rowIndex);
            switch(columnIndex) {
            	case 0:
                	return person.getName();
            	case 1:
                	return person.getProperty(Person.FAMILY_NAME);
            	case 2:
                	return person.getProperty(Person.GIVEN_NAME);
                default:
                	return "";
            }
        }

        public int getRowCount() {
            return people == null ? 0 : people.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

    }

    /**
    * Resources table
    */
    protected class ResourceTableModel
    extends AbstractTableModel
    implements ListSelectionListener
    {
        //fireTableDataChanged();
        String[] columnNames = new String[] { LanguageManager.getString("NAME") };

        public Object getValueAt(int rowIndex, int columnIndex) {
            Resource resource = (Resource)resources.elementAt(rowIndex);
        	return resource.getName();
        }

        public int getRowCount() {
            return resources == null ? 0 : resources.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        int lastRow = -1;

        /**
        * A row has been selected
        */
        public void valueChanged(ListSelectionEvent e) {
        	updateView();
        }

        public void updateView() {
            int row = resourceTable.getSelectedRow();
            int numRows = getRowCount();
            // No row selected
            if(row == -1 || numRows == 0) resource_descriptionViewer.clear();
            else if(row != lastRow) {
            	// Get the Resource Description
            	Resource resource = (Resource)resources.elementAt(row);
	        	String fileName = DataFiler.getInviteTextFileName(tc.getGUID(), resource.getGUID(), DataFiler.DESCRIPTION, false);
        		resource_descriptionViewer.loadFile(fileName, false);
            }
            //else resource_descriptionViewer.clear();
            lastRow = row;
        }
    }

}
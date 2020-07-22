package net.colloquia.views.tree;

import java.io.*;
import java.util.*;

import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

import org.jdom.*;
import org.jdom.input.*;

public class TreeState {

    public static String GUID = "guid";
    public static String EXPANDED = "expanded";
    public static String HIDE_PEOPLE = "hide_people";
    public static String HIDE_RESOURCES = "hide_resources";
    public static String HIDE_ASSIGNMENT = "hide_assignment";

    /**
    * Write tree state info
    */
    public static void save() throws XMLWriteException {
        File file = new File(DataFiler.getTreeStateFileName(true));

        Element root = new Element("tree_info");
        Element groups = new Element("groups");
        root.addContent(groups);

        ColloquiaTree.ColloquiaTreeModel treeModel = (ColloquiaTree.ColloquiaTreeModel)ColloquiaTree.getInstance().getModel();
        Enumeration nodes = treeModel.getRootNode().preorderEnumeration();
        while(nodes.hasMoreElements()) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)nodes.nextElement();
            ColloquiaComponent tc = node.getComponent();
            if(tc instanceof ColloquiaContainer) {
                Element group = new Element("group");
                if(node.isExpanded) group.addContent(new Element(EXPANDED));
                if(!node.showPeople) group.addContent(new Element(HIDE_PEOPLE));
                if(!node.showResources) group.addContent(new Element(HIDE_RESOURCES));
                if(!node.showAssignment) group.addContent(new Element(HIDE_ASSIGNMENT));

                // Got something to say
                if(group.getChildren().size() > 0) {
                    Element guid = new Element(GUID);
                    guid.setText(tc.getGUID());
                    group.addContent(guid);
                    groups.addContent(group);
                }
            }
        }

        try {
	    	Document doc = new Document(root);
        	XMLUtils.write2XMLFile(doc, file);
        }
        catch(IOException ex) {
            throw new XMLWriteException("Could not save file", ex.getMessage());
        }
    }

    public static void load() {
        File file = new File(DataFiler.getTreeStateFileName(false));
        if(!file.exists()) return;

        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            unMarshallXML(doc);
            doc = null;
        }
        catch(Exception ex) {
            ErrorHandler.showWarning("ERR12", ex, "ERR");
        }
    }

    private static void unMarshallXML(Document doc) {
        Element root = doc.getRootElement();
        if(root == null) return;
        Element groups = root.getChild("groups");
        if(groups == null) return;

        List list = groups.getChildren("group");
        for(int i = 0; i < list.size(); i++) {
            Element element = (Element)list.get(i);
            Element guid = element.getChild(GUID);
            if(guid != null) {
                ColloquiaComponent tc = DataModel.getComponent(guid.getText());
                if(tc instanceof ColloquiaContainer) {
                    ColloquiaContainer group = (ColloquiaContainer)tc;
                    Element expanded = element.getChild(EXPANDED);
                    if(expanded != null) ColloquiaTree.getInstance().expandNode(group, true);
                    Element hidePeople = element.getChild(HIDE_PEOPLE);
                    if(hidePeople != null) ColloquiaTree.getInstance().displayPeople((Activity)group, false);
                    Element hideResources = element.getChild(HIDE_RESOURCES);
                    if(hideResources != null) ColloquiaTree.getInstance().displayResources((Activity)group, false);
                    Element hideAssignment = element.getChild(HIDE_ASSIGNMENT);
                    if(hideAssignment != null) ColloquiaTree.getInstance().displayAssignment((Activity)group, false);
                }
            }
        }
    }
}

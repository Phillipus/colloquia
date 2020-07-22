package net.colloquia.io;

import java.awt.Cursor;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingUtilities;

import net.colloquia.ColloquiaConstants;
import net.colloquia.MainFrame;
import net.colloquia.datamodel.DataModel;
import net.colloquia.datamodel.MemberInfo;
import net.colloquia.datamodel.entities.ColloquiaComponent;
import net.colloquia.datamodel.entities.ColloquiaContainer;
import net.colloquia.util.ErrorHandler;
import net.colloquia.util.LanguageManager;
import net.colloquia.views.ViewPanel;
import net.colloquia.views.tree.TreeState;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


public class Open2 {
    private static boolean firstTime = true;
    private static Open instance = new Open();

    // The entities that are collected
    private static Hashtable components;

    public static void open(File file) {
        MainFrame.getInstance().statusBar.setText(LanguageManager.getString("13_4"));
        if(firstTime) {
            new Go(file).run();
            firstTime = false;
        }
        else SwingUtilities.invokeLater(new Go(file));
    }

    private static class Go implements Runnable {
        File file;

        public Go(File file) {
            this.file = file;
        }

        public void run() {
            Document doc;

            Cursor oldCursor = MainFrame.getInstance().getCursor();
            MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);

            // Clear the data
            DataModel.newData();

            components = new Hashtable();

            try {
	            SAXBuilder builder = new SAXBuilder();
    	        doc = builder.build(file);
                Element root = doc.getRootElement();

                getComponents(root.getChild("people"), ColloquiaComponent.PERSON, "person");
                getComponents(root.getChild("people_groups"), ColloquiaComponent.PERSON_GROUP, "people_group");
                getComponents(root.getChild("resources"), ColloquiaComponent.RESOURCE, "resource");
                getComponents(root.getChild("resource_groups"), ColloquiaComponent.RESOURCE_GROUP, "resource_group");
                getComponents(root.getChild("assignments"), ColloquiaComponent.ASSIGNMENT, "assignment");
                getComponents(root.getChild("activities"), ColloquiaComponent.ACTIVITY, "activity");
                getComponents(root.getChild("templates"), ColloquiaComponent.ACTIVITY, "template");

                addMembers(root.getChild("people_groups"), "people_group");
                addMembers(root.getChild("resource_groups"), "resource_group");
                addMembers(root.getChild("activities"), "activity");
                addMembers(root.getChild("templates"), "template");
            }
            catch(Exception ex) {
                ErrorHandler.showWarning("ERR13", ex, "ERR");
                System.exit(0);
            }

	        //DataModel.setActivitiesHot();

            TreeState.load();

            MainFrame.getInstance().setCursor(oldCursor);
            MainFrame.getInstance().statusBar.clearText();

            // Refresh
            ViewPanel.getInstance().updateCurrentView();

            // Don't need this
            components = null;
            doc = null;
        }
    }

    private static void getComponents(Element element, int type, String name) {
        if(element == null) return;

        List list = element.getChildren(name);

        for(int i = 0; i < list.size(); i++) {
            Element tcElement = (Element)list.get(i);

            ColloquiaComponent tc = ColloquiaComponent.createComponent("-", "-", type);

            // Name
            Attribute attName = tcElement.getAttribute(ColloquiaComponent.NAME);
            if(attName != null) tc.setName(attName.getValue(), false);

            // GUID
            Attribute attGUID = tcElement.getAttribute(ColloquiaComponent.GUID);
            if(attGUID != null) tc.setGUID(attGUID.getValue());

            // Properties / Members
            List prop_list = tcElement.getChildren();
            for(int j = 0; j < prop_list.size(); j++) {
                Element prop = (Element)prop_list.get(j);
                String tag = prop.getName();
                String value = prop.getText();
                if(tag.equalsIgnoreCase("member")) continue;
                else tc.putProperty(tag, value, false);
            }

            String GUID = tc.getGUID();
            if(!GUID.equals("-")) {
                components.put(GUID, tc);
                if(attGUID == null) tcElement.setAttribute(ColloquiaComponent.GUID, GUID);
            }
        }
    }

    private static void addMembers(Element element, String name) {
        if(element == null) return;

        List list = element.getChildren(name);
        for(int i = 0; i < list.size(); i++) {
            // Get Component Group
            Element tcElement = (Element)list.get(i);
            Attribute attGUID = tcElement.getAttribute(ColloquiaComponent.GUID);
            if(attGUID == null) continue;
            String GUID = attGUID.getValue();
            ColloquiaContainer parent = (ColloquiaContainer)components.get(GUID);
            if(parent == null) continue;

            // Re-use existing top groups
            if(DataModel.isTopPeopleGroup(parent)) parent = DataModel.getPeopleGroup();
            else if(DataModel.isTopResourceGroup(parent)) parent = DataModel.getResourceGroup();
            else if(DataModel.isTopActivityGroup(parent)) parent = DataModel.getActivityGroup();
            else if(DataModel.isTemplatesGroup(parent)) parent = DataModel.getTemplateGroup();

            // Members of Group
            List member_list = tcElement.getChildren("member");
            for(int j = 0; j < member_list.size(); j++) {
                Element member = (Element)member_list.get(j);

                // Member GUID
                String memberGUID = null;
                Attribute att = member.getAttribute(ColloquiaComponent.GUID);
                if(att != null) memberGUID = att.getValue();
                else {  // backward compat
                    Element guid = member.getChild("guid");
                    if(guid != null) memberGUID = guid.getText();
                }
                if(memberGUID == null) continue;

                // Get member component from hashtable
                ColloquiaComponent child = (ColloquiaComponent)components.get(memberGUID);
                if(child == null) continue;

                // Create new MemberInfo
                MemberInfo mInfo = new MemberInfo(child, parent);

                // FREE
                att = member.getAttribute(MemberInfo.FREE);
                if(att != null) mInfo.putProperty(MemberInfo.FREE, att.getValue());

                // Member Properties
                List member_props = member.getChildren();
                for(int k = 0; k < member_props.size(); k++) {
                    Element member_prop = (Element)member_props.get(k);
                    mInfo.putProperty(member_prop.getName(), member_prop.getText());
                }

                DataModel.addComponent(mInfo, false);
            }
        }
    }
}

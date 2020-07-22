package net.colloquia.io;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;
import net.colloquia.xml.*;

public class Open {
    private static boolean firstTime = true;
    private static Open instance = new Open();

    // The entities that are collected
    private static Hashtable entities;

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
            Cursor oldCursor = MainFrame.getInstance().getCursor();
            MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
            // Clear the data
            DataModel.newData();

            entities = new Hashtable();

            try {
                readFile(file);
                /*
                Add the components in this order so that when they are added to the
                DataModel we don't add twice
                */
                addComponents((Entity)entities.get("1-1"));
                addComponents((Entity)entities.get("2-2"));
                addComponents((Entity)entities.get("3-3"));
                addComponents((Entity)entities.get("4-4"));
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
            entities = null;
        }
    }

    /**
    * Handle Other tags relating to the tree datamodel
    */
    private static void handleTag(XMLTag xmlTag) {
        if(xmlTag == null) return;

    }

    private static void readFile(File file) throws XMLReadException {
        String line;
        String endTag = "*&*";
        Entity entity = null;
        Member member = null;
        String uline;
        XMLTag xmlTag;

        XMLFileReader reader = new XMLFileReader(file);
        reader.open();

        while((line = reader.readLine()) != null) {
            // Get lower case line
            uline = line.toLowerCase();

            // Do we have a tag?
            xmlTag = XMLTag.getXMLTag(line);
            if(xmlTag != null) {
                // Where does it go? Check for member first
                if(member != null) member.putProperty(xmlTag.tag, xmlTag.value);
                // Then component entity
                else if(entity != null) entity.putProperty(xmlTag.tag, xmlTag.value);
                // Else will be another property
                else handleTag(xmlTag);
                continue;
            }

            // Person
            if(uline.indexOf(Person.XMLStartTag) != -1) {
                entity = new Entity(new Person("-", ""));
                endTag = Person.XMLEndTag;
                continue;
            }
            // Person Group
            if(uline.indexOf(PersonGroup.XMLStartTag) != -1) {
                entity = new Entity(new PersonGroup("-", ""));
                endTag = PersonGroup.XMLEndTag;
                continue;
            }
            // Old Style since Colloquia 1.3
            if(uline.indexOf(PersonGroup.oldXMLStartTag) != -1) {
                entity = new Entity(new PersonGroup("-", ""));
                endTag = PersonGroup.oldXMLEndTag;
                continue;
            }

            // Resource
            if(uline.indexOf(Resource.XMLStartTag) != -1) {
                entity = new Entity(new Resource("-", ""));
                endTag = Resource.XMLEndTag;
                continue;
            }

            // Resource Group
            if(uline.indexOf(ResourceGroup.XMLStartTag) != -1) {
                entity = new Entity(new ResourceGroup("-", ""));
                endTag = ResourceGroup.XMLEndTag;
                continue;
            }
            // Old Style since Colloquia 1.3
            if(uline.indexOf(ResourceGroup.oldXMLStartTag) != -1) {
                entity = new Entity(new ResourceGroup("-", ""));
                endTag = ResourceGroup.oldXMLEndTag;
                continue;
            }

            // Assignment
            if(uline.indexOf(Assignment.XMLStartTag) != -1) {
                entity = new Entity(new Assignment("-", ""));
                endTag = Assignment.XMLEndTag;
                continue;
            }
            // Activity
            if(uline.indexOf(Activity.XMLStartTag) != -1) {
                entity = new Entity(new Activity("-", ""));
                endTag = Activity.XMLEndTag;
                continue;
            }
            // Template
            if(uline.indexOf(TemplateGroup.XMLStartTag) != -1) {
                entity = new Entity(new Activity("-", ""));
                endTag = TemplateGroup.XMLEndTag;
                continue;
            }
            // Member
            if(uline.indexOf(MemberInfo.XMLStartTag) != -1) {
                // Open a new Member
                if(entity != null) member = new Member();
                continue;
            }

            // End of Component
            if((uline.indexOf(endTag) != -1) && (entity != null)) {
                //(make sure it's got a GUID)
                if(!entity.getGUID().equals("")) {
                    entities.put(entity.getGUID(), entity);
                    entity = null;
                }
                continue;
            }

            // End of new Member
            if(uline.indexOf(MemberInfo.XMLEndTag) != -1) {
                // Add Member to parent Entity
                if(entity != null && member != null) entity.addMember(member);
                member = null;
                continue;
            }
        }

        reader.close();
    }


    /**
    * Adds the groups and members to the Datamodel
    */
    private static void addComponents(Entity entity) {
        if(entity == null || !entity.isContainer()) return;

        ColloquiaContainer group = (ColloquiaContainer)entity.getComponent();

        // Re-use existing top groups
        if(DataModel.isTopPeopleGroup(group)) group = DataModel.getPeopleGroup();
        else if(DataModel.isTopResourceGroup(group)) group = DataModel.getResourceGroup();
        else if(DataModel.isTopActivityGroup(group)) group = DataModel.getActivityGroup();
        else if(DataModel.isTemplatesGroup(group)) group = DataModel.getTemplateGroup();

        // Add any members to the group
        Vector members = entity.getMembers();
        if(members != null) {
            for(int i = 0; i < members.size(); i++) {
                Member member = (Member)members.elementAt(i);
                Entity subEntity = member.getEntity();
                // If subEntity is null it must be an orphan - oops!
                if(subEntity != null) {
                    ColloquiaComponent tcMember = subEntity.getComponent();
                    if(tcMember != null) {
                        MemberInfo mInfo = new MemberInfo(tcMember, group);
                        mInfo.setProperties(member.getProperties());
                        DataModel.addComponent(mInfo, false);
                    }
                    if(subEntity.isContainer()) addComponents(subEntity);
                }
            }
        }
    }

    /**
    * This represents a ColloquiaComponent
    */
    private static class Entity {
        private ColloquiaComponent tc;
        private Vector members;

        public Entity(ColloquiaComponent tc) {
            this.tc = tc;
        }

        public void addMember(Member member) {
            if(members == null) members = new Vector();
            members.addElement(member);
        }

        public Vector getMembers() {
            return members;
        }

        public boolean isContainer() {
        	return (tc instanceof ColloquiaContainer);
		}

        public ColloquiaComponent getComponent() {
            return tc;
        }

        public String getGUID() {
            return tc.getGUID();
        }

        public void putProperty(String tag, String value) {
            tc.putProperty(tag, value, false);
        }
    }

    /**
    * Stores a member GUID ref and its MemberInfo props
    */
    private static class Member {
        private Hashtable properties = new Hashtable();

        public void putProperty(String tag, String value) {
            properties.put(tag, value);
        }

        public Hashtable getProperties() {
        	return properties;
        }

        public Entity getEntity() {
        	String GUID = (String)properties.get(ColloquiaComponent.GUID);
            if(GUID == null) return null;
	        return (Entity)entities.get(GUID);
        }
    }

}

package net.colloquia.views;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

public class ComponentTableRenderer
extends ColloquiaTableRenderer
{
    ImageIcon iconPerson = Utils.getIcon(ColloquiaConstants.iconPerson);
    ImageIcon iconResource = Utils.getIcon(ColloquiaConstants.iconResource);
    ImageIcon iconAssignment = Utils.getIcon(ColloquiaConstants.iconAssignment);
    ImageIcon iconActivity = Utils.getIcon(ColloquiaConstants.iconActivityLiveFolder);
    ImageIcon iconFolder = Utils.getIcon(ColloquiaConstants.iconFolder);
    ImageIcon icon;

    Vector components;
    Class type;

    public ComponentTableRenderer(Class type) {
    	this.type = type;
       	if(type == Person.class) icon = iconPerson;
       	else if(type == Resource.class) icon = iconResource;
       	else if(type == Assignment.class) icon = iconAssignment;
       	else if(type == Activity.class) icon = iconActivity;
        else icon = iconFolder;
    }

    public ComponentTableRenderer(Vector components) {
    	setComponents(components);
	}

    public void setComponents(Vector components) {
    	this.components = components;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {

        if(components != null) {
        	ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(row);
    		if(tc instanceof Person) icon = iconPerson;
    		else if(tc instanceof Resource) icon = iconResource;
    		else if(tc instanceof Assignment) icon = iconAssignment;
    		else if(tc instanceof Activity) icon = iconActivity;
        	else icon = iconFolder;
        }

        if(column == 0) label.setIcon(icon);
        else label.setIcon(null);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

}


package net.colloquia.gui.widgets;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;

public class PComboBox
extends JComboBox
{
    DefaultComboBoxModel model;

    public PComboBox() {
        super();
        setFont(ColloquiaConstants.plainFont12);
        addEditListener();
    }

    public PComboBox(Vector v) {
        super(v);
        setFont(ColloquiaConstants.plainFont12);
        addEditListener();
    }

    public PComboBox(Object[] o) {
        super(o);
        setFont(ColloquiaConstants.plainFont12);
        addEditListener();
    }

    protected void addEditListener() {
        getEditor().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selItem = (String)getSelectedItem();
                if(selItem != null) selItem = selItem.trim();
                String editedItem = e.getActionCommand().trim();
                if(editedItem.length() == 0) {
                    removeItem(selItem);
                }
                else {
                    Vector v = getAllItems();
                    if(!v.contains(editedItem)) {
                        addItem(editedItem);
                        setSelectedItem(editedItem);
                    }
                }
            }
        });
    }

    public void setObjects(Vector v) {
        setModel(new DefaultComboBoxModel(v));
    }

    public Vector getAllItems() {
    	Vector v = new Vector();

        for(int i = 0; i < getItemCount(); i++) {
        	v.addElement(getItemAt(i));
        }

        return v;
    }
}
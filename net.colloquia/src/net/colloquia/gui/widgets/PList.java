package net.colloquia.gui.widgets;

import java.util.*;

import javax.swing.*;

import net.colloquia.*;

public class PList
extends JList
{
    Vector list;

    public PList() {
        super();
        setFont(ColloquiaConstants.plainFont12);
    }

    public PList(Vector list) {
        super(list);
        this.list = list;
        setFont(ColloquiaConstants.plainFont12);
    }

    public void setListData(Vector list) {
        super.setListData(list);
        this.list = list;
    }

    public Vector getListData() {
        return list;
    }

    public void addItem(Object o) {
        list.addElement(o);
        super.setListData(list);
    }

    public void removeItem(Object o) {
        list.removeElement(o);
        super.setListData(list);
    }
}
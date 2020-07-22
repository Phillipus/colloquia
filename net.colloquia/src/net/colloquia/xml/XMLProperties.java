package net.colloquia.xml;


import java.util.*;

/*
* A Hashtable for storing property pairs with some convenience methods
*/
public class XMLProperties
extends Hashtable
{

    public void putProperty(String key, String value) {
        if(key == null || value == null) return;
        if(key.equals("")) return;
        key = key.toLowerCase();
        put(key, value);
    }

    public void putProperty(String key, boolean value) {
    	putProperty(key, value ? "true" : "false");
    }

    public void putProperty(String key, int value) {
    	putProperty(key, String.valueOf(value));
    }

    public void putProperty(String key, Date date) {
    	putProperty(key, String.valueOf(date.getTime()));
    }


    public String getProperty(String key) {
        String value = (String)get(key.toLowerCase());
        return value == null ? "" : value;
    }

    public boolean getBooleanProperty(String key) {
        String val = getProperty(key);
        return val.toLowerCase().equals("true");
    }

    public int getIntegerProperty(String key) {
        String val = getProperty(key);
        if(val.equals("")) return 0;
        else return Integer.parseInt(val);
    }

    public Date getDateProperty(String key) {
        String val = getProperty(key);
        if(val.equals("")) return null;
        else return new Date(Long.parseLong(val));
    }

}
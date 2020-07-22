package net.colloquia.datamodel;


public class FieldValue {
    public String key, friendlyName;
    public boolean isEditable, isSendable;
    
	public FieldValue(String key, String friendlyName, boolean isEditable, boolean isSendable) {
    	this.key = key;
        this.friendlyName = friendlyName;
        this.isEditable = isEditable;
        this.isSendable = isSendable;
    }

}

package net.colloquia.xml;


public class XMLAttribute {
	public String attr;
    public String value;

    public XMLAttribute(String attr, String value) {
        this.attr = attr.toLowerCase();
        this.value = XMLUtils.escape(value);
    }

    /**
    * Marshalls the tag and value (value will already be escaped)
    */
    public String marshall2XML() {
        StringBuffer sb = new StringBuffer();

        if(attr != null && value != null) {
            if(!value.trim().equals("")) { // Don't save empty Strings
                sb.append(" ");
                sb.append(attr + "=\"");
                sb.append(value);
                sb.append("\"");
            }
        }

        return sb.toString();
    }
}

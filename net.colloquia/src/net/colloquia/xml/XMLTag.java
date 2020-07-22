package net.colloquia.xml;

public class XMLTag {
    public static String CR = System.getProperty("line.separator");

    public String tag;
    public String value;
    public XMLAttribute[] attr;

    /**
    * Creates an XMLTag ready to save that has been escaped
    */
    public XMLTag(String tag, String value) {
        this(tag, value, true);
    }

    public XMLTag(String tag, String value, XMLAttribute[] attr) {
        this(tag, value, true);
        this.attr = attr;
    }

    private XMLTag(String tag, String value, boolean escape) {
        this.tag = tag.toLowerCase();
        fix();
        if(escape) this.value = XMLUtils.escape(value);
        else this.value = XMLUtils.unescape(value);
    }

    /**
    * Fix old tag names that are not compliant
    * Introduced in Colloquia ver 1.3
    */
    private void fix() {
        if(tag.equals("due date")) tag = "due_date";
        else if(tag.equals("start date")) tag = "start_date";
        else if(tag.equals("date set")) tag = "date_set";
        else if(tag.equals("family name")) tag = "family_name";
        else if(tag.equals("given name")) tag = "given_name";
        else if(tag.equals("phone number")) tag = "phone_number";
        else if(tag.equals("physical location")) tag = "physical_location";
    }

    /**
    * Factory method for creating an XMLTag from one line from a file
    * value will be unescaped
    * Only gets it if formed like <tagname>My Value</tagname>
    */
    public static XMLTag getXMLTag(String line) {
        // Do we have a start tag?
        int startPos = line.indexOf("<");
        if(startPos == -1) return null;

        int endPos = line.indexOf(">", startPos);
        if(endPos == -1) return null;

        String tag = line.substring(startPos + 1, endPos);

        // Do we have a closing tag?
        int endTagPos = line.indexOf("</" + tag + ">", endPos);
        if(endTagPos == -1) return null;

        String value = line.substring(endPos + 1, endTagPos);

        return new XMLTag(tag, value, false);
    }

    /**
    * Marshalls the tag and value and any attributes
    */
    public String marshall2XML() {
        StringBuffer sb = new StringBuffer();

        if(tag != null && value != null) {
            if(!value.trim().equals("")) { // Don't save empty Strings
                sb.append("<" + tag);

                if(attr != null) {
                	for(int i = 0; i < attr.length; i++) {
                    	if(attr[i] != null) sb.append(attr[i].marshall2XML());
                    }
                }

                sb.append(">");
                sb.append(value);
                sb.append("</" + tag + ">" + CR);
            }
        }

        return sb.toString();
    }

}

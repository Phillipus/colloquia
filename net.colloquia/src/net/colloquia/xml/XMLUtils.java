package net.colloquia.xml;

import java.io.*;

import net.colloquia.io.*;

import org.jdom.*;
import org.jdom.output.*;

public class XMLUtils {

    public static void write2XMLFile(Document doc, File file) throws IOException {
        XMLOutputter outputter = new XMLOutputter("  ", true);
        outputter.setTextNormalize(true);
        FileOutputStream out = new FileOutputStream(file);
        outputter.output(doc, out);
        out.close();
    }

    // System independant line seperator carriage return
    public static final String CR = System.getProperty("line.separator");

    /**
    * Returns the value of the FIRST occurence of tag key in filename
    * or null if not found
    */
    public static String getXMLKey(String key, String fileName) {
        if(!DataFiler.fileExists(fileName)) return null;
        String startKey = "<" + key + ">";
        String endKey = "</" + key + ">";
        String line, tmpLine;
        String value = null;
        int startPos, endPos;

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while((line = in.readLine()) != null) {
                tmpLine = line.toLowerCase();
                startPos = tmpLine.indexOf(startKey);
                if(startPos != -1) {
                    endPos = tmpLine.indexOf(endKey);
                    if(endPos != -1) {
                        startPos += startKey.length();
                        if(startPos < endPos) {
                            value = line.substring(startPos, endPos);
                            break;
                        }
                    }
                }
            }
            in.close();
        } catch (Exception ex) {
            System.out.println("XML Parser error: " + ex);
            return value;
        }

        return value;
    }

    /**
    * Method for taking an XML value, checking for blanks
    * Returns false if value is blank
    */
    public static boolean isValue(String value) {
    	if(value == null) return false;
        if(value.trim().equals("")) return false;
        return true;
    }

    /**
    * Check a String value and replace as follows:-
    * <   -   &lt;
    * >   -   &gt;
    * &   -   &amp;
    * '   -   &apos;
    * "   -   &quot;
    */
    public static String escape(String xmlValue) {
        if(xmlValue != null) {
            xmlValue = escape(xmlValue, '&', "&amp;");      // Do this one first!!
            xmlValue = escape(xmlValue, '<', "&lt;");
            xmlValue = escape(xmlValue, '>', "&gt;");
            xmlValue = escape(xmlValue, '\'', "&apos;");
            xmlValue = escape(xmlValue, '\"', "&quot;");
        }
        return xmlValue;
    }

    public static String unescape(String xmlValue) {
        if(xmlValue != null) {
            xmlValue = unescape(xmlValue, '&', "&amp;");      // Do this one first!!
            xmlValue = unescape(xmlValue, '<', "&lt;");
            xmlValue = unescape(xmlValue, '>', "&gt;");
            xmlValue = unescape(xmlValue, '\'', "&apos;");
            xmlValue = unescape(xmlValue, '\"', "&quot;");
        }
        return xmlValue;
    }

    private static String unescape(String xmlValue, char badChar, String strReplace) {
        int index = xmlValue.indexOf(strReplace);
        // Check whether we have at least one occurrence
        if(index == -1) return xmlValue;

        // This is a bit expensive!
        while((index = xmlValue.indexOf(strReplace)) != -1) {
            xmlValue = xmlValue.substring(0, index) + badChar + xmlValue.substring(index + strReplace.length());
        }

        return xmlValue;
    }

    private static String escape(String xmlValue, char badChar, String strReplace) {
        // Check whether we have at least one occurrence
        int index = xmlValue.indexOf(badChar);
        if(index == -1) return xmlValue;

        // Replace each bad char starting from first occurrence
        StringBuffer sb = new StringBuffer(xmlValue.substring(0, index));

        for(int i = index; i < xmlValue.length(); i++) {
            char c = xmlValue.charAt(i);
            if(c == badChar) sb.append(strReplace);
            else sb.append(c);
        }

        return sb.toString();
    }

}

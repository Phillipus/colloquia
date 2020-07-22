package net.colloquia.io;

import java.io.*;
import java.util.*;

import net.colloquia.xml.*;

import org.jdom.*;
import org.jdom.input.*;

public class FileType {
    public static FileType[] DEFAULT_TYPES;
    static FileType[] fileTypes;

    static {
        DEFAULT_TYPES = new FileType[] {
            // Apps
            new FileType(".zip", "Zip File", false),
            new FileType(".rar", "WinRar Archive", false),
            new FileType(".pps", "Power Point Slideshow", false),
            new FileType(".ppt", "Power Point Presentation", false),
            new FileType(".doc", "Microsoft Word document", false),
            new FileType(".rtf", "Microsoft Word document", false),
            new FileType(".mdb", "Microsoft Access Database", false),
            new FileType(".xls", "Microsoft Excel Worksheet", false),
            new FileType(".pdf", "Adobe Acrobat Document", false),
            new FileType(".exe", "Application", false),
            new FileType(".avi", "AVI clip", false),

            // Quicktime
            new FileType(".qts", "Quicktime File", true),
            new FileType(".qt", "Quicktime Movie", true),
            new FileType(".qtl", "Quicktime Movie", true),
            new FileType(".mov", "Movie File", true),
            new FileType(".wav", "Audio File", true),
            new FileType(".mp3", "MP3 audio File", true),
            new FileType(".aif", "Audio File", true),
            new FileType(".mpg", "Movie File", true),
        };

        fileTypes = loadFileTypes();
    }

    private String ext = "";
    private String desc = "";
    private boolean isMedia = false;

	public FileType(String ext, String desc, boolean isMedia) {
    	this.ext = (ext == null) ? "" : ext.toLowerCase();
        this.desc = (desc == null) ? "" : desc;
        this.isMedia = isMedia;
    }

    public String getExtension() {
    	return ext;
    }

    public String getDescription() {
    	return desc;
    }

    public boolean isMediaFile() {
    	return isMedia;
    }

    public void setExtension(String s) {
    	ext = s;
    }

    public void setDescription(String s) {
    	desc = s;
    }

    public void setIsMedia(boolean b) {
    	isMedia = b;
    }

    public static FileType[] getFileTypes() {
        return fileTypes;
    }

	public static void save(FileType[] fileTypes) {
        if(fileTypes == null) return;

    	File file = new File(DataFiler.getColloquiaFolder(), "filetypes.xml");

        Element root = new Element("file_types");

        for(int i = 0; i < fileTypes.length; i++) {
			String ext = fileTypes[i].getExtension().trim();
            if(ext.equals("")) continue;

        	Element element = new Element("file_type");
            Attribute att = new Attribute("ext", ext);
            element.setAttribute(att);

			String desc = fileTypes[i].getDescription().trim();
            if(!desc.equals("")) {
            	Attribute att2 = new Attribute("desc", desc);
                element.setAttribute(att2);
            }

            if(fileTypes[i].isMedia) {
            	Attribute att3 = new Attribute("media", "1");
                element.setAttribute(att3);
            }

            root.addContent(element);
        }

        try {
	    	Document doc = new Document(root);
        	XMLUtils.write2XMLFile(doc, file);
        }
        catch(IOException ex) {
        	System.out.println("Could not save FileTypes: " + ex);
        }

        // Re-load so we can clear any baddies
	    FileType.fileTypes = loadFileTypes();
    }

    private static FileType[] loadFileTypes() {
    	File file = new File(DataFiler.getColloquiaFolder(), "filetypes.xml");
        if(!file.exists()) return DEFAULT_TYPES;

		Vector v = new Vector();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            unMarshallXML(doc, v);
            doc = null;
        }
        catch(Exception ex) {
            System.out.println("Could not get FileTypes: " + ex);
        }

        FileType[] ft = new FileType[v.size()];
        v.copyInto(ft);
        return ft;
    }

    private static void unMarshallXML(Document doc, Vector v) {
        Element root = doc.getRootElement();
        if(root == null) return;

        List list = root.getChildren("file_type");
        for(int i = 0; i < list.size(); i++) {
            Element file_type = (Element)list.get(i);

            Attribute ext = file_type.getAttribute("ext");
            if(ext == null) continue;

        	String extension = ext.getValue();
            String description = "";

            Attribute desc = file_type.getAttribute("desc");
            if(desc != null) description = desc.getValue();

            Attribute media = file_type.getAttribute("media");

            FileType ft = new FileType(extension, description, (media != null));
            v.addElement(ft);
        }
    }

}

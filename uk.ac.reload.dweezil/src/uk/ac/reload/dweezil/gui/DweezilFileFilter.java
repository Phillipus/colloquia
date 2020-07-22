/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton University
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@dadabeatnik.com
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.dweezil.gui;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those type extensions that it knows about.
 *
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macintosh. Case is ignored.
 *
 * Example - create a new filter that filters out all files
 * but gif and jpg image files:
 *
 *     JFileChooser chooser = new JFileChooser();
 *     ToomolFileFilter filter = new ToomolFileFilter(
 *                   new String{"gif", "jpg"}, "JPEG & GIF Images")
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 *
 */
public class DweezilFileFilter extends FileFilter {
	private static String TYPE_UNKNOWN = "Type Unknown";
	private static String HIDDEN_FILE = "Hidden File";
	
	private Hashtable filters = null;
	private String description = null;
	private String fullDescription = null;
	private boolean useExtensionsInDescription = true;
	
	/**
	 * Creates a file filter. If no filters are added, then all
	 * files are accepted.
	 *
	 * @see #addExtension
	 */
	public DweezilFileFilter() {
		this.filters = new Hashtable();
	}
	
	/**
	 * Creates a file filter that accepts files with the given extension.
	 * Example: new ToomolFileFilter("jpg");
	 *
	 * @see #addExtension
	 */
	public DweezilFileFilter(String extension) {
		this(extension, null);
	}
	
	/**
	 * Creates a file filter that accepts the given file type.
	 * Example: new ToomolFileFilter("jpg", "JPEG Image Images");
	 *
	 * Note that the "." before the extension is not needed. If
	 * provided, it will be ignored.
	 *
	 * @see #addExtension
	 */
	public DweezilFileFilter(String extension, String description) {
		this();
		if(extension != null) addExtension(extension);
		if(description != null) setDescription(description);
	}
	
	/**
	 * Creates a file filter from the given string array.
	 * Example: new ToomolFileFilter(String {"gif", "jpg"});
	 *
	 * Note that the "." before the extension is not needed and
	 * will be ignored.
	 *
	 * @see #addExtension
	 */
	public DweezilFileFilter(String[] filters) {
		this(filters, null);
	}
	
	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new ToomolFileFilter(new String {"gif", "jpg"}, "Gif and JPG Images");
	 *
	 * Note that the "." before the extension is not needed and will be ignored.
	 *
	 * @see #addExtension
	 */
	public DweezilFileFilter(String[] filters, String description) {
		this();
		for (int i = 0; i < filters.length; i++) {
			// add filters one by one
			addExtension(filters[i]);
		}
		if(description != null) setDescription(description);
	}
	
	/**
	 * Return true if this file should be shown in the directory pane,
	 * false if it shouldn't.
	 *
	 * Files that begin with "." are ignored.
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if(extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the extension portion of the file's name .
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension(File f) {
		if(f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if(i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}
	
	/**
	 * Adds a filetype "dot" extension to filter against.
	 *
	 * For example: the following code will create a filter that filters
	 * out all files except those that end in ".jpg" and ".tif":
	 *
	 *   ToomolFileFilter filter = new ToomolFileFilter();
	 *   filter.addExtension("jpg");
	 *   filter.addExtension("tif");
	 *
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public void addExtension(String extension) {
		if(filters == null) {
			filters = new Hashtable(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}
	
	
	/**
	 * Returns the human readable description of this filter. For
	 * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 */
	public String getDescription() {
		if(fullDescription == null) {
			if(description == null || isExtensionListInDescription()) {
				fullDescription = description==null ? "(" : description + " (";
				// build the description from the extension list
				Enumeration extensions = filters.keys();
				if(extensions != null) {
					fullDescription += "." + (String) extensions.nextElement();
					while (extensions.hasMoreElements()) {
						fullDescription += ", " + (String) extensions.nextElement();
					}
				}
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}
	
	/**
	 * Sets the human readable description of this filter. For
	 * example: filter.setDescription("Gif and JPG Images");
	 *
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}
	
	/**
	 * Determines whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 *
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see #getDescription
	 * @see #setDescription
	 * @see #isExtensionListInDescription
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}
	
	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 *
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see #getDescription
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
}

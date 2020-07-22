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

package uk.ac.reload.diva.prefs;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;

import uk.ac.reload.jdom.XMLUtils;

/**
 * The User Prefs file.  Implementers will have to fulfil the abstract methods
 *
 * @author Phillip Beauvoir
 * @version $Id: UserPrefs.java,v 1.3 2007/07/15 20:27:53 phillipus Exp $
 */
public abstract class UserPrefs {
	/**
	 * The JDOM Document
	 */
    private Document _doc;
	
	/**
	 * Constructor
	 */
	protected UserPrefs() {
		// Load it
		_doc = load();
		// Set defaults
		setDefaultValues();
	}
	
	/**
	 * Load the Preferences File
	 * @return the JDOM Document
	 */
	protected Document load() {
		Document doc = null;
		
		// Get Prefs file
		File prefsFile = getPrefsFile();
		
		// If the file exists read it in
		if(prefsFile.exists()) {
			try {
				doc = XMLUtils.readXMLFile(prefsFile);
			}
			catch(Exception ex) {
				// Oops, so create a new one
				doc = createNewPrefsFile();
			}
			
			// Check it's ours
			if(doc != null && doc.hasRootElement()) {
				Element root = doc.getRootElement();
				// If not ours, create new one
				String rootName = root.getName();
				if(!rootName.equals(getElementRootName())) {
					doc = createNewPrefsFile();
				}
			}
		}
		
		// Else create a new one
		if(doc == null) {
			doc = createNewPrefsFile();} 
		
		return doc;
	}
	
	/**
	 * Save the Prefs file
	 * @throws IOException
	 */
	public void save() throws IOException {
		File file = getPrefsFile();
		XMLUtils.write2XMLFile(_doc, file);
	}
	
	/**
	 * Create a new JDOM Prefs Document with root element
	 * @return the JDOM Document
	 */
	protected Document createNewPrefsFile() {
		Document doc = new Document();
		doc.setRootElement(new Element(getElementRootName()));
		return doc;
	}
	
	/**
	 * @return a value string given a key or null if not found
	 */
	public String getValue(String key) {
		if(_doc != null && _doc.hasRootElement()) {
			Element element = _doc.getRootElement().getChild(key);
			if(element != null) {
				return element.getText();
			} 
		}
		return null;
	}
	
	/**
	 * @return a boolean value given a key, if it doesn't exist the default is false
	 */
	public boolean getBooleanValue(String key) {
		String value = getValue(key);
		return value == null ? false : value.equals("true");
	}
	
	/**
	 * Add a Boolean value
	 */
	public void putBooleanValue(String key, boolean value) {
		putValue(key, value ? "true" : "false");
	}
	
	/**
	 * Put a key and value
	 */
	public void putValue(String key, String value) {
		if(_doc != null) {
			// Ensure we have a root element
			Element root = _doc.getRootElement();
			if(root == null) {
				root = new Element(getElementRootName());
				_doc.setRootElement(root);
			}
			// Do we have it already?
			Element element = root.getChild(key);
			if(element != null) {
				element.setText(value);
			} 
			// New one
			else {
				element = new Element(key);
				element.setText(value);
				root.addContent(element);
			}
		}
	}
	
	/**
	 * Remove a value
	 */
	public void removeValue(String key) {
		if(_doc != null && _doc.hasRootElement()) {
			_doc.getRootElement().removeChild(key);
		}
	}
	
	/**
	 * @return the Preferences File
	 */
	public abstract File getPrefsFile();
	
	/**
	 * Set some default values
	 */
	protected abstract void setDefaultValues();
	
	/**
	 * @return the root Element name
	 */
	public abstract String getElementRootName();
}

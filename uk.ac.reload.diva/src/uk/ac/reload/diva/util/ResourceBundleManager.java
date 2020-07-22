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

package uk.ac.reload.diva.util;

import java.io.File;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Handles ResourceBundles
 *
 * @author Phillip Beauvoir
 * @version $Id: ResourceBundleManager.java,v 1.4 2007/07/15 20:27:52 phillipus Exp $
 */
public class ResourceBundleManager {
	
	/**
	 * The ResourceBundle itself
	 */
    private ResourceBundle _bundle;
	
	/**
	 * Cached Files
	 */
    private Hashtable FILE_CACHE = new Hashtable();
	
	/**
	 * Constructor
     * Loads the language-specific resource bundle according to system locale
     *
	 * @param baseName The base name of the resource bundle, a fully qualified class name
	 */
	public ResourceBundleManager(String baseName) {
	    _bundle = ResourceBundle.getBundle(baseName);
	}
	
	/**
	 * Constructor
     * Takes a ResourceBundle.
     * Useful for when the classloader here cannot get the right classpath as in Eclipse plugins.
     * 
	 * @param bundle The ResourceBundle
	 */
	public ResourceBundleManager(ResourceBundle bundle) {
	    _bundle = bundle;
	}
	
    /**
     * @return the ResourceBundle
     */
    public ResourceBundle getResourceBundle() {
        return _bundle;
    }
    
    
    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param key the key for the desired string
     * @return the string for the given key
     */
    public String getString(String key) {
        return _bundle.getString(key);
    }

    /**
     * Get a file location from the resource bundle.<p>
     * <p>
     * System properties are surrounded with {} brace characters:<p>
     * <p>
     * {user.dir}<p>
     * {user.home}<p>
     * {working.dir}
     * <p>
     * You can declare variables in the properties file building on these two constants such as:<p>
     * <p>
     * my.dir={user.home}/reload/reload-editor<p>
     * preview.dir=my.dir/preview<p>
     * help.file={user.dir}/../editor/help/index.html<p>
     * <p>
     * Or use absolute and relative file paths like<p>
     * my.dir=c:/myfolder<p>
     * my.dir=folder/folder2<p>
     * my.dir=../folder/folder2<p>
     * 
     * @param key The ResourceBundle key
     * @return the File or null if not found
     */
    public File getFileProperty(String key) {
    	// Do we have it as a cached File?
    	File file = (File)FILE_CACHE.get(key);
    	
    	// If not, parse the String and convert to File
    	if(file == null) {
    	    String str = null;
    	    
    	    try {
    	        str = _bundle.getString(key);
    	    }
    	    catch(MissingResourceException ex) {
    	        return null;
    	    }
        	
    		String root = null;
    		String path = null;
    		
        	// Parse for a variable assuming there will be only one and at the start of the string
    		
    		// Find the first instance of the / character
    		int index = str.indexOf("/");

    		// No path so use whole string and assume that it's a variable
    		if(index == -1) {
    			root = str;
    		}
    		else {
    			root = str.substring(0, index);
    			path = str.substring(index);
    		}
    		
    		// If a system property
    		if(root.length() > 0 && root.charAt(0) == '{' && root.charAt(root.length()-1) == '}') {
    		    root = root.substring(1, root.length() - 1);
    			root = System.getProperty(root);
    			if(root == null) {  // Not found so ignore and set default
    			    root = ".";
    			}
    		}
    		// User variable or absolute path
    		else {
    			File f = getFileProperty(root);
    			if(f != null) {
    			    root = f.getPath();
    			}
    		}
    		
    		if(root != null) {
    		    if(path != null) {
    		        file = new File(root, path);
    		    }
    		    else {
    		        file = new File(root);
    		    }

    		    // Cache it
        		FILE_CACHE.put(key, file);
    		}
    	}
    	
    	return file;
    }
}

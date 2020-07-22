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

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * An implementation of JFileChooser that remembers the last directory.
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilFileChooser.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilFileChooser
extends JFileChooser
{
	/**
	 * The initial default directory
	 */
    private static File _lastFolder;
	
	/**
	 * Ask the user for a File name to open
	 * @return File or null if cancelled
	 */
	public static File askFileNameOpen(Component parent, String title, javax.swing.filechooser.FileFilter filter) {
		// Ask for the File Name
		DweezilFileChooser chooser = new DweezilFileChooser();
		chooser.setDialogTitle(title);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(parent);
		
		// User Cancelled
		if(returnVal != JFileChooser.APPROVE_OPTION) {
		    return null;
		}
		
		// Get the chosen File
		return chooser.getSelectedFileAndStore();
	}
	
	/**
	 * Ask the user for a File name to save a Document as
	 * @param parent
	 * @param title
	 * @param filter
	 * @param extension The default extension to use in case the user doesn't provide one (no dot)
	 * @return File or null if cancelled
	 */
	public static File askFileNameSave(Component parent, String title, javax.swing.filechooser.FileFilter filter, String extension) {
		// Ask for the File Name
		DweezilFileChooser chooser = new DweezilFileChooser();
		chooser.setDialogTitle(title);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(parent);
		
		// User Cancelled
		if(returnVal != JFileChooser.APPROVE_OPTION) {
		    return null;
		}
		
		// Get the chosen File
		File file = chooser.getSelectedFileAndStore();
		
		// Ensure we have an extension if given
		if(extension != null) {
			String fileName = file.getPath();
			if(fileName.indexOf(".") == -1) {
			    file = new File(fileName + "." + extension);
			}
		}
		
		return file;
	}

	/**
	 * Set the initial folder
	 */
	public static void setDefaultFolder(File defFolder) {
		_lastFolder = defFolder;
	}
	
	/**
	 * Default constructor for last saved folder
	 */
	public DweezilFileChooser() {
		this(_lastFolder);
	}
	
	/**
	 * Constructor for a given folder
	 * @param folder
	 */
	public DweezilFileChooser(File folder) {
		super(folder);
	}
	
	/**
	 * Returns the selected file and updates the local dir
	 * @return The selected file
	 */
	public File getSelectedFileAndStore() {
		File file = getSelectedFile();
		if(file != null) _lastFolder = file.getParentFile();
		return file;
	}
	
	/**
	 * Returns the selected files and updates the local dir
	 * @return The selected files
	 */
	public File[] getSelectedFilesAndStore() {
		File[] files = getSelectedFiles();
		if(files != null) _lastFolder = files[0].getParentFile();
		return files;
	}
	
	/**
	 * Store the current directory.
	 * @param folder The folder to set.
	 */
	public void setStoredFolder(File folder) {
		_lastFolder = folder;
	}
	
	public File getStoredFolder() {
		return _lastFolder;
	}
}

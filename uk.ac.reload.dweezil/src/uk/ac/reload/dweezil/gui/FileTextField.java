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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import uk.ac.reload.diva.util.FileUtils;
import uk.ac.reload.dweezil.util.DweezilUIManager;

/**
 * A Combination of JTextField and File Chooser for selecting files or folders.<br>
 * <br>
 * A filechooser will be launched when the button is clicked and the result of the chosen
 * file or folder will be set in the Text Field.
 *
 * @author Phillip Beauvoir
 * @version $Id: FileTextField.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class FileTextField
extends JPanel
{
	/**
	 * A Textfield to display the value
	 */
    private JTextField _textField;
	
	/**
	 * A button to open the File Chooser
	 */
    private JButton _buttonFileOpen;
	
	/**
	 * The Icon to display on the File Open JButton
	 */
    private Icon _iconFileOpen;
	
	/**
	 * The file chooser
	 */
    private DweezilFileChooser _fileChooser;
	
	/**
	 * The text to display in the the File Chooser
	 */
    private String _text;
	
	/**
	 * Type of chooser - FILE_TYPE or FOLDER_TYPE
	 */
    private int _type;
	
	/**
	 * Offset File if the file path should be relative.  Otherwise it will be absolute.
	 */
    private File _relativePath;
	
	/**
	 * Type of chooser - FILE_TYPE
	 */
	public static int FILE_TYPE = 1;
	
	/**
	 * Type of chooser - FOLDER_TYPE
	 */
	public static int FOLDER_TYPE = 2;
	
	/**
	 * Constructor
	 * @param type The file type to select - This can be FileTextField.FOLDER_TYPE to select
	 * folders or FileTextField.FILE_TYPE to select files
	 * @param text The Text to display in the the File Chooser
	 * @param iconFileOpen The Icon to display on the File Open JButton.  If this is null then "..." will be displayed.
	 */
	public FileTextField(int type, String text, Icon iconFileOpen) {
		_type = type;
		_text = text;
		_iconFileOpen = iconFileOpen;
		
		setup();
	}
	
	/**
	 * Setup the components.<p>
	 * This will:<p>
	 * 1. Add the textfield to the CENTER<p>
	 * 2. Add the file open button to the EAST<p>
	 * 3. Call setupAction() to add the ActionListener to the file open button
	 */
	protected void setup() {
	    // Layout
	    setLayout(new BorderLayout());
	    
		// Add text field
		add(getTextField(), BorderLayout.CENTER);
		
		// Add File Open Button
		add(getFileOpenButton(), BorderLayout.EAST);
		
		// Set up the file open button's Action behaviour
		setupFileOpenAction();
	}
	
	/**
	 * Set the UI
	 */
	protected void setUI() {
		setOpaque(false);
	}
	
	/**
	 * Update the UI
	 */
	public void updateUI() {
		super.updateUI();
		setUI();
	}
	
	/**
	 * Set up the file open button's Action behaviour
	 * This can be over-ridden by sub-classes to offer an alternative action behaviour.
	 */
	protected void setupFileOpenAction() {
		if(getFileOpenButton() != null) {
			getFileOpenButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(_fileChooser == null) {
						// Files
					    if(_type == FILE_TYPE) {
						    _fileChooser = new DweezilFileChooser();
						}
					    // Folders
						else {
						    _fileChooser = new DweezilFolderChooser();
						}
						_fileChooser.setDialogTitle(_text);
						_fileChooser.setApproveButtonText("Select");
					}
					
					int returnVal = _fileChooser.showOpenDialog(null);
					if(returnVal == DweezilFileChooser.APPROVE_OPTION) {
						// Get the chosen File
						File file = _fileChooser.getSelectedFileAndStore();
						String path;
						// relative path?
						if(_relativePath != null && _relativePath.exists()) {
							path = FileUtils.getRelativePath(_relativePath, file);
						}
						else {
						    path = file.getPath();
						}
						setTextValue(path);
					}
				}
			});
		}
	}
	
	/**
	 * @return the JTextField.
	 * This can be over-ridden by sub-classes to offer an alternative widget.
	 */
	public JTextField getTextField() {
		if(_textField == null) {
			_textField = new JTextField();
			_textField.setFont(DweezilUIManager.plainFont11);
		}
		return _textField;
	}
	
	/**
	 * @return the JButton for the "File Open..." Action
	 * This can be over-ridden by sub-classes to offer an alternative button.
	 */
	public JButton getFileOpenButton() {
		if(_buttonFileOpen == null) {
			_buttonFileOpen = new JButton(_iconFileOpen);
			if(_iconFileOpen == null) {
			    _buttonFileOpen.setText("...");
			}
		}
		return _buttonFileOpen;
	}
	
	/**
	 * Set the Value
	 * @param value
	 */
	public void setTextValue(String value) {
		getTextField().setText(value);
	}
	
	/**
	 * @return the text value
	 */
	public String getTextValue() {
		return getTextField().getText();
	}
	
	/**
	 * @return the relative file path or null if there is none
	 */
	public File getRelativePath() {
		return _relativePath;
	}
	
	/**
	 * Set the relative file path. If null, it will not be relative
	 * @param relativePath the path
	 */
	public void setRelativePath(File relativePath) {
		_relativePath = relativePath;
	}
	
	/**
	 * Set the components enabled/disabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		getTextField().setEnabled(enabled);
		getFileOpenButton().setEnabled(enabled);
	}
}
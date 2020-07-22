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

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * A ComboBox with some special properties
 *
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilComboBox.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilComboBox extends JComboBox {
	
	/**
	 * Constructor
	 */
	public DweezilComboBox() {
		super();
		setUI();
	}
	
	/**
	 * Constructor
	 */
	public DweezilComboBox(boolean editable) {
		setEditable(editable);
		setUI();
	}
	
	public DweezilComboBox(Object[] items) {
		super(items);
	}
	
	/**
	 * Populate the Combo box
	 */
	public void setItems(Object[] items) {
		setModel(new DefaultComboBoxModel(items));
	}
	
	/**
	 * Over-ride this to handle Metal L&F Border problem on editable
	 * Combo boxes
	 */
	public void setBorder(Border border) {
		super.setBorder(border);
		borderKludge();
	}
	
	/**
	 * This workaround sets the right border for Metal L&F if it is Editable
	 * Otherwise we get a double border
	 */
	private void borderKludge() {
		ComboBoxEditor ed = getEditor();
		if(ed != null) {  // Have to check editor is not null
			Component c = ed.getEditorComponent();
			if(c instanceof JComponent) {
				((JComponent) c).setBorder(null);
			}
		}
	}	
	
	/**
	 * Set the UI
	 */
	protected void setUI() {
		
	}
	
	/**
	 * Update the UI
	 */
	public void updateUI() {
		super.updateUI();
		setUI();
	}
}

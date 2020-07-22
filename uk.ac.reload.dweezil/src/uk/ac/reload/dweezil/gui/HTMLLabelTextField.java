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
import java.awt.Font;
import java.awt.event.*;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * A JLabel type component which, when clicked, becomes an editable text field.
 *
 * @author Phillip Beauvoir
 * @version $Id: HTMLLabelTextField.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class HTMLLabelTextField
extends JPanel
implements MouseListener, ActionListener, FocusListener, KeyListener
{
	
	/**
	 * html prefix
	 */
	public final static String HTML_STRING = "<html>";
    
    /**
     * The label
     */
    private JLabel _label;
	
	/**
	 * The Textfield
	 */
	private JTextField _textField;
	
	/**
	 * Flag for when user is editing
	 */
	private boolean _editing;

	/**
	 * The Text Value
	 */
	private String _text;
	
	/**
	 * The JPanel containing any Icon and Label/TextField
	 */
	private JPanel _textPanel;
	
	/**
	 * The invisible label for the icon
	 */
	private JLabel _iconLabel;
	
	/**
	 * Can be edited
	 */
	private boolean _editable = true;
	
	
	/**
	 * Default Constructor
	 */
	public HTMLLabelTextField() {
	    this("");
	}
	    
	/**
	 * Constructor for given text and icon
	 * @param text The text to set in the Field
	 * @param icon The icon to use
	 */
	public HTMLLabelTextField(String text, Icon icon) {
	    this(text);
	    setIcon(icon);
	}

	/**
	 * Constructor for given text
	 * @param text The text to set in the Field
	 */
	public HTMLLabelTextField(String text) {
		setText(text);  // Do this first so we can get font size
	    setup();
	}
	
	/**
	 * Set up the components, listeners and text
	 */
	protected void setup() {
		setLayout(new BorderLayout());
	    
	    add(getLabel());
	    
	    setUI();
	    
	    // Listen to mouse clicks on the JLabel
	    getLabel().addMouseListener(this);
	    
	    // Listen to Return pressed on the Textfield
	    getTextField().addActionListener(this);
	    
	    // Listen to Focus events on the Textfield
	    getTextField().addFocusListener(this);
	    
	    // Listen to Key events on the Textfield
	    getTextField().addKeyListener(this);
	}
	
	/**
	 * Set whether this component is editable or not
	 * @param editable True or false
	 */
	public void setEditable(boolean editable) {
	    _editable = editable;
	}
	
	/**
	 * @return whether this component is editable or not
	 */
	public boolean isEditable() {
	    return _editable;
	}
	
	/**
	 * @return The text minus the html prefix
	 */
	public String getText() {
	    String text = _text;
	    if(text.startsWith(HTML_STRING)) {
	        text = text.substring(HTML_STRING.length());
	    }
	    return text;
	}
	
    /**
     * Set the text
     * @param string The text
     */
    public void setText(String text) {
	    if(text == null) {
		    _text = "";
		}
		else {
		    _text = text;
		}
	    
	    if(!_text.startsWith(HTML_STRING)) {
	        _text = HTML_STRING + _text;
	    }
	    
	    if(getLabel() != null) {
	        getLabel().setText(_text);
	        revalidate();
	    }
    }

	/**
	 * Start the TextField Edit
	 */
	protected void startEdit() {
        if(isEditable()) {
            _editing = true;
            remove(getLabel());
            getTextField().setText(getText());
            add(getTextPanel());
            getTextField().requestFocus();
            revalidate();
        }
	}

    /**
	 * Finish the TextField Edit
	 */
	protected void finishEdit() {
	    _editing = false;
        remove(getTextPanel());
        _text = validateTextField();
        getLabel().setText(_text);
        add(getLabel());
        revalidate();
        repaint();
        // A focusLost event will now occur...
	}
	
	/**
	 * Cancel the TextField Edit
	 */
	protected void cancelEdit() {
	    _editing = false;
        remove(getTextPanel());
    	add(getLabel());
    	revalidate();
    	repaint();
    	// A focusLost event will now occur...
	}
	
	/**
	 * @return Whether we are editing the field
	 */
	public boolean isEditing() {
	    return _editing;
	}
	
	/**
	 * The Focus was lost from the Text Field - behave as if user pressed Return key
	 */
	protected void focusLost() {
        if(isEditing()) {
            finishEdit();
        }
	}
	
	/**
	 * @return The Label to use
	 */
	public JLabel getLabel() {
	    if(_label == null) {
	        _label = new JLabel();
	    }
	    return _label;
	}
	
	/**
	 * @return The Text Field to use
	 */
	public JTextField getTextField() {
	    if(_textField == null) {
	        _textField = new JTextField();
	    }
	    return _textField;
	}
	
	/**
	 * @return The Panel holding the Text Field and icon
	 */
	public JPanel getTextPanel() {
	    if(_textPanel == null) {
		    _textPanel = new JPanel();
		    _textPanel.setLayout(new BorderLayout());
		    _textPanel.setOpaque(false);
		    _textPanel.add(getIconLabel(), BorderLayout.WEST);
		    _textPanel.add(getTextField(), BorderLayout.CENTER);
	    }
	    return _textPanel;
	}

	/**
	 * @return The JLabel holding the icon for the text label
	 */
	public JLabel getIconLabel() {
	    if(_iconLabel == null) {
		    _iconLabel = new JLabel();
	    }
	    return _iconLabel;
	}

	/**
	 * Set the font to use - we're really over-riding the JPanel's setFont() method
	 * @param font The font to use
	 */
	public void setFont(Font font) {
	    if(getLabel() != null) {
	        getLabel().setFont(font);
	    }
	    if(getTextField() != null) {
		    getTextField().setFont(font);
	    }
	}
	
	/** 
	 * Get the font in use - we're really over-riding the JPanel's getFont() method
	 * @return the label font
	 */
	public Font getFont() {
	    if(getLabel() != null) {
	        return getLabel().getFont();
	    }
	    else {
	        return null;
	    }
	}
	
    /**
     * Set the label icon
     * @param icon The icon
     */
    public void setIcon(Icon icon) {
	    if(getLabel() != null) {
	        getLabel().setIcon(icon);
	    }
	    if(getIconLabel() != null) {
	        getIconLabel().setIcon(icon);
	    }
    }

	/**
	 * Validate the text in the Text Field prefixing HTML_STRING
	 * @return The validated text
	 */
	protected String validateTextField() {
	    String text = getTextField().getText();
	    // Revert to original text if empty
	    if("".equals(text)) {
	        text = _text;
	    }
	    else {
	        text = HTML_STRING + text;
	    }
	    return text;
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

	// ===========================================================================================
	// Listeners
	// ===========================================================================================
	
    public void mouseClicked(MouseEvent e) {
        int clicks = e.getClickCount();
        // Double-click on Label to edit
        if(clicks == 2 && (e.getButton() == MouseEvent.BUTTON1)) {
            startEdit();
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        // Return key
        finishEdit();
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        focusLost();
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        int key = e.getKeyChar();
        // Escape key
        if(key == KeyEvent.VK_ESCAPE) {
            cancelEdit();
        }
    }
	
}
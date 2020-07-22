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

package uk.ac.reload.dweezil.prefs;

import javax.swing.JPanel;

import uk.ac.reload.diva.prefs.UserPrefs;

/**
 * An abstract User Prefs Panel
 *
 * @author Phillip Beauvoir
 * @version $Id: PrefsPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public abstract class PrefsPanel
extends JPanel
{
	public static final int TEXTBOX_HEIGHT = 22;
	public static final int LABEL_HEIGHT = 15;
	public static final int COMBOBOX_HEIGHT = 22;
	public static final int CHECKBOX_HEIGHT = 20;
	public static final int BUTTON_HEIGHT = 22;
	
	protected PrefsPanel() {
		super();
	}
	
	/**
	 * Update UserPrefs according to the controls' settings
	 */
	public abstract void saveToUserPrefs(UserPrefs prefs);
	
	/**
	 * Set the form fields' settings from those found in UserPrefs
	 */
	public abstract void setFields(UserPrefs prefs);
	
	/**
	 * Cancel any changes
	 */
	public abstract void cancel();
}

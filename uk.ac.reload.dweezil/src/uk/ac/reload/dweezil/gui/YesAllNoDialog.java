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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uk.ac.reload.dweezil.Messages;

/**
 * Class to implement a dialog window with yes, yes to all and no options.
 *
 * @author Paul Sharples
 * @version $Id: YesAllNoDialog.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class YesAllNoDialog {
	
	public static final int CLOSE = -1;
	public static final int YES = 0;
	public static final int YES_TO_ALL = 1;
	public static final int NO = 2;
	public static final int CANCEL = 3;
	public static final String[] userOptions = {
	        Messages.getString("uk.ac.reload.dweezil.gui.YesAllNoDialog.0"), //$NON-NLS-1$
	        Messages.getString("uk.ac.reload.dweezil.gui.YesAllNoDialog.1"), //$NON-NLS-1$
	        Messages.getString("uk.ac.reload.dweezil.gui.YesAllNoDialog.2"), //$NON-NLS-1$
	        Messages.getString("uk.ac.reload.dweezil.gui.YesAllNoDialog.3") //$NON-NLS-1$
	}; 
	
	public YesAllNoDialog() {
		
	}
	
	/**
	 * A wrapper method for showOptionDialog
	 * @return - the users choice
	 */
	public int getUserResponse(JFrame parent, String msg, String title) {
		return JOptionPane.showOptionDialog(parent,
				msg, title,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null, userOptions, userOptions[0]);
	}
	
	
	
}
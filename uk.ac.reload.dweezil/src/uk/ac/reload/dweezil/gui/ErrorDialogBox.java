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
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Error Dialog Box
 *
 * @author Phillip Beauvoir
 * @version $Id: ErrorDialogBox.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class ErrorDialogBox {
    /**
     * Show warning message with parent specified
     */
    public static void showWarning(Component parent, String msg, String title, Exception ex) {
        if(ex != null) {
            msg += System.getProperty("line.separator") + ex.getMessage();
        }

        JTextPane label = new JTextPane();
        label.setEditable(false);
        label.setPreferredSize(new Dimension(350, 80));
        label.setOpaque(false);
        label.setBorder(null);
        label.setText(msg);
        JScrollPane sp = new JScrollPane(label);
        sp.setBorder(null);

        JOptionPane.showMessageDialog(parent, sp, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show warning message with default Parent
     */
    public static void showWarning(String msg, String title, Exception ex) {
        showWarning(null, msg, title, ex);
    }

}





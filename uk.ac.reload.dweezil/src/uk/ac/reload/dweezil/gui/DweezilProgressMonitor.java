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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import uk.ac.reload.diva.util.IProgressMonitor;

/**
 * My Progress Monitor based on Sun's - this one doesn't have all the waiting for
 * time millis stuff and you can reset it to start over from the beginning.
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilProgressMonitor.java,v 1.4 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilProgressMonitor
implements IProgressMonitor
{
	private JDialog         dialog;
	private JOptionPane     pane;
	private JProgressBar    myBar;
	private JLabel          messageLabel;
	private JLabel          noteLabel;
	private String          message;
	private String          note;
	private Object[]        cancelOption = null;
	private int             max;
	private int             progress;
	
	/**
	 * Constructor - specify an initial dialog title, message and note
	 */
	public DweezilProgressMonitor(Component parentComponent, String title, String message, String note, boolean indeterminate, Icon icon) {
		if(title == null) title = " ";
		if(message == null) message = " ";
		if(note == null) note = " ";
		
		cancelOption = new Object[1];
		cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
		myBar = new JProgressBar();
		myBar.setIndeterminate(indeterminate);
		myBar.setPreferredSize(new Dimension(300, myBar.getPreferredSize().height));
		messageLabel = new JLabel(message);
		noteLabel = new JLabel(note);
		
		pane = new ProgressOptionPane(new Object[] {messageLabel, noteLabel, myBar}, icon);
		dialog = pane.createDialog(parentComponent, title);
		dialog.setVisible(true);
	}
	
	/**
	 * Reset the PMonitor to start from zero with same message and note
	 */
	public void init(int max) {
		this.max = max;
		myBar.setMaximum(max);
		setProgress(0, false);
	}
	
	/**
	 * Reset the PMonitor to start from zero with new message and note
	 */
	public void init(String message, String note, int max) {
		init(max);
		setMessage(message);
		setNote(note);
	}
	
	/**
	 * Set the progress
	 */
	public void setProgress(int progress, boolean closeOnMax) {
		if(myBar != null) {
			this.progress = progress;
			myBar.setValue(progress);
			if(closeOnMax && (progress >= max)) close();
		}
	}
	
	/**
	 * Increment the progress by by inc amount
	 * If closeOnmax is true the PMonitor will close when max is reached
	 */
	public void incProgress(int inc, boolean closeOnMax) {
		setProgress(progress + inc, closeOnMax);
	}
	
	/**
	 * Set the indeterminate value
	 * @param indeterminate
	 */
	public void setIndeterminate(boolean indeterminate) {
		myBar.setIndeterminate(indeterminate);
	}
	
	public void close() {
		if(dialog != null) {
			dialog.setVisible(false);
			dialog.dispose();
			dialog = null;
		}
		pane = null;
		myBar = null;
	}
	
	public void setMessage(String message) {
		this.message = message;
		messageLabel.setText(message == null ? " " : message);
	}
	
	public void setNote(String note) {
		this.note = note;
		noteLabel.setText(note == null ? " " : note);
	}
	
	public boolean isCanceled() {
		if(pane == null) return false;
		Object v = pane.getValue();
		return ((v != null) && (cancelOption.length == 1) && (v.equals(cancelOption[0])));
	}
	
	
	private class ProgressOptionPane extends JOptionPane {
		
		ProgressOptionPane(Object messageList, Icon icon) {
			super(messageList,
					JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION,
					icon,
					DweezilProgressMonitor.this.cancelOption,
					null);
		}
		
		
		public int getMaxCharactersPerLineCount() {
			return 80;
		}
		
		
		// Equivalent to JOptionPane.createDialog,
		// but create a modeless dialog.
		// This is necessary because the Solaris implementation doesn't
		// support Dialog.setModal yet.
		public JDialog createDialog(Component parentComponent, String title) {
			Frame frame = JOptionPane.getFrameForComponent(parentComponent);
			final JDialog dialog = new JDialog(frame, title, false);
			Container contentPane = dialog.getContentPane();
			
			contentPane.setLayout(new BorderLayout());
			contentPane.add(this, BorderLayout.CENTER);
			dialog.pack();
			dialog.setLocationRelativeTo(parentComponent);
			dialog.addWindowListener(new WindowAdapter() {
				boolean gotFocus = false;
				
				public void windowClosing(WindowEvent we) {
					setValue(null);
				}
				
				public void windowActivated(WindowEvent we) {
					// Once window gets focus, set initial focus
					if (!gotFocus) {
						selectInitialValue();
						gotFocus = true;
					}
				}
			});
			
			addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					if(dialog.isVisible() &&
							event.getSource() == ProgressOptionPane.this &&
							(event.getPropertyName().equals(VALUE_PROPERTY) ||
									event.getPropertyName().equals(INPUT_VALUE_PROPERTY))){
						dialog.setVisible(false);
						dialog.dispose();
					}
				}
			});
			return dialog;
		}
	}
}

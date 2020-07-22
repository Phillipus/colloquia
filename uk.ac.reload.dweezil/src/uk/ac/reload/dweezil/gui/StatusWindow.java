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
import java.io.*;
import java.util.Date;

import javax.swing.*;

import uk.ac.reload.diva.util.GeneralUtils;
import uk.ac.reload.dweezil.Messages;
import uk.ac.reload.dweezil.menu.MenuAction;
import uk.ac.reload.dweezil.util.DweezilUIManager;
import uk.ac.reload.dweezil.util.UIUtils;

/**
 * This is a JFrame Window that can be shown or hidden from a menu bar.
 * The idea is that all status messages, debug output and error messages are diverted
 * here so that the user can see them in a nice way and save them to file if need be.
 * Setting redirect to true will redirect all System.out and
 * System.err messages to this window.
 *
 * @author Phillip Beauvoir
 * @version $Id: StatusWindow.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class StatusWindow
extends JFrame
{
	String CR = System.getProperty("line.separator"); //$NON-NLS-1$
	
	/**
	 * A kludge for Carriage returns on the Macintosh
	 */
	private boolean _macKludge;
	
	/**
	 * The Text area to display to
	 */
	private JTextArea _editor;
	
	/**
	 * The Application name
	 */
	private String _appName;
	
	/**
	 * The Application version
	 */
	private String _appVersion;
	
	/**
	 * The Application build date
	 */
	private String _buildDate;
	
	
	/**
	 * Constructor
	 * 
	 * @param appName The Application Name
	 * @param appVersion The Application Version
	 * @param buildDate The Application build date
	 * @param icon The Icon to display on the Status Window
	 * @param redirect Whether to redirect std.out and std.err to the Status Window
	 * @param logFolder The folder to write the error log file to
	 */
	public StatusWindow(String appName, String appVersion, String buildDate, ImageIcon icon, boolean redirect, File logFolder) {
		// Set Title bar
		super(appName + " - " + Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.0")); //$NON-NLS-1$ //$NON-NLS-2$
		
		_appName = appName;
		_appVersion = appVersion;
		_buildDate = buildDate;
		
		// Set application Icon
		if(icon != null) setIconImage(icon.getImage());
		
		// BorderLayout
		getContentPane().setLayout(new BorderLayout());
		
		// Set up JTextArea, read-only, text cursor
		_editor = new JTextArea();
		_editor.setEditable(false);
		_editor.setCursor(DweezilUIManager.TEXT_CURSOR);
		getContentPane().add(new JScrollPane(_editor), BorderLayout.CENTER);
		
		// Add a Menu bar
		JMenuBar menuBar = constructMenuBar();
		setJMenuBar(menuBar);
		
		// Centre the Window
		UIUtils.centreWindow(this, 450, 400);
		
		// Only hide the Window if the user closes it
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		// Do we have a Mac?
		_macKludge = (GeneralUtils.getOS() == GeneralUtils.MACINTOSH);
		
		// Re-direct system output to the Status window
		if(redirect) {
			try {
				PrintStream output = new PrintStream(new DumpStream(), true);
				PrintStream err = new PrintStream(new ErrorStream(logFolder), true);
				System.setOut(output);
				System.setErr(err);
			}
			catch (Exception e) { }
		}
		
		// Now print some useful stuff
		System.out.println(appName + " " + Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.1") + ":\t" + appVersion + " (" + buildDate + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.2") + ":\t\t" + System.getProperty("java.version")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.3") + ":\t\t" + GeneralUtils.getShortDate(GeneralUtils.getNow())); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.4") + ":\t\t" + System.getProperty("user.home")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.5") + ":\t\t" + System.getProperty("user.name")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.6") + ":\t\t" + System.getProperty("user.dir")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.out.println();
	}
	
	/**
	 * An OutputStream that redirects all System output to the Status Window
	 */
	private class DumpStream extends OutputStream {
		String s;
		
		public DumpStream() {
		}
		
		public void write(int b) {
			s = String.valueOf((char)(b & 255));
			// Kludge time!
			if(_macKludge && (s.equals(CR) || s.equals("\r"))) { //$NON-NLS-1$
				_editor.append("\r\n"); //$NON-NLS-1$
			}
			else _editor.append(s);
			_editor.setCaretPosition(_editor.getText().length());
		}
	}
	
	/**
	 * An OutputStream that redirects all Error output to the Error log file
	 */
	private class ErrorStream extends DumpStream {
		File file;
		FileWriter writer;
		boolean dateWritten;
		boolean canWrite = true;
		
		public ErrorStream(File logFolder) {
			// Open the error log file
			openErrorFile(logFolder);
		}
		
		/**
		 * Write a byte to the log file.
		 * @param b The byte
		 */
		public void write(int b) {
			if(canWrite) {
				try {
					if(!dateWritten) {
						dateWritten = true;
						writeDate();
					}
					writer.write(b);
					writer.flush();
				}
				catch(IOException ex) {
					canWrite = false;
					//System.err.println("Could not write to error log: " + ex);
				}
			}
			
			super.write(b);
		}
		
		/**
		 * Open the Error Log File.
		 */
		private void openErrorFile(File logFolder) {
			String logName = _appName + ".log"; //$NON-NLS-1$
			file = new File(logFolder, logName);
			try {
				// If file size > 128k, rename it and create a new one
				if(file.length() > (1024*128)) {
					Date date = new Date();
					File tmp = new File(logFolder + _appName + date.getTime() + ".log"); //$NON-NLS-1$
					file.renameTo(tmp);
					file = new File(logFolder + logName);
				}
				writer = new FileWriter(file.getPath(), true);
			}
			catch(IOException ex) {
				canWrite = false;
				//System.err.println("Could not open error log: " + ex);
			}
		}
		
		/**
		 * Write the Date and other bits to the log file.
		 * @throws IOException
		 */
		private void writeDate() throws IOException {
			writer.write("-----------------------------------------------------------------------------" + CR); //$NON-NLS-1$
			writer.write(_appName + " " + Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.1") + ":\t" + _appVersion + " (" + _buildDate + ")" + CR); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			writer.write(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.2") + ":\t\t" + System.getProperties().getProperty("java.version") + CR); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			writer.write(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.3") + ":\t\t" + GeneralUtils.getShortDate(GeneralUtils.getNow()) + CR); //$NON-NLS-1$ //$NON-NLS-2$
			writer.write("-----------------------------------------------------------------------------" + CR); //$NON-NLS-1$
			writer.write(CR);
			writer.flush();
		}
	}
	
	/**
	 * Show the Status Window
	 */
	public void showWindow() {
		setVisible(true);
	}
	
	/**
	 * Print a Debug Trace message to System.out, and hence the StatusWindow.
	 * @param message The message to output
	 */
	public static void printTrace(String message) {
		//boolean trace = UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.STATUS_MESSAGES);
		boolean trace = true;
		if(trace) System.out.println(message);
	}
	
	/**
	 * Construct the Status Window Menu Bar.
	 * @return The Menu bar
	 */
	private JMenuBar constructMenuBar() {
		JMenuBar mb = new JMenuBar();
		JMenuItem menuItem;
		
		// File Menu
		String menuText = Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.7"); //$NON-NLS-1$
		JMenu fileMenu = mb.add(new JMenu(MenuAction.getRemoveMnemonicText(menuText)));
		fileMenu.setMnemonic(MenuAction.getMnemonic(menuText));
		
		// Clear
		menuText = Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.8"); //$NON-NLS-1$
		menuItem = fileMenu.add(new JMenuItem(MenuAction.getRemoveMnemonicText(menuText))); 
		menuItem.addActionListener(new Action_Clear());
		menuItem.setMnemonic(MenuAction.getMnemonic(menuText));
		
		// Save text to file
		menuText = Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.9"); //$NON-NLS-1$
		menuItem = fileMenu.add(new JMenuItem(MenuAction.getRemoveMnemonicText(menuText))); 
		menuItem.addActionListener(new Action_Save());
		menuItem.setMnemonic(MenuAction.getMnemonic(menuText));
		
		// Garbage Collect
		menuText = Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.10"); //$NON-NLS-1$
		menuItem = fileMenu.add(new JMenuItem(MenuAction.getRemoveMnemonicText(menuText))); 
		menuItem.addActionListener(new Action_GC());
		menuItem.setMnemonic(MenuAction.getMnemonic(menuText));
		
		return mb;
	}
	
	/**
	 * Clear The Status Window
	 */
	private class Action_Clear extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			_editor.setText(""); //$NON-NLS-1$
		}
	}
	
	private class Action_GC extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Runtime rt = Runtime.getRuntime();
			rt.gc();
			float freeMem = (float)rt.freeMemory() /  1024 / 1024;
			System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.11") + ":\t\t" + freeMem + " Mb"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			float totalMem = (float)rt.totalMemory() /  1024 / 1024;
			System.out.println(Messages.getString("uk.ac.reload.dweezil.gui.StatusWindow.12") + ":\t\t" + totalMem + " Mb"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			System.out.println();
		}
	}
	
	/**
	 * Save the contents of the Status Window
	 */
	private class Action_Save extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			// Ask for a file name
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showSaveDialog(StatusWindow.this);
			// User cancelled
			if(returnVal != JFileChooser.APPROVE_OPTION) {
			    return;
			}
			
			try {
				File file = chooser.getSelectedFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(_editor.getText());
				out.flush();
				out.close();
			}
			catch(IOException ex) {
				System.out.println("Status log save error " + ex); //$NON-NLS-1$
			}
		}
	}
}
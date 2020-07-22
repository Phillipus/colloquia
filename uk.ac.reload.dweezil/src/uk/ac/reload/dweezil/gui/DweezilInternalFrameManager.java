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

import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;



/**
 * The Internal Frame Manager.  This manages the workings of the DweezilInternalFrame
 * objects so that we can hide or show them close them down, inform listeners etc.<br>
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilInternalFrameManager.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DweezilInternalFrameManager {
	/**
	 *  Current X Pos for Window Pos
	 */
    private static int _xpos = -25;
	
	/**
	 *  Current Y Pos for Window Pos
	 */
    private static int _ypos = -25;
	
	/**
	 *  The stagger offset
	 */
    private static final int WINDOWGAP = 25;
	
	/**
	 * The relative size factor
	 */
    private static final double SIZE_FACTOR = 0.9;
	
	/**
	 * The desktop to manage
	 */
	private JDesktopPane _desktop;
	
	
	/**
	 * Constructor
	 * @param desktop The desktop to manage
	 */
	public DweezilInternalFrameManager(JDesktopPane desktop) {
		_desktop = desktop;
	}
	
	/**
	 * Add a new DweezilInternalFrame to the desktop with autoSize true.
	 * If false, it's up to the caller to set the frame bounds
	 * @param frame The new Frame
	 * @param autoSize Whether to give the frame an auto size and position
	 */
	public void addInternalFrame(DweezilInternalFrame frame, boolean autoSize) {
		if(autoSize) {
			// Make it a size relative to the desktop size
			int width = (int)(_desktop.getWidth() * SIZE_FACTOR);
			if(width < 100) width = 100;
			int height = (int)(_desktop.getHeight() * SIZE_FACTOR);
			if(height < 100) height = 100;
			
			// Set the position
			_xpos += WINDOWGAP;
			_ypos += WINDOWGAP;
			
			if(_xpos + width > _desktop.getWidth()  || _ypos + height > _desktop.getHeight()) {
				_xpos = 0;
				_ypos = 0;
			}
			
			frame.setBounds(_xpos, _ypos, width, height);
		}
		
		// Add the window to the Desktop - it's up to the caller to show it!
		_desktop.add(frame);
	}
	
	/**
	 * Remove an existing DweezilInternalFrame and select the Previous one
	 * @param frame The Frame to remove
	 */
	public void removeInternalFrame(DweezilInternalFrame frame) {
		// Normally the frame is removed from the desktop automatically when 
		// its dispose() method is invoked.  Sometimes this doesn't happen so we'll play safe.
		_desktop.remove(frame);
		
		// Find the Last frame so we can select it
		JInternalFrame[] frames = _desktop.getAllFrames();
		if(frames.length > 0) {
			try {
				frames[frames.length - 1].setSelected(true);
			}
			catch(PropertyVetoException ex) {
				ex.printStackTrace();
			}
		}
		
		// Remove from Tracking
		removeInternalFrameFromWindowMap(frame);
	}
	
	/**
	 * Cascade all the windows
	 */
	public void cascade() {
		// Make it a size relative to the desktop size
		int width = (int)(_desktop.getWidth() * SIZE_FACTOR);
		if(width < 100) width = 100;
		int height = (int)(_desktop.getHeight() * SIZE_FACTOR);
		if(height < 100) height = 100;
		
		_xpos = -25;
		_ypos = -25;
		
		JInternalFrame[] frames = _desktop.getAllFrames();
		for(int i = 0; i < frames.length; i++) {
			JInternalFrame f = frames[i];
			
			_xpos += WINDOWGAP;
			_ypos += WINDOWGAP;
			
			if(_xpos + width > _desktop.getWidth()  || _ypos + height > _desktop.getHeight()) {
				_xpos = 0;
				_ypos = 0;
			}
			
			try {
				f.setIcon(false);
				f.setMaximum(false);
				f.setBounds(_xpos, _ypos, width, height);
				f.setSelected(true);
			}
			catch (java.beans.PropertyVetoException e) {}
		}
	}
	
	/**
	 * Tile all the windows vertically
	 */
	public void tileVertical() {
		JInternalFrame[] frames = _desktop.getAllFrames();
		if(frames.length == 0) return;
		
		int height = _desktop.getHeight();
		int width = _desktop.getWidth() / frames.length;
		
		_xpos = 0;
		_ypos = 0;
		
		for (int i = 0; i < frames.length; i++) {
			JInternalFrame f = frames[i];
			
			try {
				f.setIcon(false);
				f.setMaximum(false);
				f.setBounds(_xpos, _ypos, width, height);
				f.setSelected(true);
			}
			catch (java.beans.PropertyVetoException e) {}
			
			_xpos += width;
		}
	}
	
	/**
	 * Tile all the windows horizontally
	 */
	public void tileHorizontal() {
		JInternalFrame[] frames = _desktop.getAllFrames();
		if(frames.length == 0) return;
		
		int height = _desktop.getHeight() / frames.length;
		int width = _desktop.getWidth();
		
		_xpos = 0;
		_ypos = 0;
		
		for (int i = 0; i < frames.length; i++) {
			JInternalFrame f = frames[i];
			
			try {
				f.setIcon(false);
				f.setMaximum(false);
				f.setBounds(_xpos, _ypos, width, height);
				f.setSelected(true);
			}
			catch (java.beans.PropertyVetoException e) {}
			
			_ypos += height;
		}
	}
	
	
//	==============================================================================
//	==================== TRACKING OF OPENED FRAMES ==========================
//	==============================================================================
	
	
	private static Hashtable FileToWindowMAP = new Hashtable();
	
	/**
	 * See if we have the Window open already
	 */
	public static DweezilInternalFrame getInternalFrame(File file) {
	    if(file == null) return null;
	    // Case sensitive!
	    String key = file.getPath().toLowerCase();
		return (DweezilInternalFrame)FileToWindowMAP.get(key);
	}
	
	/**
	 * Add a Frame to the Window map
	 */
	public static void addInternalFrameToWindowMap(File file, DweezilInternalFrame frame) {
	    if(file != null && frame != null) {
	        // Remove any old one
	        removeInternalFrameFromWindowMap(frame);
		    // Case sensitive!
		    String key = file.getPath().toLowerCase();
	        FileToWindowMAP.put(key, frame);
	    }
	}
	
	/**
	 * This will be called when the window is closed from removeInternalFrame
	 * @param frame
	 */
	private static void removeInternalFrameFromWindowMap(DweezilInternalFrame frame) {
		if(frame != null && FileToWindowMAP.contains(frame)) {
			Enumeration e = FileToWindowMAP.keys();
			while(e.hasMoreElements()) {
				String filePath = (String) e.nextElement();
				DweezilInternalFrame f = (DweezilInternalFrame) FileToWindowMAP.get(filePath);
				if(frame == f) FileToWindowMAP.remove(filePath);
			}
		}
	}
}

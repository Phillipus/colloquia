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

package uk.ac.reload.dweezil.util;

import java.awt.*;

import javax.swing.JInternalFrame;

/**
 * Some useful UI Utilities
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: UIUtils.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public final class UIUtils  {
	
	/**
	 * Centres a window on the screen given the proportional size of window.
	 * It does not show the window.
	 * @param window The Window to centre
	 * @param width The width of the Window as a percentage of the Screen Width
	 * @param height The height of the Window as a percentage of the Screen Height
	 */
	public static void centreWindowProportional(Window window, double width, double height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width / 2;
		int screenHeight = screenSize.height / 2;
		int newWidth = (int)(screenSize.width * width);
		int newHeight = (int)(screenSize.height * height);
		window.setSize(newWidth, newHeight);
		window.setLocation(screenWidth - newWidth / 2, screenHeight - newHeight / 2);
	}
	
	/**
	 * Centres a window on the screen given the size of window.
	 * It does not show the window.
	 * @param window The Window to centre
	 * @param width The width of the Window
	 * @param height The height of the Window
	 */
	public static void centreWindow(Window window, int width, int height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width / 2;
		int screenHeight = screenSize.height / 2;
		window.setSize(width, height);
		window.setLocation(screenWidth - width / 2, screenHeight - height / 2);
	}
	
	/**
	 * Get the Screen Width
	 * @return The width of the screen
	 */
	public static int getScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	
	/**
	 * Get the Screen Height
	 * @return The height of the screen
	 */
	public static int getScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	
	/**
	 * @return the first JInternalFrame ancestor of c, or null if component is not contained inside a JInternalFrame
	 * or will return it if it actually is a frame
	 */
	public static JInternalFrame getInternalFrameAncestor(Component c) {
		if(c == null) {
		    return null;
		}
		
		if(c instanceof JInternalFrame) {
		    return (JInternalFrame)c;
		}
		
		for(Container p = c.getParent(); p != null; p = p.getParent()) {
			if(p instanceof JInternalFrame) {
				return (JInternalFrame)p;
			}
		}
		return null;
	}
	
	/**
	 * @return the Frame ancestor of c, or null if component is not contained inside a Frame
	 * or will return it if it actually is a frame
	 */
	public static Frame getFrameAncestor(Component c) {
		if(c == null) {
		    return null;
		}
		
		if(c instanceof Frame) {
		    return (Frame)c;
		}
		
		for(Container p = c.getParent(); p != null; p = p.getParent()) {
			if(p instanceof Frame) {
				return (Frame)p;
			}
		}
		return null;
	}

	/**
	 * @return whether Component c belongs directly to the Main Frame
	 */
	public static boolean isComponentInMainFrame(Component c) {
		// If it's in a JInternalFrame then no
		return !isComponentInInternalFrame(c);
	}
	
	/**
	 * @return whether Component c belongs in a JInternalFrame
	 */
	public static boolean isComponentInInternalFrame(Component c) {
		// If it's in a JInternalFrame then no
		JInternalFrame frame = getInternalFrameAncestor(c);
		return frame != null;
	}
}


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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.ac.reload.diva.util.GeneralUtils;


/**
 * The Dweezil UI Manager.
 * This takes care of general application UI duties such as setting the L&F
 * or getting Icons and Colours etc.
 *
 * @author Phillip Beauvoir
 * @version $Id: DweezilUIManager.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public final class DweezilUIManager {
	
	
	/**
	 * Needed for Classloader
	 */
	private static final DweezilUIManager INSTANCE = new DweezilUIManager();
	
	// ========================================================================
	// ============================== CURSORS =================================
	// ========================================================================
	public static Cursor TEXT_CURSOR = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
	public static Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	public static Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
	
	
	// ========================================================================
	// =============================== FONTS ==================================
	// ========================================================================
	public static Font plainFont11 = new Font("SansSerif", Font.PLAIN, 11);
	public static Font boldFont11 = new Font("SansSerif", Font.BOLD, 11);
	public static Font italicFont11 = new Font("SansSerif", Font.ITALIC, 11);
	public static Font bold_italicFont11 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 11);
	public static Font plainFont12 = new Font("SansSerif", Font.PLAIN, 12);
	public static Font boldFont12 = new Font("SansSerif", Font.BOLD, 12);
	public static Font italicFont12 = new Font("SansSerif", Font.ITALIC, 12);
	public static Font bold_italicFont12 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 12);
	public static Font plainFont13 = new Font("SansSerif", Font.PLAIN, 13);
	public static Font boldFont13 = new Font("SansSerif", Font.BOLD, 13);
	public static Font italicFont13 = new Font("SansSerif", Font.ITALIC, 13);
	public static Font bold_italicFont13 = new Font("SansSerif", Font.ITALIC + Font.BOLD, 13);
	public static Font plainFont14 = new Font("SansSerif", Font.PLAIN, 14);
	public static Font boldFont14 = new Font("SansSerif", Font.BOLD, 14);
	
	
	/**
	 * Singleton
	 */
	private DweezilUIManager() {}
	
	
	// =========================================================================
	// LOOK AND FEEL
	// =========================================================================
	
	/**
	 * Do some UI Defaults stuff for the Mac
	 */
	public static void setMacUIDefaults(String applicationName, OSXApplication app) {
		// APPLE MAC STUFF
		// See http://developer.apple.com/techpubs/macosx/ReleaseNotes/java141/system_properties/index.html
		// If you are using the Aqua look and feel, this puts Swing menus in the Mac OS X menu bar.
		// Note that JMenuBars in JDialogs are not moved to the Mac OS X menu bar.
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		// Sets the application name that is displayed in the application menu and in the Dock.
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);
		// Most native Mac OS X windows have a resize control in the bottom right corner.
		// By default, Java application windows that use the Aqua look and feel have the functionality of this control,
		// but there is no user interface cue that it is there. This flag causes the resize control to be displayed.
		System.setProperty("apple.awt.showGrowBox", "true");
		INSTANCE.macOSXRegistration(app);
	}
	
	/**
	 * Generic registration with the Mac OS X application menu.  Checks the platform, then attempts
	 * to register with the Apple EAWT.
	 * This method calls OSXAdapter.registerMacOSXApplication() and OSXAdapter.enablePrefs().
	 * See OSXAdapter.java for the signatures of these methods.
	 * 
	 * @param app
	 */
	public void macOSXRegistration(OSXApplication app) {
		if (GeneralUtils.getOS() == GeneralUtils.MACINTOSH) {
			try {
				Class osxAdapter = Class.forName("uk.ac.reload.dweezil.util.OSXAdapter");
				
				Class[] defArgs = {OSXApplication.class};
				Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);
				Object[] args = {app};
				registerMethod.invoke(osxAdapter, args);
				
				// This is slightly gross.  to reflectively access methods with boolean args,
				// use "boolean.class", then pass a Boolean object in as the arg, which apparently
				// gets converted for you by the reflection system.
				defArgs[0] = boolean.class;
				Method prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs", defArgs);
				Object args2[] = {Boolean.TRUE};
				prefsEnableMethod.invoke(osxAdapter, args2);
			}
			catch(NoClassDefFoundError e) {
				// This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
				// because OSXAdapter extends ApplicationAdapter in its def
				System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			}
			catch(ClassNotFoundException e) {
				// This shouldn't be reached; if there's a problem with the OSXAdapter we should get the
				// above NoClassDefFoundError first.
				System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			}
			catch(Exception e) {
				System.err.println("Exception while loading the OSXAdapter:");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Set The Look and Feel to a class name, these will have to be installed ones, or inbuilt ones.
	 * @param className The class name for the L&F
	 * @param components The components (typically JFrames) that will need to be updated
	 */
	public static void setLookAndFeel(String className, Component[] components) {
		try {
			UIManager.setLookAndFeel(className);
			updateComponentTreeUI(components);
		}
		catch(Exception ex) {
			// Try again with system l&f
			try {
				String lf = UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(lf);
				updateComponentTreeUI(components);
			}
			catch(Exception ex1) {
				System.out.println("Error loading L&F: " + ex);
			}
		}
	}
	
	/**
	 * Update any components' UI
	 */
	private static void updateComponentTreeUI(Component[] components) {
		for(int i = 0; i < components.length; i++) {
			SwingUtilities.updateComponentTreeUI(components[i]);
		}
	}
	
	// =========================================================================
	// ICONS
	// =========================================================================
	
	/**
	 * A cache of Icons
	 */
	private static Hashtable icons = new Hashtable();
	
	/**
	 * Get an Icon
	 * @param iconPath The full path of the Icon
	 * @return The ImageIcon or null if not found
	 */
	public static ImageIcon getIcon(String iconPath) {
		// See if it's cached
		ImageIcon imageIcon = (ImageIcon) icons.get(iconPath);
		
		// Not in cache
		if(imageIcon == null) {
			// try as a resource
			URL url = INSTANCE.getClass().getClassLoader().getResource(iconPath);
			if(url != null) {
				imageIcon = new ImageIcon(url);
				// cache it
				icons.put(iconPath, imageIcon);
			}
		}
		
		return imageIcon;
	}
	
	/**
	 * Gets a resource image
	 * @param imagePath The full path of the Image
	 * @return The Image or null if not found
	 */
	public static Image getImage(String imagePath) {
		ImageIcon imageIcon = getIcon(imagePath);
		if(imageIcon != null) return imageIcon.getImage();
		else return null;
	}
}
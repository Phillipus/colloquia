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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import uk.ac.reload.diva.util.FilePath;
import uk.ac.reload.diva.util.GeneralUtils;
import uk.ac.reload.dweezil.Messages;

/**
 * Native File and URL Launcher.
 * If it's a local file will launch in native associated application which could be the Browser
 * If a URL will launch Browser
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: NativeLauncher.java,v 1.5 2007/07/31 15:42:46 phillipus Exp $
 */
public final class NativeLauncher  {
	
	private NativeLauncher() {}
	
	/**
	 * Launch a File in the OS
	 */
	public static void launchFile(File file) {
		FilePath filePath = new FilePath(file);
		launchURL(filePath.getURL());
	}
	
	/**
	 * Launch a URL in the OS.
	 * @param url The String url has to be of the format "file:///c:/my%20documents/index.htm"
	 * or "http://www.wherever.com"
	 */
	public static void launchURL(String url) {
		// Find the OS
		int OS = GeneralUtils.getOS();
		
		switch(OS) {
            case GeneralUtils.WINDOWS_XP:
            case GeneralUtils.WINDOWS_2000:
            case GeneralUtils.WINDOWS_2003:
            case GeneralUtils.WINDOWS_XX:
            case GeneralUtils.WINDOWS_NT:
				try {
					Process process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "start", "\"\"", '"' + url + '"' }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					process.waitFor();
					process.exitValue();
				}
				catch(Exception ex) {
				    System.out.println(Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.0")); //$NON-NLS-1$
				}
				break;
				
			case GeneralUtils.WINDOWS_98:
				try {
					// This call expects a file path like "c:\my documents\index.htm"
					//Process process = Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler " + url);
					
					Process process = Runtime.getRuntime().exec("start " + '"' + url + '"'); //$NON-NLS-1$
					process.waitFor();
					process.exitValue();
				}
				catch(Exception ex) {
				    System.out.println(Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.0")); //$NON-NLS-1$
				}
				break;
				
			case GeneralUtils.MACINTOSH:
				try {
					// Let MRJ do all the work for us.
					Class targetClass = Class.forName("com.apple.mrj.MRJFileUtils"); //$NON-NLS-1$
					Method openURL = targetClass.getDeclaredMethod("openURL", new Class[] {String.class}); //$NON-NLS-1$
					openURL.invoke(null, new Object[] {url});
				}
				catch(Exception ex) {
					System.out.println(Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.1")); //$NON-NLS-1$
				}
				break;
				
			case GeneralUtils.UNIX:
				try {
					Runtime.getRuntime().exec("mozilla " + url); //$NON-NLS-1$
					//process.waitFor();
					//process.exitValue();
				}
				catch(Exception ex) {
					System.out.println(Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.2")); //$NON-NLS-1$
				}
				break;
				
			default:
				JOptionPane.showMessageDialog(null,
						Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.3"), //$NON-NLS-1$
						Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.4"), //$NON-NLS-1$
						JOptionPane.INFORMATION_MESSAGE);
			
		}
	}
	
	/**
	 * Show a File in the File System.
	 * You could use this for opening a directory.
	 * @param path The Path
	 */
	public static void showFileInFileSystem(String path) {
		path = normalizePath(path);
		
		switch(GeneralUtils.getOS()) {
			// Use existing method
            case GeneralUtils.WINDOWS_XP:
            case GeneralUtils.WINDOWS_2000:
            case GeneralUtils.WINDOWS_NT:
            case GeneralUtils.WINDOWS_98:
            case GeneralUtils.WINDOWS_2003:
            case GeneralUtils.WINDOWS_XX:
            case GeneralUtils.UNIX:
				launchURL(path);
				break;
				
			// Except for Mac
			case GeneralUtils.MACINTOSH:
				try {
					Process process = Runtime.getRuntime().exec(new String[] {"open", path}); //$NON-NLS-1$
					process.waitFor();
					process.exitValue();
				}
				catch(InterruptedException ex) {
				}
				catch(IOException ex) {
				}
				break;
				
			default:
				JOptionPane.showMessageDialog(null,
						Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.3"), //$NON-NLS-1$
						Messages.getString("uk.ac.reload.dweezil.util.NativeLauncher.4"), //$NON-NLS-1$
						JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private static String normalizePath(String path) {
		return new File(path).getAbsolutePath();
	}
	
}


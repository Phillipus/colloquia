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

package uk.ac.reload.dweezil.dnd;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

import uk.ac.reload.diva.util.GeneralUtils;



/**
 * Some useful Drag and Drop Utilities<br>
 *
 * @author Phillip Beauvoir
 * @version $Id: DNDUtils.java,v 1.3 2007/07/15 20:27:31 phillipus Exp $
 */
public final class DNDUtils {
	
    static String javaVersion = System.getProperty("java.version");
    static int OS = GeneralUtils.getOS();
    
	/**
	 * <p>Due to Mac specific issues, this method has been added to ensure
	 * that the correct drop target event is recorded.  On MacOS X
	 * "event.getDropAction()" does not return 1 for move and 2 for copy as
	 * it should do (like it does on PC).  This method checks to see
	 * if the OS is Mac and then makes sure the correct int value is returned</p>
	 *
	 * @param event a DropTargetDropEvent
	 * @return int representing the type of action - move or copy
	 */
	public static int getCorrectDropContext(DropTargetDropEvent event){
		if(OS == GeneralUtils.MACINTOSH && javaVersion.startsWith("1.4.1")){
			int result = event.getDropAction();
			switch(result) {
				case 0:
					return 1;
				case 1:
					return 2;
				default:
					return 1;
			}
		}
		return event.getDropAction();
	}
	
	/**
	 * <p>Due to Mac specific issues, this method has been added to ensure
	 * that the correct drop target event is recorded.  On MacOS X
	 * "event.getDropAction()" does not return 1 for move and 2 for copy as
	 * it should do (like it does on PC).  This method checks to see
	 * if the OS is Mac and then makes sure the correct int value is returned</p>
	 *
	 * @param event a DropTargetDropEvent
	 * @return int representing the type of action - move or copy
	 */
	public static int getCorrectDropContext(DropTargetDragEvent event){
		if(OS == GeneralUtils.MACINTOSH && javaVersion.startsWith("1.4.1")){
			int result = event.getDropAction();
			switch(result) {
				case 0:
					return 1;
				case 1:
					return 2;
				default:
					return 1;
			}
		}
		return event.getDropAction();
	}

}
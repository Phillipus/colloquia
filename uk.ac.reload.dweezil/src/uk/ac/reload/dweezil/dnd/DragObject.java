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


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

import javax.swing.JComponent;



/**
 * <p>A Drag Object that is a wrapper for an object dragged from JComponent to JComponent
 * Ideally, you would sub-class this to make your own type. <p>
 * <h4>Amendment History: Drag n Drop errors on MacOS X.</h4>
 * <p>It is safer if a drag object inside a DataFlavor reflects the actual object
 * type inside the tree node.  As each DragObject is created,
 * the DataFlavor is updated to represent this particular object type.  A default
 * data flavor of "DataFlavor.javaJVMLocalObjectMimeType" seems to default to an
 * instance of java.io.InputStream as opposed to an instance of the user object contained
 * with a tree node.  <BR><BR>  If an instance of the DataFlavor does not reflect the
 * corresponding Transferable instance, then a ClassCastException can occur.
 * MacOS X seems to be more fussy about this format then Windows.  For more info
 * see <BR> <BR> <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/dnd.html">http://java.sun.com/docs/books/tutorial/uiswing/misc/dnd.html</a>,
 * section - Specifying the Data Format <BR> <BR>
 * and also <BR> <BR>
 * <a href="http://forum.java.sun.com/thread.jsp?thread=184916&forum=5&message=654598">http://forum.java.sun.com/thread.jsp?thread=184916&forum=5&message=654598</a>
 * </p>
 *
 *
 * @author Phillip Beauvoir
 * @author Paul Sharples
 * @version $Id: DragObject.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class DragObject
implements Transferable
{
	/**
	 * The Object that we are transferring
	 */
    private Object _object;
	
	/**
	 * The JComponent that is the owner of the dragged object
	 */
    private JComponent _owner;
	
	/**
	 * The Data Flavor that we support - static because this is a kludge
	 */
	public static DataFlavor flavor;
	
	/**
	 * Constructor
	 * @param object The Object to drag - this has to implement Serializable
	 * @param owner The drag object's owner
	 */
	public DragObject(Serializable object, JComponent owner) {
		_object = object;
		_owner = owner;
		// we need to set up the correct object type inside the flavor container
		// as each new instance is created.
		flavor = getFlavor();
	}
	
	/**
	 * Dynamically figure out what type of object should be contained within the Flavor.
	 * @return a DataFlavor or null
	 */
	public DataFlavor getFlavor(){
		try {
			// An array of objects
			if(_object instanceof Object[]) {
				Object[] n = (Object[])_object;
				return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + n[0].getClass().getName());
			}
			else {
				return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + _object.getClass().getName());
			}
		}
		catch(Exception ex) {
			System.out.println("DataFlavor could not be ascertained from class definition");
			return null;
		}
	}
	
	/**
	 * Return the user object contained within the Flavor container
	 * @return a user object (should correspond to the class type within a tree node).
	 */
	public Object getUserObject() {
		return _object;
	}
	
	/**
	 * @return the owner of the dragged object
	 */
	public JComponent getOwner() {
		return _owner;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor fl) {
		if(!isDataFlavorSupported(fl)) {
		    return null;
		}
		return _object;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor fl) {
		return fl.equals(flavor);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}
}

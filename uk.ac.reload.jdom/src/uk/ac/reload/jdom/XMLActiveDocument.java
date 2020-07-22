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

package uk.ac.reload.jdom;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;


/**
 * This XML Document with XMLDocumentListener support and added features
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLActiveDocument.java,v 1.3 2007/07/15 20:27:46 phillipus Exp $
 */
public class XMLActiveDocument
extends XMLDocument
{
    /**
     * List of XMLDocumentListeners
     */
    private Vector _listeners = new Vector();
    
    /**
     * Default Constructor
     */
    public XMLActiveDocument() {
        super();
    }
    
    /**
     * Constructor
     */
    public XMLActiveDocument(Document doc) {
        super(doc);
    }

    /**
     * This will save the XML IMS file with the existing File ref and notify any listeners
     * @throws IOException
     */
    public void saveDocument() throws IOException {
        super.saveDocument();
        fireDocumentSaved();
    }
    
    /**
     * Add an Element at an index Position and Notify listeners.
     * If index is less than 0 or index is greater than parentElement's child size,
     * newElement is added at position 0 
     * 
     * @param source The source of who added the Element
     * @param parentElement The Element parent Element
     * @param newElement The Element to add
     * @param index Index position of added Element
     * @param doSelect A hint to any registered listeners that they might want to select the Element
     * @return The Element added.
     */
    public synchronized Element addElementAtIndex(Object source, Element parentElement, Element newElement, int index, boolean doSelect) {
        List children = parentElement.getChildren();
        if(index < 0 || index > children.size()) {
            index = 0;
        }
        children.add(index, newElement);
        
        // Notify
        XMLDocumentListenerEvent event = new XMLDocumentListenerEvent(source, this, newElement, doSelect);
        setDirty(true);  // this first
        fireElementAdded(event);
        
        return newElement;
    }
    
    /**
     * Remove a child Element from its parent and notify any listeners
     * @param source Who is telling us this
     * @param element The Element to remove
     */
    public synchronized Element removeElement(Object source, Element element) {
        Element parent = element.getParent();
        if(parent != null) {
            parent.removeContent(element);
            XMLDocumentListenerEvent event = new XMLDocumentListenerEvent(source, this, element, false);
            setDirty(true); // this first
            fireElementRemoved(event);
        }
        return element;
    }
    
    /**
     * Move an Element up one place
     * 
     * @param source The source of who added the Element
     * @param element The Element to move up
     * @param doSelect A hint to any registered listeners that they might want to select the Element
     * @return The Element moved.
     */
    public Element moveElementUp(Object source, Element element, boolean doSelect) {
        Element parent = element.getParent();
        if(parent != null) {
            List children = parent.getChildren();
            if(children.size() > 1) {
                int index = children.indexOf(element);
                if(index > 0) {
                    removeElement(source, element);
                    addElementAtIndex(source, parent, element, index - 1, doSelect);
                }
            }
        }
        return element;
    }
    
    /**
     * Move an Element up above the previous same type element.  If there is no previous same type
     * then just move up one place.  Useful for moving elements in choice groups where the elements
     * can be in any order.
     * 
     * @param source The source of who added the Element
     * @param element The Element to move up
     * @param doSelect A hint to any registered listeners that they might want to select the Element
     * @return The Element moved.
     */
    public Element moveElementUpSameType(Object source, Element element, boolean doSelect) {
        if(element == null) {
            return null;
        }
        
        Element parent = element.getParent();
        if(parent != null) {
            Element prevSibling = getPreviousSiblingSameType(element);
            if(prevSibling != null) {
                int index = indexOfElement(prevSibling);
                if(index != -1) {
                    removeElement(source, element);
                    addElementAtIndex(source, parent, element, index, doSelect);
                }
            }
            else {
                moveElementUp(source, element, doSelect);
            }
        }
        
        return element;
    }

    /**
     * Move an Element down one place
     * 
     * @param source The source of who added the Element
     * @param element The Element to move up
     * @param doSelect A hint to any registered listeners that they might want to select the Element
     * @return The Element moved.
     */
    public Element moveElementDown(Object source, Element element, boolean doSelect) {
        Element parent = element.getParent();
        if(parent != null) {
            List children = parent.getChildren();
            if(children.size() > 1) {
                int index = children.indexOf(element);
                if(index < children.size() - 1) {
                    removeElement(source, element);
                    addElementAtIndex(source, parent, element, index + 1, doSelect);
                }
            }
        }
        return element;
    }
    
    /**
     * Move an Element down below the next same type element.  If there is no next same type
     * then just move down one place.  Useful for moving elements in choice groups where the elements
     * can be in any order.
     * 
     * @param source The source of who added the Element
     * @param element The Element to move down
     * @param doSelect A hint to any registered listeners that they might want to select the Element
     * @return The Element moved.
     */
    public Element moveElementDownSameType(Object source, Element element, boolean doSelect) {
        if(element == null) {
            return null;
        }
        
        Element parent = element.getParent();
        if(parent != null) {
            Element nextSibling = getNextSiblingSameType(element);
            if(nextSibling != null) {
                int index = indexOfElement(nextSibling);
                if(index != -1) {
                    removeElement(source, element);
                    addElementAtIndex(source, parent, element, index, doSelect);
                }
            }
            else {
                moveElementDown(source, element, doSelect);
            }
        }
        
        return element;
    }

    /**
     * Somebody has notified us that they have changed an Element in some way.
     * We will notify all listeners.
     * 
     * @param source Who is telling us this
     * @param element The Element that changed
     */
    public void changedElement(Object source, Element element) {
        XMLDocumentListenerEvent event = new XMLDocumentListenerEvent(source, this, element, false);
        setDirty(true); // this first
        fireElementChanged(event);
    }
    
    /**
     * @param element the Element to copy
     * @return whether we can copy an element - will need to over-ride this
     */
    public boolean canCopyElement(Element element) {
        return false;
    }
    
    /**
     * @param element the Element to delete
     * @return whether we can delete an element - will need to over-ride this
     */
    public boolean canDeleteElement(Element element) {
        return false;
    }

    /**
     * @param element the Element to cut
     * @return whether we can cut an element - will need to over-ride this
     */
    public boolean canCutElement(Element element) {
        return false;
    }

    /**
     * @param parentElement the Parent Element
     * @return whether we can paste the XMLDocumentClipboard element as a child of element - will need to over-ride this
     */
    public boolean canPasteFromClipboard(Element parentElement) {
        return false;
    }
    
    /**
     * @param element the Element to move
     * @return Whether we can move the Element up one place - will need to over-ride this
     */
    public boolean canMoveElementUp(Element element) {
        return false;
    }
    
    /**
     * @param element the Element to move
     * @return Whether we can move the Element down one place - will need to over-ride this
     */
    public boolean canMoveElementDown(Element element) {
        return false;
    }
    
    /**
     * Destroy this Document
     */
    public void destroy() {
        super.destroy();
        _listeners.clear();
        _listeners = null;
    }
    
    //============================== LISTENER EVENTS  ==========================
    
    /**
     * Add a XMLDocumentListener
     * @param listener The XMLDocumentListener
     */
    public synchronized void addXMLDocumentListener(XMLDocumentListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.addElement(listener);
        }
    }
    
    /**
     * Remove a XMLDocumentListener
     * @param listener The XMLDocumentListener
     */
    public synchronized void removeXMLDocumentListener(XMLDocumentListener listener) {
        _listeners.removeElement(listener);
    }
    
    /**
     * Tell our listeners that we have added a new Element
     */
    protected void fireElementAdded(XMLDocumentListenerEvent event) {
        for(int i = _listeners.size() - 1; i >= 0; i--) {
            XMLDocumentListener listener = (XMLDocumentListener)_listeners.elementAt(i);
            listener.elementAdded(event);
        }
    }
    
    /**
     * Tell our listeners that we have deleted an Element
     */
    protected void fireElementRemoved(XMLDocumentListenerEvent event) {
        for(int i = _listeners.size() - 1; i >= 0; i--) {
            XMLDocumentListener listener = (XMLDocumentListener)_listeners.elementAt(i);
            listener.elementRemoved(event);
        }
    }
    
    /**
     * Tell our listeners that we have changed an Element
     */
    protected void fireElementChanged(XMLDocumentListenerEvent event) {
        for(int i = _listeners.size() - 1; i >= 0; i--) {
            XMLDocumentListener listener = (XMLDocumentListener)_listeners.elementAt(i);
            listener.elementChanged(event);
        }
    }
    
    /**
     * Tell our listeners that we have saved
     */
    protected void fireDocumentSaved() {
        for(int i = _listeners.size() - 1; i >= 0; i--) {
            XMLDocumentListener listener = (XMLDocumentListener)_listeners.elementAt(i);
            listener.documentSaved(this);
        }
    }
}

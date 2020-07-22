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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;




/**
 * This encapsulates the JDOM XML Document file plus helper methods.
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLDocument.java,v 1.3 2007/07/15 20:27:46 phillipus Exp $
 */
public class XMLDocument {
    /**
     * The JDOM Document
     */
    private Document _doc;

    /**
     * A flag to set if this Document is dirty (edited)
     */
    private boolean _dirty;

    /**
     * The File for this Document - this may be null if not saved to disk
     */
    private File _file;


    /**
     * Default Constructor
     */
    public XMLDocument() { }
        
    /**
     * Constructor
     */
    public XMLDocument(Document doc) {
        _doc = doc;
    }
    
    /**
     * Set the Document
     * @param doc
     */
    public void setDocument(Document doc) {
        _doc = doc;
    }

    /**
     * @return The JDOM Document
     */
    public Document getDocument() {
        return _doc;
    }
    
    /**
     * Set File
     * @param file
     */
    public void setFile(File file) {
        _file = file;
    }

    /**
     * @return the File of this Document.  This might be null.
     */
    public File getFile() {
        return _file;
    }

    /**
     * Load the JDOM Document File
     * @param file The XML File to read in
     * @throws JDOMException
     * @throws IOException
     */
    public void loadDocument(File file) throws JDOMException, IOException {
        _file = file;
        _doc = XMLUtils.readXMLFile(file);
        _dirty = false;
    }

    /**
     * This will save the XML IMS file with the existing File ref.
     * @throws IOException 
     */
    public void saveDocument() throws IOException {
    	if(_doc != null && _file != null) {
    		XMLUtils.write2XMLFile(_doc, _file);
    		_dirty = false;
    	}
    }

    /**
     * @return whether this Document has been changed in some way
     */
    public boolean isDirty() {
        return _dirty;
    }

    /**
     * Set whether this Document is dirty or not
     * @param isDirty true or false
     */
    public void setDirty(boolean isDirty) {
        _dirty = isDirty;
    }

    /**
     * This will save the XML IMS file with a new File ref
     * @param file The File to save as
     * @throws IOException 
     */
    public void saveAsDocument(File file) throws IOException {
        _file = file;
        saveDocument();
    }

    /**
     * @return The Root Element of the JDOM Document or null if none
     */
    public Element getRootElement() {
        if(_doc != null && _doc.hasRootElement()) {
            return _doc.getRootElement();
        }
        return null;
    }

    /**
     * @return the Root Namespace of the Document if there is one
     */
    public Namespace getRootNamespace() {
        Element root = getRootElement();
        if(root != null) {
            return root.getNamespace();
        }
        else return null;
    }

    /**
     * @return true if element belongs to the Namespace of this Document
     */
    public boolean isDocumentNamespace(Element element) {
        Namespace ns = element.getNamespace();
        if(ns == null) {
            return false;
        }
        return ns.equals(getRootNamespace());
    }

    /**
     * @return the index position of an Element in relation to its parent
     */
    public int indexOfElement(Element element) {
        int index = 0;
        Element parent = element.getParent();
        if(parent != null) {
            List children = parent.getChildren();
            index = children.indexOf(element);
            if(index == -1) {
                index = 0;
            }
        }
        return index;
    }
    
    /**
     * @param element
     * @return The previous sibling Element of the same name and Namespace as element or null
     */
    public Element getPreviousSiblingSameType(Element element) {
        Element prevSibling = element;
        
        while((prevSibling = getPreviousSibling(prevSibling)) != null) {
            if(element.getName().equals(prevSibling.getName()) && 
                    element.getNamespace().equals(prevSibling.getNamespace()))
            {
                return prevSibling;
            }
        }
        
        return null;
    }
    
    /**
     * @param element
     * @return The previous sibling Element of element or null if there isn't one
     */
    public Element getPreviousSibling(Element element) {
        if(element == null) {
            return null;
        }
        
        Element parent = element.getParent();
        if(parent == null) {
            return null;
        }
        
        int index = indexOfElement(element);
        
        // First one
        if(index < 1) {
            return null;
        }
        
        return (Element)parent.getChildren().get(index - 1);
    }

    /**
     * @param element
     * @return The next sibling Element of the same name and Namespace as element or null
     */
    public Element getNextSiblingSameType(Element element) {
        Element nextSibling = element;
        
        while((nextSibling = getNextSibling(nextSibling)) != null) {
            if(element.getName().equals(nextSibling.getName()) && 
                    element.getNamespace().equals(nextSibling.getNamespace()))
            {
                return nextSibling;
            }
        }
        
        return null;
    }
    
    /**
     * @param element
     * @return The next sibling Element of element or null if there isn't one
     */
    public Element getNextSibling(Element element) {
        if(element == null) {
            return null;
        }
        
        Element parent = element.getParent();
        if(parent == null) {
            return null;
        }
        
        int index = indexOfElement(element);
        
        // Last one
        if(index == parent.getChildren().size() - 1) {
            return null;
        }
        
        return (Element)parent.getChildren().get(index + 1);
    }

    /**
     * @return the *first* found Element for the given XMLPath or null if not found
     */
    public Element getElement(XMLPath xmlPath) {
        Element element = getDocument().getRootElement();
        
        String rootName = xmlPath.getRootPart();
        
        // Now work thru the path so we can drill into the Schema to find the correct point in the Schema
        StringTokenizer t = xmlPath.getElements();
        while(t.hasMoreElements()) {
            // Get next Element name
            String name = t.nextToken();
            
            // Ignore Root Element
            if(name.equals(rootName)) {
                continue;
            }
            
            // Namespace
            Namespace ns;
            // Check for Namespace prefix
            int idx = name.indexOf(':');
            if(idx >= 0) {
                String prefix = name.substring(0, idx);
                name = name.substring(idx + 1);
                ns = element.getNamespace(prefix);
            }
            else {
                ns = element.getNamespace();
            }

            element = element.getChild(name, ns);
            
            // Not found
            if(element == null) {
                return null;
            }
        }
        
        return element;
    }
    
    /**
     * Return *all* Elements for the given XMLPath or null if not found
     */
    public Element[] getElements(XMLPath xmlPath) {
        Element element = getElement(xmlPath);
        
        if(element == null) {
            return null;
        }
        
        Element parent = element.getParent();
        
        // Must be root element
        if(parent == null) {
            return new Element[] { element };
        }

        List list = element.getParent().getChildren(element.getName(), element.getNamespace());
        Element[] elements = new Element[list.size()];
        if(!list.isEmpty()) {
            list.toArray(elements);
        }

        return elements;
    }
    
    /**
     * Get the first child Element of parentElement and XML offset path for the child. 
	 * The XMLPath can consist of more than one element - element1/element2/element3 and is used as the
	 * child pattern from the parent element.
	 * If an Element exists that matches the pattern that will be returned, else null.
	 * 
     * @param parentElement The known existing ancestor parent element.
     * @param xmlChildPath The XML path fragment required to construct the child Element.
     * @return The first Element that matches parentElement + xmlChildPath, or null.
     */
    public Element getElement(Element parentElement, XMLPath xmlChildPath) {
        Element element = null;
        Namespace ns = null;
        
        StringTokenizer t = xmlChildPath.getElements();
        while(t.hasMoreElements()) {
            String path = t.nextToken();
            
            // Check for Namespace prefix
            int idx = path.indexOf(':');
            if(idx >= 0) {
                String prefix = path.substring(0, idx);
                path = path.substring(idx + 1);
                ns = parentElement.getNamespace(prefix);
            }
            else {
                ns = parentElement.getNamespace();
            }
            
            element = parentElement.getChild(path, ns);
            if(element == null) {
                return null;
            }
            
            parentElement = element;
        }
        
        return element;
    }

    /**
     * Destroy this Document
     */
    public void destroy() {
        _doc = null;
    }
}

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

import org.jdom.Element;




/**
 * A SchemaControlledDocument has changed - this is the Event that is fired to Listeners.
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLDocumentListenerEvent.java,v 1.3 2007/07/15 20:27:46 phillipus Exp $
 */
public class XMLDocumentListenerEvent {
    private Object _source;
    private XMLDocument _doc;
    private Element _element;
    private boolean _doSelect;

    /**
     * Constructor
     * @param source Who started this event in this first place
     * @param xmlDocument The Document for the Element
     * @param element The Element concerned
     * @param doSelect A hint to the listener that they should select the Element that changed
     */
    public XMLDocumentListenerEvent(Object source, XMLDocument xmlDocument, Element element, boolean doSelect) {
        _source = source;
        _doc = xmlDocument;
        _element = element;
        _doSelect = doSelect;
    }

    public Object getSource() {
        return _source;
    }

    public XMLDocument getXMLDocument() {
        return _doc;
    }

    public Element getElement() {
        return _element;
    }

    public boolean doSelect() {
        return _doSelect;
    }
}
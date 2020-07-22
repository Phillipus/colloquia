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
 * Clipboard for transferring XMLDocument Elements
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLDocumentClipboard.java,v 1.3 2007/07/15 20:27:46 phillipus Exp $
 */
public class XMLDocumentClipboard {

    // Some flags for moving/copying Elements
    public static final int ACTION_COPY = 0x1;
    public static final int ACTION_MOVE = 0x2;
    public static final int ACTION_CUT = 0x4;
    public static final int ACTION_DELETE = 0x8;
    public static final int ACTION_PASTE = 0x10;

    private static Element _element = null;
    private static int _action = 0x0;

    public static void addCutElement(Element element) {
        _element = element;
        _action = ACTION_CUT;
    }

    public static void addCopiedElement(Element element) {
        _element = element;
        _action = ACTION_COPY;
    }

    public static Element getElement() {
        return _element;
    }

    public static boolean isCopiedElement() {
        return _action == ACTION_COPY;
    }

    public static boolean isCutElement() {
        return _action == ACTION_CUT;
    }

}
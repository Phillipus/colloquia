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

package uk.ac.reload.dweezil.gui.layout;

import java.io.Serializable;

/**
 * XY Constraints.
 *
 * @author Unattributed
 * @version $Id: XYConstraints.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class XYConstraints implements Serializable, Cloneable {
    int x, y, width, height;

    /**
     * Default Constructor
     */
    public XYConstraints() {
        this(0, 0, 0, 0);
    }

    /**
     * Constructor specifying co-ordinates and size of component.
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public XYConstraints(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor specifying starting co-ordinates only.
     * The component's width and height will be its preferred size
     * @param x
     * @param y
     */
    public XYConstraints(int x, int y) {
    	this(x, y, 0, 0);
    }

    public String toString() {
        return "XYConstraints [" + x + ", " + y + ", " + width + ", " + height + "]";
    }

    public Object clone() {
        return new XYConstraints(x, y, width, height);
    }

    public boolean equals(Object object) {
        if(object instanceof XYConstraints) {
            XYConstraints xyConstraints = (XYConstraints)object;
            return (xyConstraints.x == x) && (xyConstraints.y == y) && (xyConstraints.width == width) && (xyConstraints.height == height);
        }
        else return false;
    }

    public int hashCode() {
        return x ^ y * 37 * width * 43 ^ height * 47;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

}
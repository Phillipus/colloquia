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

import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;


/**
 * XYLayout
 *
 * @author Unattributed
 * @version $Id: XYLayout.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class XYLayout
implements Serializable, LayoutManager2
{
	static final XYConstraints defaultConstraints = new XYConstraints();
	Hashtable info;
	int height;
	int width;
	private static final long serialVersionUID = 201L;
	
	public XYLayout(int width, int height) {
		info = new Hashtable();
		this.width = width;
		this.height = height;
	}
	
	public XYLayout() {
		info = new Hashtable();
	}
	
	
	Dimension getLayoutSize(Container container, boolean flag) {
		Dimension dimension = new Dimension(0, 0);
		if(width <= 0 || height <= 0) {
			int i = container.getComponentCount();
			for(int j = 0; j < i; j++) {
				Component component = container.getComponent(j);
				if(component.isVisible()) {
					Rectangle rectangle = getComponentBounds(component, flag);
					dimension.width = Math.max(dimension.width, rectangle.x + rectangle.width);
					dimension.height = Math.max(dimension.height, rectangle.y + rectangle.height);
				}
			}
		}
		if(width > 0) dimension.width = width;
		if(height > 0) dimension.height = height;
		Insets insets = container.getInsets();
		dimension.width += insets.left + insets.right;
		dimension.height += insets.top + insets.bottom;
		return dimension;
	}
	
	Rectangle getComponentBounds(Component component, boolean flag) {
		XYConstraints xyConstraints = (XYConstraints)info.get(component);
		if(xyConstraints == null) xyConstraints = defaultConstraints;
		Rectangle rectangle = new Rectangle(xyConstraints.x, xyConstraints.y, xyConstraints.width, xyConstraints.height);
		if(rectangle.width <= 0 || rectangle.height <= 0) {
			Dimension dimension = flag ? component.getPreferredSize() : component.getMinimumSize();
			if(rectangle.width <= 0) rectangle.width = dimension.width;
			if(rectangle.height <= 0) rectangle.height = dimension.height;
		}
		return rectangle;
	}
	
	public void invalidateLayout(Container container) {}
	
	public float getLayoutAlignmentY(Container container) {
		return 0.5F;
	}
	
	public float getLayoutAlignmentX(Container container) {
		return 0.5F;
	}
	
	public Dimension maximumLayoutSize(Container container) {
		return new Dimension(0x7fffffff, 0x7fffffff);
	}
	
	public void addLayoutComponent(Component component, Object obj) {
		if(obj instanceof XYConstraints) info.put(component, obj);
	}
	
	public void layoutContainer(Container container) {
		Insets insets = container.getInsets();
		int i = container.getComponentCount();
		for(int j = 0; j < i; j++) {
			Component component = container.getComponent(j);
			if(component.isVisible()) {
				Rectangle rectangle = getComponentBounds(component, true);
				component.setBounds(insets.left + rectangle.x, insets.top + rectangle.y, rectangle.width, rectangle.height);
			}
		}
	}
	
	public Dimension minimumLayoutSize(Container container) {
		return getLayoutSize(container, false);
	}
	
	public Dimension preferredLayoutSize(Container container) {
		return getLayoutSize(container, true);
	}
	
	public void removeLayoutComponent(Component component) {
		info.remove(component);
	}
	
	public void addLayoutComponent(String s, Component component) {}
	
	public String toString() {
		return "XYLayout [" + width + ", " + height + "]";
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
	
}
/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2004 Oleg Liber, Bill Olivier, Phillip Beauvoir
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

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;

import com.brunchboy.util.swing.relativelayout.AttributeConstraint;
import com.brunchboy.util.swing.relativelayout.AttributeType;
import com.brunchboy.util.swing.relativelayout.DependencyManager;
import com.brunchboy.util.swing.relativelayout.RelativeLayout;


/**
 * RelativeLayoutManager wraps and handles calls to RelativeLayout<br>
 * This is a work in progress - we'll add more wrapper methods as needed.<br>
 * <br>
 * See - http://www.onjava.com/pub/a/onjava/2002/09/18/relativelayout.html<br>
 * <br>
 * Here's an example:<br>
 * <pre>
 * public class MyPanel extends JPanel {
 * 		public MyPanel() {
 * 			RelativeLayoutManager layoutManager = new RelativeLayoutManager(this);
 * 			JLabel label = new JLabel("Hello World");
 * 			layoutManager.addFromLeftEdge(label, "lblMain", RelativeLayoutManager.ROOT_NAME,
 *				RelativeLayoutManager.TOP, 0, 0);
 * 		}
 * }
 * </pre>
 * 
 * @author Phillip Beauvoir
 * @version $Id: RelativeLayoutManager.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class RelativeLayoutManager 
{
	
	/**
	 * The Container to which to add the components
	 */
	private Container _container;
	
	/**
	 * The RelativeLayout
	 */
	private RelativeLayout _layout;
	
    /**
     * Wrap DependencyManager.ROOT_NAME
     */
	public static final String ROOT_NAME = DependencyManager.ROOT_NAME;

    // Enumeration of Attribute Types
    public static final int BOTTOM = 1;
    public static final int TOP = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    public static final int HEIGHT = 5;
    public static final int WIDTH = 6;
    public static final int HORIZONTAL_CENTER = 7;
    public static final int VERTICAL_CENTER = 8;
    
    
    /**
     * Constructor
     * @param container the Parent Container to which to add the components
     */
    public RelativeLayoutManager(Container container) {
		_container = container;
		_layout = new RelativeLayout();
		_container.setLayout(_layout);
	}
	
	/**
	 * @return the RelativeLayout
	 */
	public RelativeLayout getLayout() {
		return _layout;
	}
	
	/**
	 * Set the Width of a component.
	 * @param componentName the unique name of the component
	 * @param width the width in pixels
	 */
	public void setWidth(String componentName, int width) {
		_layout.addConstraint(componentName, AttributeType.WIDTH,
				new AttributeConstraint(componentName, AttributeType.LEFT, width));
	}
	
	/**
	 * Set the Height of a component.
	 * @param componentName the unique name of the component
	 * @param height the height in pixels
	 */
	public void setHeight(String componentName, int height) {
		_layout.addConstraint(componentName, AttributeType.HEIGHT,
				new AttributeConstraint(componentName, AttributeType.TOP, height));
	}

	/**
     * Removes the specified component from the layout. This should be done
     * only with great care, for if there are any other components with
     * constraints that depend on this one, the layout will crash. <p>
     * 
     * @param component the component to be removed.
     */
	public void removeLayoutComponent(Component component) {
		_layout.removeLayoutComponent(component);
	}
	
	/**
	 * Add a component relative to another component's right edge.
	 * The size of the component will depend on its preferredSize.
	 * @param component the component to add
	 * @param componentName the unique name of the component
	 * @param anchorName the name of the component to anchor to relatively.
	 * @param verticalConstraint can be either RelativeLayoutManager.TOP or RelativeLayoutManager.BOTTOM
	 * for relative positioning from the anchorName component
	 * @param topOffset the offset in pixels from the top relative to topAnchor component
	 * @param leftOffset the offset in pixels from the left edge of the parent Container
	 */
	public void addFromRightEdgeComponent(JComponent component, String componentName, String anchorName,
			int verticalConstraint, int topOffset, int leftOffset) {
		
		_container.add(component, componentName);
		
		AttributeType attType = getAttributeType(verticalConstraint);
		
		_layout.addConstraint(componentName, AttributeType.TOP,
				new AttributeConstraint(anchorName, attType, topOffset));
		
		_layout.addConstraint(componentName, AttributeType.LEFT,
				new AttributeConstraint(anchorName, AttributeType.RIGHT, leftOffset));
	}

	/**
	 * Add a component from the container's left edge.  The size of the component will depend on
	 * its preferredSize.
	 * @param component the component to add
	 * @param componentName the unique name of the component
	 * @param anchorName the name of the component to anchor the TOP constraint.  This can be
	 * RelativeLayoutManager.ROOT_NAME
	 * @param verticalConstraint can be either RelativeLayoutManager.TOP or RelativeLayoutManager.BOTTOM
	 * for relative positioning from the anchorName component
	 * @param topOffset the offset in pixels from the top relative to topAnchor component
	 * @param leftOffset the offset in pixels from the left edge of the parent Container
	 */
	public void addFromLeftEdge(JComponent component, String componentName, String anchorName,
			int verticalConstraint, int topOffset, int leftOffset) {
		
		_container.add(component, componentName);
		
		AttributeType attType = getAttributeType(verticalConstraint);
		
		_layout.addConstraint(componentName, AttributeType.TOP,
				new AttributeConstraint(anchorName, attType, topOffset));
		
		_layout.addConstraint(componentName, AttributeType.LEFT,
				new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.LEFT, leftOffset));
	}
	
	/**
	 * Add a component stretching from the container's left edge to the right edge.
	 * @param component the component to add
	 * @param componentName the unique name of the component
	 * @param anchorName the name of the component to anchor the TOP constraint.  This can be
	 * RelativeLayoutManager.ROOT_NAME
	 * @param verticalConstraint can be either RelativeLayoutManager.TOP or RelativeLayoutManager.BOTTOM
	 * for relative positioning from the anchorName component
	 * @param topOffset the offset in pixels from the top relative to topAnchor component
	 * @param leftOffset the offset in pixels from the left edge of the parent Container
	 */
	public void addFromLeftToRightEdges(JComponent component, String componentName, String anchorName,
			int verticalConstraint, int topOffset, int leftOffset) {
		
		addFromLeftEdge(component, componentName, anchorName, verticalConstraint, topOffset, leftOffset);
		
		_layout.addConstraint(componentName, AttributeType.RIGHT,
				new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.RIGHT));
	}
	
	/**
	 * Add a component from the container's right edge.  The size of the component will depend on
	 * its preferredSize.
	 * @param component the component to add
	 * @param componentName the unique name of the component
	 * @param anchorName the name of the component to anchor the TOP constraint.  This can be
	 * RelativeLayoutManager.ROOT_NAME
	 * @param verticalConstraint can be either RelativeLayoutManager.TOP or RelativeLayoutManager.BOTTOM
	 * for relative positioning from the anchorName component
	 * @param topOffset the offset in pixels from the top relative to topAnchor component
	 * @param rightOffset the offset in pixels from the right edge of the parent Container.
	 * This will be a negative number.
	 */
	public void addFromRightEdge(JComponent component, String componentName, String anchorName,
			int verticalConstraint, int topOffset, int rightOffset) {
		
		_container.add(component, componentName);
		
		AttributeType attType = getAttributeType(verticalConstraint);
		
		_layout.addConstraint(componentName, AttributeType.TOP,
				new AttributeConstraint(anchorName, attType, topOffset));
		
		_layout.addConstraint(componentName, AttributeType.RIGHT,
				new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.RIGHT, rightOffset));
	}

	/**
	 * Add a component centred to the container's edges.  The size of the component will depend on
	 * its preferredSize.
	 * @param component the component to add
	 * @param componentName the unique name of the component
	 * @param anchorName the name of the component to anchor the vertical constraint.  This can be
	 * RelativeLayoutManager.ROOT_NAME
	 * @param verticalConstraint can be either RelativeLayoutManager.TOP or RelativeLayoutManager.BOTTOM
	 * for relative positioning from the anchorName component
	 * @param topOffset the offset in pixels from the top relative to topAnchor component
	 * @param leftOffset the offset in pixels from the left edge of the parent Container
	 */
	public void addToCenter(JComponent component, String componentName, String anchorName,
			int verticalConstraint, int topOffset, int leftOffset) {
		
		_container.add(component, componentName);
		
		AttributeType attType = getAttributeType(verticalConstraint);
		
		_layout.addConstraint(componentName, AttributeType.TOP,
				new AttributeConstraint(anchorName, attType, topOffset));
		
		_layout.addConstraint(componentName, AttributeType.HORIZONTAL_CENTER,
				new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, leftOffset));
	}

	/**
	 * @param type one of BOTTOM, TOP, LEFT, RIGHT, HEIGHT, WIDTH, HORIZONTAL_CENTER, VERTICAL_CENTER
	 * @return the AttributeType enunerated from type
	 */
	public AttributeType getAttributeType(int type) {
		switch(type) {
			case BOTTOM:
				return AttributeType.BOTTOM;
			case TOP:
				return AttributeType.TOP;
			case LEFT:
				return AttributeType.LEFT;
			case RIGHT:
				return AttributeType.RIGHT;
			case HEIGHT:
				return AttributeType.HEIGHT;
			case WIDTH:
				return AttributeType.WIDTH;
			case HORIZONTAL_CENTER:
				return AttributeType.HORIZONTAL_CENTER;
			case VERTICAL_CENTER:
				return AttributeType.VERTICAL_CENTER;
			default:
				throw new IllegalStateException("AttributeType not known: " + type); 
		}
	}
}

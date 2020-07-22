/*
 * RelativeLayout.java
 *
 * Created on Sat May 11 2002
 *
 * Copyright (c) 2002, James J. Elliott (jim@brunchboy.com).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Brunch Boy Design nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.brunchboy.util.swing.relativelayout;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;


/**
 * A layout manager that uses a dependency analyzer to support an arbitrary
 * (but consistent and complete) set of relative constraints between its
 * components. <p>
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: RelativeLayout.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class RelativeLayout implements LayoutManager2 {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: RelativeLayout.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Keeps track of the specifications of all the components we are supposed
     * to manage. Keys are component logical names (as supplied when adding
     * the component to be laid out), and values are
     * {@link ComponentSpecification} objects. <p>
     * 
     * The specifications typically use constraints expressed relative
     * to the attributes of other components; we'll use a dependency analyzer
     * to manage that.
     **/
    private Map componentSpecs = new HashMap();


    /**
     * An alternate index of the components, keyed by the graphical component
     * itself. Used both to make sure the same component isn't being managed
     * twice, and to support the removal of components from management (note
     * that this must be done with great care, however, as any constraints
     * that depended on them would become unresolvable, causing crashes).
     **/
    private Map specsByAWTComponent = new HashMap();

    
    /**
     * Manages the dependencies between components, ensuring they are
     * consistent and non-cyclical. Sorts them into the order in which they
     * must be resolved. If null, we've had a fundamental change, such as a
     * new component added, so the dependencies need to be re-registered.
     **/
    private DependencyManager dependencies;


    /**
     * We'll maintain a single AttributeSource instance to hand off to clients
     * for when they need to resolve constraints.
     **/
    private final AttributeSource attributeSource = new AttributeSource() {
            public int getValue(Attribute attribute) {
                ComponentSpecifications anchor = (ComponentSpecifications)
                    componentSpecs.get(attribute.getComponent());
                if (anchor == null) {
                    throw new IllegalStateException(
                        "Need attribute of unknown component " +
                        attribute.getComponent());
                }

                return anchor.getAttributeValue(attribute.getType());
            }
        };

    
    /**
     * Look up the specifications for a named component. <p>
     * 
     * @param name logical name of the component whose specifications are
     *             desired.
     * 
     * @return the specifications, creating them if needed.
     * 
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     **/
    private ComponentSpecifications getComponentSpecifications(String name) {
        if (name == null) {
            throw new NullPointerException("Component name cannot be null");
        }

        ComponentSpecifications results = (ComponentSpecifications)
            (componentSpecs.get(name));
        
        if (results == null) {
            // We've not heard about this component yet, so create new specs
            results = new ComponentSpecifications(name);
            componentSpecs.put(name, results);
        }

        return results;
    }


    /**
     * Helper method to detect and prevent use of reserved names. <p>
     * 
     * @param name the name with which a component is being registered with
     *        the layout manager.
     * 
     * @throws IllegalArgumentException if the name is reserved.
     **/
    private void validateComponentName(String name) {
        if (DependencyManager.ROOT_NAME.equals(name)) {
            throw new IllegalArgumentException("The name " +
                          DependencyManager.ROOT_NAME + " is reserved");
        }
    }


    /**
     * Add a component to be laid out. The constraints will be set up
     * separately through {@link #addConstraint addConstraint}. <p>
     * 
     * @param name the logical name by which the component will be known; this
     *        will be used in all constraints that relate to the component. If
     *        the component's actual {@link Component#getName name} isn't
     *        already equal this value, it will be set by this method.
     * 
     * @param component the graphical component whose size and position is to
     *        be managed according to our constraint hierarchy.
     * 
     * @throws IllegalStateException if multiple components are added with the
     *         same logical name, or if the same component is added more than
     *         once.
     * 
     * @throws IllegalArgumentException if you try to use the name reserved
     *         for the container, {@link DependencyManager#ROOT_NAME}.
     * 
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     **/
    public void addLayoutComponent(String name, Component component) {
        validateComponentName(name);
        if (specsByAWTComponent.get(component) != null) {
            throw new IllegalStateException("Component " + component +
                                            " is already in the layout");
        }
        if (!name.equals(component.getName())) {
            component.setName(name);
        }
        ComponentSpecifications specs = getComponentSpecifications(name);
        specs.setComponent(component);
        specsByAWTComponent.put(component, specs);
        dependencies = null;
    }


    /**
     * Add a component to be laid out, with specified constraints.<p>
     * 
     * @param component the graphical component whose size and position is to
     *        be managed according to our constraint hierarchy.
     * 
     * @param constraints currently must be a {@link String} specifying the
     *        logical name of the component (making this method is identical
     *        to the version in which the parameters are reversed). A
     *        straightforward enhancement would be to also support passing
     *        a new class (perhaps named ConstraintList) that spelled out
     *        the component's logical name and the constraints associated
     *        with it. It turns out that the XML-based configuration is even
     *        more convenient, so this hasn't been implemented.
     * 
     * @throws IllegalStateException if multiple components are added with the
     *         same logical name, or if the same component is added more than
     *         once.
     * 
     * @throws IllegalArgumentException if the constraints parameter is not a
     *         {@link String}.
     * 
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     **/
    public void addLayoutComponent(Component component, Object constraints) {
        // Is this equivalent to the old, LayoutManager method?
        if (constraints instanceof String) {
            addLayoutComponent((String)constraints, component);
            return;
        }

        // Now try to process the constraints
    
        // Nothing we recognized
        throw new IllegalArgumentException("Unrecognizable constraints");
    }


    /**
     * Programmatically add a constraint for a component attribute. <p>
     * 
     * @param componentName the logical name of the component for which a
     *        constraint is to be recorded.
     * 
     * @param type the attribute for which a constraint is to be recorded.
     * 
     * @param constraint the information from which the attribute value should
     *        be computed (for example an {@link AttributeConstraint} or
     *        {@link AxisConstraint}).
     * 
     * @throws IllegalStateException if this would overconstrain the component.
     * 
     * @throws IllegalArgumentException if you try to use the name reserved
     *         for the container, {@link DependencyManager#ROOT_NAME}.
     * 
     * @throws NullPointerException if any argument is <code>null</code>.
     **/
    public void addConstraint(String componentName, AttributeType type,
                              Constraint constraint) {
        validateComponentName(componentName);
        getComponentSpecifications(componentName).addConstraint(type,
                                                                constraint);
        dependencies = null;
    }


    /**
     * Utility method that predefines the attributes associated with the
     * container, whose dimensions are externally defined and thus known
     * a priori. <p>
     * 
     * @param width the width of the container.
     * 
     * @param height the height of the container.
     **/
    private void resolveContainerSpecs(int width, int height) {
        getComponentSpecifications(DependencyManager.ROOT_NAME).
            setContainerSpecs(width, height);
    }


    /**
     * Utility method that prepares dependencies for resolution, and applies
     * constraints to each component specification to compute all values in
     * preparation for either actual layout or measuring the bounding box of
     * the components to compute the minimum or preferred size of the layout.
     * <p>
     * 
     * @param minimumSize if <code>true</code> the minimum sizes of components
     *        should be used in order to compute a minimum size for the overall
     *        layout. If <code>false</code>, the preferred sizes are used.
     * 
     * @param parentWidth the width of the parent container, in case we have
     *        any container-based constraints (set to zero to compute minimum
     *        or preferred layout size).
     * 
     * @param parentHeight the height of the parent container, in case we have
     *        any container-based constraints (set to zero to compute minimum
     *        or preferred layout size).
     * 
     * @throws IllegalStateException if there is a problem with the dependency
     *         tree.
     **/
    private void resolveComponents(boolean minimumSize,
                                   int parentWidth, int parentHeight) {

        // If necessary, build the list of dependencies to be sorted.
        boolean needDependencies = false;
        if (dependencies == null) {
            dependencies = new DependencyManager();
            needDependencies = true;
        }
        
        // Loop over the components clearing their current values, and if
        // necessary, asking them about their dependencies
        for (Iterator iter = componentSpecs.values().iterator();
             iter.hasNext(); ) {
            ComponentSpecifications spec = (ComponentSpecifications)
                iter.next();
            spec.clearAttributeValues();
            if (needDependencies &&
                spec.getComponentName() != DependencyManager.ROOT_NAME) {
                spec.reportDependencies(dependencies);
            }
        }

        // Prepopulate the container specs, which are known a priori
        resolveContainerSpecs(parentWidth, parentHeight);

        // Calculate all dependent attributes, sorted in the order in which
        // they can safely be computed.
        for (ListIterator iter = dependencies.sort().listIterator();
             iter.hasNext(); ) {
            Attribute attribute = (Attribute)iter.next();
            ComponentSpecifications spec =
                getComponentSpecifications(attribute.getComponent());
            spec.computeAttribute(attribute.getType(), attributeSource,
                                  minimumSize, parentWidth, parentHeight);
        }
    }


    /**
     * Lays out the specified container, according to the constraints that have
     * been established. <p>
     * 
     * @param parent the container to be laid out.
     * 
     * @throws IllegalStateException if there is a problem with the dependency
     *         tree.
     **/
    public void layoutContainer(Container parent) {

        // Find out how much space we have to work with after the insets have
        // been taken out.
        Insets insets = parent.getInsets();
        int width = parent.getSize().width - insets.left - insets.right;
        int height = parent.getSize().height - insets.top - insets.bottom;

        // Figure out the constrained attributes of all the components.
        resolveComponents(false, width, height);

        // Reshape the components according to their computed specifications.
        for (Iterator iter = componentSpecs.values().iterator();
             iter.hasNext(); ) {
            ComponentSpecifications spec = (ComponentSpecifications)
                iter.next();
            spec.layoutComponent(insets.left, insets.top);
        }
    }


    /**
     * Removes the specified component from the layout. This should be done
     * only with great care, for if there are any other components with
     * constraints that depend on this one, the layout will crash. <p>
     * 
     * @param component the component to be removed.
     **/
    public void removeLayoutComponent(Component component) {
        ComponentSpecifications specs = (ComponentSpecifications)
            specsByAWTComponent.get(component);
        if (specs != null) {
            specsByAWTComponent.remove(component);
            componentSpecs.remove(specs.getComponentName());
            dependencies = null;
        }
    }


    /**
     * Determine an overall bounding box for the components whose attributes
     * have been resolved, as part of measuring the minimum or preferred layout
     * size.
     * 
     * @return a dimension representing the distance between the leftmost and
     *         rightmost, and topmost and bottommost, points in the component
     *         specifications.
     * 
     * @throws IllegalStateException if there are any unresolved component
     *         attributes.
     **/
    private Dimension calculateBoundingBox() {
        // Collect the extreme values from each component seen.
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Iterator iter = componentSpecs.values().iterator();
             iter.hasNext(); ) {
            ComponentSpecifications spec = (ComponentSpecifications)
                iter.next();
            minX = Math.min(minX, spec.getAttributeValue(AttributeType.LEFT));
            minY = Math.min(minY, spec.getAttributeValue(AttributeType.TOP));
            maxX = Math.max(maxX, spec.getAttributeValue(AttributeType.RIGHT));
            maxY = Math.max(maxY,
                            spec.getAttributeValue(AttributeType.BOTTOM));
        }

        // The differences between the extremes are the space the layout wants
        return new Dimension(maxX - minX, maxY - minY);
    }


    /**
     * Adds the space taken up by the container's insets to the amount of space
     * that would otherwise be needed by a layout. <p>
     * 
     * @param spaceNeeded the space needed without considering parent
     *        insets.
     * 
     * @param parent the container in which we are performing layout.
     * 
     * @return the adjusted space requirements, taking insets into account.
     **/
    private Dimension addInsets(Dimension spaceNeeded, Container parent) {
        Insets insets = parent.getInsets();
        return new Dimension(spaceNeeded.width + insets.left + insets.right,
                             spaceNeeded.height + insets.top + insets.bottom);
    }


    /**
     * Calculates the preferred size dimensions for the specified
     * container, given the components it contains. Only components whose
     * natural sizes are considered in the layout will contribute (components
     * fully constrained to the container size can have no say).  Even without
     * such components, this is only an approximation because it can't take
     * into account a layout like one where components are relative to a 
     * centered component, and thus get shoved outside the container. To solve
     * that would require something like performing a binary search through
     * the size space (with care to avoid infinite loops when components are
     * explicitly constrained to extend beyond the container's bounds). <p>
     * 
     * @param parent the container to be laid out.
     *
     * @return an approximation of a size which allows all components to be
     *         drawn at their preferred widths.
     * 
     * @throws IllegalStateException if there are any unresolved component
     *         attributes.
     * 
     * @see #minimumLayoutSize
     **/
    public Dimension preferredLayoutSize(Container parent) {
        // Calculate the amounts of space needed by applying the constraints
        // to the components' preferred sizes.
        resolveComponents(false, 0, 0);
        return addInsets(calculateBoundingBox(), parent);
    }


    /**
     * Calculates the minimum size dimensions for the specified
     * container, given the components it contains. Only components whose
     * natural sizes are considered in the layout will contribute (components
     * fully constrained to the container size can have no say). Even without
     * such components, this is only an approximation because it can't take
     * into account a layout like one where components are relative to a 
     * centered component, and thus get shoved outside the container. To solve
     * that would require something like performing a binary search through
     * the size space (with care to avoid infinite loops when components are
     * explicitly constrained to extend beyond the container's bounds). <p>
     * 
     * @param parent the component to be laid out.
     * 
     * @return an approximation of a size which allows all components to be
     *         drawn at their minimum widths.
     * 
     * @throws IllegalStateException if there are any unresolved component
     *         attributes.
     * 
     * @see #preferredLayoutSize
     **/
    public Dimension minimumLayoutSize(Container parent) {
        // Calculate the amounts of space needed by applying the constraints
        // to the components' minimum sizes.
        resolveComponents(true, 0, 0);
        return addInsets(calculateBoundingBox(), parent);
    }


    /**
     * Calculates the maximum size dimensions for the specified container,
     * given the components it contains.  This implementation simply always
     * returns the maximum possible size, since there isn't any other
     * meaningful choice. <p>
     * 
     * @see java.awt.Component#getMaximumSize
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container target) {
        return Component.CENTER_ALIGNMENT;
    }


    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target) {
        return Component.CENTER_ALIGNMENT;
    }


    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded. Since no per-container
     * information is cached, this does nothing.
     */
    public void invalidateLayout(Container target) {
    }
}











/*
 * ComponentSpecification.java
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

import java.util.*;
import java.awt.Component;


/**
 * Keeps track of the specifications that have been provided for a particular
 * component, and is able to determine how to derive the rest from these
 * constraints.
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: ComponentSpecifications.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class ComponentSpecifications {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: ComponentSpecifications.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Keep track of the logical name of the component for which we're tracking
     * specifications.
     **/
    private final String componentName;

    
    /**
     * Return the logical name of the component to which these specifications
     * apply.
     * 
     * @return name by which component is known to the {@link RelativeLayout}.
     **/
    public String getComponentName() {
        return componentName;
    }


    /**
     * Keep track of the actual AWT component being controlled by these
     * specifications, once we learn about it.
     **/
    private Component component;


    /**
     * Set the component being controlled by these
     * specifications, once we learn about it.
     *
     * @param component the component to constrain.
     * 
     * @throws IllegalStateException if the component has already been set.
     **/
    public void setComponent(Component component) {
        if (this.component != null) {
            throw new IllegalStateException(componentName +
                                            ": component was already set");
        }
        this.component = component;
    }


    /**
     * Return the component being controlled by these specifications.
     * 
     * @return the component being constrained, or <code>null</code> if we
     *         don't yet have one.
     **/
    public Component getComponent() {
        return component;
    }


    /**
     * Keep track of the constraints which have been explicitly supplied
     * for this component. Keys are {@link AttributeType}s, while values
     * are concrete {@link Constraint} implementations.
     **/
    private Map constraints = new HashMap();


    /**
     * Keep track of the actual attribute values that have been computed during
     * layout. Keys are {@link AttributeType}s, while values are
     * {@link Integer}s.
     **/
    private Map values = new HashMap();


    /**
     * Return an attribute value, assuming it has been resolved. <p>
     * 
     * @param type the type of attribute desired.
     * 
     * @return the computed value of that attribute.
     * 
     * @throws IllegalStateException if this method is called before the
     *         specified attribute has been resolved.
     **/
    public int getAttributeValue(AttributeType type) {
        Integer value = (Integer)values.get(type);
        if (value == null) {
            throw new IllegalStateException(componentName + ": " + type +
                                            " not yet resolved");
        }

        return value.intValue();
    }


    /**
     * Tests whether the specified attribute value has been defined explicitly
     * (i.e. not through derivation). <p>
     * 
     * @param type the type of attribute desired.
     * 
     * @return <code>true</code> iff the attribute has been given a constraint.
     **/
    public boolean hasConstraint(AttributeType type) {
        return constraints.containsKey(type);
    }


    /**
     * Look up the natural width of the component being managed, if there
     * is one. <p>
     * 
     * @param minimumSize indicates whether we should ask the component for
     *        its minimum size rather than its preferred size.
     * 
     * @return the reported width if there is a component, or 0.
     **/
    public int getComponentWidth(boolean minimumSize) {
        if (component != null) {
            if (minimumSize) {
                return component.getMinimumSize().width;
            }
            return component.getPreferredSize().width;
        }
        return 0;
    }


    /**
     * Look up the natural height of the component being managed, if there
     * is one. <p>
     * 
     * @param minimumSize indicates whether we should ask the component for
     *        its minimum size rather than its preferred size.
     * 
     * @return the reported height if there is a component, or 0.
     **/
    public int getComponentHeight(boolean minimumSize) {
        if (component != null) {
            if (minimumSize) {
                return component.getMinimumSize().height;
            }
            return component.getPreferredSize().height;
        }
        return 0;
    }


    /**
     * Avoid creating zillions of copies of an Integer representing the number
     * zero (there should be factory methods in the Integer class to deal with
     * this for us, but...)
     **/
    private static final Integer ZERO = new Integer(0);


    /**
     * Set an attribute value, which should not yet exist. <p>
     * 
     * @param type the type of attribute desired.
     * 
     * @param value the desired value of that attribute.
     * 
     * @throws IllegalStateException if this method is called to set an
     *         attribute whose value has already been resolved.
     **/
    public void setAttributeValue(AttributeType type, int value) {
        if (values.containsKey(type)) {
            throw new IllegalStateException(componentName + ": " + type +
                                            "resolved more than once");
        }

        if (value == 0) {
            values.put(type, ZERO);
        }
        else {
            values.put(type, new Integer(value));
        }
    }


    /**
     * Clear out any existing attribute values in preparation for a new
     * layout operation. <p>
     **/
    public void clearAttributeValues() {
        values.clear();
    }


    /**
     * Create a new set of specifications for the named component. We don't
     * currently have an actual AWT {@link Component} associated with the
     * constraints; that will be set later. <p>
     * 
     * @param componentName the logical name by which the
     *        {@link RelativeLayout} knows the component.
     **/
    public ComponentSpecifications(String componentName) {
        this(componentName, null);
    }


    /**
     * Create a new set of specifications for the supplied component. <p>
     * 
     * @param componentName the logical name by which the
     *        {@link RelativeLayout} knows the component.
     * 
     * @param component the graphical component that will be controlled by
     *        these specifications.
     **/
    public ComponentSpecifications(String componentName, Component component) {
        this.componentName = componentName;
        this.component = component;
    }


    /**
     * Add a constraint for this component. <p>
     * 
     * @param type the attribute which is to be constrained.
     * 
     * @param constraint the place from which the attribute's value can be
     *        calculated.
     * 
     * @throws IllegalStateException if this would cause the component to
     *         become overconstrained.
     *
     * @throws NullPointerException if any argument is <code>null</code>.
     **/
    public void addConstraint(AttributeType type, Constraint constraint) {
        if (type == null) {
            throw new NullPointerException("type must not be null");
        }
        if (constraint == null) {
            throw new NullPointerException("constraint must not be null");
        }
        
        // Determine the existing constraints that relate to the new one
        Set relevantConstraints = new HashSet(constraints.keySet());
        relevantConstraints.retainAll(
            AttributeType.getAxisTypes(type.getAxis()));

        if (relevantConstraints.contains(type)) {
            throw new IllegalStateException(componentName + ": " + type +
                                            " is already constrained");
        }
        if (relevantConstraints.size() > 1) {
            throw new IllegalStateException(
                componentName + ": Cannot add " + type +
                " constraint without overconstraining");
        }

        constraints.put(type, constraint);
    }


    /**
     * Validate our constraints when we're about to perform a layout operation.
     * We must have at least one constraint on each axis, and if that is
     * width or height, there must be two on that axis. <p>
     * 
     * @throws IllegalStateException if we're underconstrained.
     **/
    private void validateConstraints() {
        Set axisConstraints = new HashSet(constraints.keySet());
        axisConstraints.retainAll(
            AttributeType.getAxisTypes(AttributeAxis.HORIZONTAL));
        if (axisConstraints.size() < 1 ||
            (axisConstraints.size() == 1 &&
             axisConstraints.contains(AttributeType.WIDTH))) {
            throw new IllegalStateException(componentName +
                                        " is underconstrained horizontally");
        }
        axisConstraints = new HashSet(constraints.keySet());
        axisConstraints.retainAll(
            AttributeType.getAxisTypes(AttributeAxis.VERTICAL));
        if (axisConstraints.size() < 1 ||
            (axisConstraints.size() == 1 &&
             axisConstraints.contains(AttributeType.HEIGHT))) {
            throw new IllegalStateException(componentName +
                                            " is underconstrained vertically");
        }
    }


    /**
     * Called the first time layout is being performed after components or
     * constraints have been changed, to build up the master list of
     * dependencies to be sorted. Reports both the explicit constraints that
     * have been registered for this component, and the dependencies needed
     * to compute the derived attributes. <p>
     * 
     * @param dependencies the dependency analyzer to which dependencies need
     *        to be reported.
     * 
     * @throws IllegalStateException if this leads to a bad dependency graph.
     **/
    public void reportDependencies(DependencyManager dependencies) {
        // Make sure we have enough constraints.
        validateConstraints();

        // First report the dependencies inherent in our constraints
        for (Iterator iter = constraints.entrySet().iterator() ;
              iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iter.next();
            Attribute dependent =
                new Attribute(componentName, (AttributeType)entry.getKey());
            Constraint constraint = (Constraint)entry.getValue();

            // Each constraint defines one or more attributes as dependencies
            for (Iterator catts = constraint.getDependencies().listIterator() ;
                 catts.hasNext(); ) {
                dependencies.add(dependent, (Attribute)catts.next());
            }
        }

        // Then report that each of our derived attributes (i.e. the rest)
        // are dependent on all of constrained attributes on the same axis.
        Set others = new HashSet(AttributeType.getAllTypes());
        others.removeAll(constraints.keySet());

        // Loop over all our attributes for which we have no constraints
        for (Iterator iter = others.iterator(); iter.hasNext(); ) {
            AttributeType unconstrained = (AttributeType)iter.next();
            Attribute derived = new Attribute(componentName, unconstrained);

            // Find the constraints we have on the same axis
            Set relevantConstraints = new HashSet(constraints.keySet());
            relevantConstraints.retainAll(
                AttributeType.getAxisTypes(unconstrained.getAxis()));

            // Report dependencies on all those constrained attributes for
            // this derived attribute
            for (Iterator iter2 = relevantConstraints.iterator() ;
                 iter2.hasNext(); ) {
                dependencies.add(derived, new Attribute(
                    componentName, (AttributeType)iter2.next()));
            }
        }
    }


    /**
     * Compute the specified attribute, either from a supplied constraint, or
     * by deriving it from other attributes of this component if it was not
     * constrained. Any attributes on which we depend must have been computed
     * prior to calling this. <p>
     *
     * @param attribute the type of attribute that should now be computed.
     * 
     * @param allAttrs provides read access to all existing component
     *        attributes, for use in evaluating constraints
     * 
     * @param minimumSize if <code>true</code> the minimum sizes of components
     *        should be used in order to compute a minimum size for the overall
     *        layout. If <code>false</code>, the preferred sizes are used.
     * 
     * @param parentWidth the width of the parent container, in case we have
     *        any container-based constraints.
     * 
     * @param parentHeight the height of the parent container, in case we have
     *        any container-based constraints.
     * 
     * @throws IllegalStateException if called before a required attribute
     *         has been computed, or if the component is underconstrained.
     **/
    public void computeAttribute(AttributeType attribute,
                                 AttributeSource allAttrs,
                                 boolean minimumSize,
                                 int parentWidth, int parentHeight) {
        // If it's an attribute for which we have a constraint, just resolve
        // that constraint.
        if (constraints.containsKey(attribute)) {
            Constraint constraint = (Constraint)constraints.get(attribute);
            setAttributeValue(attribute, constraint.getValue(allAttrs));
        }
        else {
            // It's a derived attribute; compute it from what we've got
            setAttributeValue(attribute, attribute.deriveValue(this,
                                                               minimumSize));
        }
    }


    /**
     * Sets the bounds of the component being managed by these specifications.
     * Called when the container is laid out. All attributes must be computed
     * before calling this method. <p>
     * 
     * @param xOffset offset to be added to horizontal coordinates (for example
     *        to account for container insets).
     * 
     * @param yOffset offset to be added to vertical coordinates (for example
     *        to account for container insets).
     * 
     * @throws IllegalStateException if any required attributes have not been
     *         computed.
     **/
    public void layoutComponent(int xOffset, int yOffset) {
        // Quietly ignore a missing component, this just means this is a
        // virtual component created solely for layout purposes.
        if (component != null) {
            component.setBounds(
                getAttributeValue(AttributeType.LEFT) + xOffset,
                getAttributeValue(AttributeType.TOP) + yOffset,
                getAttributeValue(AttributeType.WIDTH),
                getAttributeValue(AttributeType.HEIGHT));
        }
    }


    /**
     * Package-private method used by the {@RelativeLayout} to initialize the
     * specifications of the special entry that represents the parent container
     * in which layout is being performed. <p>
     * 
     * @param width the width of the container.
     * 
     * @param height the height of the container.
     **/
    void setContainerSpecs(int width, int height) {
        setAttributeValue(AttributeType.LEFT, 0);
        setAttributeValue(AttributeType.TOP, 0);
        setAttributeValue(AttributeType.RIGHT, width);
        setAttributeValue(AttributeType.WIDTH, width);
        setAttributeValue(AttributeType.BOTTOM, height);
        setAttributeValue(AttributeType.HEIGHT, height);
        setAttributeValue(AttributeType.HORIZONTAL_CENTER, width / 2);
        setAttributeValue(AttributeType.VERTICAL_CENTER, height / 2);
    }
}

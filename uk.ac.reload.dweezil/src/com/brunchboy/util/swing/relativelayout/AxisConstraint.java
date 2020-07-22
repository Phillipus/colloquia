/*
 * AxisConstraint.java
 *
 * Created on Sun May 12 2002
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


/**
 * A Constraint that represents a fractional position along an axis of another
 * component. In other words, for a vertical attribute, it can range from
 * the top of the component (0.0) to the bottom (1.0). For horizontal
 * attributes, the range spans from the left to the right edges. It is possible
 * to use values outside this range, to represent points outside the anchor
 * component, but there aren't yet any envisioned uses for such constraints.<p>
 * 
 * This is an immutable class, and so it can be
 * freely copied and shared in a multithreaded environment.
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: AxisConstraint.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class AxisConstraint implements Constraint {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: AxisConstraint.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Tracks the component relative to which this constraint will be computed.
     **/
    protected final String anchorName;


    /**
     * Track the axis along which we're going to compute our fraction.
     **/
    private final AttributeAxis axis;


    /**
     * Track the fraction to be used to position ourselves within the anchor
     * component.
     **/
    private final double fraction;


    /**
     * Keep a cached list of our dependencies, in order to speed up layout.
     **/
    private final List dependencies;


    /**
     * Keep a cached reference to the attribute that represents the origin
     * from which we're calculating.
     **/
    private final Attribute origin;


    /**
     * Keep a cached reference to the attribute that represents the length
     * along which we're calculating.
     **/
    private final Attribute length;


    /**
     * Constructor sets the immutable fields.
     * 
     * @param anchorName the component relative to which this constraint is
     *        computed.
     * 
     * @param axis the axis along which we'll compute a fractional position.
     * 
     * @param position the fraction to be used to position ourselves within the
     *        anchor component.
     **/
    public AxisConstraint(String anchorName, AttributeAxis axis,
                          double position) {
        this.anchorName = anchorName;
        this.axis = axis;
        fraction = position;

        // Create our dependency information
        if (axis == AttributeAxis.HORIZONTAL) {
            origin = new Attribute(anchorName, AttributeType.LEFT);
            length = new Attribute(anchorName, AttributeType.WIDTH);
        } else {
            origin = new Attribute(anchorName, AttributeType.TOP);
            length = new Attribute(anchorName, AttributeType.HEIGHT);
        }
        List deps = new ArrayList(2);
        deps.add(origin);
        deps.add(length);
        dependencies = Collections.unmodifiableList(deps);
    }


    /**
     * Return the attributes on which this constraint depends. <p>
     *
     * @return a list of {@link Attribute}s which must be resolved before this
     *         constraint can be evaluated.
     **/
    public List getDependencies() {
        return dependencies;
    }


    /**
     * Compute the value of the constraint, given the specifications on
     * on which it is based. Any dependencies must have been resolved
     * prior to calling this method, or the method will fail. <p>
     * 
     * @param attributes provides read access to all existing component
     *        attributes, for use in evaluating this constraint..
     * 
     * @return the value represented by this constraint, assuming any
     *         attributes of the anchor component on which it depends have been
     *         resolved already.
     * 
     * @throws IllegalStateException if any dependencies are not yet resolved.
     **/
    public int getValue(AttributeSource attributes) {
        
        return attributes.getValue(origin) +
            (int)(fraction * attributes.getValue(length));
    }

    
    /**
     * Provide a textual representation of the constraint for debugging
     * purposes.
     * 
     * @return the description of this attribute constraint.
     **/
    public String toString() {
        return "AxisConstraint: {anchor=" + anchorName +
            ", axis=" + axis + ", fraction=" + fraction + '}';
    }
}


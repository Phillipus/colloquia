/*
 * AttributeConstraint.java
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
 * A Constraint that represents a fixed offset from an attribute of another
 * component (or list of components). In the case of a list, the attribute
 * is treated as coming from the bounding box of all components in the list.
 * This is an immutable class, and so it can be freely copied and shared in
 * a multithreaded environment.
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: AttributeConstraint.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class AttributeConstraint implements Constraint {

    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: AttributeConstraint.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Tracks the comma-delimited list of components relative to which this
     * constraint will be computed.
     **/
    protected final String anchorList;

    
    /**
     * Track the attribute of the anchor components which we'll use to compute
     * our value.
     **/
    private final AttributeType attribute;


    /**
     * Track the offset to be added to the anchor attribute when computing
     * our value.
     **/
    private final int offset;


    /**
     * Keep a cached list of our dependencies, in order to speed up layout.
     **/
    private final List dependencies;


    /**
     * Constructor sets the immutable fields.
     * 
     * @param anchorList a comma-delimited list of the names the component(s)
     *        relative to which this constraint is computed.
     * 
     * @param attribute the attribute of the anchor component(s) which is the
     *        source of this constraint's value. When multiple components are
     *        combined, the attribute is treated as coming from their bounding
     *        box.
     * 
     * @param offset the offset, in pixels, to be added to the anchor
     *        attribute value to compute this constraint's value.
     **/
    public AttributeConstraint(String anchorList, AttributeType attribute,
                               int offset) {
        this.anchorList = anchorList;
        this.attribute = attribute;
        this.offset = offset;

        // Create our cached dependency list
        List deps = new ArrayList();
        for (StringTokenizer st = new StringTokenizer(anchorList, ","); 
             st.hasMoreTokens(); ) {
            String anchor = st.nextToken().trim();
            deps.add(new Attribute(anchor, attribute));
        }
        dependencies = Collections.unmodifiableList(deps);
    }


    /**
     * Simplified constructor usable when offset is zero.
     * 
     * @param anchorList a comma-delimited list of the names the component(s)
     *        relative to which this constraint is computed.
     * 
     * @param attribute the attribute of the anchor component(s) which is the
     *        source of this constraint's value. When multiple components are
     *        combined, the attribute is treated as coming from their bounding
     *        box.
     **/
    public AttributeConstraint(String anchorList, AttributeType attribute) {
        this(anchorList, attribute, 0);
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

        // Initialize tally appropriately for the sort of computation we'll do
        long tally = 0;  // Start by assuming we're averaging
        if (attribute == AttributeType.BOTTOM ||
            attribute == AttributeType.RIGHT ||
            attribute == AttributeType.WIDTH ||
            attribute == AttributeType.HEIGHT) {
            // We'll want to find the maximum of these values
            tally = Integer.MIN_VALUE;
        }
        else if (attribute == AttributeType.TOP ||
                 attribute == AttributeType.LEFT) {
            // We'll want to find the minimum of these values
            tally = Integer.MAX_VALUE;
        }

        // Tally up the attributes of the components in which we're interested
        for (ListIterator iter = dependencies.listIterator();
             iter.hasNext(); ) {
            Attribute anchorAttr = (Attribute)iter.next();
            int curValue = attributes.getValue(anchorAttr);

            // Merge the new value in the appropriate manner for the type
            if (attribute == AttributeType.BOTTOM ||
                attribute == AttributeType.RIGHT ||
                attribute == AttributeType.WIDTH ||
                attribute == AttributeType.HEIGHT) {
                tally = Math.max(tally, curValue);
            }
            else if (attribute == AttributeType.TOP ||
                     attribute == AttributeType.LEFT) {
                tally = Math.min(tally, curValue);
            }
            else {
                tally += curValue;
            }
        }

        // If we're finding the average of an attribute, now's the time
        if (attribute == AttributeType.HORIZONTAL_CENTER ||
            attribute == AttributeType.VERTICAL_CENTER) {
            tally /= dependencies.size();
        }

        // Finally, return our composite attribute, with proper offset
        return (int)tally + offset;
    }

    
    /**
     * Provide a textual representation of the constraint for debugging
     * purposes.
     * 
     * @return the description of this attribute constraint.
     **/
    public String toString() {
        return "AttributeConstraint: {anchors=" + anchorList + "; attribute=" +
            attribute + "; offset=" + offset + '}';
    }
}

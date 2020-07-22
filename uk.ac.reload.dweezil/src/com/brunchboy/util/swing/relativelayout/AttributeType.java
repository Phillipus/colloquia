/*
 * AttributeType.java
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


/**
 * A typesafe enumeration of the types of attributes that can be associated
 * with components within a {@link RelativeLayout}.  This class is immutable,
 * and thus may be safely shared without concern for threading behavior. <p> 
 * 
 * This is an abstract class; there is a concrete subclass for each defined
 * attribute type. The concrete subclasses know how to derive their own value
 * from other available attributes. This refinement is an illustration of how
 * typesafe enumerations can grow into full-fledged, polymorphic tools. <p>
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: AttributeType.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public abstract class AttributeType {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: AttributeType.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Keeps track of all known attributes. This will get built when
     * the class is loaded, during the process of instatiating the public
     * static constant values provided by the class.
     **/
    private static Set allTypes = new HashSet();


    /**
     * Map for looking up attributes by name, used by the getInstance static
     * factory. Keys in the map are names, values are the corresponding
     * AttributeTypes. This will get built when * the class is loaded, during
     * the process of instatiating the public static constant values provided
     * by the class.
     **/
    private static Map typeMap = new HashMap();

    
    /**
     * Keeps track of all known attributes, grouped by axis. Keys in the map
     * are AttributeAxis objects, while values are the Set of all
     * AttributeTypes associated with that axis. This will get built when
     * the class is loaded, during the process of instatiating the public
     * static constant values provided by the class.
     **/
    private static Map axisTypes = new HashMap();


    /**
     * The name of this attribute type.
     **/
    private final String name;

    /**
     * Return name of this attribute type.
     * 
     * @return the name by which this type is known.
     **/
    public String getName() {
        return name;
    }

    
    /**
     * The axis on which it is defined.
     **/
    private final AttributeAxis axis;

    /**
     * Return the axis on which this attribute type is defined.
     * 
     * @return the axis affected by this attribute type.
     **/
    public AttributeAxis getAxis() {
        return axis;
    }


    /**
     * Protected constructor prevents instantiation of this class. Also builds
     * the sets per-Axis attributes at class-load time. <p>
     * 
     * @param name the name of the attribute type to create.
     * 
     * @param axis the axis on which this attribute type is defined.
     * 
     * @throws NullPointerException if either parameter is null.
     **/
    protected AttributeType(String name, AttributeAxis axis) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (axis == null) {
            throw new NullPointerException("axis must not be null");
        }
        
        this.name = name;
        this.axis = axis;

        // Add this new attribute type to the set of all attributes, and
        // the name map.
        allTypes.add(this);
        typeMap.put(name, this);

        // Look up the set of attributes associated with this axis, add there
        Set axisSet = (Set)axisTypes.get(axis);
        if (axisSet == null) {
            // First time we've seen this axis, so create a new set for it
            axisSet = new HashSet();
            axisTypes.put(axis, axisSet);
        }

        // Record this new attribute type in its axis' set
        axisSet.add(this);
    }


    /**
     * Static factory that looks up an attribute type by name. <p>
     * 
     * @param name the name of the desired attribute type.
     * 
     * @return the corresponding instance.
     * 
     * @throws NoSuchElementException if the desired instance doesn't exist.
     **/
    public static AttributeType getInstance(String name) {
        AttributeType result = (AttributeType)typeMap.get(name);
        if (result == null) {
            throw new NoSuchElementException(name);
        }
        return result;
    }


    /**
     * Compute the value of this attribute given other attributes from which
     * it can be derived. Concrete subclasses must implement this method. <p>
     * 
     * @param specs source of other attributes for deriving this value.
     * 
     * @param minimumSize indicates whether, should we need to query the
     *        component itself for sizing information, we should ask for
     *        the minimum size as opposed to the preferred size.
     * 
     * @return the derived value, if possible.
     * 
     * @throws IllegalStateException if there are not enough values from which
     *         to derive ours.
     **/
    public abstract int deriveValue(ComponentSpecifications specs,
                                    boolean minimumSize);


    /**
     * Attribute type representing the left edge of a component.
     **/
    public static final AttributeType LEFT =
        new AttributeType("left", AttributeAxis.HORIZONTAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(RIGHT)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(RIGHT) -
                            specs.getAttributeValue(WIDTH);
                    }
                    if (specs.hasConstraint(HORIZONTAL_CENTER)) {
                        return 2 * specs.getAttributeValue(HORIZONTAL_CENTER) -
                            specs.getAttributeValue(RIGHT);
                    }
                    return specs.getAttributeValue(RIGHT) -
                        specs.getComponentWidth(minimumSize);
                }
                if (specs.hasConstraint(HORIZONTAL_CENTER)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(HORIZONTAL_CENTER) -
                            specs.getAttributeValue(WIDTH) / 2;
                    }
                    return specs.getAttributeValue(HORIZONTAL_CENTER) -
                        specs.getComponentWidth(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the right edge of a component.
     **/
    public static final AttributeType RIGHT =
        new AttributeType("right", AttributeAxis.HORIZONTAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(LEFT)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(LEFT) +
                            specs.getAttributeValue(WIDTH);
                    }
                    if (specs.hasConstraint(HORIZONTAL_CENTER)) {
                        return 2 * specs.getAttributeValue(HORIZONTAL_CENTER) -
                            specs.getAttributeValue(LEFT);
                    }
                    return specs.getAttributeValue(LEFT) +
                        specs.getComponentWidth(minimumSize);
                }
                if (specs.hasConstraint(HORIZONTAL_CENTER)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(HORIZONTAL_CENTER) +
                            specs.getAttributeValue(WIDTH) / 2;
                    }
                    return specs.getAttributeValue(HORIZONTAL_CENTER) +
                        specs.getComponentWidth(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the horizontal center of a component.
     **/
    public static final AttributeType HORIZONTAL_CENTER =
        new AttributeType("horizontalCenter", AttributeAxis.HORIZONTAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(LEFT)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(LEFT) +
                            specs.getAttributeValue(WIDTH) / 2;
                    }
                    if (specs.hasConstraint(RIGHT)) {
                        return (specs.getAttributeValue(LEFT) +
                                specs.getAttributeValue(RIGHT)) / 2;
                    }
                    return specs.getAttributeValue(LEFT) +
                        specs.getComponentWidth(minimumSize) / 2;
                }
                if (specs.hasConstraint(RIGHT)) {
                    if (specs.hasConstraint(WIDTH)) {
                        return specs.getAttributeValue(RIGHT) -
                            specs.getAttributeValue(WIDTH) / 2;
                    }
                    return specs.getAttributeValue(RIGHT) -
                        specs.getComponentWidth(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the width of a component.
     **/
    public static final AttributeType WIDTH =
        new AttributeType("width", AttributeAxis.HORIZONTAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(LEFT)) {
                    if (specs.hasConstraint(RIGHT)) {
                        return specs.getAttributeValue(RIGHT) -
                               specs.getAttributeValue(LEFT);
                    }
                    if (specs.hasConstraint(HORIZONTAL_CENTER)) {
                        return 2 * (specs.getAttributeValue(HORIZONTAL_CENTER)-
                                    specs.getAttributeValue(LEFT));
                    }
                }
                if (specs.hasConstraint(RIGHT) &&
                    specs.hasConstraint(HORIZONTAL_CENTER)) {
                    return 2 * (specs.getAttributeValue(RIGHT) -
                                specs.getAttributeValue(HORIZONTAL_CENTER));
                }
                return specs.getComponentWidth(minimumSize);
            }
        };


    /**
     * Attribute type representing the top edge of a component.
     **/
    public static final AttributeType TOP =
        new AttributeType("top", AttributeAxis.VERTICAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(BOTTOM)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(BOTTOM) -
                            specs.getAttributeValue(HEIGHT);
                    }
                    if (specs.hasConstraint(VERTICAL_CENTER)) {
                        return 2 * specs.getAttributeValue(VERTICAL_CENTER) -
                            specs.getAttributeValue(BOTTOM);
                    }
                    return specs.getAttributeValue(BOTTOM) -
                        specs.getComponentHeight(minimumSize);
                }
                if (specs.hasConstraint(VERTICAL_CENTER)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(VERTICAL_CENTER) -
                            specs.getAttributeValue(HEIGHT) / 2;
                    }
                    return specs.getAttributeValue(VERTICAL_CENTER) -
                        specs.getComponentHeight(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the bottom edge of a component.
     **/
    public static final AttributeType BOTTOM =
        new AttributeType("bottom", AttributeAxis.VERTICAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(TOP)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(TOP) +
                            specs.getAttributeValue(HEIGHT);
                    }
                    if (specs.hasConstraint(VERTICAL_CENTER)) {
                        return 2 * specs.getAttributeValue(VERTICAL_CENTER) -
                            specs.getAttributeValue(TOP);
                    }
                    return specs.getAttributeValue(TOP) +
                        specs.getComponentHeight(minimumSize);
                }
                if (specs.hasConstraint(VERTICAL_CENTER)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(VERTICAL_CENTER) +
                            specs.getAttributeValue(HEIGHT) / 2;
                    }
                    return specs.getAttributeValue(VERTICAL_CENTER) +
                        specs.getComponentHeight(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the vertical center of a component.
     **/
    public static final AttributeType VERTICAL_CENTER =
        new AttributeType("verticalCenter", AttributeAxis.VERTICAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(TOP)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(TOP) +
                            specs.getAttributeValue(HEIGHT) / 2;
                    }
                    if (specs.hasConstraint(BOTTOM)) {
                        return (specs.getAttributeValue(TOP) +
                                specs.getAttributeValue(BOTTOM)) / 2;
                    }
                    return specs.getAttributeValue(TOP) +
                        specs.getComponentHeight(minimumSize) / 2;
                }
                if (specs.hasConstraint(BOTTOM)) {
                    if (specs.hasConstraint(HEIGHT)) {
                        return specs.getAttributeValue(BOTTOM) -
                            specs.getAttributeValue(HEIGHT) / 2;
                    }
                    return specs.getAttributeValue(BOTTOM) -
                        specs.getComponentHeight(minimumSize) / 2;
                }
                throw new IllegalStateException(specs.getComponentName() +
                              ": Underconstrained attribute, " + this);
            }
        };


    /**
     * Attribute type representing the height of a component.
     **/
    public static final AttributeType HEIGHT =
        new AttributeType("height", AttributeAxis.VERTICAL) {

            public int deriveValue(ComponentSpecifications specs,
                                   boolean minimumSize) {
                if (specs.hasConstraint(TOP)) {
                    if (specs.hasConstraint(BOTTOM)) {
                        return specs.getAttributeValue(BOTTOM) -
                               specs.getAttributeValue(TOP);
                    }
                    if (specs.hasConstraint(VERTICAL_CENTER)) {
                        return 2 * (specs.getAttributeValue(VERTICAL_CENTER)-
                                    specs.getAttributeValue(TOP));
                    }
                }
                if (specs.hasConstraint(BOTTOM) &&
                    specs.hasConstraint(VERTICAL_CENTER)) {
                    return 2 * (specs.getAttributeValue(BOTTOM) -
                                specs.getAttributeValue(VERTICAL_CENTER));
                }
                return specs.getComponentHeight(minimumSize);
            }
        };


    /**
     * Return the set of all known attribute types.
     * 
     * @return the attributes which exist; these are the only instances of
     *         this class which will ever exist. Cannot be modified.
     **/
    public static Set getAllTypes() {
        return Collections.unmodifiableSet(allTypes);
    }


    /**
     * Return the set of attribute types associated with an axis.
     * 
     * @param axis the axis of interest.
     * 
     * @return the attribute types which are defined on that axis. May be
     *         empty, but will never be <code>null</code>. Cannot be modified.
     **/
    public static Set getAxisTypes(AttributeAxis axis) {
        Set axisSet = (Set)axisTypes.get(axis);
        
        if (axisSet == null) {
            return Collections.EMPTY_SET;
        }

        return Collections.unmodifiableSet(axisSet);
    }


    /**
     * Provide a textual representation of the attribute type for debugging
     * purposes.
     * 
     * @return the description of this attribute type.
     **/
    public String toString() {
        return "AttributeType: " + name + " (" + axis + ')';
    }

}

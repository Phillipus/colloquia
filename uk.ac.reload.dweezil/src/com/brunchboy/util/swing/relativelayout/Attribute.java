/*
 * Attribute.java
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


/**
 * A simple, immutable class that represents a particular attribute within
 * a {@link RelativeLayout}. It tracks both the name of the component for which
 * the attribute is defined, and the type of the attribute. <p>
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: Attribute.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class Attribute {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: Attribute.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * The name of the component with which this attribute is associated.
     **/
    private final String component;


    /**
     * Return the name of the component assoicated with this attribute.
     * 
     * @return component for which this attribute is defined.
     **/
    public String getComponent() {
        return component;
    }


    /**
     * The type of attribute being recorded.
     **/
    private final AttributeType type;

    /**
     * Return the type of attribute being represented.
     *
     * @return the attribute type.
     **/
    public AttributeType getType() {
        return type;
    }


    /**
     * Hashcode is precomputed for speed of lookup later, as we know these
     * will be stored in hash tables.
     **/
    private final int cachedHashCode;
    

    /**
     * Create an attribute description.
     *
     * @param component the name of the component with which this attribute
     *        is associated.
     * 
     * @param type the type of attribute being represented.
     **/
    public Attribute(String component, AttributeType type) {
        this.component = component.intern();  // Intern for speed of comparison
        this.type = type;
        cachedHashCode = component.hashCode() ^ type.hashCode();
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The <code>equals</code> method implements an equivalence relation:
     * <ul>
     * <li>It is <i>reflexive</i>: for any reference value <code>x</code>,
     *     <code>x.equals(x)</code> should return <code>true</code>.
     * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
     *     <code>y</code>, <code>x.equals(y)</code> should return
     *     <code>true</code> if and only if <code>y.equals(x)</code> returns
     *     <code>true</code>.
     * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
     *     <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
     *     returns  <code>true</code> and <code>y.equals(z)</code> returns
     *     <code>true</code>, then <code>x.equals(z)</code> should return
     *     <code>true</code>.
     * <li>It is <i>consistent</i>: for any reference values <code>x</code>
     *     and <code>y</code>, multiple invocations of <tt>x.equals(y)</tt>
     *     consistently return <code>true</code> or consistently return
     *     <code>false</code>, provided no information used in
     *     <code>equals</code> comparisons on the object is modified.
     * <li>For any non-null reference value <code>x</code>,
     *     <code>x.equals(null)</code> should return <code>false</code>.
     * </ul>
     * <p>
     * The <tt>equals</tt> method for class <code>Attribute</code> checks that
     * the two attributes refer to the same component name and are of the same
     * type.
     * <p>
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     *          argument; <code>false</code> otherwise.
     **/
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Attribute) {
            Attribute other = (Attribute)obj;
            // Because we've interned the component field, we can test for
            // object equality rather than using String.equals()
            return (component == other.component) && (type == other.type);
        }

        return false;
    }


    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     * <p>
     * The general contract of <code>hashCode</code> is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the <tt>hashCode</tt> method
     *     must consistently return the same integer, provided no information
     *     used in <tt>equals</tt> comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the <tt>equals(Object)</tt>
     *     method, then calling the <code>hashCode</code> method on each of
     *     the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the <tt>hashCode</tt> method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hashtables.
     * </ul>
     * <p>
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.util.Hashtable
     */
    public int hashCode() {
        return cachedHashCode;
    }


    /**
     * Provide a textual representation of the attribute type for debugging
     * purposes.
     * 
     * @return the description of this attribute type.
     **/
    public String toString() {
        return "Attribute: {Component: \"" + component + "\", " + type + '}';
    }
}

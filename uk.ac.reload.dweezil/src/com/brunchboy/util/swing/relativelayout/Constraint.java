/*
 * Constraint.java
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
 * Specifies the common features of a constraint, which
 * defines a means of calculating an attribute of a component. <p>
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: Constraint.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public interface Constraint {

    /**
     * Return the attributes on which this constraint depends. <p>
     * 
     * @return a list of {@link Attribute}s which must be resolved before this
     *         constraint can be evaluated.
     **/
    public List getDependencies();


    /**
     * Compute the value of the constraint, given the specifications on
     * on which it is based. Any dependencies must have been resolved
     * prior to calling this method, or the method will fail. <p>
     * 
     * @param attributes provides read access to all existing component
     *        attributes, for use in evaluating this constraint..
     * 
     * @return the value represented by this constraint, assuming any
     *         attributes of the anchor components on which it depends have
     *         been resolved already.
     * 
     * @throws IllegalStateException if any dependencies are not yet resolved.
     **/
    public int getValue(AttributeSource attributes);
}

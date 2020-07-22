/*
 * AttributeAxis.java
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


import java.util.NoSuchElementException;


/**
 * A typesafe enumeration of the axes on which attributes exist
 * within a {@link RelativeLayout}. This class is immutable, and thus may
 * be safely shared without concern for threading behavior. <p>
 *
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: AttributeAxis.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class AttributeAxis {

    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: AttributeAxis.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * The name of this attribute type.
     **/
    private final String name;

    /**
     * Private constructor prevents instantiation of this class. All access to
     * instances will be through the constants.
     **/
    private AttributeAxis(String name) {
        this.name = name;
    }

    /**
     * Axis on which the attributes left, right, width and horizontalCenter
     * apply.
     **/
    public static final AttributeAxis HORIZONTAL =
        new AttributeAxis("horizontal");

    /**
     * Axis on which the attributes top, bottom,height and verticalCenter
     * apply.
     **/
    public static final AttributeAxis VERTICAL =
        new AttributeAxis("vertical");


    /**
     * Static factory method to look up an axis by name. <p>
     * 
     * @param name the name of the desired axis.
     * 
     * @return the corresponding instance.
     * 
     * @throws NoSuchElementException if the desired axis doesn't exist.
     **/
    public static AttributeAxis getInstance(String name) {
        if (name.equals("horizontal")) {
            return HORIZONTAL;
        }
        else if (name.equals("vertical")) {
            return VERTICAL;
        }

        throw new NoSuchElementException(name);
    }


    /**
     * Provide a textual representation of the axis for debugging purposes.
     * 
     * @return the description of this axis.
     **/
    public String toString() {
        return "AttributeAxis: " + name;
    }
}


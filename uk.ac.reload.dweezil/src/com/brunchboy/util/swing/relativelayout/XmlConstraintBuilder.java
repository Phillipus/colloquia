/*
 * XmlConstraintBuilder.java
 *
 * Created on Mon May 20 2002
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

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.xml.sax.*;

import java.io.*;
import java.util.*;


/**
 * A helper class providing the ability to add constraints to a
 * {@link RelativeLayout} by parsing XML definition files. <p>
 *
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: XmlConstraintBuilder.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class XmlConstraintBuilder {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: XmlConstraintBuilder.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";

    /**
     * The JDOM SAX-based XML parse tree builder.
     **/
    private final SAXBuilder builder = new SAXBuilder(true);

    /**
     * The public identifier by which our constraint-set DTD can be resolved.
     **/
    public static final String CONSTRAINT_SET_DTD_PUBLIC =
        "-//Brunch Boy Design//RelativeLayout Constraint Set DTD 1.0//EN";

    /**
     * The system identifier by which our constraint-set DTD is loaded.
     **/
    public static final String CONSTRAINT_SET_DTD_SYSTEM =
        "constraint-set.dtd";

    /**
     * A simple class that teaches our XML parser where it can find our DTD.
     **/
    private static class DtdResolver implements EntityResolver {
        /**
         * Handle resolution of our own special constraint-set DTD.
         *
         * <p>The Parser will call this method before opening any external
         * entity except the top-level document entity (including the
         * external DTD subset, external entities referenced within the
         * DTD, and external entities referenced within the document
         * element): the application may request that the parser resolve
         * the entity itself, that it use an alternative URI, or that it
         * use an entirely different input source.</p>
         *
         * <p>Application writers can use this method to redirect external
         * system identifiers to secure and/or local URIs, to look up
         * public identifiers in a catalogue, or to read an entity from a
         * database or other input source (including, for example, a dialog
         * box).</p>
         *
         * <p>If the system identifier is a URL, the SAX parser must
         * resolve it fully before reporting it to the application.</p>
         *
         * @param publicId The public identifier of the external entity
         *        being referenced, or null if none was supplied.
         * 
         * @param systemId The system identifier of the external entity
         *        being referenced.
         * 
         * @return An InputSource object describing the new input source,
         *         or null to request that the parser open a regular
         *         URI connection to the system identifier.
         * 
         * @exception org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * 
         * @exception java.io.IOException A Java-specific IO exception,
         *            possibly the result of creating a new InputStream
         *            or Reader for the InputSource.
         * 
         * @see org.xml.sax.InputSource
         */
        public InputSource resolveEntity(String publicId, String systemId)
        {
            if (CONSTRAINT_SET_DTD_PUBLIC.equals(publicId)) {
                InputSource ourDtd = new InputSource(
                    this.getClass().getResourceAsStream(
                        CONSTRAINT_SET_DTD_SYSTEM));
                ourDtd.setPublicId(publicId);
                ourDtd.setSystemId(CONSTRAINT_SET_DTD_SYSTEM);
                return ourDtd;
            }
            return null;
        }
    }


    /**
     * An exception subclass with which we'll wrap any exceptions thrown in
     * parsing the constraints specifications, to shield our clients from
     * details about the libraries we use, unless they want to know about them.
     **/
    public static class ParseException extends Exception {
        /**
         * Keeps track of the underlying cause, if any.
         **/
        protected Throwable cause;


        /**
         * Record the message and cause of a parse exception. <p>
         *
         * @param message a brief description of the problem.
         * 
         * @param cause the underlying problem that led to this one, if any.
         **/    
        public ParseException(String message, Throwable cause)  {
            super(message);    
            this.cause = cause;
        }    


        /**
         * Returns the underlying cause of this exception, or <code>null</code>
         * if there was none. <p>
         * 
         * @return the problem that led to this exception, if any.
         */
        public Throwable getCause()  {
            return cause;             
        }
    }


    /**
     * Sets up the necessary resources for parsing XML specifications. You can
     * reuse an instance multiple times, but should not share one between
     * threads. <p>
     **/
    public XmlConstraintBuilder() {
        builder.setEntityResolver(new DtdResolver());
    }


    /**
     * Utility method to collect all the referenced target components from a
     * toAttribute constraint. They can come from either an attribute or
     * nested elements. <p>
     * 
     * @param target the toAttribute element being processed.
     * 
     * @return the comma-delimited list of component names.
     **/
    private String getReferences(Element target) {
        StringBuffer result = new StringBuffer();

        // First check if they supplied a reference attribute
        if (target.getAttributeValue("reference") != null) {
            result.append(target.getAttributeValue("reference"));
        }

        // Now check if they supplied any nested reference elements
        ListIterator references =
            target.getChildren("reference").listIterator();
        while (references.hasNext()) {
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(
                ((Element)references.next()).getAttributeValue("name"));
        }

        return result.toString();
    }


    /**
     * Utility method called once we have the element corresponding to a
     * constraint that needs to be created. Creates the corresponding object
     * and adds it to the layout. <p>
     * 
     * @param component the name of the component being constrained.
     * 
     * @param constraint the XML element representing the constraint.
     * 
     * @param layout the layout to which the constraints should be added.
     * 
     * @throws DataConversionException if there's a problem parsing an
     *         attribute.
     * 
     * @throws ParseException if there's a problem parsing the
     *         constraint.
     **/
    protected void addComponentConstraint(String component, Element constraint,
                                          RelativeLayout layout)
        throws DataConversionException, ParseException
    {
        // Figure out the type of attribute being constrained
        AttributeType type = AttributeType.getInstance(constraint.getName());

        // The only child is the target
        Element target = (Element)constraint.getChildren().get(0);
        
        // See what kind of constraint this is
        if (target.getName().equals("toAttribute")) {
            // We're building an AttributeConstraint
            int offset = 0;
            if (target.getAttribute("offset") != null) {
                offset = target.getAttribute("offset").getIntValue();
            }
            AttributeType targetAttribute = AttributeType.getInstance(
                target.getAttributeValue("attribute"));
            layout.addConstraint(component, type, new AttributeConstraint(
                getReferences(target), targetAttribute, offset));
        }
        else if (target.getName().equals("toAxis")) {
            // We're building an AxisConstraint
            layout.addConstraint(component,type, new AxisConstraint(
                target.getAttributeValue("reference"),
                AttributeAxis.getInstance(target.getAttributeValue("axis")),
                target.getAttribute("fraction").getDoubleValue()));
        }
        else throw new ParseException("Unrecognized constraint type: " +
                                      target.getName(), null);
    }

    /**
     * Utility method to do the work of walking the document tree generated by
     * parsing the supplied XML constraint specifications, and turning these
     * into actual constraints to be added to the layout. <p>
     * 
     * @param document the parsed constraint document.
     * 
     * @param layout the layout to which the constraints should be added.
     * 
     * @throws DataConversionException if there's a problem parsing an
     *         attribute.
     * 
     * @throws ParseException if there's a problem parsing the
     *         constraint.
     **/
    protected void addConstraints(Document document, RelativeLayout layout)
        throws DataConversionException, ParseException
    {
        ListIterator components =
            document.getRootElement().getChildren("constrain").listIterator();
        while (components.hasNext()) {
            Element component = (Element)components.next();
            ListIterator constraints = component.getChildren().listIterator();
            while (constraints.hasNext()) {
                addComponentConstraint(component.getAttributeValue("name"),
                                       (Element)constraints.next(), layout);
            }
        }
    }


    /**
     * Add constraints parsed from a file. <p>
     * 
     * @param file the file containing XML constraint specifications.
     * 
     * @param layout the layout to which the parsed constraints should be
     *        added.
     * 
     * @throws ParseException if there is a problem parsing the constraints.
     **/
    public void addConstraints(File file, RelativeLayout layout)
        throws ParseException
    {
        try {
            addConstraints(builder.build(file), layout);
        }
        catch (Exception e) {
            throw new ParseException("Problem parsing " + file + ": " + e, e);
        }
    }

    /**
     * Add constraints parsed from an input stream. <p>
     * 
     * @param stream the input stream containing XML constraint specifications.
     * 
     * @param layout the layout to which the parsed constraints should be
     *        added.
     * 
     * @throws ParseException if there is a problem parsing the constraints.
     **/
    public void addConstraints(InputStream stream, RelativeLayout layout)
        throws ParseException
    {
        try {
            addConstraints(builder.build(stream), layout);
        }
        catch (Exception e) {
            throw new ParseException("Problem parsing " + stream + ": " + e,
				     e);
        }
    }

}

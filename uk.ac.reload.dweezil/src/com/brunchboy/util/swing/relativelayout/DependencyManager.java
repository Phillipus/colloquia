/*
 * DependencyManager.java
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

import	java.util.*;


/**
 * Keeps track of all dependencies that have been declared or
 * derived in setting up the component constraints in the
 * {@link RelativeLayout}, and can sort them in the proper order for resolution
 * when it's time to lay the components out.
 * 
 * @author  James Elliott, jim@brunchboy.com
 * @version $Id: DependencyManager.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $
 **/
public class DependencyManager {
    
    /**
     * Provides access to the CVS version of this class.
     **/
    public static final String VERSION =
	"$Id: DependencyManager.java,v 1.1 2005/03/14 17:08:22 phillipus Exp $";


    /**
     * Keeps track of the set of nodes that depend on this one. The fundamental
     * building block of the dependency tree. <p>
     **/
    private static class Node {
        
        /**
         * Keep track of the attribute associated with this node.
         **/
        final Attribute attribute;

        /**
         * The list of attributes which depend on the value of this node.
         **/
        List dependents = new LinkedList();

        /**
         * Reference counter also used during graph traversal for sorting and
         * validation of the dependency tree.
         **/
	int refCount;

        /**
         * Constructor sets the immutable attribute association.
         * 
         * @param name the attribute associated with this node.
         **/
	Node(Attribute attribute) {
            this.attribute = attribute;
	}

        /**
         * Checks whether this node has the specified dependent.
         * 
         * @param depandent the name of the node being looked for.
         **/
        boolean hasDependent(Attribute dependent) {
            return dependents.contains(dependent);
        }


        /**
         * Returns the number of dependents of this node.
         * 
         * @return the number of nodes whose value depends on this one.
         **/
	int size() {
            return dependents.size();
	}


        /**
         * Returns the specified dependent of this node.
         * 
         * @param index identifies the desired dependent.
         * 
         * @throws IndexOutOfBoundsException if the requested dependent does
         *         not exist.
         **/
	Attribute getDependent(int index) {
            return (Attribute)(dependents.get(index));
	}


        /**
         * Adds a dependent of this node.
         * 
         * @param attribute attribute whose value depends on this node's value.
         **/
	void addDependent(Attribute attribute) {
            dependents.add(attribute);
	}
    }


    /**
     * Keeps track of the known dependencies. Keys are node names, and values
     * are {@link Node}s.
     **/
    private Map nodes = new HashMap();


    /**
     * The name of the special Component corresponding to the container in
     * which the {@link RelativeLayout} is managing components. Dependencies
     * associated with this component are allowed to be roots in the dependency
     * tree because its dimensions are known.
     **/
    public static final String ROOT_NAME = "_container";
    

    /**
     * Indicates whether we are known to have a valid set of dependencies.
     * Is set after validation, and cleared whenever dependencies are changed.
     **/
    private boolean valid = false;


    /**
     * Contains the list of root nodes found during validation. Used in
     * priming the sorted dependency list.
     * 
     * @see #validate
     **/
    private List roots;


    /**
     * Contains the list of dependencies sorted in the order in which they
     * must be resolved. Has a non-<code>null</code> value only after the
     * {@link #sort} method is called, and is cleared whenever dependencies
     * are changed.
     **/
    private List sortedNodes;


    /**
     * Find the specified node. If it does not yet exist, create it as a new
     * node and add it to the map of known nodes.
     * 
     * @param attribute the attribute whose dependency node is to be looked up.
     * 
     * @return the (possibly new) node associated with that attribute.
     **/
    private Node getNode(Attribute attribute) {
        Node node = (Node)(nodes.get(attribute));
        if (node == null) {  // Need to create it
            node = new Node(attribute);
            nodes.put(attribute, node);
        }
        return node;
    }


    /**
     * Record the fact that dependencies have changed, and that we are no
     * longer in a valid state; also discards any sorted list of dependencies
     * that might exist. <p>
     **/
    private void invalidate() {
        valid = false;
        sortedNodes = null;
        roots = null;
    }


    /**
     * Records a dependency between attributes.
     * 
     * @param dependent the attribute which depends on another.
     * 
     * @param anchor the attribute whose value controls the dependent.
     * 
     * @throws IllegalStateException if there is already a dependency between
     *         these attributes.
     **/
    public void add(Attribute dependent, Attribute anchor)
        throws IllegalStateException
    {
        Node anchorNode = getNode(anchor);
        getNode(dependent);  // Create the node if it's not already there
		
        if (anchorNode.hasDependent(dependent)) {
            throw new IllegalStateException(
                dependent + " already dependent on " + anchor);
        }
        anchorNode.addDependent(dependent);
    }


    /**
     * Resets the reference count for each node prior to validation and
     * sorting. <p>
     **/
    private void resetNodes() {
	for (Iterator iter = nodes.values().iterator(); iter.hasNext(); ) {
            Node node = (Node)iter.next();
            node.refCount = 0;
        }
    }


    /**
     * Returns a List contains all the root nodes, i.e. those which depend on
     * no other node. These had all better be attributes of the container, or
     * we have an issue with unresolved dependencies. <p>
     * 
     * @throws IllegalStateException if there are cycles detected in the
     *         dependency tree.
     **/
    private List getRootNodes() {
        resetNodes();  // Prepare for a graph traversal
		
	// Count references to each node.
	for (Iterator iter = nodes.values().iterator(); iter.hasNext(); ) {
            Node anchorNode = (Node)iter.next();
            for (int i=0; i < anchorNode.size(); i++) {
                Attribute dependentAttribute = anchorNode.getDependent(i);
                if (dependentAttribute.equals(anchorNode.attribute)) {
		    // We've detected a cycle    		
                    throw new IllegalStateException(anchorNode +
                                                    " depends on itself");
                }
                ((Node)nodes.get(dependentAttribute)).refCount++;
            }
        }

        // Return list of nodes with zero reference count.
        List roots = new ArrayList();
	for (Iterator iter = nodes.values().iterator(); iter.hasNext(); ) {
            Node candidate = (Node)iter.next();
            if (candidate.refCount == 0) {
                roots.add(candidate);
            }
        }
        return roots;
    }


    /**
     * Recursively follows all paths from a node, checking for length
     * violations. The longest legal path is the total number of nodes.
     * If a path is longer than that, at least one node must have been visited
     * multiple times, proving the existence of a cycle.
     * 
     * @param node the node to be tested.
     * 
     * @param depth the current depth of recursion, used to detect cycles.
     * 
     * @throws IllegalStateException if a cycle is detected.
     **/
    private void checkNodeForCycles(Node node, int depth)
    {
        if (++depth > nodes.size()) {
            throw new IllegalStateException("Cycle detected for attribute " +
                                            node.attribute);
        }

        for (int i=0; i<node.size(); i++) {
            Attribute dependentAttribute = node.getDependent(i);
            Node dependentNode = (Node)nodes.get(dependentAttribute);
            checkNodeForCycles(dependentNode, depth);
        }
    }


    /**
     * Validates the set of dependencies by verifying that there are no cycles
     * and that every dependency can be resolved. Normal return indicates the
     * dependency tree is valid. As a side effect, sets up the reference counts
     * in the nodes, and builds the list of root nodes. <p>
     * 
     * @throws IllegalStateException if a cycle or unresolvable dependency
     *         is detected.
     **/
    public void validate() {
        // Make sure any root nodes are associated with the container.
	roots = getRootNodes();
        //if (roots.isEmpty()) {
        //    throw new IllegalStateException("Empty dependency tree");
        //}
		
        for (Iterator iter = roots.iterator(); iter.hasNext(); ) {
            Node aRoot = (Node)iter.next();
            if (!aRoot.attribute.getComponent().equals(ROOT_NAME)) {
                throw new IllegalStateException("Unresolvable dependency: " +
                                                aRoot.attribute);
            }
            checkNodeForCycles(aRoot, 0);
        }

        // If we get this far, everything is good.
        valid = true;
    }


    /**
     * Returns the list of dependent attributes, sorted in the order in which
     * they must be resolved. <p>
     *
     * If the sort has already been performed, its cached result is returned.
     * Otherwise, ensures that the graph is in a valid state (which, as a
     * side effect, sets up the reference counts needed by the sort). Then
     * recursively scans through resolved nodes, decrementing child reference
     * counts. When a child's reference count reaches zero, that child is
     * resolved; it is added to the List and its descendants are searched. <p>
     * 
     * @return the list of {@link Attribute}s that need to be resolved, in
     *         the order that they should be resolved.
     *
     * @throws IllegalStateException if there is a cycle or otherwise invalid
     *         dependency tree.
     **/
    public List sort() {
        // Can we take a shortcut?
        if (sortedNodes != null) {
            return sortedNodes;
        }

        // No. Start by making sure the dependency tree is valid, which sets
        // up the reference counts we need.
        if (!valid) {
            validate();
        }

        List result = new ArrayList(nodes.size());

        for (Iterator iter = roots.iterator(); iter.hasNext(); ) {
            Node aRoot = (Node)iter.next();
            recursiveSort(result, aRoot);
        }
		
        sortedNodes = Collections.unmodifiableList(result);
	return sortedNodes;
    }

    /**
     * Recursively sorts dependencies on a given node, adding
     * resolved {@link Attribute}s to a list in the order that they need to be
     * calculated. Assumes the supplied node has already been resolved,
     * and is on the list if it should be. <p>
     * 
     * @param sorted the list of sorted dependencies being generated.
     * 
     * @param anchorNode the node whose dependencies are to be sorted next.
     **/
    private void recursiveSort(List sorted, Node anchorNode) {
        for (int i=0; i < anchorNode.size(); i++) {
            Attribute dependentAttribute = anchorNode.getDependent(i);
            Node dependentNode = (Node)(nodes.get(dependentAttribute));
            if (--dependentNode.refCount == 0) {
                // This node has now become resolved
                sorted.add(dependentAttribute);
                recursiveSort(sorted, dependentNode);
            }
        }
    }
}


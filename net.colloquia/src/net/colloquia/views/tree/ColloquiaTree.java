package net.colloquia.views.tree;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.colloquia.ColloquiaConstants;
import net.colloquia.MainFrame;
import net.colloquia.comms.MessageInfo;
import net.colloquia.comms.tables.Inbox;
import net.colloquia.comms.tables.InboxListener;
import net.colloquia.datamodel.ComponentSelectionListener;
import net.colloquia.datamodel.ComponentTransferListener;
import net.colloquia.datamodel.ComponentTransferManager;
import net.colloquia.datamodel.DataModel;
import net.colloquia.datamodel.NodePair;
import net.colloquia.datamodel.entities.*;
import net.colloquia.views.ViewPanel;
import uk.ac.reload.dweezil.dnd.DNDUtils;
import uk.ac.reload.dweezil.dnd.DragObject;
import uk.ac.reload.dweezil.gui.tree.DweezilTree;
import uk.ac.reload.dweezil.gui.tree.DweezilTreeDragDropHandler;
import uk.ac.reload.dweezil.gui.tree.DweezilTreeModel;
import uk.ac.reload.dweezil.gui.tree.DweezilTreeNode;

/**
 * The Main Colloquia Tree
 *
 * @author Phillip Beauvoir
 * @version $Id: ColloquiaTree.java,v 1.1 2006/04/19 14:17:51 phillipus Exp $
 */
public class        ColloquiaTree
extends             DweezilTree
implements          TreeExpansionListener,
                    TreeSelectionListener,
                    ComponentTransferListener,
                    InboxListener
{
	/**
	 * This flag is set when we expand or contract a tree node.  Then we can save
	 * The tree state if needed
	 */
	protected boolean dirtyTree;

	public static final Color treeColor = new Color(255, 255, 255);

    private static final ColloquiaTree instance = new ColloquiaTree(); // This

    private ColloquiaTreeModel model;
    
    /**
     * The Drag and Drop Handler
     */
    private ColloquiaTreeDragDropHandler _dragdropHandler;

    // Whether certain tree nodes are shown
    public boolean peopleShown = true;
    public boolean resourcesShown = true;
    public boolean assignmentsShown = true;
    public boolean completedShown = true;
    public boolean futureShown = true;
    public boolean liveShown = true;

    private ColloquiaTreeNode currentNode;
    private int currentRow;

	protected DropTarget dropTarget;
	protected DragSource dragSource;

    private ColloquiaTree() {
        model = new ColloquiaTreeModel();
        setModel(model);

        setRowHeight(-1);        // Allow Cell Renderer to determine height
    	setRootVisible(false);   // Can't see root
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Tree cell renderer
        TreeRenderer cellRenderer = new TreeRenderer();
        setCellRenderer(cellRenderer);

        // Make sure lines are visible
        putClientProperty("JTree.lineStyle", "Angled");

        setBackground(treeColor);

        // Register Listeners
        addTreeExpansionListener(this);
        addTreeSelectionListener(this);
        ComponentTransferManager.addComponentTransferListener(this);
        Inbox.getInstance().addInboxListener(this);

        // Listen to mouse-clicks for the Popup Menu
        addMouseListener(new MouseAdapter() {
           // PC
           public void mouseReleased(MouseEvent e) {
               checkPopupTrigger(e);
           }

           // Mac
           public void mousePressed(MouseEvent e) {
               checkPopupTrigger(e);
           }
        });

        // Drag and Drop
        _dragdropHandler = new ColloquiaTreeDragDropHandler(this);

        // Tooltip support
		ToolTipManager.sharedInstance().registerComponent(this);
    }

    public static ColloquiaTree getInstance() {
        return instance;
    }

    //============================ DATA LISTENER  ===========================
    //==================== FROM THE DATA TO VIEW ADAPTER =======================

    /**
    * Makes a new, blank tree.
    */
    public void newTree() {
        model.newTree();
    }

    public ColloquiaTreeNode addComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        return model.addComponent(tc, parentGroup);
    }

    public void removeComponent(ColloquiaComponent tc, ColloquiaContainer group) {
        model.removeComponent(tc, group);
    }

    public void componentOrderChanged(Vector members, ColloquiaContainer group) {
        model.componentOrderChanged(members, group);
    }

    /**
    * Expand/Contract the Tree Branch.
    */
    public void expandBranch(ColloquiaContainer group, boolean expand) {
        DweezilTreeNode node = getNode(group);
        if(node == null) return;

        // If it's a leaf don't bother
        if(node.isLeaf()) return;

        // Get last node on this branch
        ColloquiaTreeNode lastNode = (ColloquiaTreeNode)node.getLastChild();

        // Work out the path
        TreePath currentPath = getSelectionPath();
        TreePath lastPath = new TreePath(lastNode.getPath());

        int row = getRowForPath(currentPath);

        if(expand) {
            for(int i = row; lastPath.equals(currentPath) == false; i++) {
                expandRow(i);
                currentPath = getPathForRow(i);
            }
        }
        else collapseRow(row);
    }

    //==========================================================================
    //========================= NODE SELECTION STUFF ===========================
    //==========================================================================

    public boolean nodeExists(ColloquiaComponent tc, ColloquiaContainer parent) {
        return getNode(tc, parent) != null;
    }

    /**
    * Select a node
    * This assumes that all ColloquiaContainers are unique
    */
    public ColloquiaTreeNode selectNode(ColloquiaComponent tc, ColloquiaContainer parent) {
        if(tc == null || parent == null) return null;
        ColloquiaTreeNode node = ensureNode(tc, parent);
        selectNode(node);
        return node;
    }

    /**
    * Gets a node on the tree given a tc and its parent
    * This assumes that all ColloquiaContainers are unique
    */
    private ColloquiaTreeNode getNode(ColloquiaComponent tc, ColloquiaContainer parent) {
        if(tc == null || parent == null) return null;

        DweezilTreeNode parentNode = getNode(parent);
        if(parentNode == null) return null;

        Enumeration nodes = parentNode.children();
        // Drill into and find it
        while(nodes.hasMoreElements()) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)nodes.nextElement();
            ColloquiaComponent theTC = node.getComponent();
            if(theTC == tc) return node;
        }

        return null;
    }

    /**
    * Returns the currently selected component
    */
    public ColloquiaComponent getSelectedComponent() {
        ColloquiaTreeNode node = (ColloquiaTreeNode)getSelectedNode();
        return node == null ? null : node.getComponent();
    }

    /**
    * Returns the currently selected component's parent group (or null)
    */
    public ColloquiaContainer getSelectedParent() {
        ColloquiaTreeNode node = (ColloquiaTreeNode)getSelectedNode();
        if(node != null) {
            ColloquiaTreeNode parentNode = (ColloquiaTreeNode)node.getParent();
            ColloquiaComponent tc = parentNode.getComponent();
            if(tc instanceof ColloquiaContainer) return (ColloquiaContainer)tc;
            else return null;
        }
        else return null;
    }

    public boolean isPeopleVisible(Activity A) {
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        return node == null ? true : node.showPeople;
    }

    public boolean isResourcesVisible(Activity A) {
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        return node == null ? true : node.showResources;
    }

    public boolean isAssignmentVisible(Activity A) {
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        return node == null ? true : node.showAssignment;
    }

    public void displayPeople(Activity A, boolean show) {
        if(A == null) return;
        Vector v = A.getAllPeople();
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        if(node != null) node.showPeople = show;
        __show(A, v, show);
    }

    public void displayResources(Activity A, boolean show) {
        if(A == null) return;
        Vector v = A.getResources();
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        if(node != null) node.showResources = show;
        __show(A, v, show);
    }

    public void displayAssignment(Activity A, boolean show) {
        if(A == null) return;
        Vector v = A.getAssignments();
        ColloquiaTreeNode node = (ColloquiaTreeNode)getNode(A);
        if(node != null) node.showAssignment = show;
        __show(A, v, show);
    }

    private void __show(Activity A, Vector v, boolean show) {
        for(int i = 0; i < v.size(); i++) {
        	ColloquiaComponent tc = (ColloquiaComponent)v.elementAt(i);
            if(show) addComponent(tc, A);
            else removeComponent(tc, A);
        }
    }

    /**
    * This will ensure that the node is visible on the tree - the user might
    * have hidden it and then want to jump to a message
    */
    public ColloquiaTreeNode ensureNode(ColloquiaComponent tc, ColloquiaContainer parent) {
        if(!parent.hasMember(tc)) return null;
    	ColloquiaTreeNode node = getNode(tc, parent);
        if(node == null) node = addComponent(tc, parent);
        return node;
    }

    // INBOX LISTENER
	public void newMessagesRecvd(MessageInfo[] mInfo) {
    	updateNodes();
    }


    // =========================== POPUP ================================

    /**
     * Check to see if we have triggered the popup menu.
     * @param e The MouseEvent that has been triggered.
     */
    protected void checkPopupTrigger(MouseEvent e) {
       if(e.isPopupTrigger()) {
           TreePath selPath = getPathForLocation(e.getX(), e.getY());
           if(selPath == null) return;
           // Select the node
           setSelectionPath(selPath);
           // Show the popup menu
           JPopupMenu treePopup = MainFrame.getInstance().mainMenu.getPopupMenu();
           treePopup.show(this, e.getX(), e.getY());
       }
    }


    // =========================================================================
    // ========================== TREE SELECTION LISTENER =================
    // This will analyse the selected node and send out info to Listeners
    // =========================================================================
    private TreeSelectionEvent event;

    /**
    * A selection has been made
    */
    public void valueChanged(TreeSelectionEvent event) {
        this.event = event;
        if(event == null) return;

        TreePath selPath = event.getPath();
        if(selPath == null) return;

        int row = getRowForPath(selPath);

        // A non-selection can occur as a result of a cut action
        if(row == -1) {
            setSelectionRow(currentRow == 0 ? 0 : currentRow-1);
            return;
        }

        currentRow = row;

        // Get selected node
        ColloquiaTreeNode selectedNode = (ColloquiaTreeNode)selPath.getLastPathComponent();
        if(selectedNode == null) return;

        // Store in History
        addToHistory(selectedNode);

        // Get selected component
        final ColloquiaComponent selectedComponent = selectedNode.getComponent();
        if(selectedComponent == null) return;

        // Get parent container
        ColloquiaTreeNode parentNode = (ColloquiaTreeNode)selectedNode.getParent();
        if(parentNode == null) return;
        final ColloquiaContainer parentGroup = (ColloquiaContainer)parentNode.getUserObject();
        if(parentGroup == null) return;

        // Get current Activity
        // We have selected a Activity
        final Activity currentActivity;
        if(selectedComponent instanceof Activity) currentActivity = (Activity)selectedComponent;
        // We have selected a sub-component of an Activity
        else if(parentGroup instanceof Activity) currentActivity = (Activity)parentGroup;
        // We have not selected a component in an Activity
        else currentActivity = null;

        fireSelection(selectedComponent, parentGroup, currentActivity);
    }

    public void reselectCurrentNode() {
    	valueChanged(event);
    }

    private void fireSelection(ColloquiaComponent selectedComponent, ColloquiaContainer parentGroup, Activity currentActivity) {
        // View Panel is treated specially for a head start
        ViewPanel.getInstance().setComponent(selectedComponent, parentGroup, currentActivity);
        // Component Selection listeners
        fireNodeSelected(selectedComponent, parentGroup, currentActivity);
    }

    private Vector componentListeners = new Vector();

    public synchronized void addComponentSelectionListener(ComponentSelectionListener listener) {
        if(!componentListeners.contains(listener)) componentListeners.addElement(listener);
    }

    public void removeComponentSelectionListener(ComponentSelectionListener listener) {
        componentListeners.removeElement(listener);
    }

    // Don't do this on a thread! Things get unsynched
    private void fireNodeSelected(ColloquiaComponent selectedComponent, ColloquiaContainer parentGroup, Activity currentActivity) {
        // Components
        for(int i = 0; i < componentListeners.size(); i++) {
            ComponentSelectionListener listener = (ComponentSelectionListener)componentListeners.elementAt(i);
            listener.componentSelected(selectedComponent, parentGroup, currentActivity);
        }
    }


	// ============================= Tree events ===============================
	
	/**
	 * Tree Expansion Listener
	 */
	public void treeExpanded(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		DweezilTreeNode node = (DweezilTreeNode)path.getLastPathComponent();
		node.isExpanded = true;
		dirtyTree = true;
	}
	
	/**
	 * Tree Collapsed Listener
	 */
	public void treeCollapsed(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		DweezilTreeNode node = (DweezilTreeNode)path.getLastPathComponent();
		node.isExpanded = false;
		dirtyTree = true;
	}
	
	/**
	 * @return Whether or not the Tree State has changed
	 */
	public boolean isDirtyTree() {
		return dirtyTree;
	}
	

    // ========================================================================
    // ================ COMPONENT TRANSFER LISTENER ===========================
    // ========================================================================

    public void componentMarkedRemoved(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
    	repaint();
    }

    public void componentCut(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        // Non-selected node is taken care of in valueChanged()
    }

    public void componentPasted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        selectNodeByObject(parentGroup);
        expandNode(parentGroup, true);
    }

    public void componentMoved(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup) {
        selectNodeByObject(targetGroup);
        //selectNode(tc, targetGroup);
    }

    public void componentInserted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        selectNode(tc, parentGroup);
    }

    //==========================================================================
    //========================== NODE SELECTION HISTORY ========================
    //==========================================================================
    private Stack backHistory = new Stack();
    private Stack forwardHistory = new Stack();

    private void addToHistory(ColloquiaTreeNode node) {
        // Don't add same node twice
        if(node == currentNode) return;
        if(currentNode != null) backHistory.push(currentNode);
        currentNode = node;
        forwardHistory.removeAllElements();
    }

    public void selectNextNodeInHistory() {
        if(forwardHistory.isEmpty()) return;
        backHistory.push(currentNode);
        currentNode = (ColloquiaTreeNode)forwardHistory.pop();

        // If parent of node is null it will have been cut from the tree
        if(currentNode.getParent() == null) clearNodeInHistory(currentNode);
        else selectNode(currentNode);
    }

    public void selectPreviousNodeInHistory() {
        if(backHistory.isEmpty()) return;
        forwardHistory.push(currentNode);
        currentNode = (ColloquiaTreeNode)backHistory.pop();

        // If parent of node is null it will have been cut from the tree
        if(currentNode.getParent() == null) clearNodeInHistory(currentNode);
        else selectNode(currentNode);
    }

    private void clearNodeInHistory(ColloquiaTreeNode node) {
        while(backHistory.contains(node)) backHistory.removeElement(node);
        while(forwardHistory.contains(node)) forwardHistory.removeElement(node);
    }

    /**
    * For debugging
    */
    private void printHistory(String s) {
        System.out.println(s);
        System.out.println("Back History\n--------------");
        for(int i = 0; i < backHistory.size(); i++) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)backHistory.elementAt(i);
            System.out.println(node);
        }
        System.out.println(" ");
        System.out.println("Forward History\n--------------");
        for(int i = 0; i < forwardHistory.size(); i++) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)forwardHistory.elementAt(i);
            System.out.println(node);
        }
        System.out.println("---------------------------------");
    }

    //==========================================================================
    //========================== TREE MODEL ====================================
    //==========================================================================

    class   ColloquiaTreeModel
    extends DweezilTreeModel
    {
        // The nodes on a blank tree
        private ColloquiaTreeNode peopleNode;
        private ColloquiaTreeNode resourcesNode;
        private ColloquiaTreeNode templatesNode;
        private ColloquiaTreeNode activitiesNode;

        // Constructor
        private ColloquiaTreeModel() {
            // Dummy root to keep constructor happy
            super(new ColloquiaTreeNode(new Group("Root", "0-0")));
        }

        /**
        * Makes a new, blank tree.
        */
        protected void newTree() {
            // Root
            ColloquiaTreeNode rootNode = new ColloquiaTreeNode(new Group("Root", "0-0"));
            setRoot(rootNode);

            // People
            peopleNode = new ColloquiaTreeNode(DataModel.getPeopleGroup());
            rootNode.add(peopleNode);

            // Resources
            resourcesNode = new ColloquiaTreeNode(DataModel.getResourceGroup());
            rootNode.add(resourcesNode);

            // Templates
            templatesNode = new ColloquiaTreeNode(DataModel.getTemplateGroup());
            rootNode.add(templatesNode);

            // Main Activities Folder
            activitiesNode = new ColloquiaTreeNode(DataModel.getActivityGroup());
            rootNode.add(activitiesNode);

            // Other Activities
            //otherAtivitiesNode = new ColloquiaTreeNode(new Group("Other Activities", strOtherAct, "8-8", true, false, false), false);
            //newRoot.add(otherAtivitiesNode);

            reload(rootNode); // Need to put this here!
        }

        /**
        * Add Component in a node
        * @return newly inserted node or null if not successful
        */
        protected ColloquiaTreeNode addComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
            ColloquiaTreeNode parentNode;

            // Do we have it already?
            if(getNode(tc, parentGroup) != null) return null;

            // Find the node that the group is in
            parentNode = (ColloquiaTreeNode)getNode(parentGroup);
            if(parentNode == null) return null;

            // Create new node
            ColloquiaTreeNode newNode = new ColloquiaTreeNode(tc);

            // Insert sorted
            insertNodeIntoDefaultPosition(newNode, parentNode);

            // Have we got children to add as well?
            if(tc instanceof ColloquiaContainer) {
                ColloquiaContainer group = (ColloquiaContainer)tc;
                Vector members = group.getMembers();
                for(int i = 0; i < members.size(); i++) {
                    ColloquiaComponent member = (ColloquiaComponent)members.elementAt(i);
                    addComponent(member, group);
                }
            }

            return newNode;
        }

        /**
        * Remove a node
        */
        protected void removeComponent(ColloquiaComponent tc, ColloquiaContainer group) {
            // Find the node to remove
            ColloquiaTreeNode node = getNode(tc, group);
            // Remove from parent node
            if(node != null) removeNodeFromParent(node);
        }

        /**
        * The sort order of some components has changed
        */
        protected void componentOrderChanged(Vector members, ColloquiaContainer group) {

            for(int i = 0; i < members.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
                ColloquiaTreeNode node = getNode(tc, group);
                if(node != null) {
                    ColloquiaTreeNode parentNode = (ColloquiaTreeNode)node.getParent();
                    removeNodeFromParent(node);
                    insertNodeIntoDefaultPosition(node, parentNode);
                    expandNode(node, node.isExpanded);
                }
            }

        }

        /**
        * A manual node edit was done - rename
        * Over-ridden from parent class
        */

        public void valueForPathChanged(TreePath path, Object newValue) {
            ColloquiaTreeNode node = (ColloquiaTreeNode)path.getLastPathComponent();
            ColloquiaComponent tc = node.getComponent();
            // Rename
            if(newValue instanceof String && tc != null) {
                tc.rename((String)newValue);
            }
            nodeChanged(node);
        }

        public void insertNode(DweezilTreeNode newNode, DweezilTreeNode parentNode) {

        }

        /**
        * Insert Node into tree in a sensible place
        * People Group and Resource Group node order = entities followed by groups
        * Activity node order = People - Resources - Assignment - sub-Activities
        */
        private void insertNodeIntoDefaultPosition(ColloquiaTreeNode newNode, ColloquiaTreeNode parentNode) {
            int index = 0;

            // Get number of children
            int childCount = getChildCount(parentNode);

            ColloquiaComponent theComponent = newNode.getComponent();
            ColloquiaComponent parentComponent = parentNode.getComponent();

            // Inserting something into an Activity
            if(parentComponent instanceof Activity ||
                parentComponent instanceof ActivityGroup || parentComponent instanceof TemplateGroup) {
                // Get positions
                int lastPersonPosition = 0;
                int lastResourcePosition = 0;
                int lastAssignmentPosition = 0;
                int lastInvitePosition = 0;
                int lastLivePosition = 0;
                int lastCompletedPosition = 0;

                for(int i = 0; i < childCount; i++) {
                    ColloquiaTreeNode node = (ColloquiaTreeNode)getChild(parentNode, i);
                    ColloquiaComponent tc = node.getComponent();
                    if(tc instanceof Person) lastPersonPosition = i + 1;
                    else if(tc instanceof Resource) lastResourcePosition = i + 1;
                    else if(tc instanceof Assignment) lastAssignmentPosition = i + 1;
                    else if(tc instanceof Activity) {
                        Activity A = (Activity)tc;
                        switch(A.getState()) {
	                        case Activity.INVITE:
                                lastInvitePosition = i + 1;
                                break;
                            case Activity.LIVE:
                                lastLivePosition = i + 1;
                                break;
                            case Activity.COMPLETED:
                                lastCompletedPosition = i + 1;
                                break;
                        }
                    }
                }

                lastResourcePosition = Math.max(lastPersonPosition, lastResourcePosition);
                lastAssignmentPosition = Math.max(lastResourcePosition, lastAssignmentPosition);
                lastInvitePosition = Math.max(lastAssignmentPosition, lastInvitePosition);
                lastLivePosition = Math.max(lastInvitePosition, lastLivePosition);
                lastCompletedPosition = Math.max(lastLivePosition, lastCompletedPosition);

                // Person
                if(theComponent instanceof Person) index = lastPersonPosition;
                // Resource
                else if(theComponent instanceof Resource) index = lastResourcePosition;
                // Assignment
                else if(theComponent instanceof Assignment) index = lastAssignmentPosition;
                // Activity
                else if(theComponent instanceof Activity) {
                    Activity A = (Activity)theComponent;
                    switch(A.getState()) {
                        case Activity.INVITE:
                        case Activity.TEMPLATE:
                            index = lastInvitePosition;
                            break;
                        case Activity.LIVE:
                            index = lastLivePosition;
                            break;
                        case Activity.COMPLETED:
                            index = lastCompletedPosition;
                            break;
                    }
                }

                // Insert
                insertNodeInto(newNode, parentNode, index);
                return;
            }


            // Insert a new group - it goes at the end
            if(theComponent instanceof ColloquiaContainer) {
                insertNodeInto(newNode, parentNode, childCount);
                return;
            }

            // Else if an entity in a group insert just before the first group
            // Or at the end if no group
            for(index = 0; index < childCount; index++) {
                ColloquiaTreeNode node = (ColloquiaTreeNode)getChild(parentNode, index);
                ColloquiaComponent tc = node.getComponent();
                if(tc instanceof ColloquiaContainer) break;
            }

            insertNodeInto(newNode, parentNode, index);
        }

    }

	//	==============================================================================
	//	DRAG AND DROP HANDLER
	//	==============================================================================
	
	/**
	 * Drag and drop handler
	 */
    class ColloquiaTreeDragDropHandler extends DweezilTreeDragDropHandler {

        /**
         * Constructor
         * @param tree
         */
        public ColloquiaTreeDragDropHandler(DweezilTree tree) {
            super(tree);
        }
        
        // =================== Drag and Drop Source events ================

        /**
        * A drag gesture has been initiated from this tree
        */
        public void dragGestureRecognized(DragGestureEvent event) {
            //System.out.println("dragGestureRecognized");
    		if((event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0) return;

            NodePair np = getDragSourceNodePair(event);
            if(np != null) {
                DragObject dragObject = new DragObject(np, ColloquiaTree.this);

                Point ptDragOrigin = event.getDragOrigin();
                TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
                if(path == null) return;

                // Get the Drag Image
                BufferedImage imgGhost = getDragImage(path, ptDragOrigin);

                event.startDrag(DragSource.DefaultCopyNoDrop, imgGhost, new Point(5, 5), dragObject, this);
            }
        }

        /**
         * Return the selected object and it's parent that has been picked up and dragged
         */
        protected NodePair getDragSourceNodePair(DragGestureEvent event) {
            NodePair np = null;
            Point location = event.getDragOrigin();
            TreePath dragPath = getPathForLocation(location.x, location.y);

            if(dragPath != null && isPathSelected(dragPath)) {

                // Child
                DweezilTreeNode selNode = (DweezilTreeNode)dragPath.getLastPathComponent();
                ColloquiaComponent selComponent = (ColloquiaComponent)selNode.getUserObject();

                // Parent
                DweezilTreeNode selParentNode = (DweezilTreeNode)selNode.getParent();
                ColloquiaContainer selParentGroup = (ColloquiaContainer)selParentNode.getUserObject();

                np = new NodePair(selComponent, selParentGroup);
            }

            return np;
        }

        // =================== Drag and Drop Target events ================

        /**
         * We are dragging and want to know whether we can drop
         */
        public boolean isDropOK(DropTargetDragEvent event) {
            ColloquiaTreeNode targetNode = (ColloquiaTreeNode)getDragOverTreeNode(event);
            if(targetNode == null) return false;

            // Workaround for Java bug ID #4357494 and ID #4248542 and ID #4378091
            DropTargetDropEvent tempDTDropEvent = new  DropTargetDropEvent(event.getDropTargetContext(),
                            event.getLocation(), DnDConstants.ACTION_COPY, 0);
            Transferable transferable = tempDTDropEvent.getTransferable();

            // Find out whether we are the correct object
            if(transferable.isDataFlavorSupported(DragObject.flavor)) {
                try {
                    Object userObject = transferable.getTransferData(DragObject.flavor);
                    if(userObject instanceof NodePair) {
                        NodePair np = (NodePair) userObject;
                        ColloquiaComponent source = np.getChild();
                        ColloquiaContainer sourceParent = np.getParent();
                        ColloquiaComponent target = targetNode.getComponent();
                        if(target instanceof ColloquiaContainer) {
                            ColloquiaContainer targetGroup = (ColloquiaContainer)target;
                            int action = DNDUtils.getCorrectDropContext(event);
                            boolean ok = false;
                            if((action & DnDConstants.ACTION_MOVE) != 0) { // swapped!
                                ok = targetGroup.isInsertable(source);
                            }
                            else if((action & DnDConstants.ACTION_COPY) != 0) { // swapped!
                                if(sourceParent != null) ok = sourceParent.isMovable(source, targetGroup);
                            }
                            return ok;
                        }
                        else {
                            return false;
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return false;
        }


        /**
         * We dropped something!
         */
        public void drop(DropTargetDropEvent event) {
            // Where did we drop it?
            Point location = event.getLocation();
            TreePath treePath = getPathForLocation(location.x, location.y);
            if(treePath == null) return;

            // Get what we dropped
            Transferable transferable = event.getTransferable();

            // A ColloquiaComponent was dropped
            if(transferable.isDataFlavorSupported(DragObject.flavor)){
                //Accept the drop
                event.acceptDrop(DNDUtils.getCorrectDropContext(event));

                // Get the User Object and do something
                try {
                    Object userObject = transferable.getTransferData(DragObject.flavor);

                    // Dropped a NodePair
                    if(userObject instanceof NodePair) {
                        NodePair np = (NodePair)userObject;

                        // Target Component
                        ColloquiaTreeNode selNode = (ColloquiaTreeNode)treePath.getLastPathComponent();
                        ColloquiaComponent targetComponent = selNode.getComponent();

                        // Has to be a Container
                        if(targetComponent instanceof ColloquiaContainer) {
                            ColloquiaComponent sourceComponent = np.getChild();
                            ColloquiaContainer sourceParent = np.getParent();
                            ColloquiaContainer targetGroup = (ColloquiaContainer)targetComponent;

                            // Get Action - Copy or Move
                            int action = DNDUtils.getCorrectDropContext(event);

                            setCursor(ColloquiaConstants.waitCursor);

                            // Copy swapped! = MOVE
                            if((action & DnDConstants.ACTION_MOVE) != 0) {
                                if(targetGroup.isInsertable(sourceComponent)) {
                                    ComponentTransferManager.paste(sourceComponent, null, targetGroup);
                                }
                            }
                            // Move swapped! = COPY
                            else if((action & DnDConstants.ACTION_COPY) != 0) {
                                if(sourceParent.isMovable(sourceComponent, targetGroup)) {
                                    ComponentTransferManager.move(sourceComponent, sourceParent, targetGroup);
                                }
                            }
                        }
                    }
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
                finally {
                    setCursor(ColloquiaConstants.defaultCursor);
                }

                hiliteNode(_prevHilitedNode, false);
                event.getDropTargetContext().dropComplete(true);
            }

            // Else we are not the right object
            else event.rejectDrop();
        }

        /**
         * Over-ride to swap Copy and Move action
         */
        public void dragEnter(DragSourceDragEvent event) {
            //System.out.println("dragEnter source");
            DragSourceContext context = event.getDragSourceContext();
            int action = event.getDropAction();
            if((action & DnDConstants.ACTION_COPY) != 0)
                context.setCursor(DragSource.DefaultMoveDrop);     // swapped!!
            else if((action & DnDConstants.ACTION_MOVE) != 0)
                context.setCursor(DragSource.DefaultCopyDrop);     // swapped!!
            else context.setCursor(DragSource.DefaultCopyNoDrop);
        }
    }
}

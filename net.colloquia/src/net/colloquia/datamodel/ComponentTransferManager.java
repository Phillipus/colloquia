package net.colloquia.datamodel;

import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.util.undo.*;
import net.colloquia.views.*;


/**
 * Manages the cut, copy paste, move etc. of Components
 * Only from the point of view of a user not code-wise
 */
public final class ComponentTransferManager {

    /**
    * A class to store a Component and its parent and its MemberInfo
    * This can be put on the clipboard for a cut & copy action
    */
    private static class ClipBoard {
        ColloquiaComponent tc;              // The Component
        ColloquiaContainer parentGroup;     // Where it came from
        MemberInfo memberInfo;           // The MemberInfo

        // This is for a Copy action where a new MemberInfo will be created
        public ClipBoard(ColloquiaComponent tc) {
            this.tc = tc;
        }

        // This is for a Cut Action
        // We need to preserve the MemberInfo
        public ClipBoard(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
            this.tc = tc;
            this.parentGroup = parentGroup;
            memberInfo = parentGroup.getMemberInfo(tc);
        }
    }

    private static ClipBoard clipBoard;

    /**
    * Adds a brand new Component asking user for name
    */
    public static ColloquiaComponent addNewComponent(int type, ColloquiaContainer parentGroup) {
        if(parentGroup == null) return null;
        ColloquiaComponent tc = null;
        String alias = "";

        if(type == ColloquiaComponent.ACTIVITY) {
            new InsertActivityWizard(parentGroup);
            return null;
        }

        // Get alias for dialog box
        switch(type) {
            case ColloquiaComponent.PERSON:
                alias = LanguageManager.getString("6");
                break;
            case ColloquiaComponent.PERSON_GROUP:
                alias = LanguageManager.getString("11");
                break;
            case ColloquiaComponent.RESOURCE:
                alias = LanguageManager.getString("8");
                break;
            case ColloquiaComponent.RESOURCE_GROUP:
                alias = LanguageManager.getString("10");
                break;
            case ColloquiaComponent.ASSIGNMENT:
                alias = LanguageManager.getString("2");
                break;
            default:
                return null;
        }

        String caption = LanguageManager.getString("NAME") + ":";
        String message = LanguageManager.getString("INSERT") + " " + alias;

        // Get name
        String name = JOptionPane.showInputDialog(MainFrame.getInstance(), caption, message, JOptionPane.PLAIN_MESSAGE);

        // Trim spaces
        if(name != null) name = name.trim();
        if((name == null) || (name.length() == 0)) return null;

        switch(type) {
            case ColloquiaComponent.PERSON:
                tc = new Person(name, null);
                break;
            case ColloquiaComponent.PERSON_GROUP:
                tc = new PersonGroup(name, null);
                break;
            case ColloquiaComponent.RESOURCE:
                tc = new Resource(name, null);
                // Set title = name
                // removed 16th July 2001
                //tc.putProperty(Resource.TITLE, name, false);
                break;
            case ColloquiaComponent.RESOURCE_GROUP:
                tc = new ResourceGroup(name, null);
                break;
            case ColloquiaComponent.ASSIGNMENT:
                tc = new Assignment(name, null);
                // Set title = name
                tc.putProperty(Assignment.TITLE, name, false);
                break;
            default:
                return null;
        }

        boolean result = DataModel.addComponent(tc, parentGroup, true);

        if(result) {
            MemberInfo[] subActivities = null;

            // Add people to subs if flag set
            if(tc instanceof Person && parentGroup instanceof Activity) {
                subActivities = addSubPeople((Person)tc, (Activity)parentGroup, true);
            }

            fireComponentInserted(tc, parentGroup);

            UndoManager.addUndoEvent(new InsertEvent(tc, parentGroup, subActivities));

            return tc;
        }

        else return null;
    }

    /**
    * Add People and Resources to Activity
    */
    public static void addToActivity(Activity A) {
        if(A == null) return;
        new AddToActivityWizard(A);
    }


    /**
    * Add person to A's subActivities if flag set
    * Return an array of MemberInfo where added or null if not
    */
    private static MemberInfo[] addSubPeople(Person person, Activity A, boolean ask) {
        String msg = LanguageManager.getString("INHERIT_PEOPLE");
        MemberInfo[] subMInfo = null;
        int result = JOptionPane.YES_OPTION;

        // If Activity has sub-Activities ask whether to inherit Person
        if(A.hasActivities()) {

            if(ask) result = JOptionPane.showConfirmDialog
                (MainFrame.getInstance(),
                msg,
                person.getName(),
                JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.YES_OPTION) {
                Activity[] subAs = A.addPersonToInheritingSubActivities(person);
                if(subAs != null) {
                    subMInfo = new MemberInfo[subAs.length];
                    for(int i = 0; i < subAs.length; i++) {
                        subMInfo[i] = subAs[i].getMemberInfo(person);
                    }
                }
            }
        }
        return subMInfo;
    }


    /**
    * Removes additional Person or Resource to top level folder if one added to Activity
    */
    private static void removeCopies(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        // Person added/pasted/moved to Activity
        if(tc instanceof Person && parentGroup instanceof Activity) {
            // Remove from main People folder as well
            DataModel.removeComponent(tc, DataModel.getPeopleGroup(), false);
        }

        // Resource added/pasted/moved to Activity
        if(tc instanceof Resource && parentGroup instanceof Activity) {
            DataModel.removeComponent(tc, DataModel.getResourceGroup(), false);
        }
    }

    /**
    * To let us know that the insert Activity Wizard just inserted a new Activity
    */
    public static void insertedActivity(Class who, Activity A, ColloquiaContainer parentGroup) {
        fireComponentInserted(A, parentGroup);
        UndoManager.addUndoEvent(new InsertEvent(A, parentGroup, null));
    }


    /**
    * Insert Components in an existing Activity
    */
    public static void insertComponents(PersonGroup pgIn, Vector components, boolean inheritPeople) {
    MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
    Vector added = new Vector();
        for(int i = 0; i < components.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
            boolean result = DataModel.addComponent(tc, pgIn, true);
            if(result){
            added.addElement(tc);
            }
            // Don't fire component inserted!
            // Add people to subs if flag set
        }
        if(!added.isEmpty()) {
            UndoManager.addUndoEvent(new MultiInsertEvent(pgIn, added, null));
        }
   MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
   }

    /**
    * Insert Components in an existing Activity
    */
    public static void insertComponents(ActivityGroup A, Vector components, boolean inheritPeople) {
    MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        Vector added = new Vector();
        for(int i = 0; i < components.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
            boolean result = DataModel.addComponent(tc, A, true);
            if(result) added.addElement(tc);
            // Don't fire component inserted!
            // Add people to subs if flag set
        }
        if(!added.isEmpty()) {
        UndoManager.addUndoEvent(new MultiInsertEvent(A, added, null));
        }

    MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
    }


    /**
    * Insert Components in an existing Activity
    */
    public static void insertComponents(Activity A, Vector components, boolean inheritPeople) {
        //MainFrame.getInstance().setCursor(Constants.waitCursor);
        // Sub-Activities where a Person might also be added
        Vector vSubs = new Vector();
        MemberInfo[] subs = null;

        Vector added = new Vector();

        for(int i = 0; i < components.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
            boolean result = DataModel.addComponent(tc, A, true);
            if(result) added.addElement(tc);
            // Don't fire component inserted!
            // Add people to subs if flag set
            if(tc instanceof Person && inheritPeople) {
                MemberInfo[] subAs = addSubPeople((Person)tc, A, false);
                // Add subAs to subs list
                if(subAs != null) for(int j = 0; j < subAs.length; j++) {
                    vSubs.addElement(subAs[j]);
                }
            }
        }

        if(!vSubs.isEmpty()) {
            subs = new MemberInfo[vSubs.size()];
            vSubs.copyInto(subs);
        }

        if(!added.isEmpty()) {
            UndoManager.addUndoEvent(new MultiInsertEvent(A, added, subs));
        }
        //MainFrame.getInstance().setCursor(Constants.defaultCursor);
    }

    //============================== COPY ======================================

    public static boolean isCopyable(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(tc == null || parentGroup == null) return false;

        if(tc instanceof ColloquiaContainer) {
            ColloquiaContainer subGroup = (ColloquiaContainer)tc;

            // Are we a core top-level group?
            if(DataModel.isCoreGroup(subGroup)) return false;
        }

        return true;
    }

    public static void copyToClipBoard(ColloquiaComponent tc) {
        clipBoard = new ClipBoard(tc);
    }

    public static void clearClipBoard() {
        clipBoard = null;
    }

    //=============================== DELETE/CUT ======================================

    public static boolean isRemovable(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(tc == null || parentGroup == null) return false;
        return parentGroup.isRemovable(tc);
    }


    public static boolean delete(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
    	return cutIt(tc, parentGroup, false);
	}

    public static void deleteComponents(ColloquiaContainer containerIn, Vector components, boolean inheritPeople) {
    MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
        for(int i = 0; i < components.size(); i++) {
        ColloquiaComponent tc = (ColloquiaComponent)components.get(i);
        deleteComponent(tc,containerIn,false);
        }
   MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
   }

    /*
    * Cut tc from parentGroup
    * Cutting from a Completed Activity is a serious matter because
    * you cannot paste it back again (cannot add components to Completed Activity)
    * But you can undo it
    */
    public static boolean deleteComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup, boolean clip) {
        if(tc == null || parentGroup == null) return false;
        if(!isRemovable(tc, parentGroup)) return false;
        // If Tutor cuts Person from Activity (Live and Mine) for the first time they will get
        // The Person is marked as REMOVED
        if(tc instanceof Person && parentGroup instanceof Activity) {
            Activity A = (Activity)parentGroup;
            Person person = (Person)tc;
            if(A.isLive() && A.isMine() && A.isPersonRemoved(person) == false) {
                A.setPersonRemoved(person, true);
                fireComponentMarkedRemoved(tc, parentGroup);
                UndoManager.addUndoEvent(new DeactivatePersonEvent(tc, parentGroup));
                return true;
            }
        }

        boolean result = askDeleteNode(tc, parentGroup);
        if(result == false) return false;

        UndoEvent event;

        // Cut
        if(clip) {
            // Copy to clipboard FIRST
            clipBoard = new ClipBoard(tc, parentGroup);
            // Create Undo Event FIRST
            event = new CutEvent(tc, parentGroup);
        }
        // Delete
        else {
            // Create Undo Event FIRST
            event = new DeleteEvent(tc, parentGroup);
        }
        result = DataModel.removeComponent(tc, parentGroup, true);
        if(result) {
            DataFiler.cutFolder(tc);
            // If tc is a group, decrement the child group count
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
            fireComponentCut(tc, parentGroup);
            UndoManager.addUndoEvent(event);
        }
        return result;
    }


    public static boolean cut(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
    	return cutIt(tc, parentGroup, true);
	}

    /*
    * Cut tc from parentGroup
    * Cutting from a Completed Activity is a serious matter because
    * you cannot paste it back again (cannot add components to Completed Activity)
    * But you can undo it
    */
    private static boolean cutIt(ColloquiaComponent tc, ColloquiaContainer parentGroup, boolean clip) {
        if(tc == null || parentGroup == null) return false;
        if(!isRemovable(tc, parentGroup)) return false;

        // If Tutor cuts Person from Activity (Live and Mine) for the first time they will get
        // The Person is marked as REMOVED
        if(tc instanceof Person && parentGroup instanceof Activity) {
            Activity A = (Activity)parentGroup;
            Person person = (Person)tc;
            if(A.isLive() && A.isMine() && A.isPersonRemoved(person) == false) {
                A.setPersonRemoved(person, true);
                fireComponentMarkedRemoved(tc, parentGroup);
                UndoManager.addUndoEvent(new DeactivatePersonEvent(tc, parentGroup));
                return true;
            }
        }

        boolean result = askDeleteNode(tc, parentGroup);
        if(result == false) return false;

        UndoEvent event;

        // Cut
        if(clip) {
            // Copy to clipboard FIRST
            clipBoard = new ClipBoard(tc, parentGroup);
            // Create Undo Event FIRST
            event = new CutEvent(tc, parentGroup);
        }
        // Delete
        else {
            // Create Undo Event FIRST
            event = new DeleteEvent(tc, parentGroup);
        }

        result = DataModel.removeComponent(tc, parentGroup, true);

        if(result) {
            DataFiler.cutFolder(tc);
            // If tc is a group, decrement the child group count
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
            fireComponentCut(tc, parentGroup);
            UndoManager.addUndoEvent(event);
        }
        return result;
    }

    private static boolean askDeleteNode(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        switch(tc.getType()) {
            // Activity - this will always be the last instance
            case ColloquiaComponent.ACTIVITY:
                int result = JOptionPane.showConfirmDialog
                    (MainFrame.getInstance(),
                    LanguageManager.getString("ARE_YOU_SURE2"),
                    LanguageManager.getString("DELETE") + " - " + tc.getName(),
                    JOptionPane.YES_NO_OPTION);
                if(result != JOptionPane.YES_OPTION) return false;
                break;

            case ColloquiaComponent.PERSON:
            case ColloquiaComponent.PERSON_GROUP:
            case ColloquiaComponent.RESOURCE_GROUP:
                break;

            case ColloquiaComponent.RESOURCE:
            case ColloquiaComponent.ASSIGNMENT:
                // Is this the last instance of this object?
                if(tc.getInstanceCount() <= 1) {
                    String msg = LanguageManager.getString("LAST_INSTANCE") + "\n" +
                        LanguageManager.getString("ARE_YOU_SURE3") + "\n";
                    result = JOptionPane.showConfirmDialog
                        (MainFrame.getInstance(),
                        msg,
                        LanguageManager.getString("DELETE") + " - " + tc.getName(),
                        JOptionPane.YES_NO_OPTION);
                    if(result != JOptionPane.YES_OPTION) return false;
                }
                // Is it in a Completed Activity?
                else if(parentGroup instanceof Activity) {
                    Activity A = (Activity)parentGroup;
                    if(A.isCompleted()) {
                        String msg = LanguageManager.getString("DELETING_COMPLETED") + "\n" +
                            LanguageManager.getString("ARE_YOU_SURE3") + "\n";
                        result = JOptionPane.showConfirmDialog
                            (MainFrame.getInstance(),
                            msg,
                            LanguageManager.getString("DELETE") + " - " + tc.getName(),
                            JOptionPane.YES_NO_OPTION);
                        if(result != JOptionPane.YES_OPTION) return false;
                    }
                }
                break;

            default:
                return false;
        }

        return true;
    }

    //============================== PASTE =====================================

    /**
    * Can we paste the clipboard component in this group?
    */
    public static boolean isClipBoardPastable(ColloquiaContainer parentGroup) {
        if(clipBoard == null || parentGroup == null) return false;
        // Are we allowed to insert it in this group?
        return parentGroup.isInsertable(clipBoard.tc);
    }


    /*
    * Paste the clipboard component into this group
    */
    public static void pasteFromClipBoard(ColloquiaContainer parentGroup) {
        MemberInfo mInfo = null;

        // Are we pasting to original parent?
        // If so we need to restore memberInfo
        if(parentGroup == clipBoard.parentGroup) mInfo = clipBoard.memberInfo;

        paste(clipBoard.tc, mInfo, parentGroup);
    }

    /*
    * Paste the specified component into this group
    * If mInfo is not null we did a cut and need to restore it
    * ONLY IF we are restoring to original parent
    */
    public static void paste(ColloquiaComponent tc, MemberInfo mInfo, ColloquiaContainer parentGroup) {
        if(tc == null || parentGroup == null) return;

        if(!parentGroup.isInsertable(tc)) return;

        // ResourceGroup contents are pasted one at a time to an Activity
        if((tc instanceof ResourceGroup) && (parentGroup instanceof Activity)) {
            Activity A = (Activity)parentGroup;
            ResourceGroup rg = (ResourceGroup)tc;
            insertComponents(A, rg.getResources(), false);
            return;
        }

        // PersonGroup contents are pasted one at a time to an Activity
        if((tc instanceof PersonGroup) && (parentGroup instanceof Activity)) {
            Activity A = (Activity)parentGroup;
            PersonGroup pg = (PersonGroup)tc;
            insertComponents(A, pg.getPeople(), false);
            return;
        }

        // People and Resources can be cut and copied no problem

        // Pasting an Assignment or ResourceGroup - cannot have more than one instance
        // If there is more than one instance then we clone it
        // If there is no instance then we must have previously cut it
        else if(tc instanceof Assignment || tc instanceof ResourceGroup) {
            if(tc.getInstanceCount() > 0) tc = tc.copy();
        }

        // Pasting an Activity
        else if(tc instanceof Activity) {
            // Pasting into Top Templates Folder
            // So make a Template Copy if there is already an instance
            // If there no instance then we must have previously cut it
            if(parentGroup instanceof TemplateGroup) {
                if(tc.getInstanceCount() > 0) tc = ((Activity)tc).copyTemplate();
            }

            // Pasting into Top Activity Folder
            else if(parentGroup instanceof ActivityGroup) {
                // Must have been a copy so make a clone
                if(tc.getInstanceCount() > 0) tc = tc.copy();
            }

            // Pasting into Activity/Template Activity
            else if(parentGroup instanceof Activity) {
                Activity parentA = (Activity)parentGroup;

                // Pasting into a Template Activity
                // So make a Template Copy if there is already an instance
                // If there no instance we must have previously cut it
                if(parentA.isTemplate()) {
                    if(tc.getInstanceCount() > 0) tc = ((Activity)tc).copyTemplate();
                }

                // Pasting into a Live Activity
                else if(parentA.isLive()) {
                    // Must have been a copy so make a clone
                    if(tc.getInstanceCount() > 0) tc = tc.copy();
                }
            }
        }

        // Add to dataModel using mInfo if same place
        boolean result;
        if(mInfo != null) result = DataModel.addComponent(mInfo, true);
        else result = DataModel.addComponent(tc, parentGroup, true);
        if(!result) return;

        DataFiler.uncutFolder(tc);

        // If tc is a group, increment the child group count
        if(tc instanceof ColloquiaContainer) addGroupInstances((ColloquiaContainer)tc);

        fireComponentPasted(tc, parentGroup);

        // Add people to subs if flag set
        MemberInfo[] subs = null;
        if(tc instanceof Person && parentGroup instanceof Activity) {
            subs = addSubPeople((Person)tc, (Activity)parentGroup, true);
        }

        UndoManager.addUndoEvent(new PasteEvent(tc, parentGroup, subs));
    }



    /**
    * A Group has been added - therefore the group's children need to be informed
    * that they are now a member of this group
    * This is because DataModel.addComponent() doesn't do this for children
    */
    public static void addGroupInstances(ColloquiaContainer group) {
        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            tc.addMemberGroup(group);
            if(tc instanceof ColloquiaContainer) addGroupInstances((ColloquiaContainer)tc);
        }
    }

    /**
    * A Group has been removed - therefore the group's children need to be informed
    * that they are no longer a member of this group
    * This is because DataModel.removeComponent() doesn't do this for children
    */
    public static void removeGroupInstances(ColloquiaContainer group) {
        Vector members = group.getMembers();
        for(int i = 0; i < members.size(); i++) {
            ColloquiaComponent tc = (ColloquiaComponent)members.elementAt(i);
            tc.removeMemberGroup(group);
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
        }
    }




    /**
    * Move a Component from sourceGroup to targetGroup
    * Moving is only achieved by a mouse drag
    */
    public static void move(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup) {
        if(tc == null || sourceGroup == null || targetGroup == null) return;

        // Is it in a Completed Activity?
        if(sourceGroup instanceof Activity) {
            Activity A = (Activity)sourceGroup;
            if(A.isCompleted()) {
                String msg = LanguageManager.getString("MOVING_COMPLETED") + "\n" +
                    LanguageManager.getString("ARE_YOU_SURE_MOVE") + "\n";
                int result = JOptionPane.showConfirmDialog
                    (MainFrame.getInstance(),
                    msg,
                    LanguageManager.getString("MOVE") + " - " + tc.getName(),
                    JOptionPane.YES_NO_OPTION);
                if(result != JOptionPane.YES_OPTION) return;
            }
        }

        MoveEvent moveEvent = new MoveEvent(tc, sourceGroup, targetGroup);

        // CUT
        boolean result = DataModel.removeComponent(tc, sourceGroup, true);
        if(!result) return;

        // PASTE
        result = DataModel.addComponent(tc, targetGroup, true);
        if(!result) return;

        // Add people to subs if flag set
        if(tc instanceof Person && targetGroup instanceof Activity) {
            MemberInfo[] subs = addSubPeople((Person)tc, (Activity)targetGroup, true);
            moveEvent.addSubs(subs);
        }

        fireComponentMoved(tc, sourceGroup, targetGroup);
        UndoManager.addUndoEvent(moveEvent);
    }


    // =========================== UNDO ==============================

    public abstract static class TransferEvent
    extends UndoEvent
    {
        ColloquiaComponent tc;
        ColloquiaContainer parentGroup;
        MemberInfo mInfo;
        // subMInfo are any sub-Activities that might have had a Person added to as well
        // if they inherit, IF tc == person AND parentGroup == Activity
        MemberInfo[] subMInfo;
    }

    /**
    * This event occurs when a Tutor de-activates a Person from their Activity
    */
    public static class DeactivatePersonEvent
    extends TransferEvent
    {
        public DeactivatePersonEvent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
            this.tc = tc;
            this.parentGroup = parentGroup;
            mInfo = parentGroup.getMemberInfo(tc);
        }

        public void undo() {
            // Set back
            ((Activity)parentGroup).setPersonRemoved((Person)tc, false);
            fireComponentMarkedRemoved(tc, parentGroup);
        }

        public void redo() {
            // Set back
            ((Activity)parentGroup).setPersonRemoved((Person)tc, true);
            fireComponentMarkedRemoved(tc, parentGroup);
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO7");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO7");
        }
    }

    private static class CutEvent
    extends TransferEvent
    {
        public CutEvent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
            this.tc = tc;
            this.parentGroup = parentGroup;
            mInfo = parentGroup.getMemberInfo(tc);
        }

        public void undo() {
            // We do a paste whilst also restoring the MemberInfo
            DataModel.addComponent(mInfo, false);
            DataFiler.uncutFolder(tc);
            // If tc is a group, increment the child group count
            if(tc instanceof ColloquiaContainer) addGroupInstances((ColloquiaContainer)tc);
            fireComponentPasted(tc, parentGroup);
        }

        public void redo() {
            DataModel.removeComponent(tc, parentGroup, true);
            DataFiler.cutFolder(tc);
            // If tc is a group, decrement the child group count
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
            fireComponentCut(tc, parentGroup);
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO1");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO1");
        }
    }

    private static class DeleteEvent
    extends CutEvent
    {
        public DeleteEvent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
            super(tc, parentGroup);
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO6");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO6");
        }
    }

    public static class MoveEvent
    extends TransferEvent
    {
        private ColloquiaContainer targetGroup;

        public MoveEvent(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup) {
            this.tc = tc;
            this.parentGroup = sourceGroup;
            this.targetGroup = targetGroup;
            mInfo = sourceGroup.getMemberInfo(tc);
        }

        /**
        * Add any sub-Activities where a Person was added
        */
        public void addSubs(MemberInfo[] subMInfo) {
            this.subMInfo = subMInfo;
        }

        public void undo() {
            // CUT
            DataModel.removeComponent(tc, targetGroup, false);
            // remove any sub-People added
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.removeComponent(tc, subMInfo[i].getParent(), false);
            }

            // Paste back using mInfo
            DataModel.addComponent(mInfo, false);

            fireComponentMoved(tc, targetGroup, parentGroup);
        }

        public void redo() {
            // CUT
            DataModel.removeComponent(tc, parentGroup, true);

            // PASTE
            DataModel.addComponent(tc, targetGroup, true);

            // re-add any sub-People added
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.addComponent(tc, subMInfo[i].getParent(), true);
            }

            fireComponentMoved(tc, parentGroup, targetGroup);
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO3");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO3");
        }
    }

    private static class PasteEvent
    extends TransferEvent
    {
        public PasteEvent(ColloquiaComponent tc, ColloquiaContainer parentGroup, MemberInfo[] subMInfo) {
            this.tc = tc;
            this.parentGroup = parentGroup;
            mInfo = parentGroup.getMemberInfo(tc);
            this.subMInfo = subMInfo;
        }

        public void undo() {
            undoPaste(tc, parentGroup, subMInfo);
        }

        private void undoPaste(ColloquiaComponent tc, ColloquiaContainer parentGroup, MemberInfo[] subMInfo) {
            // Cut component and any that were added to sub-Activities
            DataModel.removeComponent(tc, parentGroup, false);
            DataFiler.cutFolder(tc);
            // If tc is a group, decrement the child group count
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
            fireComponentCut(tc, parentGroup);
            // remove any sub-People added
            if(subMInfo != null)
                for(int i = 0; i < subMInfo.length; i++) undoPaste(tc, subMInfo[i].getParent(), null);
        }

        public void redo() {
            // Add to dataModel using mInfo
            DataModel.addComponent(mInfo, true);
            DataFiler.uncutFolder(tc);
            // If tc is a group, increment the child group count
            if(tc instanceof ColloquiaContainer) addGroupInstances((ColloquiaContainer)tc);
            fireComponentPasted(tc, parentGroup);

            // Re-add sub-People restoring their memberInfo
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.addComponent(subMInfo[i], true);
            }
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO2");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO2");
        }
    }

    /**
    * This is currently Insert a NEW Component in a group
    */
    private static class InsertEvent
    extends TransferEvent
    {
        public InsertEvent(ColloquiaComponent tc, ColloquiaContainer parentGroup, MemberInfo[] subMInfo) {
            this.tc = tc;
            this.parentGroup = parentGroup;
            mInfo = parentGroup.getMemberInfo(tc);
            this.subMInfo = subMInfo;
        }

        public void undo() {
            DataModel.removeComponent(tc, parentGroup, false);
            // If tc is a group, decrement the child group count
            if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);

            fireComponentCut(tc, parentGroup);

            // Might have added inherited Person in sub-As
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.removeComponent(tc, subMInfo[i].getParent(), false);
            }

            // If a new Person/Resource was added to an Activity
            // Remove from main People/Resource folder as well
            removeCopies(tc, parentGroup);
        }

        public void redo() {
            // Add to dataModel using mInfo
            DataModel.addComponent(mInfo, true);
            fireComponentInserted(tc, parentGroup);

            // Re-add sub-People restoring their memberInfo
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.addComponent(subMInfo[i], true);
            }
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO4");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO4");
        }
    }


    /**
    * This is currently Insert existing Components in an Activity
    */
    public static class MultiInsertEvent
    extends TransferEvent
    {
        Vector components;

        public MultiInsertEvent(ColloquiaContainer parentGroup, Vector components, MemberInfo[] subMInfo) {
            this.parentGroup = parentGroup;
            this.components = components;
            this.subMInfo = subMInfo;
        }

        public void undo() {
            MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
            for(int i = 0; i < components.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
                DataModel.removeComponent(tc, parentGroup, false);
                // If tc is a group, decrement the child group count
                if(tc instanceof ColloquiaContainer) removeGroupInstances((ColloquiaContainer)tc);
                //fireComponentCut(tc, parentGroup);  DON'T!!!
            }
            // Might have added inherited Person in sub-As
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.removeComponent(subMInfo[i], false);
            }
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        }

        public void redo() {
            MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
            for(int i = 0; i < components.size(); i++) {
                ColloquiaComponent tc = (ColloquiaComponent)components.elementAt(i);
                DataModel.addComponent(tc, parentGroup, true);
                //fireComponentInserted(tc, parentGroup); DON'T!!!
            }
            // Re-add sub-People restoring their memberInfo
            if(subMInfo != null) for(int i = 0; i < subMInfo.length; i++) {
                DataModel.addComponent(subMInfo[i], true);
            }
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        }

        public String getUndoEventName() {
        	return LanguageManager.getString("UNDO5");
        }

        public String getRedoEventName() {
        	return LanguageManager.getString("REDO5");
        }
    }



    // =========================== LISTENER STUFF ==============================
    private static Vector listeners = new Vector();

    public static synchronized void addComponentTransferListener(ComponentTransferListener listener) {
        if(!listeners.contains(listener)) listeners.addElement(listener);
    }

    public static void removeComponentTransferListener(ComponentTransferListener listener) {
        listeners.removeElement(listener);
    }

    public static void fireComponentMarkedRemoved(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        for(int i = 0; i < listeners.size(); i++) {
            ComponentTransferListener listener = (ComponentTransferListener)listeners.elementAt(i);
            listener.componentMarkedRemoved(tc, parentGroup);
        }
    }

    private static void fireComponentCut(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        for(int i = 0; i < listeners.size(); i++) {
            ComponentTransferListener listener = (ComponentTransferListener)listeners.elementAt(i);
            listener.componentCut(tc, parentGroup);
        }
    }

    private static void fireComponentPasted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        for(int i = 0; i < listeners.size(); i++) {
            ComponentTransferListener listener = (ComponentTransferListener)listeners.elementAt(i);
            listener.componentPasted(tc, parentGroup);
        }
    }

    private static void fireComponentMoved(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup) {
        for(int i = 0; i < listeners.size(); i++) {
            ComponentTransferListener listener = (ComponentTransferListener)listeners.elementAt(i);
            listener.componentMoved(tc, sourceGroup, targetGroup);
        }
    }

    private static void fireComponentInserted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        for(int i = 0; i < listeners.size(); i++) {
            ComponentTransferListener listener = (ComponentTransferListener)listeners.elementAt(i);
            listener.componentInserted(tc, parentGroup);
        }
    }
}

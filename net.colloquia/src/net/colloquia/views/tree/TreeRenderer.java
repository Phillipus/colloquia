package net.colloquia.views.tree;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

import net.colloquia.*;
import net.colloquia.comms.index.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * Renderer for the tree
 */
public class TreeRenderer
extends JLabel
implements TreeCellRenderer
{
    private ImageIcon icon;
    private ImageIcon iconPerson = Utils.getIcon(ColloquiaConstants.iconPerson);
    private ImageIcon iconPersonDeclined = Utils.getIcon(ColloquiaConstants.iconPersonDeclined);
    private ImageIcon iconPersonPending = Utils.getIcon(ColloquiaConstants.iconPersonPending);
    private ImageIcon iconPersonAwaiting = Utils.getIcon(ColloquiaConstants.iconPersonAwaiting);
    private ImageIcon iconResource = Utils.getIcon(ColloquiaConstants.iconResource);
    private ImageIcon iconResourceFree = Utils.getIcon(ColloquiaConstants.iconResourceFree);
    private ImageIcon iconAssignment = Utils.getIcon(ColloquiaConstants.iconAssignment);
    private ImageIcon iconAssignmentFree = Utils.getIcon(ColloquiaConstants.iconAssignmentFree);
    private ImageIcon iconFolder = Utils.getIcon(ColloquiaConstants.iconFolder);
    private ImageIcon iconOpenFolder = Utils.getIcon(ColloquiaConstants.iconOpenFolder);
    private ImageIcon iconActivityLiveFolder = Utils.getIcon(ColloquiaConstants.iconActivityLiveFolder);
    private ImageIcon iconActivityFuture = Utils.getIcon(ColloquiaConstants.iconActivityFuture);
    private ImageIcon iconActivityCompletedFolder = Utils.getIcon(ColloquiaConstants.iconActivityCompletedFolder);
    private ImageIcon iconActivityTemplateFolder = Utils.getIcon(ColloquiaConstants.iconActivityTemplateFolder);
    private ImageIcon iconActivityDeclined = Utils.getIcon(ColloquiaConstants.iconActivityDeclined);
    private ImageIcon iconActivityAccepted = Utils.getIcon(ColloquiaConstants.iconActivityAccepted);
    private ImageIcon iconActivityHot = Utils.getIcon(ColloquiaConstants.iconActivityHot);

    private ImageIcon iconPersonX, iconActivityX;

    private Font plainFont = ColloquiaConstants.plainFont11;
    private Font italicFont = ColloquiaConstants.italicFont11;
    private Font boldFont = ColloquiaConstants.boldFont11;
    private Font boldItalicFont = ColloquiaConstants.bold_italicFont11;

    private Color foreColor;

    private Color focusSelectColor = ColloquiaConstants.color1;
    private Color selectColor = Color.lightGray;

    private ColloquiaTreeNode selNode, parentNode;

    private Activity A;
    private ColloquiaComponent selComponent;
    private ColloquiaContainer parent;

    private String grade;
    private int msgUnread;

    private String txt;
    private String tip;

    private String strUnread = LanguageManager.getString("UNREAD");
    private String strMessages = LanguageManager.getString("MESSAGES") + ": ";

    // Constructor
    public TreeRenderer() {
        setOpaque(true);

        // Deleted node icons
        iconPersonX = new ImageIcon(GrayFilter.createDisabledImage(iconPerson.getImage()));
        iconActivityX = new ImageIcon(GrayFilter.createDisabledImage(iconActivityLiveFolder.getImage()));
    }


    /**
    * If a Person has a photo, return an Icon made from that
    */
    /*
    private ImageIcon getPhotoIcon(Person person) {
        ImageIcon photoIcon = new ImageIcon();
        Image image = Utils.getPhotoImage(person.getProperty(Person.PHOTOGRAPH));
        if(image != null) {
            photoIcon.setImage(image.getScaledInstance(24, -1, Image.SCALE_SMOOTH));
            return photoIcon;
        }
        else return iconPerson;
    }
    */

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        selNode = (ColloquiaTreeNode)value;
        if(selNode == null) return this;
        selComponent = selNode.getComponent();

        parentNode = (ColloquiaTreeNode)selNode.getParent();
        // If parentNode = null it must be the root node
        if(parentNode == null) return this;
        parent = (ColloquiaContainer)parentNode.getComponent();

        // Check whether we are the Activity - FIRST! so we don't inherit our parent!
        if(selComponent instanceof Activity) A = (Activity)selComponent;
        // Do we have an Activity as a parent?
        else if(parent instanceof Activity) A = (Activity)parent;
        else A = null;

        // Defaults
        setFont(selComponent.isMine() ? boldFont : plainFont);
        foreColor = Color.black;
        txt = selComponent.getName();
        msgUnread = 0;
        tip = null;

        // Set text color of Activity according to status
        if(selComponent instanceof Activity) {
            switch(((Activity)selComponent).getState()) {
            	case Activity.LIVE:
                    foreColor = ColloquiaConstants.liveColor;
                	break;
            	case Activity.COMPLETED:
                    foreColor = ColloquiaConstants.completedColor;
                	break;
            	case Activity.TEMPLATE:
                    foreColor = ColloquiaConstants.templateColor;
                	break;
            	case Activity.INVITE:
                    foreColor = ColloquiaConstants.futureColor;
                	break;

            }
        }

        if(selNode.isHiLited) setBorder(ColloquiaConstants.focusBorder);
        else setBorder(hasFocus ? ColloquiaConstants.focusBorder : ColloquiaConstants.noFocusBorder);

        if(hasFocus) {
            setForeground(selected ? Color.white : foreColor);
            setBackground(selected ? focusSelectColor : tree.getBackground());
        }
        else {
            setForeground(foreColor);
            setBackground(selected ? selectColor : tree.getBackground());
        }

        // FOR EACH COMPONENT TYPE
        switch(selComponent.getType()) {

            // PERSON
            case ColloquiaComponent.PERSON:
                icon = iconPerson;
                Person person = (Person)selComponent;

                // If we are viewing a Person in an Activity
                if(A != null) {
                    // Tutor view
                    if(A.isMine()) {
                        // Removed
                        if(A.isPersonRemoved(person)) icon = iconPersonX;
                        // Show accepted/declined/pending icon
                        else if(A.getPersonAcceptedStatus(person) == Activity.DECLINED) icon = iconPersonDeclined;
                        else if(A.getPersonAcceptedStatus(person) == Activity.PENDING) icon = iconPersonPending;
                        else if(!A.doesPersonHaveActivity(person)) icon = iconPersonAwaiting;

                        // Show assignment grade (if any) against name
                        grade = A.getPersonAssignmentGrade(person);
                        if(!grade.equals("")) txt += " (" + grade + ")";

                        // Assignment unread message count
        	            AssignmentMessageIndex index = new AssignmentMessageIndex(A.getGUID(), person.getGUID(), false);
		                msgUnread = MessageIndex.getUnreadMessageCount(index);
                    }
                    // Student view
                    else {
                        // Submitter of Activity with asterisks
                        if(A.isSubmitter(person)) txt = "*" + person.getName() + "*";
                        // Set cuttable node if allowed to be cut
                        if(!person.isMine()) {
                            if(A.isComponentFree(person)) icon = iconPersonX;
                        }
                    }

                    SingleMessageIndex index = new SingleMessageIndex(A.getGUID(), person.getGUID(), false);

	                // Add on number of unread Single messages
    	            msgUnread += MessageIndex.getUnreadMessageCount(index);

                    // Tooltip text for total messages
                    int numMessages = MessageIndex.getTotalMessageCount(index);
                    if(numMessages != -1) tip = strMessages + numMessages;
                }

                // Font
                if(selComponent.getInstanceCount() == 1) setFont(selComponent.isMine() ? boldItalicFont : italicFont);
                break;

            // RESOURCE
            case ColloquiaComponent.RESOURCE:
                // Set cuttable node if in Activity and allowed to be cut
                if(A != null && !A.isMine() && !selComponent.isMine()) {
                    if(A.isComponentFree(selComponent)) icon = iconResourceFree;
                    else icon = iconResource;
                }
                else icon = iconResource;
                if(selComponent.getInstanceCount() == 1) setFont(selComponent.isMine() ? boldItalicFont : italicFont);
                break;

            // ASSIGNMENT
            case ColloquiaComponent.ASSIGNMENT:
                // Student view of Assignment
                if(A != null && !A.isMine() && !selComponent.isMine()) {
	                // Set cuttable node if in Activity and allowed to be cut
                    if(A.isComponentFree(selComponent)) icon = iconAssignmentFree;
                    else icon = iconAssignment;

                    // Show grade (for a Student)
                    grade = A.getAssignmentGrade((Assignment)selComponent);
                    if(!grade.equals("")) txt += " (" + grade + ")";

                    // Show message count
			        Person tutor = DataModel.getPersonByEmailAddress(A.getSubmitter());
                    if(tutor != null) {
                        // Assignment unread message count
        	            AssignmentMessageIndex index = new AssignmentMessageIndex(A.getGUID(), tutor.getGUID(), false);
		                msgUnread = MessageIndex.getUnreadMessageCount(index);
                    }
                }
                // Tutor view
                else icon = iconAssignment;
                break;

            // ACTIVITY
            case ColloquiaComponent.ACTIVITY:
                // Set cuttable node if allowed to be cut
                if(!A.isMine() && parent.isComponentFree(A)) icon = iconActivityX;

                else if(A.isInvite()) {
                    if(A.getAcceptedState() == Activity.ACCEPTED) icon = iconActivityAccepted;
                    else if(A.getAcceptedState() == Activity.DECLINED) icon = iconActivityDeclined;
                    else icon = iconActivityFuture;
                }
                else if(A.isCompleted()) icon = iconActivityCompletedFolder;
                else if(A.isTemplate()) icon = iconActivityTemplateFolder;
                else if(A.isHot()) icon = iconActivityHot;
                else icon = iconActivityLiveFolder;

        		GroupMessageIndex index = new GroupMessageIndex(A.getGUID(), false);

                // Count number of unread messages
                msgUnread = MessageIndex.getUnreadMessageCount(index);

		        // Tooltip text
                int numMessages = MessageIndex.getTotalMessageCount(index);
                if(numMessages != -1) tip = strMessages + numMessages;
                break;

            default:
                icon = expanded ? iconOpenFolder : iconFolder;
        }

        if(msgUnread > 0) txt += " (" + msgUnread + " " + strUnread + ")";

        setText(txt);
        setIcon(icon);
        setToolTipText(tip);

        return this;
    }

}


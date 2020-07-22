package net.colloquia.views;

import java.awt.CardLayout;

import javax.swing.JPanel;

import net.colloquia.comms.MessageInfo;
import net.colloquia.comms.tables.Inbox;
import net.colloquia.comms.tables.InboxListener;
import net.colloquia.comms.wizard.AcceptActivityWizard;
import net.colloquia.datamodel.ComponentTransferListener;
import net.colloquia.datamodel.ComponentTransferManager;
import net.colloquia.datamodel.entities.Activity;
import net.colloquia.datamodel.entities.ColloquiaComponent;
import net.colloquia.datamodel.entities.ColloquiaContainer;
import net.colloquia.datamodel.entities.Person;
import net.colloquia.views.entities.*;

public class        ViewPanel
extends             JPanel
implements          ComponentTransferListener, InboxListener
{
    private ColloquiaView _currentView;
    
    private static final ViewPanel _instance = new ViewPanel(); // This
    
    public static ViewPanel getInstance() {
        return _instance;
    }
    
    private ViewPanel() {
        setLayout(new CardLayout());
        // Listen to Components
        ComponentTransferManager.addComponentTransferListener(this);
        // Listen to Inbox
        Inbox.getInstance().addInboxListener(this);
    }
    
    public void setComponent(ColloquiaComponent selComponent,
            ColloquiaContainer parentGroup, Activity currentActivity) {
        
        ColloquiaView view = determineView(selComponent, parentGroup, currentActivity);
        if(view != null) {
            view.setComponent(selComponent, parentGroup);
        }
        
        // remember
        _currentView = view;
        
        // A null value = blank view
        if(_currentView == null) {
            validate();
            repaint();
            return;
        }
        
        // Add new view to panel
        if(_currentView.getParent() != this) {
            add(_currentView, _currentView.getClass().getName());
        }
        
        ((CardLayout)getLayout()).show(this, _currentView.getClass().getName()); 
        
        // If the component is an Activity and its ACCEPTED state is PENDING
        // then launch the AcceptActivityWizard
        if(selComponent instanceof Activity) {
            Activity A = (Activity)selComponent;
            if(A.isPending()) {
                new AcceptActivityWizard(A);
            }
        }
    }
    
    /**
     * Returns the current view - but we have to wait for the ViewThread to finish
     * if it is running because we might return the wrong view.
     * This could be null!
     */
    public ColloquiaView getCurrentView() {
        return _currentView;
    }
    
    public void updateCurrentView() {
        ColloquiaView cv = getCurrentView();
        if(cv != null) {
            cv.updateView();
        }
    }
    
    public void repaintCurrentView() {
        ColloquiaView cv = getCurrentView();
        if(cv != null) {
            cv.repaint();
        }
    }
    
    /**
     * Determine the right-hand view depending on the selected node.
     */
    private ColloquiaView determineView(ColloquiaComponent selComponent,
            ColloquiaContainer parentGroup, Activity currentActivity) {
        
        ColloquiaView theView = null;
        
        switch(selComponent.getType()) {
            // Person
            // There are three Person views -
            // 1. Base People Folder view in top level folder
            // 2. Tutor's View of Person in a Live/Completed Activity created by me
            // 3. Student's View of Person in a Live/Completed Activity sent to me
            case ColloquiaComponent.PERSON:
                // Not in an Activity
                if(currentActivity == null) {
                    theView = BasePersonView.getInstance();
                }
                // In Live / Completed
                else if(currentActivity.isLive() || currentActivity.isCompleted()) {
                    Person person = (Person)selComponent;
                    if(currentActivity.getPersonAcceptedStatus(person) == Activity.ACCEPTED) {
                        if(currentActivity.isMine()) {
                            theView = TutorsPersonView.getInstance();
                        }
                        else {
                            theView = StudentsPersonView.getInstance();
                        }
                    }
                    else {
                        theView = BasePersonView.getInstance();
                    }
                }
                else {
                    theView = BasePersonView.getInstance();
                }
            break;
            
            // Resource
            case ColloquiaComponent.RESOURCE:
                theView = ResourceView.getInstance();
            break;
            
            // Assignment
            case ColloquiaComponent.ASSIGNMENT:
                if(selComponent.isMine()) {
                    theView = TutorsAssignmentView.getInstance();
                }
                else {
                    theView = StudentsAssignmentView.getInstance();
                }
            break;
            
            // Top Activity Group
            case ColloquiaComponent.ACTIVITY_GROUP:
                theView = ActivityGroupView.getInstance();
            break;
            
            // Activity
            case ColloquiaComponent.ACTIVITY:
                Activity A = (Activity)selComponent;
            if(A.isLive()) {
                theView = LiveActivityView.getInstance();
            }
            // Invite
            else if(A.isInvite()) {
                theView = ActivityInviteView.getInstance();
            }
            else if(A.isCompleted()) {
                theView = LiveActivityView.getInstance();
            }
            else if(A.isTemplate()) {
                theView = TemplateView.getInstance();
            }
            break;
            
            // Top Template Group
            case ColloquiaComponent.TEMPLATE_GROUP:
                theView = TemplateGroupView.getInstance();
            break;
            
            // Person Group
            case ColloquiaComponent.PERSON_GROUP:
                theView = PersonGroupView.getInstance();
            break;
            
            // Resource Group
            case ColloquiaComponent.RESOURCE_GROUP:
                theView = ResourceGroupView.getInstance();
            break;
            
            default:
                theView = null;
            break;
        }
        
        return theView;
    }
    
    public void componentCut(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        updateCurrentView();
    }
    
    public void componentPasted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        updateCurrentView();
    }
    
    public void componentMoved(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup) {
    }
    
    public void componentInserted(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
    }
    
    public void componentMarkedRemoved(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
    }
    
    
    public void newMessagesRecvd(MessageInfo[] mInfo) {
        ColloquiaView cv = getCurrentView();
        if(cv != null) {
            cv.repaint();
            if(cv instanceof MessageView) {
                ((MessageView)cv).reloadMessages();
            }
        }
    }
}

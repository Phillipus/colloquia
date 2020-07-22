package net.colloquia.comms.messages;

import java.io.*;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;


/**
* A received Assignment Message
*/
public class AssignmentMessageIn
extends TextMessageIn
{

    /**
    * Constructor
    */
    protected AssignmentMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }

    /**
    * Over-ride this so that we can set Assignment grade (if any)
    */
    public MessageInfo fileMessage(boolean deleteFile) throws ColloquiaFileException {
    	super.fileMessage(deleteFile);
        if(mInfo.getState() == MessageInfo.RCVD) addAssignmentGrade();
        return mInfo;
    }

    /*
    * If the Message is an AssignmentMessage and filed check whether we are the Student.
    * If it is, check whether there's a grade.  If there is, add it and save DataModel!
    */
    private void addAssignmentGrade() {
        // Do we have a Grade?
        String grade = mInfo.getAssignmentGrade();
        if(!grade.equals("")) {
            // Activity
            Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
            if(A == null || A.isMine()) return;   // Not for a Tutor!

            // Get Assignment
            Assignment assignment = A.getAssignment();
            if(assignment == null || A.isMine()) return; // Not for a Tutor!

            // Set grade
            A.setAssignmentGrade(assignment, grade);
        }
    }

}
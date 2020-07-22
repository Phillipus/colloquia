package net.colloquia.comms.messages;

import net.colloquia.comms.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;

public class AssignmentMessageOut
extends SingleMessageOut
{

    /**
    * Constructor
    */
    protected AssignmentMessageOut(MessageInfo mInfo) throws InvalidMessageException {
        super(mInfo);
    }

    /**
    * If we are a Tutor sending an Assignment Message
    * we need to set Assignment grade from this person's MemberInfo
    */
    protected MessageInfo createSentMessageInfo() {
        MessageInfo sentmInfo = super.createSentMessageInfo();

        Activity A = (Activity)DataModel.getComponent(mInfo.getActivityID());
        // Only if I'm the owner
        if(A != null && A.isMine()) {
            // Get Assignment grade (from MemberInfo)
            Person person = (Person)DataModel.getComponent(mInfo.getPersonID());
            if(person != null) {
                String grade = A.getPersonAssignmentGrade(person);
                sentmInfo.setAssignmentGrade(grade);
            }
        }

        return sentmInfo;
    }
}
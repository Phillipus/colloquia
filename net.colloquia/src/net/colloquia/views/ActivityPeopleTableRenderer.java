package net.colloquia.views;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

public class ActivityPeopleTableRenderer
extends ColloquiaTableRenderer
{
    ImageIcon icon;
    ImageIcon iconPerson = Utils.getIcon(ColloquiaConstants.iconPerson);
    ImageIcon iconPersonDeclined = Utils.getIcon(ColloquiaConstants.iconPersonDeclined);
    ImageIcon iconPersonPending = Utils.getIcon(ColloquiaConstants.iconPersonPending);
    ImageIcon iconPersonAwaiting = Utils.getIcon(ColloquiaConstants.iconPersonAwaiting);
    ImageIcon iconPersonX = new ImageIcon(GrayFilter.createDisabledImage(iconPerson.getImage()));
    Activity A;
    Vector people;

    public ActivityPeopleTableRenderer(Activity A, Vector people) {
        this.A = A;
        this.people = people;
    }

    public void setGroup(Activity A, Vector people) {
        this.A = A;
        this.people = people;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
        if(column == 0 && A != null && people != null) {
            Person person = (Person)people.elementAt(row);
            if(A.isPersonRemoved(person)) icon = iconPersonX;
            else if(A.getPersonAcceptedStatus(person) == Activity.DECLINED) icon = iconPersonDeclined;
            else if(A.getPersonAcceptedStatus(person) == Activity.PENDING) icon = iconPersonPending;
            else icon = A.doesPersonHaveActivity(person) ? iconPerson : iconPersonAwaiting;
            label.setIcon(icon);
        }
        else label.setIcon(null);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

}



package net.colloquia.views.entities;

import net.colloquia.comms.*;

public interface MessageView {

    void reloadMessages();
    void selectMessage(MessageInfo mInfo);
    void editMessage(MessageInfo mInfo);

}

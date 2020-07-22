package net.colloquia.comms.tables;

import net.colloquia.comms.*;

public interface InboxListener {
    void newMessagesRecvd(MessageInfo[] mInfo);
}
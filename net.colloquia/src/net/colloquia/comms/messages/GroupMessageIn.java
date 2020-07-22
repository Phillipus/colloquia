package net.colloquia.comms.messages;

import java.io.*;

import net.colloquia.comms.*;


/**
* A received Group Message
*/
public class GroupMessageIn
extends TextMessageIn
{

    /**
    * Constructor
    */
    protected GroupMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


}
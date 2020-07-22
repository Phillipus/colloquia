package net.colloquia.comms.messages;

import java.io.*;

import net.colloquia.comms.*;


/**
* A received Single Message
*/
public class SingleMessageIn
extends TextMessageIn
{

    /**
    * Constructor
    */
    protected SingleMessageIn(MessageInfo mInfo, File zipFile) {
        super(mInfo, zipFile);
    }


}

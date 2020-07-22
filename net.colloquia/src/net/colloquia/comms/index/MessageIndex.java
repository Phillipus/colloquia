package net.colloquia.comms.index;

import java.io.*;
import java.util.*;

import net.colloquia.comms.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.xml.*;

/**
* A message index containing MessageInfo classes
* We ALWAYS use the fileName and ActivityID to find an entry
*/
public abstract class MessageIndex
{
    protected Vector messages;    // Vector of MessageInfo class

    // The types of Index
    public static final int INBOX = 0;                      // UNREAD
    public static final int OUTBOX = 1;                     // UNSENT
    public static final int PENDINGBOX = 2;                 // UNMATCHED
    public static final int GROUP_MESSAGEBOX = 3;
    public static final int SINGLE_MESSAGEBOX = 4;
    public static final int ASSIGNMENT_MESSAGEBOX = 5;
    public static final int READBOX = 6;                    // READ
    public static final int SENTBOX = 7;                    // SENT

    public static String XMLStartTag = "<message_index>";
    public static String XMLEndTag = "</message_index>";

    // Keys to main properties
    public static final String TYPE = "type";
    public static final String SORTED_COLUMN = "sorted_column";
    public static final String ASCENDING = "ascending";
    public static final String COUNT = "count";
    public static final String UNREAD = "unread";

    protected Hashtable properties = new Hashtable();
    public Hashtable getProperties() { return properties; }

    public void putProperty(String key, String value) {
        if(key == null || value == null) return;
        if(key.equals("")) return;
        key = key.toLowerCase();
        properties.put(key, value);
    }

    public String getProperty(String key) {
        String value = (String)properties.get(key.toLowerCase());
        return value == null ? "" : value;
    }

    /**
    * Factory method for returning a MessageIndex consistent with mInfo
    */
    public static MessageIndex getMessageIndex(MessageInfo mInfo, boolean doLoad) {
        switch(mInfo.getMessageType()) {
            case MessageInfo.SINGLE_MESSAGE:
                return new SingleMessageIndex(mInfo.getActivityID(), mInfo.getPersonID(), doLoad);

            case MessageInfo.GROUP_MESSAGE:
                return new GroupMessageIndex(mInfo.getActivityID(), doLoad);

            case MessageInfo.ASSIGNMENT_MESSAGE:
                return new AssignmentMessageIndex(mInfo.getActivityID(), mInfo.getPersonID(), doLoad);

            default:
                return null;
        }
    }

    /**
    * Simple Constructor, no loading or specific settings
    */
    protected MessageIndex() {
        messages = new Vector();
        putProperty("version", "1.2");
        putProperty(TYPE, String.valueOf(getType()));
        setSortedColumn(0);
        setAscending(true);
    }

    /**
    * Find the message that corresponds to mInfo in its index, and set it to this
    * Then save the index - used for updating values of mInfo
    */
    public static void updateMessageInfo(MessageInfo mInfo) {
        MessageIndex index = getMessageIndex(mInfo, true);
        if(index == null) return;
        if(index.getMessage(mInfo) != null) {     // Got it
            index.addMessage(mInfo, true);        // Replace
            try {
                index.save();                     // Save
            }
            catch(XMLWriteException ex) {
                ErrorHandler.showWarning("ERR6", ex, "ERR");
            }
        }
    }

    /**
    * Return the number of messages in an index file by peeking into it
    * Returns -1 if unknown (this feature added in 1.3.2)
    */
    public static int getTotalMessageCount(MessageIndex mIndex) {
        int num = -1;

        File file = mIndex.getFileName(false);
        if(!file.exists()) return 0;

        String msgs = XMLUtils.getXMLKey(COUNT, file.getPath());

        try {
        	num = Integer.parseInt(msgs);
        }
        catch(NumberFormatException nex) {
        	return -1;
        }

        return num;
    }

    /**
    * Return the number of unread messages in an index file by peeking into it
    * (this feature added in 1.3.2)
    */
    public static int getUnreadMessageCount(MessageIndex mIndex) {
        int num = 0;

        File file = mIndex.getFileName(false);
        if(!file.exists()) return 0;

        String msgs = XMLUtils.getXMLKey(UNREAD, file.getPath());

        try {
        	num = Integer.parseInt(msgs);
        }
        catch(NumberFormatException nex) {
        	return 0;
        }

        return num;
    }

    /**
    * The full file name of this index
    */
    protected abstract File getFileName(boolean checkFolder);

    /**
    * The numerical type of this index
    */
    protected abstract int getType();

    //==================== CONVENIENCE METHODS ==============================

    public void setSortedColumn(int column) {
        putProperty(SORTED_COLUMN, String.valueOf(column));
    }

    public int getSortedColumn() {
        String column = getProperty(SORTED_COLUMN);
        return Integer.parseInt(column);
    }

    public void setAscending(boolean ascending) {
        putProperty(ASCENDING, ascending ? "true" : "false");
    }

    public boolean isAscending() {
        String val = getProperty(ASCENDING);
        return val.equalsIgnoreCase("true");
    }


    /**
    * Add a message entry
    * If the message exists we replace the entry if required
    */
    public void addMessage(MessageInfo mInfo, boolean attemptReplace) {
        // Replace attempt?
        if(attemptReplace) {
            for(int i = 0; i < messages.size(); i++) {
                MessageInfo mi = (MessageInfo)messages.elementAt(i);
                if(mInfo.matches(mi)) {
                    messages.setElementAt(mInfo, i);
                    return;
                }
            }
        }
        // Couldn't find replacement so add to end
        messages.addElement(mInfo);
    }

    /*
    * Return a message using a match
    * Return null if not found
    */
    public MessageInfo getMessage(MessageInfo template) {
        for(int i = 0; i < messages.size(); i++) {
            MessageInfo mi = (MessageInfo)messages.elementAt(i);
            if(template.matches(mi)) return mi;
        }
        return null;
    }

    /**
    * Delete a message from the index
    * It also deletes the linking content (html file)
    * But if we're deleting from the INBOX/OUTBOX/SENTBOX/READBOX we just delete the MessageInfo
    * mInfo need not be the real object, just a match
    * It's up to the caller to save the index!
    */
    public boolean deleteMessage(MessageInfo mInfo) {
        if(mInfo == null) return false;

        // Delete the referenced html file
        int indexType = getType();
        String folder;

        switch(indexType) {
        	case PENDINGBOX:
	            folder = DataFiler.getPendingMailFolder(false);
    	        DataFiler.deleteFile(folder + mInfo.getFileName());
                break;
            case GROUP_MESSAGEBOX:
            case SINGLE_MESSAGEBOX:
            case ASSIGNMENT_MESSAGEBOX:
    	        folder = mInfo.getFolderForMessage(false);
	            DataFiler.deleteFile(folder + mInfo.getFileName());
                break;

        }

        return deleteEntry(mInfo);
    }

    /**
    * Deletes an entry - mInfo acts as a template
    * Returns true if deleted
    */
    protected boolean deleteEntry(MessageInfo mInfo) {
        MessageInfo mi = getMessage(mInfo);
        if(mi != null) return messages.removeElement(mi);
        else return false;
    }

    public int size() {
        return messages.size();
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public MessageInfo getMessageAt(int index) {
        return (MessageInfo)messages.elementAt(index);
    }

    public Vector getMessages() {
        return messages;
    }

    public MessageInfo[] getUnreadMessages() {
        Vector v = new Vector();
        for(int i = 0; i < messages.size(); i++) {
            MessageInfo mInfo = (MessageInfo)messages.elementAt(i);
            if(!mInfo.isSeen()) v.addElement(mInfo);
        }
        if(v.isEmpty()) return null;

        MessageInfo mInfo[] = new MessageInfo[v.size()];
        v.copyInto(mInfo);
        return mInfo;
    }

    protected int countUnreadMessages() {
        int count = 0;
        for(int i = 0; i < messages.size(); i++) {
            MessageInfo mInfo = (MessageInfo)messages.elementAt(i);
            if(!mInfo.isSeen()) count++;
        }
        return count;
    }

    /**
    * Save this MessageIndex in XML format
    */
    public void save() throws XMLWriteException {
		File tmpBak = null;

        // Write to a temp file first in case of disaster
        String tmp = DataFiler.generateTmpFileName(DataFiler.getTempFolder(true));
        File tmpFile = new File(tmp);

    	XMLFileWriter writer = new XMLFileWriter(tmpFile);
        writer.open();
        write2XML(writer);
        writer.close();

        // Uh oh.......
        if(!tmpFile.exists() || tmpFile.length() == 0) {
            System.out.println("Error saving data!");
            return;
        }

        File indexFile = getFileName(true);

        //If exists, rename to temp file
        if(indexFile.exists()) {
            String tmp1 = DataFiler.addFileSepChar(indexFile.getParent());
            tmp1 = DataFiler.generateTmpFileName(tmp1);
            tmpBak = new File(tmp1);
            indexFile.renameTo(tmpBak);
        }

        // Now copy over
        tmpFile.renameTo(indexFile);

        // Delete temp
        if(tmpBak != null) tmpBak.delete();
    }

    protected void write2XML(XMLWriter writer) throws XMLWriteException {
        writer.writeln(XMLStartTag);

        // Message Count
        putProperty(COUNT, String.valueOf(size()));

        // Unread Messages
        putProperty(UNREAD, String.valueOf(countUnreadMessages()));

        // Main properties
        Hashtable props = getProperties();
        Enumeration e = props.keys();
        while(e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            String value = (String)props.get(tag);
            writer.write(new XMLTag(tag, value));
        }

        // Messages
        writer.writeln("<messages>");
        for(int i = 0; i < messages.size(); i++) {
            MessageInfo mInfo = (MessageInfo)messages.elementAt(i);
            mInfo.write2XML(writer);
        }
        writer.writeln("</messages>");

        // Close
        writer.writeln(XMLEndTag);
    }

    /**
    * Read xml from disk
    */
    protected void load() {
        File file = getFileName(false);

        if(!file.exists()) return;             // Must be a blank one

        MessageInfo mInfo = null;
        String line, tmpLine;
        XMLTag xmlTag;

        try {
            XMLFileReader reader = new XMLFileReader(file);
            reader.open();

            while((line = reader.readLine()) != null) {
                // Do we have a tag?
                xmlTag = XMLTag.getXMLTag(line);

                if(xmlTag != null) {
                    // Is it an index property or a MessageInfo property?
                    if(mInfo != null) mInfo.putProperty(xmlTag.tag, xmlTag.value);
                    else putProperty(xmlTag.tag, xmlTag.value);
                    continue;
                }

                // Get lower case line
                tmpLine = line.toLowerCase();

                // New MessageInfo
                if(tmpLine.indexOf(MessageInfo.XMLStartTag) != -1) {
                    mInfo = new MessageInfo();
                    continue;
                }

                // End of new MessageInfo
                if((tmpLine.indexOf(MessageInfo.XMLEndTag) != -1) && (mInfo != null)) {
                    addMessage(mInfo, false);
                    mInfo = null;
                    continue;
                }
            }

            reader.close();
        }
        catch(XMLReadException ex) {
            ErrorHandler.showWarning("ERR9", ex, "ERR");
        }
    }


    //========================================================================
    // MESSAGE SORTING
    //========================================================================

    protected static final int SORT_FROM = 1;
    protected static final int SORT_TO = 2;
    protected static final int SORT_ACTIVITY = 3;
    protected static final int SORT_SUBJECT = 4;
    protected static final int SORT_DATE_SENT = 5;
    protected static final int SORT_DATE_RCVD = 6;

    protected abstract int getSortKey(int column);

    /**
    * Sorts Messages on a specified index value which relates to a table column
    */
    public void sort(int column) {
        // Map it
        int sort_item = getSortKey(column);
        // Zero means do not sort
        if(sort_item == 0) return;

        // If it's already the current sorted column
        // then just reverse the sorting order
        if(column == getSortedColumn()) setAscending(!isAscending());
        else setSortedColumn(column);

        QuickSort(0, messages.size() - 1, sort_item);

        // Reverse the vector if descending
        if(!isAscending()) {
            Vector v = new Vector();
            for(int i = messages.size() - 1; i >= 0; i--) v.add(messages.get(i));
            messages = v;
        }
    }

    private void QuickSort(int lo0, int hi0, int sort_item) {
        if(lo0 >= hi0) return;

        int lo = lo0;
        int hi = hi0;

        // Sort a two element list by swapping if necessary
        if(lo == hi - 1) {
            if(compare(messages, lo, hi, sort_item) > 0) swap(messages, lo, hi);
            return;
        }

        // Pick a pivot and move it out of the way
        MessageInfo pivot = (MessageInfo)messages.elementAt((lo + hi) / 2);
        swap(messages, (lo + hi) / 2, hi);

        while(lo < hi) {
            // Search forward from a[lo] until an element is found that is greater than the pivot or lo >= hi
            while(compare((MessageInfo)messages.elementAt(lo), pivot, sort_item) <= 0 && lo < hi) lo++;

            // Search backward from a[hi] until element is found that is less than the pivot, or lo >= hi
            while(compare(pivot, (MessageInfo)messages.elementAt(hi), sort_item) <= 0 && lo < hi) hi--;

            // Swap elements a[lo] and a[hi]
            if(lo < hi) swap(messages, lo, hi);
        }

        // Put the median in the "center" of the list
        messages.setElementAt(messages.elementAt(hi), hi0);
        messages.setElementAt(pivot, hi);

        QuickSort(lo0, lo - 1, sort_item);
        QuickSort(hi + 1, hi0, sort_item);
    }

    private int compare(Vector messages, int i, int j, int sort_item) {
        MessageInfo msg1 = (MessageInfo)messages.elementAt(i);
        MessageInfo msg2 = (MessageInfo)messages.elementAt(j);
        return compare(msg1, msg2, sort_item);
    }

    private int compare(MessageInfo msg1, MessageInfo msg2, int sort_item) {
        Object item1 = "", item2 = "";

        switch(sort_item) {
            case SORT_FROM:
                item1 = msg1.getFrom();
                item2 = msg2.getFrom();
                break;
            case SORT_TO:
                item1 = msg1.getTo();
                item2 = msg2.getTo();
                break;
            case SORT_ACTIVITY:
                item1 = msg1.getActivityName();
                item2 = msg2.getActivityName();
                break;
            case SORT_SUBJECT:
                // Ignore "Re: "
                int index1 = msg1.getSubject().lastIndexOf("Re:");
                int index2 = msg2.getSubject().lastIndexOf("Re:");
                if(index1 == -1) index1 = 0;
                else index1 += 4;
                if(index2 == -1) index2 = 0;
                else index2 += 4;
                item1 = msg1.getSubject().substring(index1).trim();
                item2 = msg2.getSubject().substring(index2).trim();
                break;
            case SORT_DATE_SENT:
                item1 = msg1.getDateSent();
                item2 = msg2.getDateSent();
                break;
            case SORT_DATE_RCVD:
                item1 = msg1.getDateRcvd();
                item2 = msg2.getDateRcvd();
                break;
        }

        return Utils.compare(item1, item2);
    }

    private void swap(Vector messages, int i, int j) {
        MessageInfo msg1 = (MessageInfo)messages.elementAt(i);
        MessageInfo msg2 = (MessageInfo)messages.elementAt(j);
        messages.setElementAt(msg1, j);
        messages.setElementAt(msg2, i);
    }

/**
    public void oldsort(int column) {
        // Map it
        int sort = getSortKey(column);
        // Zero means do not sort
        if(sort == 0) return;

        // If it's already the current sorted column
        // then just reverse the sorting order
        if(column == getSortedColumn()) setAscending(!isAscending());
        else setSortedColumn(column);

        Object item1 = "", item2 = "";

        for(int i = 0; i < messages.size(); i++) {
            for(int j = 0; j < messages.size() - 1; j++) {
                MessageInfo msg1 = (MessageInfo)messages.elementAt(j);
                MessageInfo msg2 = (MessageInfo)messages.elementAt(j + 1);
                switch(sort) {
                    case SORT_FROM:
                        item1 = msg1.getFrom();
                        item2 = msg2.getFrom();
                        break;
                    case SORT_TO:
                        item1 = msg1.getTo();
                        item2 = msg2.getTo();
                        break;
                    case SORT_ACTIVITY:
                        item1 = msg1.getActivityName();
                        item2 = msg2.getActivityName();
                        break;
                    case SORT_SUBJECT:
                        // Ignore "Re: "
                        int index1 = msg1.getSubject().lastIndexOf("Re:");
                        int index2 = msg2.getSubject().lastIndexOf("Re:");
                        if(index1 == -1) index1 = 0;
                        else index1 += 4;
                        if(index2 == -1) index2 = 0;
                        else index2 += 4;
                        item1 = msg1.getSubject().substring(index1).trim();
                        item2 = msg2.getSubject().substring(index2).trim();
                        break;
                    case SORT_DATE_SENT:
                        item1 = msg1.getDateSent();
                        item2 = msg2.getDateSent();
                        break;
                    case SORT_DATE_RCVD:
                        item1 = msg1.getDateRcvd();
                        item2 = msg2.getDateRcvd();
                        break;
                }
                int comparison = Utils.compare(item1, item2);
                if((isAscending() && comparison > 0) || (!isAscending() && comparison < 0)) {
                    messages.removeElementAt(j);
                    messages.insertElementAt(msg1, j + 1);
                }
            }
        }
    }
*/
}

package net.colloquia.datamodel;

import net.colloquia.datamodel.entities.*;

public interface ComponentTransferListener {
    void componentMarkedRemoved(ColloquiaComponent tc, ColloquiaContainer parentGroup);
    void componentCut(ColloquiaComponent tc, ColloquiaContainer parentGroup);
    void componentPasted(ColloquiaComponent tc, ColloquiaContainer parentGroup);
    void componentMoved(ColloquiaComponent tc, ColloquiaContainer sourceGroup, ColloquiaContainer targetGroup);
    void componentInserted(ColloquiaComponent tc, ColloquiaContainer parentGroup);
}

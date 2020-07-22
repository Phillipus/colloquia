package net.colloquia.datamodel;

import java.util.*;

import net.colloquia.datamodel.entities.*;

public interface DataListener {

    void newDataCreated();
    void componentAdded(ColloquiaComponent tc, ColloquiaContainer group);
    void componentRemoved(ColloquiaComponent tc, ColloquiaContainer group);
    void componentOrderChanged(Vector members, ColloquiaContainer group);
}
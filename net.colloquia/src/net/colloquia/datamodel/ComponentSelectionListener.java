package net.colloquia.datamodel;

import net.colloquia.datamodel.entities.*;

public interface ComponentSelectionListener {

    void componentSelected(ColloquiaComponent selComponent,
                            ColloquiaContainer parentGroup,
                            Activity currentActivity);

}
package net.colloquia.views.datatable;


/**
* A Panel that holds the table for displaying metadata for an object.
*/
public class ResourceDataSheet
extends BaseDataSheet
{

    public ResourceDataSheet() {
        super(new ResourceTableModel());
    }

}

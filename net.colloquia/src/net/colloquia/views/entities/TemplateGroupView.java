package net.colloquia.views.entities;

import java.awt.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.views.summary.*;

public class TemplateGroupView extends ColloquiaView {
    private Template_SummaryView summaryView;
    private static final TemplateGroupView instance = new TemplateGroupView();

    private TemplateGroupView() {
        summaryView = new Template_SummaryView();
        setLayout(new BorderLayout());
        add(summaryView, BorderLayout.CENTER);
    }

    public static TemplateGroupView getInstance() {
        return instance;
    }

    public void setComponent(ColloquiaComponent tc, ColloquiaContainer parentGroup) {
        if(!(tc instanceof TemplateGroup) || parentGroup == null) return;
        this.tc = tc;
        this.parentGroup = parentGroup;
        MainFrame.getInstance().statusBar.setText(msgLoading + tc.getName() + ".......");
        summaryView.setGroup((TemplateGroup)tc);
        MainFrame.getInstance().statusBar.clearText();
    }

    public void updateView() {
        setComponent(tc, parentGroup);
    }
}


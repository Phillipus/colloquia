package net.colloquia.menu;

import java.awt.event.*;

import net.colloquia.*;
import net.colloquia.io.*;
import net.colloquia.util.*;

/**
 */
public class Action_Save extends MenuAction {
	private int nagInterval = 0;
    private javax.swing.Timer timer;

    public Action_Save(int nagInterval) {
        super(LanguageManager.getString("SAVE"), ColloquiaConstants.iconSave,
            MainFrame.getInstance().statusBar);
        setButtonText(LanguageManager.getString("BUT3"));
        setNagInterval(nagInterval);
    }

    /**
    */
    public void actionPerformed(ActionEvent e) {
        try {
            DataFiler.saveAll();
            setMenuIcon(Utils.getIcon(ColloquiaConstants.iconSave));
            setButtonIcon(Utils.getIcon(ColloquiaConstants.iconSave));
            if(timer != null) timer.restart();
        }
        catch(Exception ex) {
            ErrorHandler.showWarning("ERR2", ex, "ERR");
        }
    }

    public void setNagInterval(int mins) {
    	nagInterval = mins;
        if(mins == 0) nagSaveStop();
        else nagSaveStart(mins);
    }

    public void nagSaveStart(int mins) {
        if(timer != null && timer.isRunning()) return;

    	ActionListener listener = new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        //DataModel.setActivitiesHot();
                setMenuIcon(Utils.getIcon(ColloquiaConstants.iconSaveHot));
                setButtonIcon(Utils.getIcon(ColloquiaConstants.iconSaveHot));
            }
        };

        timer = new javax.swing.Timer(mins*60000, listener);
        timer.start();
    }

    public void nagSaveStop() {
    	if(timer != null) timer.stop();
    }


}



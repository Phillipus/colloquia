package net.colloquia.comms.editors;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;


/**
* Views read-only e-mails in a floating window
*/
public class EMailFloatViewer extends JFrame {
    private EMailViewer viewer;
    private MessageInfo mInfo;
    // Instances of the viewer
    protected static Hashtable instances = new Hashtable();

    private EMailFloatViewer(MessageInfo mInfo) {
        this.mInfo = mInfo;
        setIconImage(Utils.getIcon(ColloquiaConstants.iconAppIcon).getImage());
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Get Icon
        setIconImage(mInfo.getIcon().getImage());

        setTitle(mInfo.getSubject());

        viewer = new EMailViewer();
        getContentPane().add(viewer);
        viewer.showMessage(mInfo);

        ColloquiaToolBar toolBar = createToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);

        // Centre on screen
        Utils.centreWindow(this, Utils.getScreenWidth()/2, Utils.getScreenHeight()/2);
        setVisible(true);

        registerWindow();
    }


    public static JFrame show(MessageInfo mInfo) {
        JFrame win = getWindow(mInfo);
        if(win != null) {
            win.toFront();
            win.requestFocus();
            return win;
        }
        else return new EMailFloatViewer(mInfo);
    }

    /**
    * Query whether we have an instance of the Editor open with a specific
    * Message.  If we do, return a handle to this window so we can focus it, else null.
    */

    private static JFrame getWindow(MessageInfo mInfo) {
        return (JFrame)instances.get(mInfo.getFullFileName());
    }

    private void registerWindow() {
       instances.put(mInfo.getFullFileName(), this);
    }

    private void unregisterWindow() {
        if(mInfo != null) instances.remove(mInfo.getFullFileName());
    }

    /**
    * Must do this!
    */
    public void dispose() {
        unregisterWindow();
        super.dispose();
    }

    protected ColloquiaToolBar createToolBar() {
		ColloquiaToolBar toolBar = new ColloquiaToolBar();

        return toolBar;
    }
}

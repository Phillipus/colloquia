package net.colloquia.views.help;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;


/**
 * Help window
 */
public class HelpWindow
extends JFrame
implements HyperlinkListener
{
    private static final HelpWindow instance = new HelpWindow();

    HelpTree helpTree;
    JEditorPane viewer;

    private HelpWindow() {
        setIconImage(Utils.getIcon(ColloquiaConstants.iconAppIcon).getImage());
        setTitle(ColloquiaConstants.APP_NAME + " " + LanguageManager.getString("HELP"));

        getContentPane().setLayout(new BorderLayout());

        constructToolBar();

        helpTree = new HelpTree(this);

        viewer = new JEditorPane();
        viewer.setEditable(false);
        viewer.addHyperlinkListener(this);
        viewer.setContentType("text/html");

        PSplitPane sp1 = new PSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp1.setLeftComponent(new JScrollPane(helpTree));
        sp1.setRightComponent(new JScrollPane(viewer));

        getContentPane().add(sp1, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Centre on screen
        Utils.centreWindow(this, 720, 520);
        helpTree.selectNode(helpTree.treeModel.getRootNode());
    }

    public static HelpWindow getInstance() {
        return instance;
    }

    /**
    * Just re-display the Help Window
    */
    public void showHelp() {
        setVisible(true);
        setExtendedState(JFrame.NORMAL);
        requestFocus();
    }

    public void setPage(URL url) {
        try {
            if(url != null && url.getFile().endsWith(".html")) {
                viewer.setPage(url);
            }
        }
        catch(Exception ex) {
            ErrorHandler.showWarning(this, "ERR20", ex, "HELP");
        }
    }

    private void constructToolBar() {
        ColloquiaToolBar toolBar = new ColloquiaToolBar();

        //toolBar.add(new Action_Back());

        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    /**
	* If url starts with http:// launch external
    */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = e.getURL();
            if(url != null) {
                String s = url.toString();
                if(s.startsWith("http")) {
                    try {
                		BrowserLauncher.openURL(s);
                    }
                    catch(IOException ex) {
                    	ErrorHandler.showWarning(this, "ERR20", ex, "HELP");
                    }
                }
            	else setPage(e.getURL());
            }
        }
    }
}




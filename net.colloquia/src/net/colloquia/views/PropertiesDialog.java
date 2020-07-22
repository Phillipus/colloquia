package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

public class PropertiesDialog extends JDialog {

    public PropertiesDialog(ColloquiaComponent tc) {
        super(MainFrame.getInstance(), LanguageManager.getString("5_1"), true);
        setResizable(false);

        Runtime rt = Runtime.getRuntime();

        // Add OK Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
  	    btnOK.addActionListener(new btnOKClick());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Add info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        infoPanel.setLayout(new GridLayout(10, 2, 5, 5));
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        if(tc != null) {
            // Name
            infoPanel.add(new PLabel(LanguageManager.getString("5_2")));
            infoPanel.add(new PLabel(tc.getName()));
            // Blank
            infoPanel.add(new PLabel(""));
            infoPanel.add(new PLabel(""));
            // Date Created
            infoPanel.add(new PLabel(LanguageManager.getString("5_3")));
            String date = Utils.parseDate(tc.getPropertyDate(ColloquiaComponent.DATE_CREATED));
            infoPanel.add(new PLabel(date));
            // Date Modified
            infoPanel.add(new PLabel(LanguageManager.getString("5_4")));
            date = Utils.parseDate(tc.getPropertyDate(ColloquiaComponent.DATE_MODIFIED));
            infoPanel.add(new PLabel(date));
            // ID
            infoPanel.add(new PLabel("GUID:"));
            infoPanel.add(new PLabel(tc.getGUID()));
            // Instances
            infoPanel.add(new PLabel(LanguageManager.getString("5_5")));
            infoPanel.add(new PLabel("" + tc.getInstanceCount()));
        }

        // Blank
        infoPanel.add(new PLabel(""));
        infoPanel.add(new PLabel(""));
        // Free Memory
        float freeMem = (float)rt.freeMemory() /  1048576;
        infoPanel.add(new PLabel(LanguageManager.getString("5_6")));
        infoPanel.add(new PLabel("" + freeMem + " MB"));
        // Total Memory
        float totalMem = (float)rt.totalMemory() /  1048576;
        infoPanel.add(new PLabel(LanguageManager.getString("5_7")));
        infoPanel.add(new PLabel("" + totalMem + " MB"));

        // Centre it in relation to our main frame
        setSize(400, 310);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
    }

    /** Close */
    private class btnOKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}

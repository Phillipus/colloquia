package net.colloquia.gui.widgets;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.util.*;

/**
 */
public abstract class InputDialog {
    protected String value;
    protected JDialog dialog;
    protected JTextField tField;
    protected JLabel tLabel;
    protected JButton ok;
    protected JButton cancel;
    protected ActionListener cancelListener;
    protected ActionListener okListener;
    protected JPanel buttonPanel;
    protected JPanel inPanel;
    protected Frame owner;
    protected String message;
    protected String title;
    protected GridLayout gridLayout;

    protected int retValue;

    public InputDialog(Frame owner, String message, String title) {
        this.owner = owner;
        this.message = message;
        this.title = title;

        dialog = new JDialog(owner, title, true);
        dialog.setResizable(false);

        tField = new JTextField();
        tLabel = new JLabel(message);

        ok = new JButton(LanguageManager.getString("OK"));
        ok.setPreferredSize(new Dimension(75, 25));

        cancel = new JButton(LanguageManager.getString("CANCEL"));
        cancel.setPreferredSize(new Dimension(95, 25));

        cancelListener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                cancelClicked();
            }
        };

        cancel.addActionListener(cancelListener);

        okListener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                okClicked();
            }
        };

        ok.addActionListener(okListener);


        tField.addActionListener(okListener);

        buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        gridLayout = new GridLayout(1, 2);
        inPanel = new JPanel(gridLayout);
        inPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        inPanel.add(tLabel);
        inPanel.add(tField);

        dialog.getRootPane().setDefaultButton(ok);

        Container contentPane = dialog.getContentPane();
        contentPane.add(inPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    protected void setGridSize(int rows, int columns) {
    	gridLayout.setRows(rows);
        gridLayout.setColumns(columns);
    }

    protected void okClicked() {
        value = tField.getText();
        retValue = 0;
        dialog.dispose();
    }

    protected void cancelClicked() {
        value = null;
        retValue = -1;
        dialog.dispose();
    }

    /**
     * Should return user input -1 is cancel
     * @return
     */
    public abstract int show();

    public String getValue() {
    	return value;
    }
}
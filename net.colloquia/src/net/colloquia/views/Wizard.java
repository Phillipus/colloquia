package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.util.*;



/**
 * The main dialog abstract wizard
 */
public abstract class Wizard
extends JDialog
implements ActionListener
{
    protected String title = "";
    protected JTextPane messagePanel;
    protected WizardPage currentPage;
    protected JPanel buttonPanel;

    protected JButton btnPrevious;
    protected JButton btnNext;
    protected JButton btnFinish;
    protected JButton btnCancel;

    protected Wizard() {
        super(MainFrame.getInstance(), true);

        setResizable(false);

        messagePanel = new JTextPane();
        messagePanel.setOpaque(false);
        messagePanel.setEditable(false);
        messagePanel.setFont(new Font("SansSerif", Font.BOLD, 12));
        messagePanel.setMargin(new Insets(10, 10, 10, 10));
        getContentPane().add(messagePanel, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setOpaque(false);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        addButtons();
    }

    public void setHeading(String title) {
        this.title = title;
    }

    public void setColor(Color color) {
    	getContentPane().setBackground(color);
    }

    /**
    * Set the wizard panel
    */
    protected void setPanel(WizardPage page) {
        setTitle(title + " - " + LanguageManager.getString("17_1") + " " + page.getPageNum());
        messagePanel.setText(page.getMessage());

        btnPrevious.setEnabled(page.getBtnPreviousState());
        btnNext.setEnabled(page.getBtnNextState());
        btnFinish.setEnabled(page.getBtnFinishState());
        btnCancel.setEnabled(page.getBtnCancelState());

        // Main Panel
        if(currentPage != null) getContentPane().remove(currentPage);
        getContentPane().add(page, BorderLayout.CENTER);
        page.repaint();
        currentPage = page;
    }

    /**
    * Add buttons to the bottom button panel
    */
    protected void addButtons() {
        btnPrevious = new JButton(LanguageManager.getString("17_2"));
        buttonPanel.add(btnPrevious);
        btnPrevious.addActionListener(this);

        btnNext = new JButton(LanguageManager.getString("17_3"));
        buttonPanel.add(btnNext);
        btnNext.addActionListener(this);

        btnFinish = new JButton(LanguageManager.getString("FINISH"));
        buttonPanel.add(btnFinish);
        btnFinish.addActionListener(this);

        btnCancel = new JButton(LanguageManager.getString("CANCEL"));
        buttonPanel.add(btnCancel);
        btnCancel.addActionListener(this);
    }



    /**
    * Abstract definition of a wizard page
    */
    protected abstract class WizardPage extends JPanel {
    	public WizardPage() {
        	setOpaque(false);
        }

        public abstract int getPageNum();
        public abstract String getMessage();
        public abstract boolean getBtnPreviousState();
        public abstract boolean getBtnNextState();
        public abstract boolean getBtnFinishState();
        public abstract boolean getBtnCancelState();
    }

    /**
    * Click a button listener
    */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(btnPrevious)) buttonPreviousClicked(currentPage);
        else if(e.getSource().equals(btnNext)) buttonNextClicked(currentPage);
        else if(e.getSource().equals(btnFinish)) buttonFinishClicked(currentPage);
        else if(e.getSource().equals(btnCancel)) buttonCancelClicked(currentPage);
    }

    protected abstract void buttonPreviousClicked(WizardPage currentPage);
    protected abstract void buttonNextClicked(WizardPage currentPage);
    protected abstract void buttonFinishClicked(WizardPage currentPage);
    protected abstract void buttonCancelClicked(WizardPage currentPage);

}
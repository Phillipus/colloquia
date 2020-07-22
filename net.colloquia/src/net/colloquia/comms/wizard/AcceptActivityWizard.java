package net.colloquia.comms.wizard;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.comms.tables.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;


/**
 * Accept/Decline an Activity
 */
public class AcceptActivityWizard
extends SendWizard
{
    String myEmailAddress;
    String myGivenName;
    String myFamilyName;

    private Page1 page1;
    private Activity A;
    private boolean accepted = true;

    UserPrefs prefs;

    public AcceptActivityWizard(Activity A) {
        this.A = A;

        // Get sender's details
        prefs = UserPrefs.getUserPrefs();
        myEmailAddress = prefs.getProperty(UserPrefs.EMAIL_ADDRESS);
        myGivenName = prefs.getProperty(UserPrefs.GIVEN_NAME);
        myFamilyName = prefs.getProperty(UserPrefs.FAMILY_NAME);

        setHeading(LanguageManager.getString("17_44"));
        setColor(ColloquiaConstants.color2);
        page1 = new Page1();
        setPanel(page1);
        setVisible(true);
    }

    /**
    * Page 1 - Accept Decline
    */
    private class Page1 extends SendWizardPage {
        PTextField tfmyEmailAddress;
        PTextField tfmyGivenName;
        PTextField tfmyFamilyName;
        private ButtonGroup bg;
    	private MinkyRadioButton rb1;
    	private MinkyRadioButton rb2;

        public Page1() {
            setLayout(new BorderLayout());

            JPanel dPanel = new JPanel();
            dPanel.setOpaque(false);
            dPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);

            tfmyEmailAddress = new PTextField(myEmailAddress, 20);
            tfmyGivenName = new PTextField(myGivenName, 20);
            tfmyFamilyName = new PTextField(myFamilyName, 20);

            c.gridx = 0; c.gridy = 0;
            dPanel.add(new PLabel(LanguageManager.getString("17_8") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyEmailAddress, c);

            c.gridx = 0; c.gridy = 1;
            dPanel.add(new PLabel(LanguageManager.getString("17_9") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyGivenName, c);

            c.gridx = 0; c.gridy = 2;
            dPanel.add(new PLabel(LanguageManager.getString("17_10") + ":"), c);
            c.gridx = 1;
            dPanel.add(tfmyFamilyName, c);

            add(dPanel, BorderLayout.NORTH);

            // Accept
            JPanel rbPanel = new JPanel();
            rbPanel.setOpaque(false);
        	rb1 = new MinkyRadioButton(LanguageManager.getString("17_42"), true);
            // Decline
        	rb2 = new MinkyRadioButton(LanguageManager.getString("17_43"), false);
            bg = new ButtonGroup();
            bg.add(rb1);
            bg.add(rb2);
        	rbPanel.add(rb1);
        	rbPanel.add(rb2);
            add(rbPanel, BorderLayout.CENTER);

            rb1.addActionListener(new Listener());
            rb2.addActionListener(new Listener());
		}

        /**
        * Make sure that the sender has filled in stuff
        */
        public boolean checkSenderDetails() {
            String errMsg = LanguageManager.getString("17_11") + ":\n";
            boolean correct = true;

            // Check my e-mail address
            if(tfmyEmailAddress.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_8") + ".\n";
                correct = false;
            }

            // Check my Given Name
            if(tfmyGivenName.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_9") + ".\n";
                correct = false;
            }

            // Check my Family Name
            if(tfmyFamilyName.getText().trim().length() == 0) {
                errMsg += LanguageManager.getString("17_10") + ".\n";
                correct = false;
            }

            if(!correct) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    errMsg,
                    LanguageManager.getString("ERROR"),
                    JOptionPane.ERROR_MESSAGE);
            }

            return correct;
        }

        class Listener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                accepted = rb1.isSelected();
            }
        }

        public int getPageNum() {
            return 1;
        }

        public String getMessage() {
            return LanguageManager.getString("17_45") + "\n" + LanguageManager.getString("17_46")
            	+ "\n" + LanguageManager.getString("17_47");
        }

        public boolean getBtnPreviousState() {
            return false;
        }

        public boolean getBtnNextState() {
            return false;
        }

        public boolean getBtnFinishState() {
            return true;
        }

        public boolean getBtnOutboxState() {
            return true;
        }

        public boolean getBtnCancelState() {
            return true;
        }
    }

    /**
    * Hit the << Prev button in the dialog box
    */
    protected void buttonPreviousClicked(WizardPage currentPage){
    }

    protected void buttonNextClicked(WizardPage currentPage) {
    }

    protected void buttonOutboxClicked(WizardPage currentPage) {
        sendAcceptance(false);
    }

    protected void buttonFinishClicked(WizardPage currentPage) {
        sendAcceptance(true);
    }

    protected void sendAcceptance(boolean sendNow) {
        if(page1.checkSenderDetails()) {
            // Save some stuff that the user might have changed
            prefs.putProperty(UserPrefs.EMAIL_ADDRESS, page1.tfmyEmailAddress.getText());
            prefs.putProperty(UserPrefs.GIVEN_NAME, page1.tfmyGivenName.getText());
            prefs.putProperty(UserPrefs.FAMILY_NAME, page1.tfmyFamilyName.getText());
            MessageInfo mInfo = makeMessageInfo();
            if(mInfo != null) Outbox.getInstance().addMessage(MainFrame.getInstance(), mInfo, sendNow);
            dispose();
        }
    }

    protected void buttonCancelClicked(WizardPage currentPage) {
        dispose();
    }

    /**
    * Make a MessageInfo class to send to the Outbox
    */
    private MessageInfo makeMessageInfo() {
        MessageInfo mInfo = new MessageInfo(MessageInfo.ACTIVITY_ACCEPT);

        // Set some properties
        mInfo.setSubject(LanguageManager.getString("15"));
        mInfo.setState(MessageInfo.PENDING);
        mInfo.setTo(A.getSubmitter());
        mInfo.setActivityName(A.getName());
        mInfo.setActivityID(A.getGUID());

        // Accepted
        mInfo.setAccepted(accepted);

        // Recipient
        mInfo.addRecipient(A.getSubmitter());

        // Photograph as attachment if accepted
        // PENDING - check if allowed to send
        if(accepted) {
        	String fileName = UserPrefs.getUserPrefs().getProperty(UserPrefs.PHOTOGRAPH);
        	if(!fileName.equals("")) mInfo.addAttachment(fileName);
        }

        return mInfo;
    }

    /*
    * Return the topmost Invited Activity from the one selected
    * Conditions:
    * Parent is Activity
    * Parent's submitter == this Submitter
    */
    protected Activity getTopActivity(Activity A, String submitter) {
        ColloquiaContainer parent = A.getParent();
        if(parent instanceof Activity) {
            Activity parentA = (Activity)parent;
            if(parentA.isInvite() && parentA.getSubmitter().equalsIgnoreCase(submitter))
            	return getTopActivity(parentA, submitter);
        }
        return A;
    }
}



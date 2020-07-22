package net.colloquia.prefs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import uk.ac.reload.dweezil.gui.layout.*;


public class PrefsMyDetailsPanel
extends PrefsPanel
{
    PhotoPanel photoPanel;
    PTextField tfGivenName;
    PTextField tfFamilyName;
    PTextField tfAddress;
    PTextField tfHomePage;
    PTextField tfPhoneNumber;
    PTextField tfPhoto;
    PTextField tfID;

    public PrefsMyDetailsPanel(JFrame owner) {
        super(owner, new BorderLayout());

        JPanel dataPanel = new JPanel();
        dataPanel.setMinimumSize(new Dimension(0, 0));
        dataPanel.setBackground(ColloquiaConstants.color2);
        dataPanel.setBorder(new EmptyBorder(2, 10, 10, 0));
        dataPanel.setLayout(new XYLayout());

        // First Name
        PLabel label = new PLabel(LanguageManager.getString("10_2") + ":");
        dataPanel.add(label, new XYConstraints(0, 2, 120, LABEL_HEIGHT));
        tfGivenName = new PTextField();
        dataPanel.add(tfGivenName, new XYConstraints(130, 0, 180, TEXTBOX_HEIGHT));

        // Surname
        label = new PLabel(LanguageManager.getString("10_1") + ":");
        dataPanel.add(label, new XYConstraints(0, 32, 120, LABEL_HEIGHT));
        tfFamilyName = new PTextField();
        dataPanel.add(tfFamilyName, new XYConstraints(130, 30, 180, TEXTBOX_HEIGHT));

        // Address
        label = new PLabel(LanguageManager.getString("10_3") + ":");
        dataPanel.add(label, new XYConstraints(0, 62, 130, LABEL_HEIGHT));
        tfAddress = new PTextField();
        dataPanel.add(tfAddress, new XYConstraints(130, 60, 320, TEXTBOX_HEIGHT));

        // HomePage
        label = new PLabel(LanguageManager.getString("10_7") + ":");
        dataPanel.add(label, new XYConstraints(0, 92, 120, LABEL_HEIGHT));
        tfHomePage = new PTextField();
        dataPanel.add(tfHomePage, new XYConstraints(130, 90, 320, TEXTBOX_HEIGHT));

        // Phone Number
        label = new PLabel(LanguageManager.getString("10_4") + ":");
        dataPanel.add(label, new XYConstraints(0, 122, 120, LABEL_HEIGHT));
        tfPhoneNumber = new PTextField();
        dataPanel.add(tfPhoneNumber, new XYConstraints(130, 120, 180, TEXTBOX_HEIGHT));

        // Photograph
        PButton photoButton = new PButton(LanguageManager.getString("10_5"));
        photoButton.addActionListener(new SetPhoto());
        dataPanel.add(photoButton, new XYConstraints(0, 150, 120, BUTTON_HEIGHT));

        tfPhoto = new PTextField();
        tfPhoto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                photoPanel.setImage(tfPhoto.getText());
            }
        });

        dataPanel.add(tfPhoto, new XYConstraints(130, 150, 320, TEXTBOX_HEIGHT));

        // ID
        label = new PLabel(LanguageManager.getString("10_9") + ":");
        dataPanel.add(label, new XYConstraints(0, 182, 120, LABEL_HEIGHT));
        tfID = new PTextField();
        dataPanel.add(tfID, new XYConstraints(130, 180, 180, TEXTBOX_HEIGHT));

        photoPanel = new PhotoPanel();
        PrefsMeBrowser browser = new PrefsMeBrowser(tfHomePage);

        PSplitPane mainSplit = new PSplitPane(JSplitPane.VERTICAL_SPLIT);
        PSplitPane topSplit = new PSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        mainSplit.setTopComponent(topSplit);
        topSplit.setLeftComponent(photoPanel);
        topSplit.setRightComponent(dataPanel);
        mainSplit.setBottomComponent(browser);

        add(mainSplit);
    }

    /**
    * Update UserPrefs according to the controls' settings
    */
    public void updateUserPrefs(UserPrefs prefs) {
		prefs.putProperty(UserPrefs.FAMILY_NAME, tfFamilyName.getText().trim());
		prefs.putProperty(UserPrefs.GIVEN_NAME, tfGivenName.getText().trim());
		prefs.putProperty(UserPrefs.ADDRESS, tfAddress.getText().trim());
		prefs.putProperty(UserPrefs.HOME_PAGE, tfHomePage.getText().trim());
		prefs.putProperty(UserPrefs.PHONE, tfPhoneNumber.getText().trim());
		prefs.putProperty(UserPrefs.PHOTOGRAPH, tfPhoto.getText().trim());
		prefs.putProperty(UserPrefs.ID, tfID.getText().trim());
    }

    /**
    * Set the controls' settings from those found in UserPrefs
    */
    public void setSettings(UserPrefs prefs) {
        tfFamilyName.setText(prefs.getProperty(UserPrefs.FAMILY_NAME));
        tfGivenName.setText(prefs.getProperty(UserPrefs.GIVEN_NAME));
        tfAddress.setText(prefs.getProperty(UserPrefs.ADDRESS));
        tfHomePage.setText(prefs.getProperty(UserPrefs.HOME_PAGE));
        tfPhoneNumber.setText(prefs.getProperty(UserPrefs.PHONE));
        tfPhoto.setText(prefs.getProperty(UserPrefs.PHOTOGRAPH));
        photoPanel.setImage(prefs.getProperty(UserPrefs.PHOTOGRAPH));
        tfID.setText(prefs.getProperty(UserPrefs.ID));
    }

    private class SetPhoto extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            PFileChooser chooser = new PFileChooser();
            ColloquiaFileFilter filter = new ColloquiaFileFilter();
            filter.addExtension("gif");
            filter.addExtension("jpg");
            filter.setDescription("JPEG and GIF Image Files");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(PrefsMyDetailsPanel.this);
            if(returnVal != PFileChooser.APPROVE_OPTION) return;
            String filename = chooser.getSelectedFileAndStore().toString();
            tfPhoto.setText(filename);
            photoPanel.setImage(filename);
        }
    }
}

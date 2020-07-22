package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.util.*;

/**
 * The "About" Box
 *
 * @author Phillip Beauvoir
 * @version 2003-11-18
 */
public class AboutBox
extends JDialog
{
    /**
     * Constructor
     */
    public AboutBox() {
        super(MainFrame.getInstance(), LanguageManager.getString("ABOUT") + " " + ColloquiaConstants.APP_NAME, true);
        setResizable(false);

        // Add OK Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
  	    btnOK.addActionListener(new btnOKClick());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        String javaVersion = Utils.getJavaVersion();
        String version = LanguageManager.getString("VERSION");

        String msg = ColloquiaConstants.APP_NAME + " " + version + " " + ColloquiaConstants.VERSION + "\n";
        msg += LanguageManager.getString("DATE") + ": " + ColloquiaConstants.BUILD_DATE + "\n";
        msg = msg + "Java " + version + " " + javaVersion + "\n\n";
        msg = msg + LanguageManager.getString("EMAIL") + ":  " + ColloquiaConstants.EMAIL_CONTACT + "\n";
        msg = msg + LanguageManager.getString("WEBSITE") + ":  " + ColloquiaConstants.WEB_PAGE;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel(Utils.getIcon(ColloquiaConstants.iconSplash)), BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.NORTH);

        JTextPane messagePanel = new JTextPane();
        messagePanel.setOpaque(false);
        messagePanel.setEditable(false);
        messagePanel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messagePanel.setMargin(new Insets(10, 50, 10, 10));
        messagePanel.setText(msg);
        getContentPane().add(messagePanel, BorderLayout.CENTER);

        //JOptionPane.showMessageDialog(MainFrame.getInstance(), msg, Constants.APP_NAME,
        //    JOptionPane.PLAIN_MESSAGE, Utils.getIcon(Constants.iconSplash));

        setSize(400, 310);
        setLocationRelativeTo(MainFrame.getInstance());
        setVisible(true);
	}

    /**
     * Close
     */
    private class btnOKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

}
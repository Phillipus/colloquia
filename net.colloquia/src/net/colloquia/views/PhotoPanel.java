package net.colloquia.views;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.util.*;

public class PhotoPanel extends JPanel {
    private Image photo = null;
    private int photoHeight;
    private int photoWidth;
    private JLabel lblPhoto;

    public PhotoPanel() {
        setLayout(new BorderLayout());
        lblPhoto = new JLabel(LanguageManager.getString("10_5"));
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        //setBackground(new Color(150, 150, 140));
        setBackground(ColloquiaConstants.color2);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && e.isShiftDown()) {
                    photo = Utils.getIcon(ColloquiaConstants.iconPhillipus).getImage();
                    repaint();
                }
            }
        });
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }

    public void setImage(String imagePath) {
        if(imagePath == null || imagePath.trim().equals("")) photo = null;
        else photo = Utils.getPhotoImage(imagePath);
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        if(photo != null) {
            remove(lblPhoto);

            photoWidth = photo.getWidth(this);
            photoHeight = photo.getHeight(this);

            while(photoWidth > getWidth() || photoHeight > getHeight()) {
                photoWidth /= 1.1;
                photoHeight /= 1.1;
            }

            g.drawImage(photo, 0, 0, photoWidth, photoHeight, this);
            validate();
        }
        else {
            add(lblPhoto, BorderLayout.NORTH);
            validate();
        }
    }
}

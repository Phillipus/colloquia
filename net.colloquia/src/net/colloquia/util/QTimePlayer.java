package net.colloquia.util;

import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import quicktime.*;
import quicktime.app.*;
import quicktime.app.display.*;
import quicktime.app.players.*;
import quicktime.std.*;
import quicktime.std.movies.*;
import quicktime.std.movies.media.*;


public class QTimePlayer extends JFrame {
    private QTCanvas qtCanvas;
	private Movie movie;
	private QTPlayer qtPlayer;
    private MovieController mc;
    private MoviePresenter mp;
    private DataRef urlMovie;
    private static boolean QTInstalled;

    private static QTimePlayer instance;

    public static final QTimePlayer getInstance() {
        if(instance == null) {
        	try {
            	instance = new QTimePlayer();
            }
            catch(Exception ex) {
            	ErrorHandler.showWarning(MainFrame.getInstance(), "QT_2", null, "QT");
            }
        }
    	return instance;
    }

    private QTimePlayer() {
        try {
        	QTSession.open();
		} catch (Exception ex) {
            ErrorHandler.showWarning(this, "QT_2", ex, "QT");
            QTInstalled = false;
            QTSession.close();
            return;
        }

        QTInstalled = true;

        setTitle("QuickTime Player");
        setIconImage(Utils.getImage(ColloquiaConstants.iconAppIcon));

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Stop the movie when Window closed

        addWindowListener(new WindowAdapter() {
  	        public void windowClosing(WindowEvent e) {
                try {
                    // Stop movie
  	                if(qtPlayer != null) {
						qtPlayer.stopTasking();
                    	qtPlayer.setRate(0);
                    }
                }
                catch(Exception ex) {}
  	        }
  	    });

		qtCanvas = new QTCanvas();
        getContentPane().add(qtCanvas);
        //add(qtCanvas);
        setSize(500, 400);
        Utils.centreWindow(this, getSize().width, getSize().height);
    }


    public void closeSession() {
        QTSession.close();
    }

    public void dispose() {
        closeSession();
        super.dispose();
    }

    public void finalize() throws Throwable {
        closeSession();
        super.finalize();
    }

    public void playFile(String url) {
        if(!QTInstalled) {
            ErrorHandler.showWarning(this, "QT_3", null, "QT");
            return;
        }

        try {
            url = normaliseAddress(url);

            // create the DataRef that contains the information about where the movie is
            urlMovie = new DataRef(url);
            // create the movie
            movie = Movie.fromDataRef(urlMovie, StdQTConstants.newMovieActive);

            mc = new MovieController(movie);
            mc.setKeysEnabled(true);

            qtPlayer = new QTPlayer(mc);
			qtCanvas.setClient(qtPlayer, true);

	        qtPlayer.startTasking(); // this call creates a task that periodically calls MCIdle()
	        qtPlayer.setRate(1);
		} catch(Exception ex) {
            ErrorHandler.showWarning(this, "QT_3", ex, "QT");
            return;
        }

        setVisible(true);
        pack();
        toFront();
    }

    public void __playFile(String url) {
        if(!QTInstalled) {
            ErrorHandler.showWarning(this, "QT_3", null, "QT");
            return;
        }

    	try {
	      	//String soundLocation = QTFactory.findAbsolutePath(fileName).getPath();
	      	String soundLocation = normaliseAddress(url);

			if(qtPlayer != null) { // if a sound has been already loaded, stop it
				qtPlayer.setRate(0);
				qtPlayer.stopTasking();
			}

			//this call works with a file://, http://, rtsp:// located movies
	        //qtPlayer = (QTPlayer)QTFactory.makeDrawable("file://"  + soundLocation);
	        qtPlayer = (QTPlayer)QTFactory.makeDrawable(soundLocation);
	        qtPlayer.startTasking(); // this call creates a task that periodically calls MCIdle()
	        qtPlayer.setRate(1);
	 	}
    	catch (Exception ex) {
            ErrorHandler.showWarning(this, "QT_3", ex, "QT");
        }

        setVisible(true);
        pack();
        toFront();
    }

    private String normaliseAddress(String url) {
        String u = url.toLowerCase();
        if(u.startsWith("http:") || u.startsWith("file:")) return url;
        else return "file:" + url;
    }
}
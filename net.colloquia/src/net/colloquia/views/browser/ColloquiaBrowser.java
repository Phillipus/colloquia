package net.colloquia.views.browser;

import java.net.*;
import java.util.*;

import net.colloquia.util.*;

import com.netclue.xml.gui.*;

/**
* Sub-class of Third-Party Browser so we can do some stuff on it
*/
public class ColloquiaBrowser
extends XContainer
{
    public ColloquiaBrowser() {
    }

    public void setCurrentLocation(String loc) {
    	URL url = URLUtils.makeURL(loc);
        if(url != null) setPage(url);
    }

    public void stop() {
    	stopLoading();
    }

    public String getCurrentLocation() {
        URL url = getBaseURL();
    	return url == null ? "" : getBaseURL().toString();
    }

    public void clearHistory() {
        getHistoryModel().clear();
    }

    /**
     * Not supported
     */
    public void print() {
        Properties props = new Properties();
        //printDocument(props, true);
    }

	public void back() {
    	moveBackward();
    }

	public void forward() {
    	moveForward();
    }

    public void reload() {
    	super.reload();
    }

    public void clear() {
        // This will clear the view but the current location will be d:\localdir
        //setPageContent("");
    }
}


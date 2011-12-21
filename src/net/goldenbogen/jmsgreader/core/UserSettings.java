/**
 * UserSettings.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:58:28
 */
package net.goldenbogen.jmsgreader.core;

import java.util.prefs.Preferences;

import net.goldenbogen.jmsgreader.JMsgReader;

/**
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:58:28
 * 
 */
public class UserSettings {

	private static Preferences	prefs	= Preferences.userRoot().node(JMsgReader.class.getName().toLowerCase().substring(0, JMsgReader.class.getName().toLowerCase().lastIndexOf(".")).replace(".", "/"));

	private static boolean		manualSearch;
	private static boolean		enableCaching;

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 15.12.2011 13:59:31
	 * 
	 * @return the liveSearch
	 */
	public static boolean isManualSearch() {
		int tmp = 0;
		tmp = prefs.getInt(new String("ManualSearch").toLowerCase(), tmp);
		if (tmp == 1) {
			manualSearch = true;
		} else {
			manualSearch = false;
		}
		return manualSearch;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 15.12.2011 13:59:31
	 * 
	 * @param liveSearch
	 *            the liveSearch to set
	 */
	public static void setManualSearch(boolean ManualSearch) {
		int tmp = 0;
		if (ManualSearch) {
			tmp = 1;
		}
		prefs.putInt(new String("ManualSearch").toLowerCase(), tmp);
		manualSearch = ManualSearch;
	}

	/**
	 * @author Goldenbogen, Pierre
	 * Created: 21.12.2011 16:38:51
	 *
	 * @return the enableCaching
	 */
	public static boolean isEnableCaching() {
		int tmp = 0;
		tmp = prefs.getInt(new String("EnableCaching").toLowerCase(), tmp);
		if (tmp == 1) {
			enableCaching = true;
		} else {
			enableCaching = false;
		}
		return enableCaching;
	}

	/**
	 * @author Goldenbogen, Pierre
	 * Created: 21.12.2011 16:38:51
	 *
	 * @param enableCaching the enableCaching to set
	 */
	public static void setEnableCaching(boolean EnableCaching) {
		int tmp = 0;
		if (EnableCaching) {
			tmp = 1;
		}
		prefs.putInt(new String("enableCaching").toLowerCase(), tmp);
		enableCaching = EnableCaching;
	}

}

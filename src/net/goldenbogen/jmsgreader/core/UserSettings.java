/**
 * UserSettings.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:58:28
 */
package net.goldenbogen.jmsgreader.core;

import java.util.Locale;
import java.util.prefs.Preferences;

import net.goldenbogen.jmsgreader.JMsgReader;

/**
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:58:28
 * 
 */
public class UserSettings {

	private static Preferences	prefs			= Preferences.userRoot().node(JMsgReader.class.getName().toLowerCase().substring(0, JMsgReader.class.getName().toLowerCase().lastIndexOf(".")).replace(".", "/"));	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static boolean		manualSearch;
	private static boolean		enableCaching;
	private static Locale		currentLocale	= Locale.getDefault();

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 15.12.2011 13:59:31
	 * 
	 * @return the liveSearch
	 */
	public static boolean isManualSearch() {
		int tmp = 0;
		tmp = prefs.getInt(new String("ManualSearch").toLowerCase(), tmp); //$NON-NLS-1$
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
		prefs.putInt(new String("ManualSearch").toLowerCase(), tmp); //$NON-NLS-1$
		manualSearch = ManualSearch;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 21.12.2011 16:38:51
	 * 
	 * @return the enableCaching
	 */
	public static boolean isEnableCaching() {
		int tmp = 0;
		tmp = prefs.getInt(new String("EnableCaching").toLowerCase(), tmp); //$NON-NLS-1$
		if (tmp == 1) {
			enableCaching = true;
		} else {
			enableCaching = false;
		}
		return enableCaching;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 21.12.2011 16:38:51
	 * 
	 * @param enableCaching
	 *            the enableCaching to set
	 */
	public static void setEnableCaching(boolean EnableCaching) {
		int tmp = 0;
		if (EnableCaching) {
			tmp = 1;
		}
		prefs.putInt(new String("EnableCaching").toLowerCase(), tmp); //$NON-NLS-1$
		enableCaching = EnableCaching;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 30.12.2011 13:56:37
	 * 
	 * @return the currentLocalte
	 */
	public static Locale getCurrentLocale() {
		return currentLocale;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 30.12.2011 13:56:37
	 * 
	 * @param currentLocalte
	 *            the currentLocalte to set
	 */
	public static void setCurrentLocale(Locale currentLocale) {
		UserSettings.currentLocale = currentLocale;
	}

}

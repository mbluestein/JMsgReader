/**
 * Messages.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:17:49
 */
package net.goldenbogen.jmsgreader;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.goldenbogen.jmsgreader.core.UserSettings;

/**
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:17:49
 * 
 */
public class Messages {
	
	
	
	private static final String			BUNDLE_NAME		= "net.goldenbogen.jmsgreader.translation";	//$NON-NLS-1$

	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME,UserSettings.getCurrentLocale());

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

/**
 * FileAssociation.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 04.05.2011 14:47:47
 */
package net.goldenbogen.jmsgreader.core;

/**
 * @author Goldenbogen, Pierre
 *         Created: 04.05.2011 14:47:47
 * 
 */
public class FileAssociation {

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 05.05.2011 10:44:56
	 * 
	 *         Checks current OS to decide how to associate the given file
	 *         extension to our application.
	 * 
	 * @param FileExtension
	 * @return
	 */
	public static boolean setFileAssociation(String FileExtension) {
		boolean bOk = true;
		if (!FileExtension.isEmpty()) {
			if (System.getProperty("os.name").toLowerCase().contains("win")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (!SetWindowsFileAssociation(FileExtension)) {
					bOk = false;
				}
			}
		}
		return bOk;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 05.05.2011 10:06:57
	 * 
	 *         Method to associate the given file extension to our software on
	 *         windows. We use the REG command line tool to do this.
	 * 
	 * @param FileExtension
	 * @return
	 */
	private static boolean SetWindowsFileAssociation(String FileExtension) {
		boolean bOk = true;
		try {
			Process RegistryProcess = null;
			int exitVal = -1;
			RegistryProcess = Runtime.getRuntime().exec("REG ADD HKCR\\" + FileExtension + "\\ /ve /t REG_SZ /d jmsgreader /f"); //$NON-NLS-1$ //$NON-NLS-2$
			exitVal = RegistryProcess.waitFor();
			switch (exitVal) {
				case 0:
					bOk = true;
					break;
				case 1:
					bOk = false;
					break;
			}
			exitVal = -1;
			RegistryProcess = Runtime.getRuntime().exec("REG ADD HKCR\\jmsgreader\\ /ve /t REG_SZ /d \"jmsgreader File\" /f"); //$NON-NLS-1$
			exitVal = RegistryProcess.waitFor();
			switch (exitVal) {
				case 0:
					bOk = true;
					break;
				case 1:
					bOk = false;
					break;
			}
			exitVal = -1;
			RegistryProcess = Runtime.getRuntime().exec("REG ADD HKCR\\jmsgreader\\Shell\\Open\\Command /ve /t REG_SZ /d \"javaw -jar " + System.getProperty("user.dir") + "\\jmsgreader.jar \"\"\"%1\"\" /f"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			exitVal = RegistryProcess.waitFor();
			switch (exitVal) {
				case 0:
					bOk = true;
					break;
				case 1:
					bOk = false;
					break;
			}
		} catch (Exception e) {
			bOk = false;
			e.printStackTrace();
		}
		return bOk;
	}
}

/**
 * MailMessages.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 01.12.2011 09:23:32
 */
package net.goldenbogen.jmsgreader.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.goldenbogen.jmsgreader.JMsgReader;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;


/**
 * @author Goldenbogen, Pierre
 *         Created: 01.12.2011 09:23:32
 * 
 */
public class MailMessages {

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:52:57
	 * 
	 */
	public enum SearchType {
		CONTENT, PERSON, RECEIVER_ONLY, SENDER_ONLY, SUBJECT;
	}

	private static String	cacheFolderPath	= ".jmsgreader";

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 15.12.2011 09:56:18
	 * 
	 * @return the cacheFolderPath
	 */
	private static String getCacheFolderPath() {
		return cacheFolderPath;
	}

	private ArrayList<Message>	Messages		= new ArrayList<Message>();

	private ArrayList<Message>	Result			= new ArrayList<Message>();

	private ArrayList<Message>	cacheMessages	= new ArrayList<Message>();

	private String				SearchFolder;

	/**
	 * @param SearchFolderForMessages
	 */
	public MailMessages(String SearchFolderForMessages, JProgressBar statusBar, JLabel statusText) {
		if (!SearchFolderForMessages.isEmpty()) {
			setSearchFolder(SearchFolderForMessages);
			generateMessageList(statusBar, statusText);
		}
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:30:45
	 * 
	 * @return
	 */
	private boolean generateMessageList(JProgressBar statusBar, JLabel statusText) {
		boolean bOk = true;

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);

		File root = new File(getSearchFolder());
		try {
			boolean recursive = true;
			String[] extensions = null;
			statusText.setText("Ermittle Dateien ...");
			Collection<File> files = FileUtils.listFiles(root, extensions, recursive);
			statusBar.setMaximum(files.size());
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				statusBar.setValue(statusBar.getValue() + 1);
				File file = iterator.next();

				if (file.getName().toLowerCase().endsWith(".msg")) {

					String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1, file.getAbsolutePath().lastIndexOf(".")) + ".cache";

					File cacheFile = new File(getSearchFolder() + "\\" + getCacheFolderPath() + "\\" + fileName);
					if (cacheFile.exists() && cacheFile.lastModified() > cal.getTimeInMillis()) {
						statusText.setText("Lade Daten aus Cache-Datei: " + cacheFile.getName());
						FileInputStream fis = null;
						ObjectInputStream in = null;
						fis = new FileInputStream(cacheFile);
						in = new ObjectInputStream(fis);
						Message cachedMessage = null;
						cachedMessage = (Message) in.readObject();
						Messages.add(cachedMessage);
						in.close();
					} else {

						statusText.setText("Ermittle Daten aus eMail-Datei: " + file.getName());

						MAPIMessage msg = new MAPIMessage(file.getAbsolutePath());

						// Get Date
						Date date = new Date(file.lastModified());
						try {
							date = msg.getMessageDate().getTime();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageDATE-CHUNK");
						}

						// Get To
						String displayTO = "";
						try {
							displayTO = msg.getDisplayTo();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageTO-CHUNK");
						}

						// Get From
						String displayFROM = "";
						try {
							displayFROM = msg.getDisplayFrom();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageFROM-CHUNK");
						}

						// Get CC
						String displayCC = "";
						try {
							displayCC = msg.getDisplayCC();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageCC-CHUNK");
						}

						// Get BCC
						String displayBCC = "";
						try {
							displayBCC = msg.getDisplayBCC();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageBCC-CHUNK");
						}

						// Get Subject
						String displaySUBJECT = "";
						try {
							displaySUBJECT = msg.getSubject();
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageSUBJECT-CHUNK");
						}

						// Get TextBody
						String textBODY = "";
						try {
							textBODY = msg.getTextBody();
						} catch (Exception e) {
							textBODY = "<i>... kein Text vorhanden ...</i>";
							System.err.println("Error: " + file.getName() + " no messageTEXTBODY-CHUNK");
						}

						// Get Attachments
						AttachmentChunks[] attachments;
						ArrayList<String> attachmentFiles = new ArrayList<String>();
						try {
							attachments = msg.getAttachmentFiles();
							if (attachments.length > 0) {
								for (AttachmentChunks attachment : attachments) {
									attachmentFiles.add(attachment.attachLongFileName.toString());
								}
							}
						} catch (Exception e) {
							System.err.println("Error: " + file.getName() + " no messageATTACHMENTS-CHUNK");
						}

						Message myMessage = new Message(file.getAbsolutePath(), date, displayTO, displayFROM, displayCC, displayBCC, displaySUBJECT, textBODY, attachmentFiles);

						cacheMessages.add(myMessage);
						Messages.add(myMessage);

					}
				}
			}
			initResults();
		} catch (Exception e) {
			e.printStackTrace();
			bOk = false;
		}

		File hiddenCacheFolder = new File(getSearchFolder() + "\\" + getCacheFolderPath());
		if (!hiddenCacheFolder.exists()) {
			hiddenCacheFolder.mkdir();
		}

		try {
			Process p = Runtime.getRuntime().exec("attrib +h \"" + hiddenCacheFolder.getAbsolutePath() + "\"");
			p.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			bOk = false;
		} catch (IOException e2) {
			e2.printStackTrace();
			bOk = false;
		}

		statusBar.setValue(0);
		statusBar.setMaximum(cacheMessages.size());
		for (int i = 0; i < cacheMessages.size(); i++) {
			statusBar.setValue(statusBar.getValue() + 1);
			String file = cacheMessages.get(i).getFilePath().substring(cacheMessages.get(i).getFilePath().lastIndexOf("\\") + 1, cacheMessages.get(i).getFilePath().lastIndexOf(".")) + ".cache";
			File checkFile = new File(getSearchFolder() + "\\" + getCacheFolderPath() + "\\" + file);
			statusText.setText("Erstelle Cache-Datei: " + checkFile.getName());
			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try {
				fos = new FileOutputStream(checkFile);
				out = new ObjectOutputStream(fos);
				out.writeObject(cacheMessages.get(i));
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				bOk = false;
			}

		}

		return bOk;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:30:22
	 * 
	 * @return
	 */
	public ArrayList<Message> getResult() {
		return Result;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:30:38
	 * 
	 * @return
	 */
	private String getSearchFolder() {
		return SearchFolder;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:29:44
	 * 
	 */
	private void initResults() {
		setResult(Messages);
		SortResults();
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:54:07
	 * 
	 * @param SearchValue
	 * @param SearchType
	 */
	public void searchMails(String SearchValue, SearchType Type, JProgressBar statusBar) {
		ArrayList<Message> tmpMessages = new ArrayList<Message>();
		statusBar.setMaximum(Messages.size());
		for (Iterator<Message> iterator = Messages.iterator(); iterator.hasNext();) {
			statusBar.setValue(statusBar.getValue() + 1);
			Message aMessage = iterator.next();
			switch (Type) {
				case PERSON:
					if (JMsgReader.isShowCC_BCC()) {
						if (aMessage.getReceiver().toLowerCase().contains(SearchValue.toLowerCase()) || aMessage.getSender().toLowerCase().contains(SearchValue.toLowerCase()) || aMessage.getCC().toLowerCase().contains(SearchValue.toLowerCase()) || aMessage.getBCC().toLowerCase().contains(SearchValue.toLowerCase())) {
							aMessage.colorizeElements(SearchValue, Type);
							tmpMessages.add(aMessage);
						}
					} else {
						if (aMessage.getReceiver().toLowerCase().contains(SearchValue.toLowerCase()) || aMessage.getSender().toLowerCase().contains(SearchValue.toLowerCase())) {
							aMessage.colorizeElements(SearchValue, Type);
							tmpMessages.add(aMessage);
						}
					}
					break;
				case SUBJECT:
					if (aMessage.getSubject().toLowerCase().contains(SearchValue.toLowerCase())) {
						aMessage.colorizeElements(SearchValue, Type);
						tmpMessages.add(aMessage);
					}
					break;
				case CONTENT:
					if (aMessage.getContent().toLowerCase().contains(SearchValue.toLowerCase())) {
						aMessage.colorizeElements(SearchValue, Type);
						tmpMessages.add(aMessage);
					}
					break;
				case SENDER_ONLY:
					if (aMessage.getSender().toLowerCase().contains(SearchValue.toLowerCase())) {
						aMessage.colorizeElements(SearchValue, Type);
						tmpMessages.add(aMessage);
					}
					break;
				case RECEIVER_ONLY:
					if (aMessage.getReceiver().toLowerCase().contains(SearchValue.toLowerCase())) {
						aMessage.colorizeElements(SearchValue, Type);
						tmpMessages.add(aMessage);
					}
					break;
				default:
					break;
			}
		}
		setResult(tmpMessages);
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:30:18
	 * 
	 * @param result
	 */
	private void setResult(ArrayList<Message> result) {
		Result = result;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:30:32
	 * 
	 * @param searchFolder
	 */
	private void setSearchFolder(String searchFolder) {
		SearchFolder = searchFolder;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:31:10
	 * 
	 */
	private void SortResults() {
		Collections.sort(Result, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Message message1 = (Message) o1;
				Message message2 = (Message) o2;
				return message2.getReceivedDate().compareTo(message1.getReceivedDate());
			}
		});
	}

}

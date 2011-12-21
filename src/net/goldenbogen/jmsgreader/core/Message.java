/**
 * Message.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 01.12.2011 09:23:47
 */
package net.goldenbogen.jmsgreader.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import net.goldenbogen.jmsgreader.core.MailMessages.SearchType;


/**
 * @author Goldenbogen, Pierre
 *         Created: 01.12.2011 09:23:47
 * 
 */
/**
 * @author Goldenbogen, Pierre
 *         Created: 01.12.2011 14:46:59
 * 
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4533330067689696810L;
	private ArrayList<String>	Attachments;
	private String				BCC;
	private String				CC;
	private String				ColorizedBCC;
	private String				ColorizedCC;
	private String				ColorizedContent;
	private String				ColorizedReceiver;
	private String				ColorizedSender;
	private String				ColorizedSubject;
	private String				Content;
	private String				FilePath;
	private Date				ReceivedDate;
	private String				Receiver;
	private String				Sender;
	private String				Subject;

	/**
	 * @param ReceivedDate
	 * @param Receiver
	 * @param Sender
	 * @param Subject
	 * @param Content
	 */
	public Message(String FilePath, Date ReceivedDate, String Receiver, String Sender, String CC, String BCC, String Subject, String Content, ArrayList<String> Attachments) {
		setFilePath(FilePath);
		setReceivedDate(ReceivedDate);
		setReceiver(Receiver);
		setSender(Sender);
		setCC(CC);
		setBCC(BCC);
		setSubject(Subject);
		setContent(Content);
		setAttachments(Attachments);
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:38
	 * 
	 * @param SearchText
	 * @param Text
	 * @return
	 */
	private String colorize(String SearchText, String Text) {
		if (!SearchText.toLowerCase().isEmpty()) {
			int lastIndex = 0;
			int increment = 0;
			Vector<Integer> places = new Vector<Integer>();
			while (lastIndex != -1) {
				lastIndex = Text.toLowerCase().indexOf(SearchText.toLowerCase(), lastIndex + increment);
				increment = SearchText.length();
				if (lastIndex != -1) {
					places.add(lastIndex);
				}
			}
			String output = "";
			String htmlfront = "<font bgcolor=yellow color=black>";
			String htmlback = "</font>";
			Collections.reverse(places);
			for (int iRunner = 0; iRunner < places.size(); iRunner++) {
				output = Text.substring(0, places.get(iRunner));
				output = output + htmlfront;
				output = output + Text.substring(places.get(iRunner), places.get(iRunner) + SearchText.length());
				output = output + htmlback;
				Text = output + Text.substring(places.get(iRunner) + SearchText.length(), Text.length());
			}
		}
		return Text;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:39:51
	 * 
	 * @param SearchText
	 */
	public void colorizeElements(String SearchText, SearchType Type) {
		switch (Type) {
			case PERSON:
				setColorizedReceiver(colorize(SearchText, getReceiver()));
				setColorizedSender(colorize(SearchText, getSender()));
				setColorizedCC(colorize(SearchText, getCC()));
				setColorizedBCC(colorize(SearchText, getBCC()));
				setColorizedSubject(getSubject());
				setColorizedContent(getContent());
				break;
			case SUBJECT:
				setColorizedReceiver(getReceiver());
				setColorizedSender(getSender());
				setColorizedCC(getCC());
				setColorizedBCC(getBCC());
				setColorizedSubject(colorize(SearchText, getSubject()));
				setColorizedContent(getContent());
				break;
			case CONTENT:
				setColorizedReceiver(getReceiver());
				setColorizedSender(getSender());
				setColorizedCC(getCC());
				setColorizedBCC(getBCC());
				setColorizedSubject(getSubject());
				setColorizedContent(colorize(SearchText, getContent()));
				break;
			case SENDER_ONLY:
				setColorizedReceiver(getReceiver());
				setColorizedSender(colorize(SearchText, getSender()));
				setColorizedCC(getCC());
				setColorizedBCC(getBCC());
				setColorizedSubject(getSubject());
				setColorizedContent(getContent());
				break;
			case RECEIVER_ONLY:
				setColorizedReceiver(colorize(SearchText, getReceiver()));
				setColorizedSender(getSender());
				setColorizedCC(getCC());
				setColorizedBCC(getBCC());
				setColorizedSubject(getSubject());
				setColorizedContent(getContent());
				break;
			default:
				setColorizedReceiver(getReceiver());
				setColorizedSender(getSender());
				setColorizedCC(getCC());
				setColorizedBCC(getBCC());
				setColorizedSubject(getSubject());
				setColorizedContent(getContent());
				break;
		}
	}

	/**
	 * @return the attachments
	 */
	public ArrayList<String> getAttachments() {
		return Attachments;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:24
	 * 
	 * @return
	 */
	public String getBCC() {
		return BCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:29
	 * 
	 * @return
	 */
	public String getCC() {
		return CC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:08
	 * 
	 * @return
	 */
	public String getColorizedBCC() {
		return ColorizedBCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:03
	 * 
	 * @return
	 */
	public String getColorizedCC() {
		return ColorizedCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:24
	 * 
	 * @return
	 */
	public String getColorizedContent() {
		return ColorizedContent;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:00
	 * 
	 * @return
	 */
	public String getColorizedReceiver() {
		return ColorizedReceiver;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:32
	 * 
	 * @return
	 */
	public String getColorizedSender() {
		return ColorizedSender;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:28
	 * 
	 * @return
	 */
	public String getColorizedSubject() {
		return ColorizedSubject;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:13
	 * 
	 * @return
	 */
	public String getContent() {
		return Content;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return FilePath;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:31:51
	 * 
	 * @return
	 */
	public Date getReceivedDate() {
		return ReceivedDate;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:31:56
	 * 
	 * @return
	 */
	public String getReceiver() {
		return Receiver;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:02
	 * 
	 * @return
	 */
	public String getSender() {
		return Sender;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:07
	 * 
	 * @return
	 */
	public String getSubject() {
		return Subject;
	}

	/**
	 * @param attachments
	 *            the attachments to set
	 */
	public void setAttachments(ArrayList<String> attachments) {
		Attachments = attachments;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:26
	 * 
	 * @param bCC
	 */
	private void setBCC(String bCC) {
		BCC = bCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:31
	 * 
	 * @param cC
	 */
	private void setCC(String cC) {
		CC = cC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:11
	 * 
	 * @param colorizedBCC
	 */
	private void setColorizedBCC(String colorizedBCC) {
		ColorizedBCC = colorizedBCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 02.12.2011 10:17:05
	 * 
	 * @param colorizedCC
	 */
	private void setColorizedCC(String colorizedCC) {
		ColorizedCC = colorizedCC;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:19
	 * 
	 * @param colorizedContent
	 */
	private void setColorizedContent(String colorizedContent) {
		ColorizedContent = "<html><head></head><body font face=\"Tahoma\">" + colorizedContent.replaceAll("(\r\n|\r|\n|\n\r)", "<br>") + "</body></html>";
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:34
	 * 
	 * @param colorizedReceiver
	 */
	private void setColorizedReceiver(String colorizedReceiver) {
		ColorizedReceiver = colorizedReceiver;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:30
	 * 
	 * @param colorizedSender
	 */
	private void setColorizedSender(String colorizedSender) {
		ColorizedSender = colorizedSender;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 14:47:26
	 * 
	 * @param colorizedSubject
	 */
	private void setColorizedSubject(String colorizedSubject) {
		ColorizedSubject = colorizedSubject;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:15
	 * 
	 * @param content
	 */
	private void setContent(String content) {
		Content = content;
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	private void setFilePath(String filePath) {
		FilePath = filePath;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:31:54
	 * 
	 * @param receivedDate
	 */
	private void setReceivedDate(Date receivedDate) {
		ReceivedDate = receivedDate;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:31:59
	 * 
	 * @param receiver
	 */
	private void setReceiver(String receiver) {
		Receiver = receiver;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:05
	 * 
	 * @param sender
	 */
	private void setSender(String sender) {
		Sender = sender;
	}

	/**
	 * @author Goldenbogen, Pierre
	 *         Created: 01.12.2011 11:32:10
	 * 
	 * @param subject
	 */
	private void setSubject(String subject) {
		Subject = subject;
	}

}

/**
 * JMsgReader.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 20.06.2011 10:11:17
 */
package net.goldenbogen.jmsgreader;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.goldenbogen.jmsgreader.core.AdvancedCellRenderer;
import net.goldenbogen.jmsgreader.core.FileAssociation;
import net.goldenbogen.jmsgreader.core.MailMessages;
import net.goldenbogen.jmsgreader.core.Message;
import net.goldenbogen.jmsgreader.core.UserSettings;

/**
 * @author Goldenbogen, Pierre
 *         Created: 20.06.2011 10:11:17
 * 
 */
public class JMsgReader {

	private static JComboBox	cbSelectSearchType;
	private static JList		listResults;
	private static MailMessages	Mails;
	private static String		path			= "";		//$NON-NLS-1$
	private static boolean		showAttachments	= false;
	private static boolean		showCC_BCC		= false;

	/**
	 * @return the showAttachments
	 */
	public static boolean isShowAttachments() {
		return showAttachments;
	}

	/**
	 * @return the showCC_BCC
	 */
	public static boolean isShowCC_BCC() {
		return showCC_BCC;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (args.length > 0) {
			path = args[0];
			if (path.toLowerCase().equals("--setassoc")) { //$NON-NLS-1$
				if (args.length >= 1) {
					FileAssociation.setFileAssociation("." + args[1]); //$NON-NLS-1$
					System.exit(0);
				}
			}
			path = path.substring(0, path.lastIndexOf("\\")); //$NON-NLS-1$
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JMsgReader window = new JMsgReader();
					window.frmMsgCrawler.setVisible(true);
					window.frmMsgCrawler.setExtendedState(Frame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @param showAttachments
	 *            the showAttachments to set
	 */
	public static void setShowAttachments(boolean showAttachments) {
		JMsgReader.showAttachments = showAttachments;
	}

	/**
	 * @param showCC_BCC
	 *            the showCC_BCC to set
	 */
	private static void setShowCC_BCC(boolean showCC_BCC) {
		JMsgReader.showCC_BCC = showCC_BCC;
	}

	private JCheckBox				chbxShowCCandBCC;

	private JCheckBox				chbxShowAttachedFiles;

	private JEditorPane				epMessageBody;

	private JFrame					frmMsgCrawler;

	private JLabel					lblInfo;

	private JLabel					lblWork		= new JLabel();

	private JDialog					loadingDialog;

	private JProgressBar			progressBar	= new JProgressBar();

	private JSplitPane				splitPane;

	private SpringLayout			springLayout;

	private JTextField				tfSearchText;

	private MailMessages.SearchType	Type;
	private JButton					btnSettings;

	/**
	 * Create the application.
	 */
	public JMsgReader() {
		initialize();
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() {
				Boolean bOk = true;
				loadingDialog = new JDialog(frmMsgCrawler, Messages.getString("JMsgReader.Loading")); //$NON-NLS-1$
				final JLabel label = new JLabel();
				label.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/generator.gif"))); //$NON-NLS-1$
				final JPanel contentPane = new JPanel();
				contentPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
				contentPane.setLayout(new BorderLayout());
				contentPane.add(label, BorderLayout.NORTH);
				loadingDialog.setContentPane(contentPane);
				loadingDialog.pack();
				loadingDialog.setLocationRelativeTo(null);
				loadingDialog.setVisible(true);
				if (!path.isEmpty()) {
					setType(MailMessages.SearchType.SUBJECT);
					Mails = new MailMessages(path.toString(), progressBar, lblWork);
					RefreshView(""); //$NON-NLS-1$
				}
				lblWork.setVisible(false);
				progressBar.setVisible(false);
				springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -10, SpringLayout.SOUTH, frmMsgCrawler.getContentPane());
				return bOk;
			}

			@Override
			protected void done() {
				try {
					get();
					loadingDialog.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}

	/**
	 * @return the type
	 */
	private MailMessages.SearchType getType() {
		return Type;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMsgCrawler = new JFrame();
		frmMsgCrawler.setIconImage(Toolkit.getDefaultToolkit().getImage(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/mail-receive.png"))); //$NON-NLS-1$
		frmMsgCrawler.setTitle(Messages.getString("JMsgReader.AppName")); //$NON-NLS-1$
		frmMsgCrawler.setBounds(100, 100, 754, 550);
		frmMsgCrawler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frmMsgCrawler.getContentPane().setLayout(springLayout);

		tfSearchText = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, tfSearchText, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		tfSearchText.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		tfSearchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (UserSettings.isManualSearch()) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						RefreshView(tfSearchText.getText());
					}
				} else {
					RefreshView(tfSearchText.getText());
				}
			}
		});
		frmMsgCrawler.getContentPane().add(tfSearchText);
		tfSearchText.setColumns(10);

		JLabel lblSearchtext = new JLabel(Messages.getString("JMsgReader.lblSearchText")); //$NON-NLS-1$
		lblSearchtext.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, tfSearchText, 6, SpringLayout.SOUTH, lblSearchtext);
		springLayout.putConstraint(SpringLayout.SOUTH, tfSearchText, 34, SpringLayout.SOUTH, lblSearchtext);
		springLayout.putConstraint(SpringLayout.NORTH, lblSearchtext, 10, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblSearchtext, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblSearchtext, 26, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblSearchtext, 97, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		frmMsgCrawler.getContentPane().add(lblSearchtext);

		cbSelectSearchType = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, cbSelectSearchType, 6, SpringLayout.EAST, tfSearchText);
		springLayout.putConstraint(SpringLayout.EAST, cbSelectSearchType, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		cbSelectSearchType.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		cbSelectSearchType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String type = (String) cb.getSelectedItem();
				if (type == Messages.getString("JMsgReader.typeSubject")) { //$NON-NLS-1$
					setType(MailMessages.SearchType.SUBJECT);
				}
				if (type == Messages.getString("JMsgReader.typePerson")) { //$NON-NLS-1$
					setType(MailMessages.SearchType.PERSON);
				}
				if (type == Messages.getString("JMsgReader.typeText")) { //$NON-NLS-1$
					setType(MailMessages.SearchType.CONTENT);
				}
				if (type == Messages.getString("JMsgReader.typeSenderOnly")) { //$NON-NLS-1$
					setType(MailMessages.SearchType.SENDER_ONLY);
				}
				if (type == Messages.getString("JMsgReader.typeReceiverOnly")) { //$NON-NLS-1$
					setType(MailMessages.SearchType.RECEIVER_ONLY);
				}
				if (!type.equals("")) { //$NON-NLS-1$
					RefreshView(tfSearchText.getText());
				} else {
					switch (getType()) {
						case SUBJECT:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeSubject")); //$NON-NLS-1$
							break;
						case PERSON:
							cb.setSelectedItem(Messages.getString("JMsgReader.typePerson")); //$NON-NLS-1$
							break;
						case CONTENT:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeText")); //$NON-NLS-1$
							break;
						case SENDER_ONLY:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeSenderOnly")); //$NON-NLS-1$
							break;
						case RECEIVER_ONLY:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeReceiverOnly")); //$NON-NLS-1$
							break;
					}
				}
			}
		});
		cbSelectSearchType.setModel(new DefaultComboBoxModel(new String[] { Messages.getString("JMsgReader.typeSubject"), Messages.getString("JMsgReader.typePerson"), Messages.getString("JMsgReader.typeText"), "", Messages.getString("JMsgReader.typeSenderOnly"), Messages.getString("JMsgReader.typeReceiverOnly") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		frmMsgCrawler.getContentPane().add(cbSelectSearchType);

		JLabel lblSearchIn = new JLabel(Messages.getString("JMsgReader.lblSearchIn")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, cbSelectSearchType, 7, SpringLayout.SOUTH, lblSearchIn);
		springLayout.putConstraint(SpringLayout.EAST, lblSearchIn, 0, SpringLayout.EAST, cbSelectSearchType);
		springLayout.putConstraint(SpringLayout.NORTH, lblSearchIn, 0, SpringLayout.NORTH, lblSearchtext);
		springLayout.putConstraint(SpringLayout.WEST, lblSearchIn, 0, SpringLayout.WEST, cbSelectSearchType);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSearchIn, 26, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		lblSearchIn.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		frmMsgCrawler.getContentPane().add(lblSearchIn);

		splitPane = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmMsgCrawler.getContentPane().add(splitPane);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scrollPane);

		listResults = new JList();
		listResults.setDoubleBuffered(true);
		listResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listResults);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		epMessageBody = new JEditorPane();
		epMessageBody.setEditable(false);
		epMessageBody.setContentType("text/html"); //$NON-NLS-1$
		epMessageBody.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
		scrollPane_1.setViewportView(epMessageBody);
		splitPane.setDividerLocation(550);

		chbxShowAttachedFiles = new JCheckBox(Messages.getString("JMsgReader.lblShowAttachedFiles")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, chbxShowAttachedFiles, 6, SpringLayout.SOUTH, tfSearchText);
		springLayout.putConstraint(SpringLayout.WEST, chbxShowAttachedFiles, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		chbxShowAttachedFiles.setMnemonic('D');
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 6, SpringLayout.SOUTH, chbxShowAttachedFiles);
		chbxShowAttachedFiles.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		chbxShowAttachedFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chbxShowAttachedFiles.isSelected()) {
					setShowAttachments(true);
				} else {
					setShowAttachments(false);
				}
				RefreshView(tfSearchText.getText());
			}
		});
		frmMsgCrawler.getContentPane().add(chbxShowAttachedFiles);

		chbxShowCCandBCC = new JCheckBox(Messages.getString("JMsgReader.lblShowCCbCC")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, chbxShowCCandBCC, 6, SpringLayout.SOUTH, tfSearchText);
		chbxShowCCandBCC.setMnemonic('C');
		chbxShowCCandBCC.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		chbxShowCCandBCC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chbxShowCCandBCC.isSelected()) {
					setShowCC_BCC(true);
				} else {
					setShowCC_BCC(false);
				}
				RefreshView(tfSearchText.getText());
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, chbxShowCCandBCC, 6, SpringLayout.EAST, chbxShowAttachedFiles);
		frmMsgCrawler.getContentPane().add(chbxShowCCandBCC);

		JButton btnHelp = new JButton(Messages.getString("JMsgReader.dlgHelpCaption")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.SOUTH, cbSelectSearchType, -6, SpringLayout.NORTH, btnHelp);
		btnHelp.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/help.png"))); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, btnHelp, 66, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnHelp, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmMsgCrawler, Messages.getString("JMsgReader.HelpMsgText"), Messages.getString("JMsgReader.dlgHelpCaption"), JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		frmMsgCrawler.getContentPane().add(btnHelp);

		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		frmMsgCrawler.getContentPane().add(progressBar);

		lblInfo = new JLabel(""); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, lblInfo, 69, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblInfo, -9, SpringLayout.NORTH, splitPane);
		springLayout.putConstraint(SpringLayout.EAST, tfSearchText, 0, SpringLayout.EAST, lblInfo);
		lblInfo.setForeground(SystemColor.textHighlight);
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 14)); //$NON-NLS-1$
		frmMsgCrawler.getContentPane().add(lblInfo);

		lblWork = new JLabel();
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -6, SpringLayout.NORTH, lblWork);
		springLayout.putConstraint(SpringLayout.WEST, lblWork, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblWork, -6, SpringLayout.NORTH, progressBar);
		frmMsgCrawler.getContentPane().add(lblWork);

		btnSettings = new JButton(Messages.getString("JMsgReader.lblSettings")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.EAST, lblInfo, -6, SpringLayout.WEST, btnSettings);
		springLayout.putConstraint(SpringLayout.NORTH, btnSettings, 0, SpringLayout.NORTH, btnHelp);
		springLayout.putConstraint(SpringLayout.EAST, btnSettings, -6, SpringLayout.WEST, btnHelp);
		btnSettings.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/setting_tools.png"))); //$NON-NLS-1$
		btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings settings = new Settings();
				settings.setVisible(true);
			}
		});
		frmMsgCrawler.getContentPane().add(btnSettings);
	}

	private void RefreshView(String SearchText) {
		lblWork.setText(Messages.getString("JMsgReader.lblRendering")); //$NON-NLS-1$
		progressBar.setValue(0);
		if (!path.isEmpty()) {
			Mails.searchMails(SearchText, getType(), progressBar);
			ArrayList<Message> mails = Mails.getResult();
			DefaultListModel model = new DefaultListModel();
			progressBar.setValue(0);
			progressBar.setMaximum(mails.size());
			for (Iterator<Message> iterator = mails.iterator(); iterator.hasNext();) {
				Message message = iterator.next();
				model.addElement(message);
				progressBar.setValue(progressBar.getValue() + 1);
			}
			for (int i = 0; i < listResults.getMouseListeners().length; i++) {
				listResults.removeMouseListener(listResults.getMouseListeners()[i]);
			}
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (listResults.getModel().getSize() != -1) {
						if (e.getClickCount() == 1) {
							int index = listResults.locationToIndex(e.getPoint());
							Object item = listResults.getModel().getElementAt(index);
							listResults.setSelectedIndex(index);
							epMessageBody.setText(((Message) item).getColorizedContent());
							epMessageBody.setCaretPosition(0);
						}
						if ((e.getClickCount() == 1) && (e.getModifiers() == InputEvent.BUTTON3_MASK)) {
							int index = listResults.locationToIndex(e.getPoint());
							Object item = listResults.getModel().getElementAt(index);
							try {
								if (Desktop.isDesktopSupported()) {
									Desktop.getDesktop().open(new File(((Message) item).getFilePath()));
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			};
			listResults.setModel(model);
			listResults.setCellRenderer(new AdvancedCellRenderer());
			listResults.addMouseListener(mouseListener);
		}
	}

	/**
	 * @param type
	 *            the type to set
	 */
	private void setType(MailMessages.SearchType type) {
		Type = type;
	}

}
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

	private static JComboBox	comboBox;
	private static JList		list;
	private static MailMessages	Mails;
	private static String		path			= "";		 //$NON-NLS-1$
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
			if (path.toLowerCase().equals("--setassoc")) {  //$NON-NLS-1$
				if (args.length >= 1) {
					FileAssociation.setFileAssociation("." + args[1]);  //$NON-NLS-1$
					System.exit(0);
				}
			}
			path = path.substring(0, path.lastIndexOf("\\"));  //$NON-NLS-1$
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

	private JCheckBox				chckbxZeigeCcsUnd;

	private JCheckBox				chckbxZeigeDateienIn;

	private JEditorPane				editorPane;

	private JFrame					frmMsgCrawler;

	private JLabel					lblInfo;

	private JLabel					lblWork		= new JLabel();

	private JDialog					loadingDialog;

	private JProgressBar			progressBar	= new JProgressBar();

	private JSplitPane				splitPane;

	private SpringLayout			springLayout;

	private JTextField				textField;

	private MailMessages.SearchType	Type;
	private JButton					btnOptionen;

	/**
	 * Create the application.
	 */
	public JMsgReader() {
		initialize();
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() {
				Boolean bOk = true;
				loadingDialog = new JDialog(frmMsgCrawler, Messages.getString("JMsgReader.Loading"));  //$NON-NLS-1$
				final JLabel label = new JLabel();
				label.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/generator.gif")));  //$NON-NLS-1$
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
					RefreshView("");  //$NON-NLS-1$
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
		frmMsgCrawler.setIconImage(Toolkit.getDefaultToolkit().getImage(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/mail-receive.png")));  //$NON-NLS-1$
		frmMsgCrawler.setTitle(Messages.getString("JMsgReader.AppName"));  //$NON-NLS-1$
		frmMsgCrawler.setBounds(100, 100, 754, 550);
		frmMsgCrawler.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frmMsgCrawler.getContentPane().setLayout(springLayout);

		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		textField.setFont(new Font("Tahoma", Font.BOLD, 11));  //$NON-NLS-1$
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (UserSettings.isManualSearch()) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						RefreshView(textField.getText());
					}
				} else {
					RefreshView(textField.getText());
				}
			}
		});
		frmMsgCrawler.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblSuchtext = new JLabel(Messages.getString("JMsgReader.lblSearchText"));  //$NON-NLS-1$
		lblSuchtext.setFont(new Font("Tahoma", Font.BOLD, 11));  //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, textField, 6, SpringLayout.SOUTH, lblSuchtext);
		springLayout.putConstraint(SpringLayout.SOUTH, textField, 34, SpringLayout.SOUTH, lblSuchtext);
		springLayout.putConstraint(SpringLayout.NORTH, lblSuchtext, 10, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblSuchtext, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblSuchtext, 26, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblSuchtext, 97, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		frmMsgCrawler.getContentPane().add(lblSuchtext);

		comboBox = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 6, SpringLayout.EAST, textField);
		springLayout.putConstraint(SpringLayout.EAST, comboBox, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		comboBox.setFont(new Font("Tahoma", Font.BOLD, 11));  //$NON-NLS-1$
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String type = (String) cb.getSelectedItem();
				if (type == Messages.getString("JMsgReader.typeSubject")) {  //$NON-NLS-1$
					setType(MailMessages.SearchType.SUBJECT);
				}
				if (type == Messages.getString("JMsgReader.typePerson")) {  //$NON-NLS-1$
					setType(MailMessages.SearchType.PERSON);
				}
				if (type == Messages.getString("JMsgReader.typeText")) {  //$NON-NLS-1$
					setType(MailMessages.SearchType.CONTENT);
				}
				if (type == Messages.getString("JMsgReader.typeSenderOnly")) {  //$NON-NLS-1$
					setType(MailMessages.SearchType.SENDER_ONLY);
				}
				if (type == Messages.getString("JMsgReader.typeReceiverOnly")) {  //$NON-NLS-1$
					setType(MailMessages.SearchType.RECEIVER_ONLY);
				}
				if (!type.equals("")) {  //$NON-NLS-1$
					RefreshView(textField.getText());
				} else {
					switch (getType()) {
						case SUBJECT:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeSubject"));  //$NON-NLS-1$
							break;
						case PERSON:
							cb.setSelectedItem(Messages.getString("JMsgReader.typePerson"));  //$NON-NLS-1$
							break;
						case CONTENT:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeText"));  //$NON-NLS-1$
							break;
						case SENDER_ONLY:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeSenderOnly"));  //$NON-NLS-1$
							break;
						case RECEIVER_ONLY:
							cb.setSelectedItem(Messages.getString("JMsgReader.typeReceiverOnly"));  //$NON-NLS-1$
							break;
					}
				}
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] { Messages.getString("JMsgReader.typeSubject"), Messages.getString("JMsgReader.typePerson"), Messages.getString("JMsgReader.typeText"), Messages.getString("JMsgReader.27"), Messages.getString("JMsgReader.typeSenderOnly"), Messages.getString("JMsgReader.typeReceiverOnly") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		frmMsgCrawler.getContentPane().add(comboBox);

		JLabel lblSuchenIn = new JLabel(Messages.getString("JMsgReader.lblSearchIn"));  //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, 7, SpringLayout.SOUTH, lblSuchenIn);
		springLayout.putConstraint(SpringLayout.EAST, lblSuchenIn, 0, SpringLayout.EAST, comboBox);
		springLayout.putConstraint(SpringLayout.NORTH, lblSuchenIn, 0, SpringLayout.NORTH, lblSuchtext);
		springLayout.putConstraint(SpringLayout.WEST, lblSuchenIn, 0, SpringLayout.WEST, comboBox);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSuchenIn, 26, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		lblSuchenIn.setFont(new Font("Tahoma", Font.BOLD, 11));
		frmMsgCrawler.getContentPane().add(lblSuchenIn);

		splitPane = new JSplitPane();
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, splitPane, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmMsgCrawler.getContentPane().add(splitPane);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scrollPane);

		list = new JList();
		list.setDoubleBuffered(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");  //$NON-NLS-1$
		editorPane.setFont(new Font("Tahoma", Font.PLAIN, 14));  //$NON-NLS-1$
		scrollPane_1.setViewportView(editorPane);
		splitPane.setDividerLocation(550);

		chckbxZeigeDateienIn = new JCheckBox(Messages.getString("JMsgReader.lblShowAttachedFiles")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, chckbxZeigeDateienIn, 6, SpringLayout.SOUTH, textField);
		springLayout.putConstraint(SpringLayout.WEST, chckbxZeigeDateienIn, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		chckbxZeigeDateienIn.setMnemonic('D');
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 6, SpringLayout.SOUTH, chckbxZeigeDateienIn);
		chckbxZeigeDateienIn.setFont(new Font("Tahoma", Font.BOLD, 11));  //$NON-NLS-1$
		chckbxZeigeDateienIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxZeigeDateienIn.isSelected()) {
					setShowAttachments(true);
				} else {
					setShowAttachments(false);
				}
				RefreshView(textField.getText());
			}
		});
		frmMsgCrawler.getContentPane().add(chckbxZeigeDateienIn);

		chckbxZeigeCcsUnd = new JCheckBox(Messages.getString("JMsgReader.lblShowCCbCC")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, chckbxZeigeCcsUnd, 6, SpringLayout.SOUTH, textField);
		chckbxZeigeCcsUnd.setMnemonic('C');
		chckbxZeigeCcsUnd.setFont(new Font("Tahoma", Font.BOLD, 11));  //$NON-NLS-1$
		chckbxZeigeCcsUnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxZeigeCcsUnd.isSelected()) {
					setShowCC_BCC(true);
				} else {
					setShowCC_BCC(false);
				}
				RefreshView(textField.getText());
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, chckbxZeigeCcsUnd, 6, SpringLayout.EAST, chckbxZeigeDateienIn);
		frmMsgCrawler.getContentPane().add(chckbxZeigeCcsUnd);

		JButton btnHilfe = new JButton(Messages.getString("JMsgReader.dlgHelpCaption"));
		springLayout.putConstraint(SpringLayout.SOUTH, comboBox, -6, SpringLayout.NORTH, btnHilfe);
		btnHilfe.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/help.png"))); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, btnHilfe, 66, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnHilfe, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		btnHilfe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmMsgCrawler, Messages.getString("JMsgReader.HelpMsgText"), Messages.getString("JMsgReader.dlgHelpCaption"), JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		frmMsgCrawler.getContentPane().add(btnHilfe);

		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -10, SpringLayout.SOUTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, frmMsgCrawler.getContentPane());
		frmMsgCrawler.getContentPane().add(progressBar);

		lblInfo = new JLabel(""); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, lblInfo, 69, SpringLayout.NORTH, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblInfo, -9, SpringLayout.NORTH, splitPane);
		springLayout.putConstraint(SpringLayout.EAST, textField, 0, SpringLayout.EAST, lblInfo);
		lblInfo.setForeground(SystemColor.textHighlight);
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 14));  //$NON-NLS-1$
		frmMsgCrawler.getContentPane().add(lblInfo);

		lblWork = new JLabel();
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, -6, SpringLayout.NORTH, lblWork);
		springLayout.putConstraint(SpringLayout.WEST, lblWork, 10, SpringLayout.WEST, frmMsgCrawler.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblWork, -6, SpringLayout.NORTH, progressBar);
		frmMsgCrawler.getContentPane().add(lblWork);

		btnOptionen = new JButton(Messages.getString("JMsgReader.lblSettings"));  //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.EAST, lblInfo, -6, SpringLayout.WEST, btnOptionen);
		springLayout.putConstraint(SpringLayout.NORTH, btnOptionen, 0, SpringLayout.NORTH, btnHilfe);
		springLayout.putConstraint(SpringLayout.EAST, btnOptionen, -6, SpringLayout.WEST, btnHilfe);
		btnOptionen.setIcon(new ImageIcon(JMsgReader.class.getResource("/net/goldenbogen/jmsgreader/setting_tools.png"))); //$NON-NLS-1$
		btnOptionen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings settings = new Settings();
				settings.setVisible(true);
			}
		});
		frmMsgCrawler.getContentPane().add(btnOptionen);
	}

	private void RefreshView(String SearchText) {
		lblWork.setText(Messages.getString("JMsgReader.lblRendering"));  //$NON-NLS-1$
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
			for (int i = 0; i < list.getMouseListeners().length; i++) {
				list.removeMouseListener(list.getMouseListeners()[i]);
			}
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (list.getModel().getSize() != -1) {
						if (e.getClickCount() == 1) {
							int index = list.locationToIndex(e.getPoint());
							Object item = list.getModel().getElementAt(index);
							list.setSelectedIndex(index);
							editorPane.setText(((Message) item).getColorizedContent());
							editorPane.setCaretPosition(0);
						}
						if (e.getClickCount() == 1 && e.getModifiers() == InputEvent.BUTTON3_MASK) {
							int index = list.locationToIndex(e.getPoint());
							Object item = list.getModel().getElementAt(index);
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
			list.setModel(model);
			list.setCellRenderer(new AdvancedCellRenderer());
			list.addMouseListener(mouseListener);
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
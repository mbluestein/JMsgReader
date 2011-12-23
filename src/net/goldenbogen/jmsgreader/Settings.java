/**
 * Settings.java
 * 
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:11:33
 */
package net.goldenbogen.jmsgreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.goldenbogen.jmsgreader.core.UserSettings;

/**
 * @author Goldenbogen, Pierre
 *         Created: 15.12.2011 13:11:33
 * 
 */
public class Settings extends JDialog {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7258379775854225921L;
	private final JPanel		contentPanel		= new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			Settings dialog = new Settings();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Settings() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Settings.class.getResource("/net/goldenbogen/jmsgreader/setting_tools.png"))); //$NON-NLS-1$
		setModal(true);
		setTitle(Messages.getString("JMsgReader.AppName") + " - " + Messages.getString("JMsgReader.lblSettings")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setBounds(100, 100, 490, 167);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		final JCheckBox chbxSearchAfterRetrunOnly = new JCheckBox(Messages.getString("Settings.lblOnlySearchAfterReturnWasHit")); //$NON-NLS-1$
		chbxSearchAfterRetrunOnly.setMnemonic('S');
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chbxSearchAfterRetrunOnly, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chbxSearchAfterRetrunOnly, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chbxSearchAfterRetrunOnly, -7, SpringLayout.EAST, contentPanel);
		chbxSearchAfterRetrunOnly.setSelected(UserSettings.isManualSearch());
		contentPanel.add(chbxSearchAfterRetrunOnly);

		JLabel lblSearchAfterReturnInfoText = new JLabel(Messages.getString("Settings.lblInfoForlblOnlySearchAfterReturnWasHit")); //$NON-NLS-1$
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblSearchAfterReturnInfoText, 6, SpringLayout.SOUTH, chbxSearchAfterRetrunOnly);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblSearchAfterReturnInfoText, -10, SpringLayout.EAST, chbxSearchAfterRetrunOnly);
		lblSearchAfterReturnInfoText.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11)); //$NON-NLS-1$
		lblSearchAfterReturnInfoText.setForeground(new Color(51, 153, 255));
		contentPanel.add(lblSearchAfterReturnInfoText);

		final JCheckBox chbxEnableCaching = new JCheckBox(Messages.getString("Settings.lblEnableCaching")); //$NON-NLS-1$
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chbxEnableCaching, 6, SpringLayout.SOUTH, lblSearchAfterReturnInfoText);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chbxEnableCaching, 0, SpringLayout.WEST, chbxSearchAfterRetrunOnly);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chbxEnableCaching, 0, SpringLayout.EAST, chbxSearchAfterRetrunOnly);
		chbxEnableCaching.setSelected(UserSettings.isEnableCaching());
		chbxEnableCaching.setMnemonic('S');
		contentPanel.add(chbxEnableCaching);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton btnOk = new JButton(Messages.getString("Settings.btnOk")); //$NON-NLS-1$
		btnOk.setIcon(new ImageIcon(Settings.class.getResource("/net/goldenbogen/jmsgreader/accept.png"))); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UserSettings.setManualSearch(chbxSearchAfterRetrunOnly.isSelected());
				UserSettings.setEnableCaching(chbxEnableCaching.isSelected());
				dispose();
			}
		});

		btnOk.setMnemonic('O');
		btnOk.setActionCommand("OK"); //$NON-NLS-1$
		buttonPane.add(btnOk);
		getRootPane().setDefaultButton(btnOk);
		JButton btnCancel = new JButton(Messages.getString("Settings.btnCancel")); //$NON-NLS-1$
		btnCancel.setIcon(new ImageIcon(Settings.class.getResource("/net/goldenbogen/jmsgreader/cancel.png"))); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnCancel.setMnemonic('b');
		btnCancel.setActionCommand("Cancel"); //$NON-NLS-1$
		buttonPane.add(btnCancel);

	}
}

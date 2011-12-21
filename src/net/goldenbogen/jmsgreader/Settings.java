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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import javax.swing.ImageIcon;

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
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Settings.class.getResource("/net/goldenbogen/jmsgreader/setting_tools.png"))); //$NON-NLS-1$
		setModal(true);
		setTitle(Messages.getString("JMsgReader.AppName") + " - " + Messages.getString("JMsgReader.lblSettings")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setBounds(100, 100, 490, 167);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		final JCheckBox chckbxSucheErstMit = new JCheckBox(Messages.getString("Settings.lblOnlySearchAfterReturnWasHit")); //$NON-NLS-1$
		chckbxSucheErstMit.setMnemonic('S');
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chckbxSucheErstMit, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chckbxSucheErstMit, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chckbxSucheErstMit, -7, SpringLayout.EAST, contentPanel);
		chckbxSucheErstMit.setSelected(UserSettings.isManualSearch());
		contentPanel.add(chckbxSucheErstMit);

		JLabel lblNewLabel = new JLabel(Messages.getString("Settings.lblInfoForlblOnlySearchAfterReturnWasHit")); //$NON-NLS-1$
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNewLabel, 6, SpringLayout.SOUTH, chckbxSucheErstMit);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblNewLabel, -10, SpringLayout.EAST, chckbxSucheErstMit);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11)); //$NON-NLS-1$
		lblNewLabel.setForeground(new Color(51, 153, 255));
		contentPanel.add(lblNewLabel);

		final JCheckBox chckbxEnableCaching = new JCheckBox(Messages.getString("Settings.lblEnableCaching")); //$NON-NLS-1$
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chckbxEnableCaching, 6, SpringLayout.SOUTH, lblNewLabel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chckbxEnableCaching, 0, SpringLayout.WEST, chckbxSucheErstMit);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chckbxEnableCaching, 0, SpringLayout.EAST, chckbxSucheErstMit);
		chckbxEnableCaching.setSelected(UserSettings.isEnableCaching());
		chckbxEnableCaching.setMnemonic('S');
		contentPanel.add(chckbxEnableCaching);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton(Messages.getString("Settings.btnOk")); //$NON-NLS-1$
		okButton.setIcon(new ImageIcon(Settings.class.getResource("/net/goldenbogen/jmsgreader/accept.png"))); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.setManualSearch(chckbxSucheErstMit.isSelected());
				UserSettings.setEnableCaching(chckbxEnableCaching.isSelected());
				dispose();
			}
		});
		
		okButton.setMnemonic('O');
		okButton.setActionCommand("OK"); //$NON-NLS-1$
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		JButton cancelButton = new JButton(Messages.getString("Settings.btnCancel")); //$NON-NLS-1$
		cancelButton.setIcon(new ImageIcon(Settings.class.getResource("/net/goldenbogen/jmsgreader/cancel.png"))); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		cancelButton.setMnemonic('b');
		cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
		buttonPane.add(cancelButton);

	}
}

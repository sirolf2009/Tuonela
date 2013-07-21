package menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MenuLogin {

	public boolean shouldContinue;
	public JPanel loginPanel;
	public JButton button;
	public JTextField txtUsername;
	public JLabel lblUsername;
	public JPasswordField txtPassword;
	public JLabel lblPassword;
	public JTextArea update;
	public JPanel updatePanel;
	
	public MenuLogin(JFrame frame) {
		shouldContinue = false;
		
		frame.setBackground(new Color(192, 192, 192));
		loginPanel = new JPanel();
		loginPanel.setBounds(16, 16, 96+80+16, 200);
		loginPanel.setBackground(new Color(192, 192, 192));
		loginPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		txtUsername = new JTextField();
		txtUsername.setSize(80, 20);
		txtUsername.setLocation(96, 16);
		lblUsername = new JLabel("Username");
		lblUsername.setSize(100, 20);
		lblUsername.setLocation(16, 16);
		
		txtPassword = new JPasswordField();
		txtPassword.setSize(80, 20);
		txtPassword.setLocation(96, 32+20);
		lblPassword = new JLabel("Password");
		lblPassword.setSize(100, 20);
		lblPassword.setLocation(16, 32+20);
		
		BufferedImage background = null;
		try {
			background = ImageIO.read(getClass().getClassLoader().getResource("buttonLogin.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		button = new JButton(new ImageIcon(background));
		button.setBounds(16, 32+20+20+32, 96+80-16, 50);
		button.setBorderPainted(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(txtUsername.getText() != "" && txtPassword.getPassword() != null)
					shouldContinue = login();
			}
		});
		
		loginPanel.add(button);
		loginPanel.add(txtUsername);
		loginPanel.add(lblUsername);
		loginPanel.add(txtPassword);
		loginPanel.add(lblPassword);
		
		updatePanel = new JPanel();
		updatePanel.setBounds(96+80+48, 16, 800-(96+80+48)-32, 600-64);
		updatePanel.setBackground(new Color(192, 192, 192));
		updatePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		update = new JTextArea();
		update.setBounds(16, 16, 800-(96+80+48)-64, 600-64-32);
		update.setEditable(false);
		update.setBackground(new Color(194, 194, 194));
		
		try {
			URL url = new URL("https://dl.dropboxusercontent.com/u/50553915/Tuonela/updates.txt");
			Scanner s = new Scanner(url.openStream());
			while(s.hasNext())
				update.append(s.next()+"\n");
			s.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updatePanel.add(update);
		
		frame.add(loginPanel);
		frame.add(updatePanel);
		
		while(!shouldContinue) {
			frame.repaint();
		}
		frame.remove(loginPanel);
		frame.remove(updatePanel);
	}
	
	@SuppressWarnings({ "resource", "deprecation" })
	public boolean login() {
		try {
			URL url = new URL("https://dl.dropboxusercontent.com/u/50553915/Tuonela/accounts.txt");
			Scanner s = new Scanner(url.openStream());
			while(s.hasNext()) {
				String line = s.next();
				int delimeter = line.indexOf("@");
				String username = line.substring(0, delimeter);
				String password = line.substring(delimeter+1);
				if(username.equals(txtUsername.getText()) && password.equals(txtPassword.getText()))
					return true;
			}
			s.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}

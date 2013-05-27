package menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MenuConnect {

	public boolean shouldContinue;
	public JButton button;
	public JTextField ip, port;
	public JLabel lblIp, lblPort;

	public MenuConnect(JFrame frame) {
		shouldContinue = false;

		button = new JButton("connect");
		button.setText("connect");
		button.setBounds(200, 50, frame.getWidth()/2-100, frame.getHeight()/2-25);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ip.getText() != "")
					shouldContinue = true;
			}
		});

		ip = new JTextField("localhost");
		ip.setSize(100, 50);
		ip.setLocation(frame.getWidth()/2-ip.getWidth(), frame.getHeight()/2-ip.getHeight());
		lblIp = new JLabel("IP address");
		lblIp.setBounds(ip.getX(), ip.getY()-16, ip.getWidth(), 16);
		
		port = new JTextField();
		port.setText("999");
		port.setSize(100, 50);
		port.setLocation(frame.getWidth()/2+port.getWidth(), frame.getHeight()/2+port.getHeight());
		lblPort = new JLabel("Port");
		lblPort.setBounds(port.getX(), port.getY()-16, port.getWidth(), 16);

		frame.add(button);
		frame.add(ip);
		frame.add(port);
		frame.add(lblIp);
		frame.getGraphics().setColor(new Color(0, 0, 0));
		while(!shouldContinue) {
			ip.setSize(100, 50);
			ip.setLocation(frame.getWidth()/2-ip.getWidth(), frame.getHeight()/2-ip.getHeight());
			lblIp.setBounds(ip.getX(), ip.getY()-16, ip.getWidth(), 16);
			port.setSize(100, 50);
			port.setLocation(frame.getWidth()/2, frame.getHeight()/2-port.getHeight());
			lblPort.setBounds(port.getX(), port.getY()-16, port.getWidth(), 16);
			button.setBounds(frame.getWidth()/2-100, frame.getHeight()/2-25+50, 200, 50);
			frame.repaint();
		}
		frame.remove(button);
		frame.remove(ip);
		frame.remove(port);
		frame.remove(lblIp);
	}

}

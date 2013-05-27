package core;

import helper.ParserXML;
import helper.Reference;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import packet.PacketWorld;

import entity.EntityPlayer;

import render.RenderEntity;
import server.Client;
import server.Connector;
import server.ServerClientCommunicator;
import world.World;

public class TuonelaServer implements Runnable{

	public JFrame frame;
	public JList<String> clientsList;
	public JLabel lblName;
	public JTextArea txtName;
	public JLabel lblPing;
	public JTextArea txtPing;
	public JLabel lblIP;
	public JTextArea txtIP;
	public JTextArea txtLog;
	public JButton kick, ban;
	public Map<String, Client> clients;
	public World world;
	public ServerClientCommunicator communicator;
	public Connector connector;

	public TuonelaServer(int port) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		//redirectSystemStreams();
		initScreen();
		world = new World("map/testMap.png");
		clients = new HashMap<String, Client>();
		communicator = new ServerClientCommunicator(clients);
		connector = new Connector(this, server);
		new Thread(new Connector(this, server), "Connector").start();
		new Thread(new ServerClientCommunicator(clients)).start();
	}
	
	@Override
	public void run() {
		long startingTime = System.currentTimeMillis();
		long lastTime = startingTime;
		while(true) {
			long deltaTime = System.currentTimeMillis() - lastTime;
			lastTime += deltaTime;
			world.update(deltaTime);
		}
	}

	private void initScreen() {
		frame = new JFrame("Tuonela Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(null);
		clientsList = new JList<String>();
		clientsList.addListSelectionListener(new ClientListListener());
		clientsList.setBounds(16, 16, 200, 569-32);
		lblName = new JLabel("Name: ");
		lblName.setBounds(224, 12, 48, 16);
		txtName = new JTextArea();
		txtName.setBounds(224+48, 12, 200, 16);
		txtName.setEditable(false);
		lblIP = new JLabel("IP: ");
		lblIP.setBounds(224, 24+12, 24+12, 16);
		txtIP = new JTextArea();
		txtIP.setBounds(224+48, 24+12, 200, 16);
		txtIP.setEditable(false);
		kick = new JButton("kick");
		kick.setBounds(224+48, 24+26+12, 64, 64);
		ban = new JButton("ban");
		ban.setBounds(224+48+12+64, 24+26+12, 64, 64);
		txtLog = new JTextArea();
		txtLog.setBounds(232,600-200-48,553-16,200);
		txtLog.setEditable(false);
		txtLog.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(txtLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setPreferredSize(new Dimension(16, 200));
		frame.add(clientsList);
		frame.add(lblName);
		frame.add(txtName);
		frame.add(lblIP);
		frame.add(txtIP);
		frame.add(kick);
		frame.add(ban);
		frame.add(scroll);
		frame.add(txtLog);
		frame.setVisible(true);
	}

	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				txtLog.append(text);
			}
		});
	}

	@SuppressWarnings("unused")
	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	private class ClientListListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				String selection = clientsList.getSelectedValue().toString();
				Client client = clients.get(selection);
				txtName.setText(client.username);
				txtIP.setText(client.socket.getRemoteSocketAddress().toString());
			}
		}
	}

	public void addClient(Client client) {
		URI location = null;
		try {
			location = getClass().getClassLoader().getResource("data/players.xml").toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if(location == null) {
			System.out.println("creating new players file");
			Document doc = ParserXML.createDocument(location);
			doc.appendChild(doc.createElement("players"));
		}
		Document doc = ParserXML.parse(Reference.RESOURCE_PLAYERS_DATA);
		NodeList list = ParserXML.getNodeListByName(doc, "player");
		if(ParserXML.retrieveValueFromNodeWithAttribute(list, "name", client.username) == null) {
			System.out.println("creating player data");
			Element rootElement = doc.getDocumentElement();
			Element player = doc.createElement("player");
			player.setAttribute("name", client.username);
			Element posX = doc.createElement("posX");
			posX.appendChild(doc.createTextNode("0"));
			player.appendChild(posX);
			Element posY = doc.createElement("posY");
			posY.appendChild(doc.createTextNode("0"));
			player.appendChild(posY);
			rootElement.appendChild(player);
			doc.getDocumentElement().appendChild(player);
			ParserXML.saveDocument(doc, location);
		}
		NodeList playerlist = ParserXML.retrieveValueFromNodeWithAttribute(list, "name", client.username).getChildNodes();
		int posX = Integer.parseInt(ParserXML.retrieveValueFromNodeName(playerlist, "posX"));
		int posY = Integer.parseInt(ParserXML.retrieveValueFromNodeName(playerlist, "posY"));
		EntityPlayer player = new EntityPlayer(world, posX, posY);
		player.setRenderer(new RenderEntity(player));
		world.addEntityToWorld(player);
		client.player = player;
		clients.put(client.username, client);
		new PacketWorld(world, player.getEntityID()).send(client.out);
		Iterator<Entry<String, Client>> itr = clients.entrySet().iterator();
		DefaultListModel<String> model = new DefaultListModel<String>();
		while(itr.hasNext()) {
			Client client2 = itr.next().getValue();
			model.addElement(client2.username);
			clientsList.setModel(model);
			frame.repaint();
		}
	}

	public static void main(String[] args) {
		int port;
		if(args.length > 1) {
			port = Integer.parseInt(args[0]);
		} else {
			System.err.println("No port defined, default set to 999");
			port = 999;
		}
		new TuonelaServer(port);
	}

}


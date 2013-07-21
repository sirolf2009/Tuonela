package server;

import helper.LogType;
import helper.Logger;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import packet.Packet;
import packet.PacketEntityRemoval;
import core.TuonelaServer;

public class ServerClientCommunicator implements Runnable {

	private TuonelaServer server;

	public ServerClientCommunicator(TuonelaServer server) {
		this.server = server;
	}

	public byte[] convertImageToBytes(BufferedImage img) {
		return ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
	}

	public synchronized void sendPacket(Packet packet) {
		for(Client client : server.clients.values()) {
			if(!packet.ignoreList.contains(client.username)) {
				client.sendPacket(packet);
			}
		}
	}

	@Override
	public void run() {
		Sender sender = new Sender();
		Receiver receiver = new Receiver();
		new Thread(sender, "server sender").start();
		new Thread(receiver, "server receiver").start();
	}

	private class Sender implements Runnable {

		@Override
		public synchronized void run() {
			while(true) {
				try {
					sendPackets();
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private synchronized void sendPackets() {
			Iterator<Client> itrClients = server.clients.values().iterator();
			while(itrClients.hasNext()) {
				Client client = itrClients.next();
				client.sendPackets();
			}
		}

	}

	private class Receiver implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					List<Client> disconnected = new ArrayList<Client>();
					Iterator<Client> itr = server.clients.values().iterator();
					while(itr.hasNext()) {
						Client client = itr.next();
						try {
							String packetID = client.in.readLine();
							if(packetID.isEmpty() || packetID.contains(" ") || Packet.PacketsIDToClass.get(packetID) == null) {
								System.err.println("weird packet ID: "+packetID);
							} else {
								Packet packet = (Packet) Packet.PacketsIDToClass.get(packetID).newInstance();
								Logger.log(LogType.LOG_PACKET_RECEIVING, "Server received "+ packet);
								packet.receive(client.in);
								client.ping = packet.ping;
								packet.receivedOnServer(server);
							}
						} catch(SocketException e) {
							disconnected.add(client);
						} catch (IOException | InstantiationException | IllegalAccessException e) {
							if(!(e instanceof EOFException))
								e.printStackTrace();
						}
					}
					for(Client client : disconnected) {
						server.removeClient(client);
						sendPacket(new PacketEntityRemoval(client.player));
						System.out.println(client.username+" disconnected.");
					}
					Thread.sleep(20); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

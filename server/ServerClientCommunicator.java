package server;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import packet.Packet;
import packet.PacketPing;
import packet.PacketWorld;

public class ServerClientCommunicator implements Runnable {

	public Map<String, Client> clients;
	private List<Packet> packets = new ArrayList<Packet>();

	public ServerClientCommunicator(Map<String, Client> clients) {
		this.clients = clients;
	}

	public byte[] convertImageToBytes(BufferedImage img) {
		return ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
	}

	public void sendPacket(Packet packet) {
		System.out.println("adding "+packet);
		packets.add(packet);
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
		public void run() {
			while(true) {
				Iterator<Client> itrClients = clients.values().iterator();
				while(itrClients.hasNext()) {
					Client client = itrClients.next();
					Iterator<Packet> itrPackets = packets.iterator();
					while(itrPackets.hasNext()) {
						Packet packet = itrPackets.next();
						if(client.worldConfirmed) {
							packet.send(client.out);
							System.out.println("send "+packet+" on server");
						}
					}
				}
				packets.clear();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class Receiver implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Iterator<Client> itr = clients.values().iterator();
					while(itr.hasNext()) {
						Client client = itr.next();
						String packetID = client.in.readUTF();
						if(packetID.equals("@ping")) {
							PacketPing ping = (PacketPing) client.in.readObject();
							ping.receivedOnServer();
							packets.add(ping);
						} else if(packetID.equals("@world")) {
							PacketWorld world = (PacketWorld) client.in.readObject();
							world.receivedOnServer();
							client.worldConfirmed = true;
						}
					}
					Thread.sleep(20); 
				} catch (IOException | ClassNotFoundException | InterruptedException e) {
					if(!(e instanceof EOFException))
						e.printStackTrace();
				}
			}
		}
	}
}

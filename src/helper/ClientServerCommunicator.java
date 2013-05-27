package helper;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import packet.Packet;
import packet.PacketPing;
import packet.PacketWorld;

import core.Tuonela;

public class ClientServerCommunicator implements Runnable {

	public ObjectInputStream in;
	public ObjectOutputStream out;
	public Socket serverSocket;
	public Tuonela client;
	private List<Packet> packets = new ArrayList<Packet>();

	public ClientServerCommunicator(ObjectInputStream in, ObjectOutputStream out, Socket serverSocket, Tuonela client, String userName) {
		this.in = in;
		this.out = out;
		this.serverSocket = serverSocket;
		this.client = client;
	}

	public void sendPacket(Packet packet) {
		packets.add(packet);
	}

	@Override
	public void run() {
		long startingTime = System.currentTimeMillis();
		long lastTime = startingTime;
		long pingCounter = 0;
		Sender sender = new Sender();
		Receiver receiver = new Receiver(client);
		new Thread(sender, "ClientServerSender").start();
		new Thread(receiver, "ClientServerReceiver").start();
		while(true) {
			long deltaTime = System.currentTimeMillis() - lastTime;
			lastTime += deltaTime;
			pingCounter += deltaTime;
			if(pingCounter > 1000) {
				pingCounter = 0;
				sendPacket(new PacketPing());
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class Sender implements Runnable {

		@Override
		public void run() {
			while(true) {
				Iterator<Packet> itr = packets.iterator();
				while(itr.hasNext()) {
					Packet packet = itr.next();
					packet.send(out);
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
		
		Tuonela tuonela;
		
		private Receiver(Tuonela tuonela) {
			this.tuonela = tuonela;
		}

		@Override
		public void run() {
			while(true) {
				try {
					String packetID = in.readUTF();
					System.out.println("received "+packetID+" on client");
					if(packetID.equals("@ping")) {
						PacketPing ping = (PacketPing) in.readObject();
						ping.receivedOnClient(tuonela);
						System.out.println("ping: "+(ping.ping/1000));
					} else if(packetID.equals("@world")) {
						PacketWorld packet = (PacketWorld) in.readObject();
						packet.receivedOnClient(Tuonela.instance);
						packet.send(out);
					} else {
						Packet packet = (Packet) in.readObject();
						packet.receivedOnClient(tuonela);
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
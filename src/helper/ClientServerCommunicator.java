package helper;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import packet.Packet;
import core.GameState;
import core.Tuonela;

public class ClientServerCommunicator implements Runnable {

	public BufferedReader in;
	public PrintWriter out;
	public Socket serverSocket;
	public Tuonela client;
	public long ping;
	private List<Packet> packetqueue = new ArrayList<Packet>();
	private List<Packet> packets = new ArrayList<Packet>();

	public ClientServerCommunicator(BufferedReader serverReader, PrintWriter serverWriter, Socket serverSocket, Tuonela client) {
		this.in = serverReader;
		this.out = serverWriter;
		this.serverSocket = serverSocket;
		this.client = client;
	}

	public synchronized void sendPacket(Packet packet) {
		packetqueue.add(packet);
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
				//sendPacket(new PacketPing());
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
			packets = new ArrayList<Packet>(packetqueue);
			packetqueue.clear();
			Iterator<Packet> itr = packets.iterator();
			while(itr.hasNext()) {
				Packet packet = itr.next();
				Logger.log(LogType.LOG_PACKET_SENDING, "Client send "+ packet);
				packet.send(out);
			}
			packets.clear();
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
					String packetID = in.readLine();
					if(packetID.isEmpty() || packetID.contains(" ") || Packet.PacketsIDToClass.get(packetID) == null) {
						Logger.logErr(LogType.LOG_PACKET, "weird packet ID: "+packetID);
					} else {
						Packet packet = (Packet) Packet.PacketsIDToClass.get(packetID).newInstance();
						Logger.log(LogType.LOG_PACKET_RECEIVING, "Client received "+ packet);
						packet.receive(in);
						ping = packet.ping;
						packet.receivedOnClient(tuonela);
						Thread.sleep(20);
					}
				} catch (SocketException e) {
					Tuonela.gameState = GameState.MENU_CONNECT;
				} catch (IOException | InterruptedException | InstantiationException | IllegalAccessException e) {
					if(!(e instanceof EOFException))
						e.printStackTrace();
				}
			}
		}

	}
}
package server;

import helper.LogType;
import helper.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import packet.Packet;
import entity.EntityPlayer;

public class Client {

	public String username;
	public BufferedReader in;
	public PrintWriter out;
	public Socket socket;
	public EntityPlayer player;
	public long ping;
	private List<Packet> packetQue;

	public Client(Socket socket) {
		this.socket = socket;
		packetQue = new ArrayList<Packet>();
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void sendPacket(Packet packet) {
		packetQue.add(packet);
	}
	
	public synchronized void sendPackets() {
		List<Packet> packets = new ArrayList<Packet>(packetQue);
		packetQue.clear();
		Iterator<Packet> itr = packets.iterator();
		while(itr.hasNext()) {
			Packet packet = itr.next();
			Logger.log(LogType.LOG_PACKET_SENDING, "Sending "+ packet +" to "+ this);
			packet.send(out);
		}
		packets.clear();
	}
}

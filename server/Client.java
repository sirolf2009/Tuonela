package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import entity.EntityPlayer;

public class Client {

	public String username;
	public ObjectInputStream in;
	public ObjectOutputStream out;
	public Socket socket;
	public EntityPlayer player;
	public boolean worldConfirmed = false;

	public Client(Socket socket) {
		this.socket = socket;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

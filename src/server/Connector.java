package server;

import java.io.IOException;
import java.net.ServerSocket;
import core.TuonelaServer;

public class Connector implements Runnable {
	
	TuonelaServer server;
	ServerSocket serverSocket;
	
	public Connector(TuonelaServer server, ServerSocket serverSocket) {
		this.server = server;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Client client = new Client(serverSocket.accept());
				if(client != null) {
					System.out.println("Connecting: "+client.socket.getRemoteSocketAddress());
					client.username = client.in.readLine();
					if(client.username == null || client.username == "") {
						System.err.println("lost connection with "+client.socket.getRemoteSocketAddress());
						client.socket.close();
						continue;
					}
					System.out.println(client.socket.getRemoteSocketAddress()+" recognised as " + client.username);
					server.addClient(client);
					System.out.println(client.username+" connected.");
				}
			} catch (IOException e) {}
		}
	}

}

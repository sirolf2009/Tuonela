package packet;

import core.Tuonela;

public class PacketPing extends Packet {

	private static final long serialVersionUID = 9066160144575262012L;
	public long ping;
	private long oldTime;

	public PacketPing() {
		super("ping");
		oldTime = System.currentTimeMillis();
	}
	
	@Override
	public void receivedOnClient(Tuonela tuonela) {
		long newTime = System.currentTimeMillis();
		ping = newTime - oldTime;
	}

}

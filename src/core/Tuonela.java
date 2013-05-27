package core;

import handler.KeyHandler;
import handler.MouseHandler;
import helper.ClientServerCommunicator;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import menu.MenuConnect;
import entity.EntityPlayer;
import render.RenderManager;
import screen.ScreenManager;
import world.World;

public class Tuonela implements Runnable {

	public ScreenManager screenManager;
	public RenderManager renderManager;
	public KeyHandler keyHandler;
	public MouseHandler mouseHandler;
	public World world;
	public boolean fullscreen;
	public boolean shouldCenterMouse;
	public int playerID = -1;
	public JFrame frame;
	public String appName;
	public String ipToConnect;
	public String portToConnect;
	public static GameState gameState;

	public Socket serverSocket = null;
	public ObjectOutputStream serverWriter = null;
	public ObjectInputStream serverReader = null;

	public static Tuonela instance;

	public Tuonela(boolean fullscreen) {
		this.appName = "Tuonela";
		this.fullscreen = fullscreen;
	}

	public void run() {
		init(appName, fullscreen);
		try {
			menuLoop();
			gameLoop();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			screenManager.restoreScreen();
		}
	}

	private void init(String appName, boolean fullscreen) {
		gameState = GameState.INITIALIZING;
		instance = this;
		screenManager = new ScreenManager();
		renderManager = new RenderManager();
		renderManager.init();
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler();
		if(fullscreen) {
			DisplayMode mode = screenManager.getFirstSupportedMode(modes);
			frame = screenManager.setFullscreen(mode);
		} else
			frame = new JFrame();
		frame.setTitle(appName);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.setBackground(Color.GREEN);
		frame.setForeground(Color.WHITE);
		frame.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.setFocusTraversalKeysEnabled(false);
		frame.addKeyListener(keyHandler);
		frame.addMouseListener(mouseHandler);
		frame.addMouseMotionListener(mouseHandler);
		frame.addMouseWheelListener(mouseHandler);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		shouldCenterMouse = false;
	}

	public void menuLoop() {
		while(true) {
			//MenuLogin login = new MenuLogin(frame);
			//player.username = login.txtUsername.getText();
			gameState = GameState.MENU_CONNECT;
			MenuConnect menu = new MenuConnect(frame);
			ipToConnect = menu.ip.getText();
			portToConnect = menu.port.getText();
			if(connect())
				break;
		}
	}

	public boolean connect() {
		try {
			gameState = GameState.CONNECTING;
			serverSocket = new Socket(ipToConnect, Integer.valueOf(portToConnect));
			serverWriter = new ObjectOutputStream(serverSocket.getOutputStream());
			serverReader = new ObjectInputStream(serverSocket.getInputStream());
			ClientServerCommunicator rcv = new ClientServerCommunicator(serverReader, serverWriter, serverSocket, this, "sirolf2009");
			serverWriter.writeUTF("sirolf2009");
			serverWriter.flush();
			Thread rcvThread = new Thread(rcv, "ClientServerCommunicator");
			rcvThread.start();
			while(gameState == GameState.CONNECTING){}
			return true;
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: "+ipToConnect);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: "+ipToConnect);
		}
		System.err.println("connecting failed");
		return false;
	}

	public void gameLoop() {
		long startingTime = System.currentTimeMillis();
		long lastTime = startingTime;
		while(gameState == GameState.PLAYING) {
			long deltaTime = System.currentTimeMillis() - lastTime;
			lastTime += deltaTime;
			if(shouldCenterMouse)
				mouseHandler.centerMouse(screenManager);
			update(deltaTime);
			draw();
			screenManager.update();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.err.println("If you can't sleep, don't drink coffee");
			}
		}
		frame.setEnabled(false);
		frame.dispose();
		System.exit(0);
	}

	private void update(long deltaTime) {
		renderManager.update(deltaTime, screenManager);
		world.update(deltaTime);
	}

	private void draw() {
		Graphics2D g = screenManager.getGraphics();
		int offsetX = (int) (getLocalPlayer().getPosX()-frame.getWidth()/2);
		int offsetY = (int) (getLocalPlayer().getPosY()-frame.getHeight()/2);
		g.translate(-offsetX, -offsetY);
		renderManager.renderBackground(g, world.tiles, world.levelWidth, world.levelHeight, offsetX, offsetY, frame.getWidth(), frame.getHeight());
		world.renderEntities(g);
	}
	
	public EntityPlayer getLocalPlayer() {
		if(playerID == -1)
			return null;
		return (EntityPlayer) world.livingEntities.get(playerID);
	}

	public static void stop() {
		gameState = GameState.SHUTTING_DOWN;
	}
	
	public static void main(String[] args) {
		boolean fullscreen = false;
		if(args.length > 0 && args[0] != null) {
			fullscreen = args[0] == "true" ? true : false;
		}
		Thread tuonela = new Thread(new Tuonela(fullscreen), "Tuonela");
		Thread tuonelaServer = new Thread(new TuonelaServer(999), "Tuonela Server");
		tuonela.setPriority(1);
		tuonelaServer.setPriority(2);
		tuonela.start();
		tuonelaServer.start();
	}

	public static final DisplayMode[] modes = {
		new DisplayMode(800, 600, 32, 0),
		new DisplayMode(800, 600, 24, 0),
		new DisplayMode(800, 600, 16, 0),
		new DisplayMode(640, 480, 32, 0),
		new DisplayMode(640, 480, 24, 0),
		new DisplayMode(640, 480, 16, 0),
	};
}

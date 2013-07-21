package core;

import handler.KeyHandler;
import handler.MouseHandler;
import helper.ClientServerCommunicator;
import helper.Logger;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import menu.MenuConnect;
import menu.MenuLogin;
import render.RenderManager;
import screen.ScreenManager;
import world.World;
import entity.EntityPlayer;

public class Tuonela implements Runnable {

	public ScreenManager screenManager;
	public RenderManager renderManager;
	public KeyHandler keyHandler;
	public MouseHandler mouseHandler;
	public World world;
	public boolean fullscreen;
	public boolean shouldCenterMouse;
	private int playerID = -1;
	public JFrame frame;
	public String appName;
	public String ipToConnect;
	public String portToConnect;
	public String username;
	public static GameState gameState;
	public static int instanceID = new Random().nextInt(255);

	public Socket serverSocket = null;
	public PrintWriter serverWriter = null;
	public BufferedReader serverReader = null;
	public ClientServerCommunicator communicator;

	public static Tuonela instance;

	public Tuonela(boolean fullscreen) {
		this.appName = "Tuonela";
		this.fullscreen = fullscreen;
		instance = this;
	}

	@Override
	public void run() {
		gameState = GameState.CONNECTING;
		try {
			while(true) {
				switch(gameState) {
				case CONNECTING:
					initScreen(appName, fullscreen);
					//TODO add loading screen
					init();
				case MENU_LOGIN:
					MenuLogin login = new MenuLogin(frame);
					username = login.txtUsername.getText();
					gameState = GameState.MENU_CONNECT;
				case MENU_CONNECT:
					MenuConnect menu = new MenuConnect(frame);
					ipToConnect = menu.ip.getText();
					portToConnect = menu.port.getText();
					connect();
				default:
					gameLoop();
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			screenManager.restoreScreen();
		}
	}

	private void init() {
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler();
		frame.addKeyListener(keyHandler);
		frame.addMouseListener(mouseHandler);
		frame.addMouseMotionListener(mouseHandler);
		frame.addMouseWheelListener(mouseHandler);
		renderManager = new RenderManager();
		renderManager.init();
	}
	
	private void initScreen(String appName, boolean fullscreen) {
		screenManager = new ScreenManager();
		if(fullscreen) {
			DisplayMode mode = screenManager.getFirstSupportedMode(modes);
			frame = screenManager.setFullscreen(mode);
		} else
			frame = new JFrame();
		frame.setTitle(appName + ":" + instanceID);
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.setBackground(Color.GREEN);
		frame.setForeground(Color.WHITE);
		frame.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.setFocusTraversalKeysEnabled(false);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		shouldCenterMouse = false;
		Graphics2D g = screenManager.getGraphics();
		try {
			BufferedImage loading = ImageIO.read(new File(getClass().getClassLoader().getResource("sprites/menu/loading.png").toURI()));
			g.drawImage(loading, 0, 0, 800, 600, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public boolean connect() {
		try {
			gameState = GameState.CONNECTING;
			serverSocket = new Socket(ipToConnect, Integer.valueOf(portToConnect));
			serverWriter = new PrintWriter(serverSocket.getOutputStream());
			serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			communicator = new ClientServerCommunicator(serverReader, serverWriter, serverSocket, this);
			serverWriter.println(username);
			serverWriter.flush();
			Thread rcvThread = new Thread(communicator, "ClientServerCommunicator");
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
		int offsetX = (int) (getLocalPlayer().getPosX()+getLocalPlayer().getRenderer().sprite.getWidth()/2-frame.getWidth()/2);
		int offsetY = (int) (getLocalPlayer().getPosY()+getLocalPlayer().getRenderer().sprite.getHeight()/2-frame.getHeight()/2);
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
		tuonela.setPriority(1);
		tuonela.start();
	}

	public static final DisplayMode[] modes = {
		new DisplayMode(800, 600, 32, 0),
		new DisplayMode(800, 600, 24, 0),
		new DisplayMode(800, 600, 16, 0),
		new DisplayMode(640, 480, 32, 0),
		new DisplayMode(640, 480, 24, 0),
		new DisplayMode(640, 480, 16, 0),
	};

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		Logger.log("player id set to " + playerID);
	}
}

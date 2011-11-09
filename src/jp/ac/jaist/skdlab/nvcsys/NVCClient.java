package jp.ac.jaist.skdlab.nvcsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javafx.application.Platform;

import jp.ac.jaist.skdlab.nvcsys.ui.OperationScene;
import jp.ac.jaist.skdlab.nvcsys.ui.StartScene;

/**
 * The non-verbal communication support system - Client program
 * 
 * @author Yutaka Kato
 * @version 0.3.0
 */
public class NVCClient implements Runnable {

	public static final String VERSION = "0.3.0";
	public static final int DEFAULT_PORT = 300001;
	
	public static String name = null;
	public static String title = null;
	
	private static NVCClient instance = null;
	private static Object currentScene = null;
	private Socket socket = null;
	private Thread thread = null;
	private List<String> discussionList = null;
	private List<String> discussionUserList = null;
	
	/**
	 * Singleton constructor
	 */
	private NVCClient() {
		// Do nothing
	}
	
	public static NVCClient getInstance() {
		if (instance == null) {
			instance = new NVCClient();
		}
		return instance;
	}
	
	public static void setCurrentShowingScene(Object scene) {
		currentScene = scene;
	}
	
	public boolean connectServer(String address, int port) {
		
		discussionList = new ArrayList<String>();
		discussionUserList = new ArrayList<String>();
		
		try {
			socket = new Socket(address, port);
			System.out.println("Connected with " + address + ":" + port);
			
			thread = new Thread(this);
			thread.start();
			
			return true;
		} catch (IOException e) {
			System.err.println("IO Error at connectServer()");
			return false;
		}
	}

	public void close() throws IOException {
		sendMessage("CLOSE");
		socket.close();
	}
	
	/**
	 * Send message to NVCServer
	 * 
	 * @param message Message string
	 */
	public void sendMessage(String message) {
		System.out.println("Sending message: " + message);
		try {
			PrintWriter writer = new PrintWriter(
					socket.getOutputStream());
			writer.println(message);
			writer.flush();
		} catch (IOException e) {
			System.err.println("IO Error at sendMessage()");
		}
	}
	
	/**
	 * Process messages from server
	 * 
	 * @param name Command name
	 * @param value Command value
	 */
	public void reachedMessage(final String name, final String value) {
		
		System.out.println("Reached: " + name + " " + value);
		
		// Update status string at OperationScene
		if (currentScene instanceof OperationScene) {
			((OperationScene) currentScene).setStatus(
					"[Reaced] " + name + " " + value);
		}

		
		if (name.equals("GETD_R")) {
			discussionList.clear();
			StringTokenizer token = new StringTokenizer(value, ",");
			while (token.hasMoreTokens()) {
				discussionList.add(token.nextToken());
			}
			
			sendMessage("CHANGE " + NVCClient.name);
			
			// Change trigger of StartScene
			if (currentScene instanceof StartScene) {
				
			}

			// Update status string at OperationScene
			if (currentScene instanceof OperationScene) {
				((OperationScene) currentScene).setStatus(
						"[Change] " + name + " " + value);
			}
		}
		
		else if (name.equals("GETU_R")) {
			discussionUserList.clear();
			StringTokenizer token = new StringTokenizer(value, ",");
			while (token.hasMoreTokens()) {
				discussionUserList.add(token.nextToken());
			}
			
			// Out me (operator)
			if (discussionUserList.contains(NVCClient.name)) {
				discussionUserList.remove(NVCClient.name);
			}
		}
		
		else if (name.equals("ADDD_R")) {
			
			// Change trigger of DiscussionActivity
				sendMessage("GETU " + title);
	    		enteredDiscussion(title);
		}
		
		else if (name.equals("ENTER_R")) {
			
			// Change trigger of DiscussionActivity
				sendMessage("GETU " + title);
	    		enteredDiscussion(title);
		}
		
		else if (name.equals("ENTER")) {
			System.out.println("Entered: " + value);
			
			if (value != null && discussionUserList != null) {
				if (!discussionUserList.contains(value)) {
					discussionUserList.add(value);					
				}
				// Out me (operator)
				if (discussionUserList.contains(NVCClient.name)) {
					discussionUserList.remove(NVCClient.name);
				}
			}
			
			// Update status string at OperationScene
			if (currentScene instanceof OperationScene) {
				((OperationScene) currentScene).setStatus(
						"[Entered] " + value);
//				Platform.runLater(new Runnable() {
//					@Override public void run() {
//						((OperationScene) currentScene).setMembers(
//								discussionUserList);
//					}
//				});
				new JFXSetMembersThread().start();
			}
		}
		
		else if (name.equals("LEAVE")) {
			System.out.println("Leaved: " + value);
			
			if (value != null && discussionUserList != null) {
				if (discussionUserList.contains(value)) {
					discussionUserList.remove(value);
				}
			}

			// Update status string at OperationScene
			if (currentScene instanceof OperationScene) {
				((OperationScene) currentScene).setStatus(
						"[Leaved] " + value);
//				Platform.runLater(new Runnable() {
//					@Override public void run() {
//						((OperationScene) currentScene).setMembers(
//								discussionUserList);
//					}
//				});
				new JFXSetMembersThread().start();
			}
		}
		
		else if (name.equals("MESSAGE")) {
			System.out.println("Message: " + value);
			

		}
		
		else if (name.equals("UP_ALL")) {

		}
		
		else if (name.equals("DOWN_ALL")) {

		}
		
		else if (name.equals("UP")) {

		}
		
		else if (name.equals("OK")) {
//			System.out.println("OK reached");
		}
		
		else if (name.equals("ERROR")) {
			System.err.println("Error message: " + value);
//			NVCClientUtility.showAlertDialog("ERROR", value, activity);
		}
	}
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while (!socket.isClosed()) {
				String message = reader.readLine();
				String[] messageArray = message.split(" ", 2);
				String name = messageArray[0];
				String value = messageArray.length < 2 ? "" : messageArray[1];
				reachedMessage(name, value);
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// Closed
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Discussing state
	public void enteredDiscussion(String title) {
		NVCClient.title = title;
		System.out.println("entered discussion");
	}
	
	// Non-discussing state
	public void exitedDiscussion() {
		NVCClient.title = null;
		System.out.println("exited discussion");
	}
	
	public List<String> getDiscussionList() {
		return discussionList;
	}
	
	public List<String> getDiscussionUserList() {
		return discussionUserList;
	}
	
	class JFXSetMembersThread extends Thread {
		
		@Override
		public void run() {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					((OperationScene) currentScene).setMembers(
							discussionUserList);
				}
			});
		}
	}
}
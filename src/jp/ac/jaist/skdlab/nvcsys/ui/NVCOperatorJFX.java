package jp.ac.jaist.skdlab.nvcsys.ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Non-verbal communication support system - PC Client program (for operator)
 * 
 * @author Yutaka Kato
 * @version 0.2.0
 */
public class NVCOperatorJFX extends Application {
	
	public static final String VERSION = "0.1.0";
	public static final int STATE_DISCONNECTED = 100;
	public static final int STATE_PREDISCUSSING = 200;
	public static final int STATE_DISCUSSING = 300;
	
	public volatile boolean connected = false;
	public volatile int state = STATE_DISCONNECTED;
	
	private Stage primaryStage = null;

	/**
	 * Launcher method
	 * 
	 * @param args Program arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;		
		Group root = new Group();
		root.getChildren().add(new StartScene(this).getContent());
		this.primaryStage.setScene(
				new Scene(root, JFXUtility.WIDTH, JFXUtility.HEIGHT));
		this.primaryStage.setTitle("NVC Client");
		this.primaryStage.setResizable(false);
		this.primaryStage.show();
	}
	
	protected void showOperationScene() {
		Group root = new Group();
		root.getChildren().add(new OperationScene(this).getContent());
		primaryStage.setScene(
				new Scene(root, JFXUtility.WIDTH, JFXUtility.HEIGHT));
		primaryStage.setTitle("NVC Client - Operation");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	protected void showStartScene() {
		Group root = new Group();
		root.getChildren().add(new StartScene(this).getContent());
		primaryStage.setScene(
				new Scene(root, JFXUtility.WIDTH, JFXUtility.HEIGHT));
		primaryStage.setTitle("NVC Client - Operation");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}

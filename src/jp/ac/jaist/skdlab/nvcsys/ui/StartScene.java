package jp.ac.jaist.skdlab.nvcsys.ui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import jp.ac.jaist.skdlab.nvcsys.NVCClient;

/**
 * Create and manage "Start" scene.
 * 
 * @author Yutaka Kato
 *
 */
public class StartScene {
	
	private NVCClient client = NVCClient.getInstance();
	private NVCOperatorJFX parent = null;
	
	private TextField textFieldAddress = null;
	private TextField textFieldPort = null;
	private TextField textFieldName = null;
	private TextField textFieldTitle = null;
	private Button buttonConnect = null;
	private Label labelStatus = null;
	
	public StartScene(NVCOperatorJFX parent) {
		this.parent = parent;
		NVCClient.setCurrentShowingScene(this);
	}

	/**
	 * Create contents of start pane.
	 * 
	 * @return javafx.scene.layout.Pane object
	 */
	public Pane getContent() {
		
		Font font = new Font("Arial", 16);
		double columWidth = (JFXUtility.WIDTH / 2) - 10;
		
		BorderPane border = new BorderPane();
		border.setTop(JFXUtility.createTitleHBox("NVC Operator UI"));
		border.setBottom(JFXUtility.createBottomHBox());
		GridPane grid = JFXUtility.createContentPane();
		
		// Label "Server address:"
		final Label labelAddress = new Label("Server address:");
		labelAddress.setFont(font);
		labelAddress.setPrefWidth(columWidth);
		grid.add(labelAddress, 0, 0);					// column 0, row 0
		
		// Text field "xxx.xxx.xxx.xxx"
		textFieldAddress = new TextField();
		textFieldAddress.setText("150.65.227.109");
		textFieldAddress.setPromptText("xxx.xxx.xxx.xxx");
		textFieldAddress.setPrefWidth(columWidth);
		grid.add(textFieldAddress, 1, 0);				// column 1, row 0
		
		// Label "Port"
		final Label labelPort = new Label("Port:");
		labelPort.setFont(font);
		grid.add(labelPort, 0, 1);						// column 0, row 1
		
		// Text field "(port number)"
		textFieldPort = new TextField();
		textFieldPort.setText("30001");
		textFieldPort.setPromptText("30001");
		grid.add(textFieldPort, 1, 1);					// column 1, row 1
		
		// Label "Name:"
		final Label labelName = new Label("Name:");
		labelName.setFont(font);
		grid.add(labelName, 0, 2);						// column 0, row 2
		
		// Text field "(name)"
		textFieldName = new TextField();
		textFieldName.setText("Operator");
		grid.add(textFieldName, 1, 2);					// column 1, row 2
		
		// Label "Title:"
		final Label labelTitle = new Label("Title:");
		labelTitle.setFont(font);
		grid.add(labelTitle, 0, 3);						// column 0, row 3
		
		// Text field "(title)"
		textFieldTitle = new TextField();
		textFieldTitle.setText("Shikida-lab");
		grid.add(textFieldTitle, 1, 3);					// column 1, row 3
		
		// Button "Connect"
		buttonConnect = new Button("Connect");
		buttonConnect.setOnAction(new ButtonConnectHandler());
		grid.add(buttonConnect, 0, 4);					// column 0, row 4
		
		// Label "Status"
		labelStatus = new Label("Status: no connection");
		labelStatus.setFont(font);
		grid.add(labelStatus, 0, 5, 2, 1);				// column 0(2), row 5(1)
		
		// 
		border.setCenter(grid);
		
		return border;
	}
	
	/**
	 * Event handler implementation for "Connect" button.
	 */
	class ButtonConnectHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent e) {
			if (!parent.connected) {
				if (client.connectServer(textFieldAddress.getText(),
						Integer.parseInt(textFieldPort.getText()))) {
					parent.connected = true;
					NVCClient.name = textFieldName.getText();
					NVCClient.title = textFieldTitle.getText();
					
					// Set name
					client.sendMessage("CHANGE " + NVCClient.name);
					
					// Add new discussion
					client.sendMessage("ADDD " + NVCClient.title);
					
					labelStatus.setText("Status: connected");
					buttonConnect.setText("Disconnect");

					// Change to OperationScene
					parent.showOperationScene();
				} else {
					labelStatus.setText("Status: connection failed");
				}				
			} else {
				// Disconnect
				try {
					client.close();
				} catch (IOException e1) {
					// no action
				} finally {
					parent.connected = false;
					buttonConnect.setText("Connect");
				}
			}
		}
	}
}

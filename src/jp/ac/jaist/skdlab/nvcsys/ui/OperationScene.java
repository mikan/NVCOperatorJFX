package jp.ac.jaist.skdlab.nvcsys.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import jp.ac.jaist.skdlab.nvcsys.NVCClient;
import jp.ac.jaist.skdlab.nvcsys.NVCOperation;

/**
 * Create and manage "Operator" scene.
 * 
 * @author Yutaka Kato
 *
 */
public class OperationScene {
	
	private NVCClient client = NVCClient.getInstance();
	private NVCOperatorJFX parent = null;
	private Button buttonGestured = null;
	private Button buttonHandGestured = null;
	private Button buttonDownAll = null;
	private Button buttonDisconnect = null;
	private Label labelStatus = null;
	private TilePane tileMembers = null;
	private ToggleGroup toggleGroup = null;
	private List<ToggleButton> buttonMemberList = null;
	
	private double columWidth = (JFXUtility.WIDTH / 2) - 10;
		
	public OperationScene(NVCOperatorJFX parent) {
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
		
		BorderPane border = new BorderPane();
		border.setTop(JFXUtility.createTitleHBox("Operation menu"));
		border.setBottom(JFXUtility.createBottomHBox());
		
		GridPane grid = JFXUtility.createContentPane();
		
		// Label "Status"
		labelStatus = new Label("Status: connected");
		labelStatus.setFont(font);
		grid.add(labelStatus, 0, 0, 2, 1);				// column 0(2), row 0(1)
		
		// TilePane
		tileMembers = new TilePane();
//		tileMembers.setPrefColumns(1);
		tileMembers.setPrefWidth(columWidth);
		tileMembers.setPrefHeight(130);
//		tileMembers.setHgap(10);
		tileMembers.setVgap(10);
		grid.add(tileMembers, 0, 1, 1, 3);				// column 0(1), row 1(2)
		
		// ToggleGroup
		toggleGroup = new ToggleGroup();
		toggleGroup.selectedToggleProperty().addListener(
				new MemberToggleChangeListener());

		// ToggleButton "Remote"
		ToggleButton tb0 = new ToggleButton(NVCOperation.REMOTE_NAME);
		tb0.setToggleGroup(toggleGroup);
		tb0.setStyle("-fx-base: salmon");
		tb0.setPrefWidth(columWidth - 20);
		tb0.setSelected(false);
		buttonMemberList = new ArrayList<ToggleButton>();
		buttonMemberList.add(tb0);
		tileMembers.getChildren().add(tb0);
		
		// Button "Hand gestured"
		buttonHandGestured = new Button("Hand gestured");
		buttonHandGestured.setOnAction(new ButtonHandGesturedHandler());
		buttonHandGestured.setPrefWidth(columWidth);
		grid.add(buttonHandGestured, 1, 1);				// column 1, row 1
		
		// Button "Gestured"
		buttonGestured = new Button("Gestured");
		buttonGestured.setOnAction(new ButtonGesturedHandler());
		buttonGestured.setPrefWidth(columWidth);
		grid.add(buttonGestured, 1, 2);					// column 1, row 2
		
		// Button "Down all"
		buttonDownAll = new Button("Down all");
		buttonDownAll.setOnAction(new ButtonDownAllHandler());
		grid.add(buttonDownAll, 1, 4);					// column 1, row 4
		
		// Button "Disconnect"
		buttonDisconnect = new Button("End discussion");
		buttonDisconnect.setOnAction(new ButtonDisconnectHandler());
		grid.add(buttonDisconnect, 0, 4);				// column 0, row 4
		
		// 
		border.setCenter(grid);
		return border;
	}
	
	/**
	 * Update status label.
	 * 
	 * @param status Status string
	 */
	public void setStatus(String status) {
		if (labelStatus != null) {
			labelStatus.setText("Status: " + status);			
		}
	}
	
	/**
	 * Set member list to ToggleButton objects.
	 * 
	 * @param memberList Current discussion member list
	 */
	public synchronized void setMembers(List<String> memberList) {
		
		if (tileMembers == null) {
			return;
		}
		
		// PreProcess: Clear local users
		int numberOfItems = tileMembers.getChildren().size();
		if (numberOfItems == 0) {
			return;
		} else {
			// Remove
			while (tileMembers.getChildren().size() > 1) {
				tileMembers.getChildren().remove(1);
			}
		}
		
		// Add all
		for (String s : memberList) {
			ToggleButton tb = new ToggleButton(s);
			tb.setToggleGroup(toggleGroup);
			tb.setStyle("-fx-base: lightgreen");
			tb.setPrefWidth(columWidth - 20);
			buttonMemberList.add(tb);
			tileMembers.getChildren().add(tb);
		}
	}
	
	public synchronized void enterMember(String name) {
		
		if (tileMembers == null) {
			return;
		}
		
		ToggleButton tb = new ToggleButton(name);
		tb.setToggleGroup(toggleGroup);
		tb.setStyle("-fx-base: lightgreen");
		tb.setPrefWidth(columWidth - 20);
		buttonMemberList.add(tb);
		tileMembers.getChildren().add(tb);
	}
	
	public synchronized void leaveMember(String name) {
		
		if (tileMembers == null) {
			return;
		}
		
		for (int i = 0; i < buttonMemberList.size(); i++) {
			ToggleButton tb = buttonMemberList.get(i);
			if (tb.getText().equals(name)) {
				buttonMemberList.remove(tb);
				return;
			}
		}
	}
	
	/**
	 * Event handler implementation for "Connect" button.
	 */
	class ButtonDisconnectHandler implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent e) {
			try {
				client.close();
				parent.connected = false;
			} catch (IOException e1) {
				
			} finally {
				// Back to StartScene when connection closed
				parent.showStartScene();
			}
		}
	}
	
	/**
	 * Event handler implementation for "Down all manually" button.
	 */
	class ButtonDownAllHandler implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent e) {
			NVCOperation.getInstance().turnOffAllManually();
		}
	}
	
	/**
	 * Event handler implementation for "Gestured" button.
	 */
	class ButtonGesturedHandler implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent e) {
			NVCOperation.getInstance().gestured();
		}
	}
	
	/**
	 * Event handler implementation for "Hand gestured" button.
	 */
	class ButtonHandGesturedHandler implements EventHandler<ActionEvent> {
		@Override public void handle(ActionEvent e) {
			NVCOperation.getInstance().handGestured();
		}
	}
	
	/**
	 * Change listener implementation for toggle buttons
	 */
	class MemberToggleChangeListener implements ChangeListener<Toggle> {
		@Override
		public void changed(ObservableValue<? extends Toggle> ov,
				Toggle toggle1, Toggle toggle2) {
			if (toggle2 == null) {
				// Button released
			} else {
				if (toggle2 instanceof ToggleButton) {
					NVCOperation.getInstance().setCurrentTalkingMember(
							((ToggleButton) toggle2).getText());					
				}
			}
		}
	}
}

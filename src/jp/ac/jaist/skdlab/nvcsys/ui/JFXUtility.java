package jp.ac.jaist.skdlab.nvcsys.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class JFXUtility {
	
	protected static final int WIDTH = 300;
	protected static final int HEIGHT = 300;
	private static final int HEIGHT_TOP = 50;
	private static final int HEIGHT_CENTER = 205;
	private static final int HEIGHT_BOTTOM = 45;
	
	protected static GridPane createContentPane() {
		
		GridPane grid = new GridPane();
		grid.setPrefHeight(HEIGHT_CENTER);
		grid.setPrefWidth(WIDTH);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
//		grid.setHgap(25);
		
		return grid;
	}
	
	/**
	 * Create Title pane (Top of window)
	 * 
	 * @param title Title string
	 * @return Created HBox object
	 */
	protected static HBox createTitleHBox(String title) {
		
		// Top label
		final Label labelAppTitle = new Label(title);
		labelAppTitle.setFont(new Font("Arial", 30));
		labelAppTitle.setStyle("-fx-text-fill: white;");
		HBox box = new HBox();
		box.setStyle("-fx-background-color: #336699;");
		box.getChildren().add(labelAppTitle);
		box.setPadding(new Insets(10, 10, 10, 10));
		box.setPrefHeight(HEIGHT_TOP);
		box.setPrefWidth(WIDTH);
		return box;
	}
	
	/**
	 * Create credits pane (Bottom of window)
	 * 
	 * @return Created HBox object
	 */
	protected static HBox createBottomHBox() {
		
		String br = System.getProperty("line.separator");
		
		
		// Top label
		final Label labelAppTitle = new Label(
				"NVCOperatorJFX Version " + NVCOperatorJFX.VERSION + br +
				"2011 JAIST Shilida Lab.");
		labelAppTitle.setFont(new Font("Arial", 10));

		HBox box = new HBox();
		box.setStyle("-fx-background-color: silver;");
		box.getChildren().add(labelAppTitle);
		box.setPadding(new Insets(10, 10, 10, 10));
		box.setPrefHeight(HEIGHT_BOTTOM);
		box.setPrefWidth(WIDTH);
		return box;
	}
}

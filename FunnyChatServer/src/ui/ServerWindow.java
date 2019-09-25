package ui;

import Background.ChatServer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerWindow extends Application implements EventHandler<ActionEvent> {
	
	Button btn_start;
	TextArea txtDisplay;
	ChatServer chatServer = new ChatServer();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane borderPane = new BorderPane();
		borderPane.setPrefSize(600, 800);
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(2, 0, 0, 0));
		borderPane.setCenter(txtDisplay);
		
		btn_start = new Button();
		btn_start.setText("Start");
		btn_start.setPrefHeight(30);
		btn_start.setMaxWidth(Double.MAX_VALUE);
		btn_start.setOnAction(e->{
			if (btn_start.getText().equals("Start")) {
				chatServer.startServer();
			} else if (btn_start.getText().equals("stop")) {
				chatServer.stopServer();
			}
		});
		borderPane.setTop(btn_start);
		
		
		Scene scene = new Scene(borderPane);
		primaryStage.setTitle("ServerWindow");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(event->chatServer.stopServer());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	void displayText(String text) {
		txtDisplay.appendText(text + '\n');
	}

}

package Background;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.google.gson.*;

import Background.ChatServer.Client;

public class ChatServer extends Application {

	String Room;
	ExecutorService executorService;
	ServerSocketChannel serverSocketChannel;
	List<Client> connections = new Vector<Client>();
	List<String> userNames = new Vector<String>();
	List<String> roomList = new Vector<String>();
	
	public void startServer() {
		executorService = Executors.newFixedThreadPool(8);
		
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(1000));
		} catch (Exception e) {
			if (serverSocketChannel.isOpen()) {
				System.out.println("already using port number on another prosess");
				stopServer();
			}
			return;
		}		
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				Platform.runLater(()->{
					displayText("[서버시작]");
					btnStartStop.setText("stop");
				});
				while (true) {
					try {
						SocketChannel socketChannel = serverSocketChannel.accept();
						ByteBuffer byteBuffer = ByteBuffer.allocate(100);
						socketChannel.read(byteBuffer);
						byteBuffer.flip();
						Charset charset = Charset.forName("UTF-8");
						
						String message = charset.decode(byteBuffer).toString();
						JsonParser par = new JsonParser();
						JsonObject obj = (JsonObject) par.parse(message);
						String JSONmessage = String.valueOf(obj).replace(',', ' ').replace('"', ' ');
						String name = obj.get("name").getAsString();

						Platform.runLater(()->displayText(JSONmessage));
						connections.add(new Client(socketChannel, name));
						userNames.add(name);
						
						Platform.runLater(()->displayText("[연결 갯 수: " + connections.size() + "]")); 
					} catch (Exception e) {
						if (serverSocketChannel.isOpen()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
		executorService.submit(runnable);
	}
	
	
	public void stopServer() {
		try {
			Iterator<Client> iterator = connections.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socketChannel.close();
				iterator.remove();
			}
			if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
				serverSocketChannel.close();
			}
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			Platform.runLater(()->{
				displayText("[서버종료]");
				btnStartStop.setText("start");
			});
		} catch (Exception e) {
			System.out.println("서버 종료 오류");
		}
	}
	
	
	class Client {
		
		SocketChannel socketChannel;
		private String name;
		
		Client(SocketChannel socketChannel, String name) {
			this.socketChannel = socketChannel;
			this.name = name;
			receive();
		}
				
		String getUserName() {
			return name;
		}
		
		void receive() {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							ByteBuffer byteBuffer = ByteBuffer.allocate(100);
							
							int readByteCount = socketChannel.read(byteBuffer);
							
							if (readByteCount == -1) {
								throw new IOException();
							}
							
							byteBuffer.flip();
							Charset charset = Charset.forName("UTF-8");
							String message = charset.decode(byteBuffer).toString();
							JsonParser par = new JsonParser();
							JsonObject obj = (JsonObject) par.parse(message);
							String JSONmessage = String.valueOf(obj).replace(',', ' ').replace('"', ' ');
							String name = obj.get("name").getAsString();
							Platform.runLater(()->displayText(JSONmessage));
							
							for (Client client : connections) {
								if (client.getUserName().equals(name)) continue;
								client.send(message);
							}					
							
						} catch (Exception e) {
							try {
								connections.remove(Client.this);
								String message = "[클라이언트 통신 끊김: " + socketChannel.getRemoteAddress() + ": "
												 + Thread.currentThread().getName() + "]";
								Platform.runLater(()->displayText(message));
								socketChannel.close();
							} catch(IOException e2)  {}
							
							break;
						}
					}
				}
			};
			executorService.submit(runnable);
		}
		
		void send(String data) {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						Charset charset = Charset.forName("UTF-8");
						ByteBuffer byteBuffer = charset.encode(data);
						socketChannel.write(byteBuffer);
					} catch(Exception e) {
						try {
							String message = "[클라이언트 통신 끊김: " + socketChannel.getRemoteAddress() + ": "
											 + Thread.currentThread().getName() + "]";
							Platform.runLater(()->displayText(message));
							connections.remove(Client.this);
							socketChannel.close();
						} catch(IOException e2) {}
					}
				}
			};
			executorService.submit(runnable);
		}
	}
	
	
	TextArea txtDisplay;
	Button btnStartStop;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(600, 600);
		
		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0, 0, 0, 2));
		root.setCenter(txtDisplay);
		
		btnStartStop = new Button("start");
		btnStartStop.setPrefHeight(30);
		btnStartStop.setMaxWidth(Double.MAX_VALUE);
		btnStartStop.setOnAction(e->{
			if(btnStartStop.getText().equals("start")) {
				startServer();
			} else {
				stopServer();
			}
		});
		root.setTop(btnStartStop);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server");
		primaryStage.setOnCloseRequest(event->stopServer());
		primaryStage.show();
	}
	
	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
	
	

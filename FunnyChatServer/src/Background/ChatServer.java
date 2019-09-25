package Background;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

	private ExecutorService executorService;
	private ServerSocketChannel serverSocketChannel;
	private Set<UserThread> userThreads = new HashSet<>();
	private Set<UserThread> userNames = new HashSet<>();
	
	public void startServer() {
		ExecutorService executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()
			);
		
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(1000));
		} catch (Exception e) {
			if (serverSocketChannel.isOpen()) 
			{
				System.out.println("already using this port on another program");
				stopServer();
			}
			return;
		}
	}
	
	public void stopServer() {
		
	}
	
	public static void main(String[] args) {
	

	}

}

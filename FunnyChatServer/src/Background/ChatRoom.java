package Background;

import java.util.*;

import Background.ChatServer.Client;

public class ChatRoom {

	List<Client> userList = new Vector<Client>();
	Client Owner;
	String roomName;
	
	public ChatRoom(Client client) {
		userList = new ArrayList<Client>();
		userList.add(client);
		this.Owner = client;
	}
	
	public ChatRoom(List<Client> _userList) {
		this.userList = _userList;
		this.Owner = userList.get(0);
	}
	
	public void EnterRoom(Client user) {
		userList.add(user);
	}
	
	public void ExitRoom(Client user) {
		userList.remove(user);
		
	}
	
}

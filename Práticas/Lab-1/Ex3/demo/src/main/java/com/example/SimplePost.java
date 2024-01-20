package com.example;

import java.util.Set;
import java.util.Map;
import java.util.List;

import redis.clients.jedis.Jedis;
 
public class SimplePost {
 
	private Jedis jedis;
	public static String USERS = "users"; // Key set for users' name
    public static String USERSLIST = "users List"; // List for users' name
	public static String HASHMAP_USERS = "Hash Map Users"; // Hash Map for users' name

    // Set method's
	public SimplePost() {
		this.jedis = new Jedis();
	}
 
	public void saveUser(String username) {
		jedis.sadd(USERS, username);
	}
	public Set<String> getUser() {
		return jedis.smembers(USERS);
	}
	
	public Set<String> getAllKeys() {
		return jedis.keys("*");
	}

    // List method's
    public void saveUserList(String username) {
		jedis.lpush(USERSLIST, username);
	}
	public List<String> getUserList() {
		return jedis.lrange(USERSLIST, 0, -1);
	}
 
    // HashMap method's
    public void saveUserHashMap(String animal, String telemovel){
        jedis.hset(HASHMAP_USERS, animal, telemovel);
    }

	public Map<String, String> getUserMap(){
		return jedis.hgetAll(HASHMAP_USERS);
	}

	public Set<String> getUserKeysMap(){
		return jedis.hkeys(HASHMAP_USERS);
	}
 	
	public static void main(String[] args) {
		SimplePost board = new SimplePost();
		// set some users
		String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        
        String[] usersList = { "Marcolino", "Pedrocas", "Mariazinha", "Luizão" };

		String[] usersHashMap = { "cobra", "935687241", "leao", "234567456", "porca", "917125332", "vaca", "925366871"};

		for (String user: users) 
			board.saveUser(user);
		board.getAllKeys().stream().forEach(System.out::println);
		board.getUser().stream().forEach(System.out::println);
		
        // List Methods
        for(String u: usersList)
            board.saveUserList(u);
        board.getUserList().stream().forEach(System.out::println);


		// Hash Map Methods
		for(int i=0 ; i < usersHashMap.length; i+=2){
			board.saveUserHashMap(usersHashMap[i], usersHashMap[i+1]);
		}

		for (Map.Entry<String, String> entry : board.getUserMap().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }	
	}

}




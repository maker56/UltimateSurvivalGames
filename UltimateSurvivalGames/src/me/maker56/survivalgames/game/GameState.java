package me.maker56.survivalgames.game;

public enum GameState {
	
	WAITING,
	
	VOTING,
	
	COOLDOWN,
	
	INGAME,
	
	DEATHMATCH,
	
	RESET;
	
	public boolean isIngame() {
		return this == INGAME || this == DEATHMATCH;
	}

}

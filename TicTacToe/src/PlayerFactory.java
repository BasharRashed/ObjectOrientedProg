public class PlayerFactory {
	public PlayerFactory() {

	}
	public Player buildPlayer(String type) {
		switch (type.toLowerCase()) {
			case "whatever":
				return new WhateverPlayer();
			case "human":
				return new HumanPlayer();
			case "clever":
				return new CleverPlayer();
			case "genius":
				return new GeniusPlayer();
			default:
				return null;
		}
	}
}

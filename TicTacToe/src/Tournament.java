/**
 * Tournament class that initiates a tournament between two players.
 * playing a number of rounds then showing each player's wins,
 * and the ties.
 */
public class Tournament {
	private int rounds;
	private Renderer renderer;
	private Player player1;
	private Player player2;
	private RendererFactory rendererFactory;

	/**
	 * Constructor to initialize the tournament with the necessary settings.
	 */
	public Tournament(int rounds, Renderer renderer, Player player1, Player player2) {
		this.rounds = rounds;
		this.renderer = renderer;
		this.player1 = player1;
		this.player2 = player2;
		this.rendererFactory = new RendererFactory();
	}

	/**
	 * Method to run the tournament and simulate multiple rounds.
	 */
	public void playTournament(int size, int winStreak, String player1Name, String player2Name) {
		int player1Wins = 0;
		int player2Wins = 0;
		int ties = 0;

		for (int i = 0; i < this.rounds; i++) {
			// Alternate roles after each game
			Player currentX;
			Player currentO;
			if (i % 2 == 0) {
				currentX = player1;
				currentO = player2;
			}
			else{
				currentX = player2;
				currentO = player1;
			}

			Game game = new Game(currentX, currentO, size, winStreak, renderer);
			Mark result = game.run();

			if (result == Mark.X) {
				if (currentX == player1) {
					player1Wins++;
				}
				else {
					player2Wins++;
				}
			}
			else if (result == Mark.O) {
				if (currentO == player1){
					player1Wins++;
				}
				else {
					player2Wins++;
				}
			}
			else {
				ties++;
			}
		}
		System.out.println("######### Results #########");
		System.out.printf("Player 1, %s won: %d rounds\n", player1Name, player1Wins);
		System.out.printf("Player 2, %s won: %d rounds\n",player2Name , player2Wins);
		System.out.println("Ties: " + ties);
	}


	/**
	 * Main method to start the tournament using command-line arguments.
	 */
	public static void main(String[] args) {
		int rounds = Integer.parseInt(args[0]);
		int size = Integer.parseInt(args[1]);
		int winStreak = Integer.parseInt(args[2]);
		String rendererName = args[3];
		String player1Name = args[4];
		String player2Name = args[5];

		PlayerFactory playerfactory = new PlayerFactory();
		Player player1 = playerfactory.buildPlayer(player1Name);
		Player player2 = playerfactory.buildPlayer(player2Name);

		RendererFactory rendererfactory = new RendererFactory();
		Renderer renderer = rendererfactory.buildRenderer(rendererName, size);

		Tournament tournament = new Tournament(rounds,renderer, player1, player2);
		tournament.playTournament(size,winStreak,player1Name,player2Name);
	}
}

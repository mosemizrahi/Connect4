import java.util.Random;

/**
 * Instances of this class:
 *	 Keep track of the game state
 *	 Serve as the AI player of a game
 *						  
 * @author mosemizrahi
 */
public class Connect4AI {
	
	public static final int SIMULATION_SIZE = 100;
	
	/** The search depth of the AI */
	public static final int SEARCH_DEPTH = 6;
	
	/** The combo required to win */
	public static final int COMBO_REQ = 4;
	
	/** The height of the gameBoard */
	public static final int HEIGHT = 6;

	/** The width of the gameBoard */
	public static final int WIDTH = 7;
	
	public int[][] getBoard() {
		return gameBoard;
	}
	
	 /** 0: No winner yet, 1: AI, 2: human, 3: draw */
	private int winner = 0;
	
	/** Keeps the game state, in [column][row] format.
	 * 0: empty slots, 1: tiles belonging to the AI, 2: tiles belonging to the human.
	 */
	private int[][] gameBoard;
	
	/** Keeps the number of tiles in each column. The maximum is HEIGHT */
	private int[] stackBoard;
	
	/** Keeps the number of columns where tiles can be dropped. */
	private int openColumns = WIDTH;
	
	/** This will provide us with random integers for the move-order randomization. */
	private Random randomizer = new Random();

	/**
	 * The constructor for Connect4AI. Creates a blank gameBoard and a blank stackBoard.
	 */
	public Connect4AI() {
		this.gameBoard = new int[WIDTH][HEIGHT];
		this.stackBoard = new int[WIDTH];
	}
	
	/**
	 * Drops a tile on a column, assuming it has space. 
	 * @param xPos is the column where a tile is being dropped.
	 * @param player: 1 if AI, 2 if human
	 */
	private void setTile(final int xPos, final int player) {
		gameBoard[xPos][stackBoard[xPos]] = player;
		++stackBoard[xPos];
		if (stackBoard[xPos] == HEIGHT) {
			--openColumns;
		}
	}
	
	/**
	 * Tries dropping a tile on a column. Will fail for already full columns, invalid columns and if the winner is known.
	 * Does victory/draw checking after tile setting and updates the winner accordingly.
	 * @param xPos is the column where a tile is being dropped.
	 * @param player: 1 if AI, 2 if human
	 * @return the yPos of the tile if it was dropped, -1 otherwise.
	 */
	public int setTileSafe(final int xPos, final int player) {
		if (winner != 0) {
			return -1;
		}
		if (xPos < 0 || xPos >= WIDTH) {
			return -1;
		}
		if (stackBoard[xPos] == HEIGHT) {
			return -1;
		}
		setTile(xPos, player);
		if (openColumns == 0) {
			winner = 3;
		} else if(victoryCheck(xPos)) {
			winner = player;
		}
		return stackBoard[xPos] - 1;
	}
	
	/**
	 * Removes a tile from a column
	 * @param xPos is the column where a tile is being removed from.
	 */
	private void removeTile(final int xPos) {
		if (stackBoard[xPos] == HEIGHT) {
			++openColumns;
		}
		--stackBoard[xPos];
		gameBoard[xPos][stackBoard[xPos]] = 0;
	}
	
	/**
	 * @return 0: no winner, 1: AI winner, 2: human winner, 3: draw
	 */
	public int getWinner() {
		return winner;
	}
	
	/**
	 * @param xPos is the x-coordinate of the dropped tile.
	 * @param yPos is the y-coordinate of the dropped tile.
	 * @return whether the player who has just dropped a tile on column xPos has won.
	 */
	private boolean victoryCheck(final int xPos) {
		int yPos = stackBoard[xPos] - 1;
		int player = gameBoard[xPos][yPos];
		boolean victory = false;
		
		//Checking for a vertical victory (tile just dropped is the top tile)
		int combo = 0;
		if (yPos >= COMBO_REQ - 1) {
			for (int y = yPos - 1; y > yPos - COMBO_REQ; --y) {
				if (gameBoard[xPos][y] == player) {
					++combo;
				} else {
					break;
				}
			}
			if (combo == COMBO_REQ - 1) {
				return true;
			}
		}

		//Checking for a horizontal victory
		combo = 0;
		int xStart = xPos - COMBO_REQ + 1; //inclusive
		int xEnd = xPos + COMBO_REQ - 1; //inclusive
		for (int x = xStart; x <= xEnd; ++x) {
			if (x < 0) {
				continue;
			}
			if (x >= WIDTH) {
				break;
			}
			if (gameBoard[x][yPos] == player) {
				++combo;
				if (combo == COMBO_REQ) {
					victory = true;
					break;
				}
			} else {
				combo = 0;
			}
		}
		if (victory) {
			return true;
		}
		
		//Checking for a / diagonal victory
		combo = 0;
		int yStart = yPos -COMBO_REQ + 1; //inclusive
		int yEnd = yPos + COMBO_REQ - 1; //inclusive
		for (int x = xStart, y = yStart; x <= xEnd && y <= yEnd; ++x, ++y) {
			if (x < 0 || y < 0) {
				continue;
			}
			if (x >= WIDTH || y >= HEIGHT) {
				break;
			}
			if (gameBoard[x][y] == player) {
				++combo;
				if (combo == COMBO_REQ) {
					victory = true;
					break;
				}
			} else {
				combo = 0;
			}
		}
		if (victory) {
			return true;
		}
		//Checking for a \ diagonal victory
		combo = 0;
		for (int x = xEnd, y = yStart; x >= xStart && y <= yEnd; --x, ++y) {
			if (x >= WIDTH || y < 0) {
				continue;
			}
			if (x < 0 || y >= HEIGHT) {
				break;
			}
			if (gameBoard[x][y] == player) {
				++combo;
				if (combo == COMBO_REQ) {
					victory = true;
					break;
				}
			} else {
				combo = 0;
			}
		}
		//If still no victory, then we can return false
		return victory;
	}
	
	/**
	 * @param positions is the array we are shuffling to randomly order moves.
	 */
	private void shuffle(final int[] positions) {
		for (int maxIndex = positions.length - 1; maxIndex > 0; --maxIndex) {
			int randIndex = randomizer.nextInt(maxIndex + 1); //excludes maxIndex
			int temp = positions[randIndex];
			positions[randIndex] = positions[maxIndex];
			positions[maxIndex] = temp;
		}
	}
	
	/**
	 * Creates an array of the possible columns where tiles can be dropped, and shuffles it.
	 * @return the created array.
	 */
	private int[] positionsGenerator() {
		int[] positions = new int[openColumns];
		for (int x = 0, positionIndex = 0; positionIndex < openColumns; ++x) {
			if (stackBoard[x] != HEIGHT) {
				positions[positionIndex++] = x;
			}
		}
		shuffle(positions);
		return positions;
	}
		
	/**
	 * This is the function that is called when the AI is supposed to play after the human.
	 * @param xPos is the column on which the human drops a tile.
	 * @return an array of three integers: [flag, humanX, humanY decisionX, decisionY].
	 * flag = -1: error, 0: nothing special, 1: AI wins, 2: human wins, 3: draw
	 * humanX = the x-coordinate where human decided to play, -1 if error - Didn't play
	 * humanY = the y-coordinate where human decided to play, -1 if error - Didn't play
	 * decisionX = the x-coordinate of the tile the AI decided to play, -1 if error - Didn't play
	 * decisionY = the y-coordinate of the tile the AI decided to play. -1 if error - Didn't play
	 */
	public int[] play(final int xPos) {
		if (setTileSafe(xPos, 2) == -1) { //Failed to drop human's tile. That's an error.
			return new int[] {-1, -1, -1, -1, -1};
		}
		int yPos = stackBoard[xPos] - 1;
		if (winner != 0) {
			return new int[] {winner, xPos, yPos, -1, -1};
		}
		int[] AIresult = play();
		return new int[] {AIresult[0], xPos, yPos, AIresult[1], AIresult[2]};
	}
	
	/**
	 * This is the function that is called when the AI is supposed to play now.
	 * @return an array of three integers: [flag, decisionX, decisionY].
	 * flag = -1: error, 0: nothing special, 1: AI wins, 2: human wins, 3: draw
	 * decisionX = the x-coordinate of the tile the AI decided to play, -1 if error - Didn't play
	 * decisionX = the y-coordinate of the tile the AI decided to play. -1 if error - Didn't play
	 */
	public int[] play() {
		
		if (winner != 0) { //The winner is already known. That's an error.
			return  new int[] {-1, -1, -1};
		}
		
		int[] positions = positionsGenerator();
		
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		int maxIndex = 0;
		for (int i = 0; i < positions.length; ++i) {
			//The AI is dropping a tile on column position[0]. It will see calculate how good that move is.
			setTile(positions[i], 1);
			if (victoryCheck(positions[i])) { //AI won by playing, we can return now.
				winner = 1;
				return new int[] {1, positions[i], stackBoard[positions[i]] - 1};
			}
			int temp = min(alpha, beta, SEARCH_DEPTH - 1);
			if (temp > alpha) {
				alpha = temp;
				maxIndex = i;
			}
			//The score of that move is calculated, the dropped tile can be removed.
			removeTile(positions[i]);
		}
		//System.out.println(alpha);
		//We now know the best move!
		int best = positions[maxIndex];
		setTile(best, 1);
		
		if (victoryCheck(best)) {
			//With the last tile drop, the AI has won!
			winner = 1;
			return new int[] {1, best, stackBoard[best] - 1};
		} else {
			if (openColumns == 0) {
				//With the last tile drop, the board is filled. It's a draw.
				winner = 3;
				return new int[] {3, best, stackBoard[best] - 1};
			} else {
				return new int[] {0, best, stackBoard[best] - 1};
			}
		}
	}
	
	/**
	 * min part of the minimax algorithm.
	 * @param alpha is used for alpha-pruning.
	 * @param beta is used for beta-pruning.
	 * @param depth is the depth of minimax tree after this min node.
	 * @return beta.
	 */
	private int min(final int alpha, int beta, final int depth) {
		if (openColumns == 0) {
			//nowhere to play, we have a draw. Draws have neutral scores of 0.
			return 0;
		}
		if (depth == 0) {
			//This is a min node, so the human is playing.
			return evaluator(2);
		}
		
		int[] positions = positionsGenerator();
		
		for (int i = 0; i < positions.length; ++i) {
			if (alpha >= beta) {
				return beta;
			}
			setTile(positions[i], 2);
			if (victoryCheck(positions[i])) { //Human won, we can return the minimum score.
				removeTile(positions[i]);
				//a few extra points off for losing quickly.
				return - SIMULATION_SIZE - depth;
			}
			int temp = max(alpha, beta, depth - 1);
			if (temp < beta) {
				beta = temp;
			}
			removeTile(positions[i]);
		}
		return beta;
	}
	
	/**
	 * max part of the minimax algorithm.
	 * @param alpha is used for alpha-pruning.
	 * @param beta is used for beta-pruning.
	 * @param depth is the depth of minimax tree after this max node.
	 * @return alpha
	 */
	private int max(int alpha, final int beta, final int depth) {
		if (openColumns == 0) {
			//nowhere to play, we have a draw. Draws have neutral scores of 0.
			return 0;
		}
		if (depth == 0) {
			//This is a max node, so the AI is playing.
			return evaluator(1);
		}
		
		int[] positions = positionsGenerator();
		
		for (int i = 0; i < positions.length; ++i) {
			if (alpha >= beta) {
				return alpha;
			}
			setTile(positions[i], 1);
			if (victoryCheck(positions[i])) { //AI won, we can return the maximum score.
				removeTile(positions[i]);
				return SIMULATION_SIZE + depth;
			}
			int temp = min(alpha, beta, depth - 1);
			if (temp > alpha) {
				alpha = temp;
			}
			removeTile(positions[i]);
		}
		return alpha;
	}
		

	/**
	 * Calculates how good the board is from the perspective of the AI by running random simulations.
	 * Returns a value between [-SIMULATION_SIZE, SIMULATION_SIZE].
	 * @player is the person who played last. 1: AI, 2: human
	 * @return the score of the board. Better for AI => higher scores.
	 */
	private int evaluator(final int player) {
		int score = 0;
		for (int i = 0; i < SIMULATION_SIZE; ++i) {
			score += simulate(player);
		}
		return score;
	}
	
	/**
	 * Runs a Monte Carlo simulation of the game until it ends.
	 * @param player is the player who is going to randomly drop a tile in the simulation.
	 * @return -1: human won, 0: draw, 1: AI won
	 */
	private int simulate(final int player) {
		int randX = randomizer.nextInt(WIDTH);
		while (stackBoard[randX] == HEIGHT) {
			randX = randomizer.nextInt(WIDTH);
		}
		setTile(randX, player);
		if (openColumns == 0) {
			removeTile(randX);
			return 0; //0 for draw
		} else if (victoryCheck(randX)) {
			removeTile(randX);
			return 3 - 2 * player; //1 for AI victory, -1 for human victory
		}
		int result = simulate(3 - player);
		removeTile(randX);
		return result;
	}
}

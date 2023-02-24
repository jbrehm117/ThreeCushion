package hw2;

import api.PlayerPosition;
import api.BallType;
import static api.PlayerPosition.*;
import static api.BallType.*;

/**
 * Class that models the game of three-cushion billiards.
 * 
 * @author Joseph_Brehm
 */
public class ThreeCushion {
	private PlayerPosition lagWinner;
	private PlayerPosition inningPlayer;

	private BallType lagWinnerCueBall;
	private BallType player2CueBall;
	private BallType currentCueBall;

	private int pointsToWin;
	private int inningCount;
	private int cushionsHit;
	private int lagWinnerPoints;
	private int player2Points;
	private int ballsHit;
	private int ballsHitPrevious;

	private boolean gameStart;
	private boolean inningStarted;
	private boolean shotStarted;
	private boolean cueStickStrike;
	private boolean redHit;
	private boolean yellowHit;
	private boolean whiteHit;
	private boolean shotValid;
	private boolean isBreakShot;
	private boolean missedShot;
	private boolean isBankShot;
	private boolean foulCalled;
	private boolean ballStopMotion;

	/**
	 * Creates a new game of three-cushion billiards with a given lag winner and the
	 * predetermined number of points required to win the game. The inning count
	 * starts at 1.
	 * 
	 * @param lagWinner:   Either player A or B
	 * @param pointsToWin: The number of points a player needs to reach for the game
	 *                     to end
	 */
	public ThreeCushion(PlayerPosition lagWinner, int pointsToWin) {
		this.lagWinner = lagWinner;
		this.pointsToWin = pointsToWin;

		inningPlayer = null;
		lagWinnerCueBall = null;
		player2CueBall = null;
		currentCueBall = null;
		
		inningStarted = false;
		shotStarted = false;
		shotValid = true;
		gameStart = false;
		isBreakShot = true;
		redHit = false;
		yellowHit = false;
		whiteHit = false;
		missedShot = false;
		isBankShot = false;
		foulCalled = false;
		ballStopMotion = true;
		cueStickStrike = false;

		inningCount = 1;
		lagWinnerPoints = 0;
		player2Points = 0;
		cushionsHit = 0;
		ballsHit = 0;
		ballsHitPrevious = 0;
		
	}

	/**
	 * Sets whether or not the player who won the lag takes the first shot. Method
	 * can only be called once because the lagWinner can only decide to break or not
	 * once.
	 * 
	 * @param selfBreak: True or false value that decides if the lagWinner breaks or
	 *                   not
	 * @param cueBall:   Sets the cueBall for the lagWinner and sets the remaining
	 *                   cue ball for player 2
	 */
	public void lagWinnerChooses(boolean selfBreak, BallType cueBall) {
		gameStart = true;
		// Doesn't allow method to be called more than once
		boolean alreadyExecuted = false;
		if (!alreadyExecuted) {
			alreadyExecuted = true;
			lagWinnerCueBall = cueBall;
			// Sets each players cueBall
			if (lagWinnerCueBall == BallType.WHITE) {
				player2CueBall = BallType.YELLOW;
			} else {
				player2CueBall = BallType.WHITE;
			}
			
			// Decides whether or not the lagWinner has chosen to break
			if (selfBreak == true) {
				inningPlayer = lagWinner;
				currentCueBall = lagWinnerCueBall;
			}

			// Makes the inningPlayer the other player if lagWinner has chosen not to break
			if (inningPlayer != lagWinner) { 
				if (lagWinner == PlayerPosition.PLAYER_A) {
					inningPlayer = PlayerPosition.PLAYER_B;
					currentCueBall = player2CueBall;
				} else {
					inningPlayer = PlayerPosition.PLAYER_A;
					currentCueBall = player2CueBall;

				}
			}
		}
	}

	/**
	 * Marks the beginning of a shot if a shot has not already begun. If method is
	 * called after shot has started, the play results in a foul.
	 * 
	 * @param ball: The ball that the cue stick strikes. If it is not the chosen
	 *              cueBall, a foul is called.
	 */
	public void cueStickStrike(BallType ball) {
		if (gameStart) {
			isBankShot = false;
			inningStarted = true;
			// checks if a foul or shot has already been started
			if (!shotStarted && !foulCalled) { 
				shotStarted = true;
				cueStickStrike = true;
				// checks if the ball hit was the currentCueBall
				if (ball != currentCueBall) {
					foul();
				}
			} else {
				foulCalled = false;
				foul();
			}
		}
	}

	/**
	 * Indicates the player's cue ball has struck the given ball. Cannot be called
	 * after the end of a game, and cannot be called before the start of a shot or
	 * stick strike.
	 * 
	 * @param ball: the ball that the cueBall hits
	 */
	public void cueBallStrike(BallType ball) {
		if (cueStickStrike) {
			if (currentCueBall == BallType.WHITE) {
				if (ball == BallType.RED) {
					redHit = true;
					ballsHit += 1;
				} else if ((ball == BallType.YELLOW)) {
					// red ball has to be hit first on a break shot
					if ((isBreakShot == true) && (redHit == false)) {
						foul();
					} else {
						yellowHit = true;
						ballsHit += 1;
					}
				}
			} else if (currentCueBall == BallType.YELLOW) {
				if (ball == BallType.RED) {
					redHit = true;
					ballsHit += 1;
				} else if ((ball == BallType.WHITE)) {
					// red ball has to be hit first on a break shot
					if ((isBreakShot == true) && (redHit == false)) {
						foul();
					} else {
						whiteHit = true;
						ballsHit += 1;
					}
				}
			}
		}
		checkShotValid();
	}

	/**
	 * Indicates the given ball has impacted the given cushion. A cushion impact
	 * cannot happen before the start of a shot or stick strike. Method cannot be
	 * called after the end of a game or before a game starts.
	 */
	public void cueBallImpactCushion() {
		if (cueStickStrike) {
			// red ball has to be hit first on a break shot
			if ((isBreakShot == true) && (redHit == false)) {
				foul();
			} else {
				cushionsHit += 1;
				// checks if current shot is counted as a bank shot if the cueBall is yellow
				if (currentCueBall == BallType.YELLOW) {
					if ((cushionsHit == 3) && (whiteHit == false) && (redHit == false)) {
						isBankShot = true;
					}
					// checks if current shot is counted as a bank shot if the cueBall is white
				} else if (currentCueBall == BallType.WHITE) {
					if ((cushionsHit == 3) && (yellowHit == false) && (redHit == false)) {
						isBankShot = true;
					}
				}
			}

		}
		checkShotValid();
	}

	/**
	 * Indicates that all balls have stopped motion. If the shot was valid and no
	 * foul was committed, the player scores 1 point. Method cannot be called before
	 * the start of a shot.
	 * 
	 * If method is not called before the start of the next shot, the shot results
	 * in a foul.
	 * 
	 * Method cannot be called after the game ends.
	 */
	public void endShot() {
		if ((cueStickStrike == true) && (shotStarted == true)) {
			 // checks if shot has met requirements to score a point
			if ((shotValid == true) && ((redHit && whiteHit) || (redHit && yellowHit) || (yellowHit && whiteHit))
					&& (cushionsHit >= 3)) {
				if (inningPlayer == lagWinner) {
					lagWinnerPoints += 1;
				} else {
					player2Points += 1;
				}
			} else {
				isBankShot = false;
				foul();
			}
		}
		// Ends the game if a player gets the points to win
		if ((lagWinnerPoints == pointsToWin) || (player2Points == pointsToWin)) {
			inningStarted = false;
			gameStart = false;
		}
		shotValid = true;
		shotStarted = false;
		cueStickStrike = false;
		cushionsHit = 0;
		redHit = false;
		whiteHit = false;
		yellowHit = false;
		isBreakShot = false;
		missedShot = false;
		foulCalled = false;
		ballStopMotion = true;

	}

	/**
	 * A foul immediately ends the player's inning, even if the current shot has not
	 * yet ended. When a foul is called, the player does not score a point for the
	 * shot.
	 * 
	 * A foul may also be called before the inning has started. In that case the
	 * player whose turn it was to shot has their inning forfeited and the inning
	 * count is increased by one.
	 * 
	 * A foul cannot be called before or after the game has started, and no foul can
	 * be called if the lag winner has not chosen to break
	 * 
	 * A foul can only be called once during a players turn
	 */
	public void foul() {
		if ((gameStart) && (foulCalled == false)) {
			// Changes the inning and sets values back to default
			shotValid = false;
			inningCount += 1;
			cushionsHit = 0;
			redHit = false;
			whiteHit = false;
			yellowHit = false;
			changeCueBall();
			switchInningPlayer();
			foulCalled = true;
			inningStarted = false;
		}
	}
	
	/**
	 * Method used to change current players cueBall
	 */
	private void changeCueBall() {
		if (currentCueBall == lagWinnerCueBall) {
			currentCueBall = player2CueBall;
		} else {
			currentCueBall = lagWinnerCueBall;
		}
	}

	/**
	 * Method used to change the current inning player
	 */
	private void switchInningPlayer() {
		if (inningPlayer == PlayerPosition.PLAYER_A) {
			inningPlayer = PlayerPosition.PLAYER_B;
		} else {
			inningPlayer = PlayerPosition.PLAYER_A;
		}
	}

	/**
	 * Method used to check if the shot is valid or not
	 * 
	 * All three cushions have to be hit before the last object ball can be hit
	 */
	private void checkShotValid() {
		if ((redHit) && (whiteHit)) {
			if (cushionsHit < 3) {
				shotValid = false;
				isBankShot = false;
			}
		} else if ((yellowHit) && (whiteHit)) {
			if (cushionsHit < 3) {
				shotValid = false;
				isBankShot = false;
			}
		} else if ((redHit) && (yellowHit)) {
			if (cushionsHit < 3) {
				shotValid = false;
				isBankShot = false;
			}
		}
	}

	/**
	 * Gets the number of points scored by Player A.
	 * 
	 * @return the number of points
	 */
	public int getPlayerAScore() {
		if (PlayerPosition.PLAYER_A == lagWinner) {
			return lagWinnerPoints;
		} else {
			return player2Points;
		}
	}

	/**
	 * Gets the number of points scored by Player B.
	 * 
	 * @return the number of points
	 */
	public int getPlayerBScore() {
		if (PlayerPosition.PLAYER_B == lagWinner) {
			return lagWinnerPoints;
		} else {
			return player2Points;
		}
	}

	/**
	 * Gets the inning number. The inning count starts at 1.
	 * 
	 * @return the inning count
	 */
	public int getInning() {
		return inningCount;
	}

	/**
	 * Gets the cue ball of the current player. If this method is called in between
	 * innings, the cue ball should be the for the player of the upcoming inning. If
	 * this method is called before the lag winner has chosen a cue ball, the cue
	 * ball is undefined (this method may return anything).
	 * 
	 * @return the current player's cueBall
	 */
	public BallType getCueBall() {
		return currentCueBall;
	}

	/**
	 * Gets the current player. If this method is called in between innings, the
	 * current player is the player of the upcoming inning. If this method is called
	 * before the lag winner has chosen to break, the current player is undefined
	 * (this method may return anything).
	 * 
	 * @return the current inningPlayer
	 */
	public PlayerPosition getInningPlayer() {
		return inningPlayer;
	}

	/**
	 * Returns true if and only if this is the break shot (i.e., the first shot of the game).
	 * 
	 * @return true if the current shot is the break shot, and false otherwise
	 */
	public boolean isBreakShot() {
		return isBreakShot;
	}

	/**
	 * Returns true if and only if the most recently completed shot was a bank shot.
	 * A bank shot is when the cue ball impacts the cushions at least 3 times and
	 * then strikes both object balls.
	 * 
	 * @return true if shot was a bank shot, false otherwise
	 */
	public boolean isBankShot() {
		return isBankShot;
	}

	/**
	 * Returns true if a shot has been taken (see cueStickStrike()), but not ended (see endShot()).
	 * 
	 * @return true if the shot has been started, false otherwise
	 */
	public boolean isShotStarted() {
		return shotStarted;
	}

	/**
	 * Returns true if the shooting player has taken their first shot of the inning.
	 * The inning starts at the beginning of the shot (i.e., the shot may not have
	 * ended yet).
	 * 
	 * @return true if the inning has started, false otherwise
	 */
	public boolean isInningStarted() {
		return inningStarted;
	}

	/**
	 * Returns true if the game is over (i.e., one of the players has reached the
	 * designated number of points to win).
	 * 
	 * @return true if the game is over, false otherwise
	 */
	public boolean isGameOver() {
		if (!gameStart) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a one-line string representation of the current game state. The
	 * format is:
	 * <p>
	 * <tt>Player A*: X Player B: Y, Inning: Z</tt>
	 * <p>
	 * The asterisks next to the player's name indicates which player is at the
	 * table this inning. The number after the player's name is their score. Z is
	 * the inning number. Other messages will appear at the end of the string.
	 * 
	 * @return one-line string representation of the game state
	 */
	public String toString() {
		String fmt = "Player A%s: %d, Player B%s: %d, Inning: %d %s%s";
		String playerATurn = "";
		String playerBTurn = "";
		String inningStatus = "";
		String gameStatus = "";
		if (getInningPlayer() == PLAYER_A) {
			playerATurn = "*";
		} else if (getInningPlayer() == PLAYER_B) {
			playerBTurn = "*";
		}
		if (isInningStarted()) {
			inningStatus = "started";
		} else {
			inningStatus = "not started";
		}
		if (isGameOver()) {
			gameStatus = ", game result final";
		}
		return String.format(fmt, playerATurn, getPlayerAScore(), playerBTurn, getPlayerBScore(), getInning(),
				inningStatus, gameStatus);
	}
}

package com.cbuk.kalah.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.model.Pit;
import com.cbuk.kalah.repository.GameRepository;
import com.cbuk.kalah.rest.exception.KalahException;

import lombok.extern.slf4j.Slf4j;

/**
 * Service implementing game logic for Kalah
 *
 */
@Service
@Slf4j
public class GameService {

	public static final int STONE_COUNT = 6;
	public static final int NORTH_KALAH = 14;
	public static final int SOUTH_KALAH = NORTH_KALAH / 2;

	public static final String SOUTH = "S";
	public static final String NORTH = "N";

	@Autowired
	private GameRepository gameRepository;

	/**
	 * Create a new Game
	 * 
	 * The game is populated with the correct pits and stones
	 * 
	 * @return Game
	 */
	public Game newGame() {
		Game game = new Game();
		List<Pit> pits = new ArrayList<>();
		for (int x = 0; x < NORTH_KALAH; x++) {
			Pit pit = new Pit();
			pit.setGame(game);
			if ((x + 1) % SOUTH_KALAH != 0) {
				pit.setStoneCount(STONE_COUNT);
			}
			pits.add(pit);
		}
		game.setPits(pits);
		game.setNextTurn(SOUTH);

		gameRepository.save(game);
		return game;
	}

	/**
	 * Retrieve a game based on its ID
	 * 
	 * @param gameId
	 * 
	 * @return Game
	 * @throws KalahExcpetion if game not found
	 */
	public Game getGame(long gameId) {
		Optional<Game> maybeGame = gameRepository.findById(gameId);

		if (maybeGame.isEmpty()) {
			log.warn("Invalid game ID of {} requested", gameId);
			throw new KalahException("Invalid game ID");
		}

		return maybeGame.get();
	}

	/**
	 * Perform a move in a game of Kalah
	 * 
	 * Moves the stones from the pit with ID pitNo in the game with ID gameId
	 * 
	 * @param gameId
	 * @param pitNo
	 * @return Game with state after this move is performed
	 * @throws KalahException if IDs are incorrect or move is not allowed
	 */
	public Game move(long gameId, int pitNo) {

		Game game = getGame(gameId);

		if (pitNo < 1 || pitNo >= NORTH_KALAH || isSouthKalah(pitNo)) {
			log.warn("Invalid pit ID of {} requested", pitNo);
			throw new KalahException("Invalid pit ID");
		}

		if (!((game.getNextTurn().equals(SOUTH) && isSouthPit(pitNo))
				|| (game.getNextTurn().equals(NORTH) && isNorthPit(pitNo)))) {
			log.warn("Invalid pit ID of {} for turn {} requested", pitNo, game.getNextTurn());
			throw new KalahException("Invalid pit ID for this turn");
		}

		boolean isSouthMove = isSouthPit(pitNo);
		if (game.getPits().get(pitNo - 1).getStoneCount() > 0) {
			moveStones(pitNo, game, isSouthMove);
		} else {
			log.warn("Pit requested with ID of {} is empty", pitNo);
			throw new KalahException("No stones in pit");
		}

		return game;
	}

	/**
	 * Move stones based on the rules of the game
	 */
	private void moveStones(int pitNo, Game game, boolean isSouthMove) {

		List<Pit> pits = game.getPits();
		Pit pit = pits.get(pitNo - 1);
		int stoneCount = pit.getStoneCount();
		pit.setStoneCount(0);

		int nextPitNo = pitNo;
		Pit nextPit = null;
		while (stoneCount > 0) {
			nextPitNo = nextPit(nextPitNo);
			if ((isSouthMove && !isNorthKalah(nextPitNo)) || (!isSouthMove && !isSouthKalah(nextPitNo))) {
				nextPit = pits.get(nextPitNo - 1);
				nextPit.addStones(1);
				stoneCount--;
			}
		}

		captureStones(game, isSouthMove, nextPitNo, nextPit);

		List<Pit> southStones = pits.subList(0, SOUTH_KALAH - 1);
		int southStoneCount = southStones.stream().mapToInt(Pit::getStoneCount).sum();
		List<Pit> northStones = pits.subList(SOUTH_KALAH, NORTH_KALAH - 1);
		int northStoneCount = northStones.stream().mapToInt(Pit::getStoneCount).sum();

		if (southStoneCount == 0 || northStoneCount == 0) {
			pits.get(SOUTH_KALAH - 1).addStones(southStoneCount);
			southStones.stream().forEach(p -> p.setStoneCount(0));
			pits.get(NORTH_KALAH - 1).addStones(northStoneCount);
			northStones.stream().forEach(p -> p.setStoneCount(0));
		}

		if (!((isSouthMove && isSouthKalah(nextPitNo)) || (!isSouthMove && isNorthKalah(nextPitNo)))) {
			game.setNextTurn(game.getNextTurn().equals(SOUTH) ? NORTH : SOUTH);
		}

		gameRepository.save(game);
	}

	/**
	 * Handle the capture of an opponents stones when the last stone lands opposite
	 * a non-empty pit
	 */
	private void captureStones(Game game, boolean isSouthMove, int nextPitNo, Pit nextPit) {

		if (nextPit != null && nextPit.getStoneCount() == 1
				&& (isSouthMove && (isSouthPit(nextPitNo)) || !isSouthMove && isNorthPit(nextPitNo))) {

			Pit oppositePit = game.getPits().get(getOppositePitNo(nextPitNo) - 1);
			if (oppositePit.getStoneCount() > 0) {
				int kalahIndex = NORTH_KALAH - 1;
				if (isSouthMove) {
					kalahIndex = SOUTH_KALAH - 1;
				}
				Pit kalah = game.getPits().get(kalahIndex);
				kalah.addStones(oppositePit.getStoneCount() + 1);
				oppositePit.setStoneCount(0);
				nextPit.setStoneCount(0);
			}

		}
	}

	private boolean isSouthPit(int pitNo) {
		return pitNo >= 1 && pitNo < SOUTH_KALAH;
	}

	private boolean isNorthPit(int pitNo) {
		return pitNo > SOUTH_KALAH && pitNo < NORTH_KALAH;
	}

	private int nextPit(int pitNo) {
		if (pitNo >= NORTH_KALAH) {
			return 1;
		} else {
			return pitNo + 1;
		}
	}

	private boolean isSouthKalah(int pitNo) {
		return pitNo == SOUTH_KALAH;
	}

	private boolean isNorthKalah(int pitNo) {
		return pitNo == NORTH_KALAH;
	}

	private int getOppositePitNo(int pitNo) {
		return NORTH_KALAH - pitNo;
	}

}

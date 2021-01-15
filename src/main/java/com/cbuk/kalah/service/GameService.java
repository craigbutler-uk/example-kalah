package com.cbuk.kalah.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.model.Pit;
import com.cbuk.kalah.repository.GameRepository;

@Service
public class GameService {

	public final static int STONE_COUNT = 6;
	public final static int PIT_COUNT = 14;
	public final static int SOUTH_KALAH = PIT_COUNT / 2;

	public final static String SOUTH = "S";
	public final static String NORTH = "N";

	@Autowired
	private GameRepository gameRepository;

	public Game newGame() {
		Game game = new Game();
		List<Pit> pits = new ArrayList<>();
		for (int x = 0; x < PIT_COUNT; x++) {
			Pit pit = new Pit();
			pit.setGame(game);
			if ((x + 1) % SOUTH_KALAH != 0) { // TODO
				pit.setStoneCount(STONE_COUNT);
			}
			pits.add(pit);
		}
		game.setPits(pits);
		game.setNextTurn(SOUTH);

		gameRepository.save(game);
		return game;
	}

	public Game move(long gameId, int pitNo) {
		Optional<Game> maybeGame = gameRepository.findById(gameId);

		if (maybeGame.isEmpty()) {
			throw new RuntimeException("Invalid game ID");
		}

		Game game = maybeGame.get();

		// TODO handle failure
		if (pitNo < 1 || pitNo >= PIT_COUNT || isSouthKalah(pitNo)) {
			throw new RuntimeException("Invalid pit ID");
		}

		if (!((game.getNextTurn().equals(SOUTH) && isSouthPit(pitNo))
				|| (game.getNextTurn().equals(NORTH) && isNorthPit(pitNo)))) {
			throw new RuntimeException("Invalid pit ID for this turn");
		}

		Pit pit = game.getPits().get(pitNo - 1);
		int stoneCount = pit.getStoneCount();
		pit.setStoneCount(0);
		boolean isSouthMove = isSouthPit(pitNo);
		if (stoneCount > 0) {
			int nextPitNo = pitNo;
			Pit nextPit = null;
			while (stoneCount > 0) {
				nextPitNo = nextPit(nextPitNo);
				if ((isSouthMove && !isNorthKalah(nextPitNo)) || (!isSouthMove && !isSouthKalah(nextPitNo))) {
					nextPit = game.getPits().get(nextPitNo - 1);
					nextPit.setStoneCount(nextPit.getStoneCount() + 1);
					stoneCount--;

				}
			}

			if (nextPit.getStoneCount() == 1) {
				// we may get all of the stones from the opposite pit
				if (isSouthMove && (isSouthPit(nextPitNo)) || !isSouthMove && isNorthPit(nextPitNo)) {

					Pit oppositePit = game.getPits().get(getOppositePitNo(nextPitNo) - 1);
					if (oppositePit.getStoneCount() > 0) {
						if (isSouthMove) {
							game.getPits().get(SOUTH_KALAH - 1).setStoneCount(oppositePit.getStoneCount() + 1);
						} else {
							game.getPits().get(PIT_COUNT - 1).setStoneCount(oppositePit.getStoneCount() + 1);
						}
						oppositePit.setStoneCount(0);
						nextPit.setStoneCount(0);
					}
				}
			}

			if (!((isSouthMove && isSouthKalah(nextPitNo)) || (!isSouthMove && isNorthKalah(nextPitNo)))) {
				// indicate it is other players turn
				game.setNextTurn(game.getNextTurn().equals(SOUTH) ? NORTH : SOUTH);
			}

			gameRepository.save(game);
		} else {
			throw new RuntimeException("No stones in pit");
		}

		return game;
	}

	private boolean isSouthPit(int pitNo) {
		return pitNo >= 1 && pitNo < SOUTH_KALAH;
	}

	private boolean isNorthPit(int pitNo) {
		return pitNo > SOUTH_KALAH && pitNo < PIT_COUNT;
	}

	private int nextPit(int pitNo) {
		if (pitNo >= PIT_COUNT) {
			return 1;
		} else {
			return pitNo + 1;
		}
	}

	private boolean isSouthKalah(int pitNo) {
		return pitNo == SOUTH_KALAH;
	}

	private boolean isNorthKalah(int pitNo) {
		return pitNo == PIT_COUNT;
	}

	private int getOppositePitNo(int pitNo) {
		return PIT_COUNT - pitNo;
	}

}

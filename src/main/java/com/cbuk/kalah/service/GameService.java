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

	public Game getGame(long gameId) {
		Optional<Game> maybeGame = gameRepository.findById(gameId);

		if (maybeGame.isEmpty()) {
			throw new KalahException("Invalid game ID");
		}

		return maybeGame.get();
	}

	public Game move(long gameId, int pitNo) {

		Game game = getGame(gameId);

		// TODO handle failure
		if (pitNo < 1 || pitNo >= PIT_COUNT || isSouthKalah(pitNo)) {
			throw new KalahException("Invalid pit ID");
		}

		if (!((game.getNextTurn().equals(SOUTH) && isSouthPit(pitNo))
				|| (game.getNextTurn().equals(NORTH) && isNorthPit(pitNo)))) {
			throw new KalahException("Invalid pit ID for this turn");
		}

		List<Pit> pits = game.getPits();
		Pit pit = pits.get(pitNo - 1);
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
					nextPit.addStones(1);
					stoneCount--;

				}
			}

			if (nextPit.getStoneCount() == 1) {
				// we may get all of the stones from the opposite pit
				if (isSouthMove && (isSouthPit(nextPitNo)) || !isSouthMove && isNorthPit(nextPitNo)) {

					Pit oppositePit = game.getPits().get(getOppositePitNo(nextPitNo) - 1);
					if (oppositePit.getStoneCount() > 0) {
						int kalahIndex = PIT_COUNT - 1;
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

			List<Pit> southStones = pits.subList(0, SOUTH_KALAH - 1);
			int southStoneCount = southStones.stream().mapToInt(Pit::getStoneCount).sum();
			List<Pit> northStones = pits.subList(SOUTH_KALAH, PIT_COUNT - 1);
			int northStoneCount = northStones.stream().mapToInt(Pit::getStoneCount).sum();

			if (southStoneCount == 0 || northStoneCount == 0) {
				pits.get(SOUTH_KALAH - 1).addStones(southStoneCount);
				southStones.stream().forEach(p -> p.setStoneCount(0));
				pits.get(PIT_COUNT - 1).addStones(northStoneCount);
				northStones.stream().forEach(p -> p.setStoneCount(0));
			}

			if (!((isSouthMove && isSouthKalah(nextPitNo)) || (!isSouthMove && isNorthKalah(nextPitNo)))) {
				// indicate it is other players turn
				game.setNextTurn(game.getNextTurn().equals(SOUTH) ? NORTH : SOUTH);
			}

			gameRepository.save(game);
		} else {
			throw new KalahException("No stones in pit");
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

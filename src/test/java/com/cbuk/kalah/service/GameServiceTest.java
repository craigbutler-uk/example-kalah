package com.cbuk.kalah.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.model.Pit;
import com.cbuk.kalah.repository.GameRepository;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@InjectMocks
	private GameService gameService;

	@Test
	public void testCreateNewGame() {

		Game game = gameService.newGame();

		assertThat(game.getNextTurn(), is("S"));
		verify(gameRepository).save(game);
	}

	@Test
	public void testSouthEndsInKalah() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0 })));

		Game game = gameService.move(99L, 6);

		assertThat(game.getNextTurn(), is("S"));
		assertThat(game.getPits().get(5).getStoneCount(), is(0));
		assertThat(game.getPits().get(6).getStoneCount(), is(1));
	}

	@Test
	public void testNorthEndsInKalah() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "N", new int[] { 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0 })));

		Game game = gameService.move(99L, 13);

		assertThat(game.getNextTurn(), is("N"));
		assertThat(game.getPits().get(12).getStoneCount(), is(0));
		assertThat(game.getPits().get(13).getStoneCount(), is(1));
	}

	@Test
	public void testSouthEndsWithOneStone() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0 })));

		Game game = gameService.move(99L, 1);

		assertThat(game.getNextTurn(), is("N"));
		assertThat(game.getPits().get(0).getStoneCount(), is(0));
		assertThat(game.getPits().get(1).getStoneCount(), is(0));
		assertThat(game.getPits().get(6).getStoneCount(), is(10));
		assertThat(game.getPits().get(12).getStoneCount(), is(0));
	}

	@Test
	public void testSouthEndsWithOneStoneNothingOpposite() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Game game = gameService.move(99L, 1);

		assertThat(game.getNextTurn(), is("N"));
		assertThat(game.getPits().get(0).getStoneCount(), is(0));
		assertThat(game.getPits().get(1).getStoneCount(), is(1));
		assertThat(game.getPits().get(6).getStoneCount(), is(0));

	}

	@Test
	public void testNorthEndsWithOneStone() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "N", new int[] { 0, 0, 0, 0, 8, 0, 0, 1, 0, 0, 0, 0, 0, 0 })));

		Game game = gameService.move(99L, 8);

		assertThat(game.getNextTurn(), is("S"));
		assertThat(game.getPits().get(4).getStoneCount(), is(0));
		assertThat(game.getPits().get(8).getStoneCount(), is(0));
		assertThat(game.getPits().get(6).getStoneCount(), is(0));
		assertThat(game.getPits().get(13).getStoneCount(), is(9));
	}

	@Test
	public void testNorthMoveWrapsCorrectly() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "N", new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0 })));

		Game game = gameService.move(99L, 13);

		assertThat(game.getNextTurn(), is("S"));
		assertThat(game.getPits().get(12).getStoneCount(), is(0));
		assertThat(game.getPits().get(13).getStoneCount(), is(1));
		assertThat(game.getPits().get(0).getStoneCount(), is(1));
		assertThat(game.getPits().get(1).getStoneCount(), is(1));
		assertThat(game.getPits().get(2).getStoneCount(), is(0));
	}

	@Test
	public void testSouthMoveWrapsCorrectly() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Game game = gameService.move(99L, 6);

		assertThat(game.getNextTurn(), is("N"));
		assertThat(game.getPits().get(5).getStoneCount(), is(0));
		assertThat(game.getPits().get(6).getStoneCount(), is(1));
		assertThat(game.getPits().get(7).getStoneCount(), is(1));
		assertThat(game.getPits().get(8).getStoneCount(), is(1));
		assertThat(game.getPits().get(9).getStoneCount(), is(1));
		assertThat(game.getPits().get(10).getStoneCount(), is(1));
		assertThat(game.getPits().get(11).getStoneCount(), is(1));
		assertThat(game.getPits().get(12).getStoneCount(), is(1));
		assertThat(game.getPits().get(13).getStoneCount(), is(0));
		assertThat(game.getPits().get(0).getStoneCount(), is(2));
		assertThat(game.getPits().get(1).getStoneCount(), is(0));
	}

	@Test
	public void testInvalidGameId() {
		when(gameRepository.findById(99L)).thenReturn(Optional.empty());

		Assertions.assertThrows(RuntimeException.class, () -> {
			gameService.move(99L, 7);
		});

	}

	@Test
	public void testInvalidPitId() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assertions.assertThrows(RuntimeException.class, () -> {
			gameService.move(99L, 7);
		});

	}

	@Test
	public void testInvalidPitIdForThisTurn() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "N", new int[] { 1, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assertions.assertThrows(RuntimeException.class, () -> {
			gameService.move(99L, 7);
		});

	}
	
	@Test
	public void testNoStonesInPit() {
		when(gameRepository.findById(99L))
				.thenReturn(Optional.of(getGame(99L, "S", new int[] { 1, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0 })));

		Assertions.assertThrows(RuntimeException.class, () -> {
			gameService.move(99L, 2);
		});

	}

	private Game getGame(long id, String nextTurn, int[] stones) {
		Game game = new Game();

		List<Pit> pits = new ArrayList<>();

		for (int x = 0; x < stones.length; x++) {
			Pit pit = new Pit();
			pit.setStoneCount(stones[x]);
			pits.add(pit);
		}
		game.setId(id);
		game.setNextTurn(nextTurn);
		game.setPits(pits);

		return game;
	}
}

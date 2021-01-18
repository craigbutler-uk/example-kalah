package com.cbuk.kalah.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.model.Pit;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class GameRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private GameRepository gameRepository;

	@Test
	public void testFindByIde() {

		Game inGame = new Game();
		inGame.setNextTurn("S");
		Pit pit1 = new Pit();
		pit1.setStoneCount(1);

		Pit pit2 = new Pit();
		pit2.setStoneCount(2);

		inGame.setPits(Arrays.asList(pit1, pit2));

		entityManager.persist(inGame);
		entityManager.flush();

		Long gameId = inGame.getId();

		Optional<Game> maybeOutGame = gameRepository.findById(gameId);

		assertThat(maybeOutGame.isPresent(), is(true));

		Game outGame = maybeOutGame.get();
		assertThat(outGame.getNextTurn(), is("S"));
		assertThat(outGame.getPits().get(0).getStoneCount(), is(1));
		assertThat(outGame.getPits().get(1).getStoneCount(), is(2));

	}

}

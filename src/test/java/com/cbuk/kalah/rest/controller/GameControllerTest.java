package com.cbuk.kalah.rest.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.service.GameService;

@WebMvcTest(GameController.class)
@ExtendWith(SpringExtension.class)
public class GameControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GameService gameService;

	@InjectMocks
	GameController gameController;

	@Test
	public void testNewGame() throws Exception {

		Game game = new Game();
		game.setId(99L);

		when(gameService.newGame()).thenReturn(game);
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/games")).andExpect(status().isOk())
				.andReturn();

		assertThat(result.getResponse().getContentAsString(), is("{\"id\":99,\"uri\":\"http://localhost/games/99\"}"));
	}

	@Test
	public void testMove() throws Exception {

		Game game = new Game();
		game.setId(99L);
		game.setPits(new ArrayList<>());

		when(gameService.move(99L, 1)).thenReturn(game);
		MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.put("/games/99/pits/1"))
				.andExpect(status().isOk()).andReturn();

		assertThat(result.getResponse().getContentAsString(),
				is("{\"id\":99,\"uri\":\"http://localhost/games/99\",\"pits\":{}}"));
	}

}

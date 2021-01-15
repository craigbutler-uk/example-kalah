package com.cbuk.kalah.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.cbuk.kalah.model.Game;
import com.cbuk.kalah.model.Pit;
import com.cbuk.kalah.rest.model.GameDto;
import com.cbuk.kalah.service.GameService;

@RestController
public class GameController {

	@Autowired
	private GameService gameService;

	@PostMapping("/games")
	public GameDto newGame(UriComponentsBuilder uriB) {

		Game game = gameService.newGame();
		Long gameId = game.getId();

		UriComponents uriComponents = uriB.path("/game/{id}").buildAndExpand(gameId);

		return new GameDto(gameId, uriComponents.toUri(), null);

	}

	@PutMapping("/games/{gameId}/pits/{pitId}")
	public GameDto move(UriComponentsBuilder uriB, @PathVariable long gameId, @PathVariable int pitId) {

		Game game = gameService.move(gameId, pitId);

		UriComponents uriComponents = uriB.path("/game/{id}").buildAndExpand(gameId);

		//TODO move this to mapper class?
		List<Pit> pits = game.getPits();
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 1; i <= pits.size(); i++) {
			map.put(i, pits.get(i - 1).getStoneCount());
		}
		return new GameDto(gameId, uriComponents.toUri(), map);

	}
}

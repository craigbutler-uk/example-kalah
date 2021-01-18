package com.cbuk.kalah.rest.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class GameController {

	@Autowired
	private GameService gameService;

	@PostMapping("/games")
	@ApiOperation(value = "Creates new game", response = GameDto.class)
	public GameDto newGame(UriComponentsBuilder uriB) {

		Game game = gameService.newGame();
		Long gameId = game.getId();

		return new GameDto(gameId, getUri(uriB, gameId), null);
	}

	private URI getUri(UriComponentsBuilder uriB, Long gameId) {
		UriComponents uriComponents = uriB.path("/games/{id}").buildAndExpand(gameId);
		return uriComponents.toUri();
	}

	@PutMapping("/games/{gameId}/pits/{pitId}")
	@ApiOperation(value = "Moves stones from pit", response = GameDto.class)
	public GameDto move(UriComponentsBuilder uriB,
			@ApiParam(value = "ID of game", required = true) @PathVariable long gameId,
			@ApiParam(value = "ID of pit from which stones are to be moved ", allowableValues = "1,2,3,4,5,6,8,9,10,11,12,13", required = true) @PathVariable int pitId) {

		Game game = gameService.move(gameId, pitId);

		return getGameDto(game, getUri(uriB, gameId));
	}

	@GetMapping("/games/{gameId}")
	@ApiOperation(value = "Get info on game", response = GameDto.class)
	public GameDto move(UriComponentsBuilder uriB,
			@ApiParam(value = "ID of game", required = true) @PathVariable long gameId) {

		Game game = gameService.getGame(gameId);

		return getGameDto(game, getUri(uriB, gameId));
	}

	private GameDto getGameDto(Game game, URI uri) {
		List<Pit> pits = game.getPits();
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 1; i <= pits.size(); i++) {
			map.put(i, pits.get(i - 1).getStoneCount());
		}
		return new GameDto(game.getId(), uri, map);
	}
}

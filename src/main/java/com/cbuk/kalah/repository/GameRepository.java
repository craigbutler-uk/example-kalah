package com.cbuk.kalah.repository;

import org.springframework.data.repository.CrudRepository;

import com.cbuk.kalah.model.Game;

public interface GameRepository extends CrudRepository<Game, Long> {

}

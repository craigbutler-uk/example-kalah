package com.cbuk.kalah.repository;

import org.springframework.data.repository.CrudRepository;

import com.cbuk.kalah.model.Game;

/**
 * JPA Repository for Game object
 *
 */
public interface GameRepository extends CrudRepository<Game, Long> {

}

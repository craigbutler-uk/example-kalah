package com.cbuk.kalah.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import lombok.Data;

/**
 * Game entity
 * 
 * Represents the state of a single game of Kalah. The pits on the board are
 * represented by a simple list.
 */
@Entity
@Data
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	@OrderColumn
	private List<Pit> pits;

	private String nextTurn;
}

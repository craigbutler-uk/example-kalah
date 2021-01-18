package com.cbuk.kalah.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * Pit entity
 */
@Entity
@Data
public class Pit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;
	
	private int stoneCount;
	
	public void addStones (int stones) {
		this.stoneCount = this.stoneCount + stones;
	}
}

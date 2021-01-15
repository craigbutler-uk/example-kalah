CREATE TABLE game (
  id bigint NOT NULL AUTO_INCREMENT,
  next_turn char(1) DEFAULT 'S',
  PRIMARY KEY (id)
);

CREATE TABLE pit (
  id bigint NOT NULL AUTO_INCREMENT,
  game_id bigint,
  pits_order int,
  stone_count int,
  PRIMARY KEY (id)
);
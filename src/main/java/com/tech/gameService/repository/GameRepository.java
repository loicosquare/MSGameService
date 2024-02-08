package com.tech.gameService.repository;

import com.tech.gameService.entities.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GameRepository extends MongoRepository<Game, String>{
}

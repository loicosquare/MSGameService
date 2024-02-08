package com.tech.gameService.services;

import com.tech.gameService.common.entities.HttpResponse;
import com.tech.gameService.common.exception.game.GameExistException;
import com.tech.gameService.common.exception.game.GameNotFoundException;
import com.tech.gameService.entities.Game;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public interface GameService {
    HttpResponse<Game> createGame(Game game) throws GameExistException, MalformedURLException;
    HttpResponse<Game> updateGame(Game game) throws GameNotFoundException;
    HttpResponse<Game> getGameById(String gameId);
    HttpResponse<Game> getAllGames() throws GameNotFoundException;
    HttpResponse<Game> deleteGame(String gameId) throws GameNotFoundException;
}

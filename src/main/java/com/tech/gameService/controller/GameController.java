package com.tech.gameService.controller;

import com.tech.gameService.common.entities.HttpResponse;
import com.tech.gameService.common.exception.ExceptionHandling;
import com.tech.gameService.common.Constant.ConstantUrl;
import com.tech.gameService.common.exception.game.GameExistException;
import com.tech.gameService.common.exception.game.GameNotFoundException;
import com.tech.gameService.entities.Game;
import com.tech.gameService.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = ConstantUrl.GAMES)
public class GameController extends ExceptionHandling {

    private final GameService gameService;

    @GetMapping(value = {ConstantUrl.GET_ALL_GAME})
    public ResponseEntity<HttpResponse<Game>> getAllGames() throws GameNotFoundException {
        HttpResponse<Game> allGames = gameService.getAllGames();
        if (allGames != null && !allGames.getGames().isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(allGames);
        }else{
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value ={ConstantUrl.GET_ONE_GAME})
    public ResponseEntity<HttpResponse<Game>> getOneGame(@RequestParam String gameId) {
        HttpResponse<Game> foundedGame = gameService.getGameById(gameId);
        if (foundedGame != null){
            return ResponseEntity.status(HttpStatus.OK).body(foundedGame);
        }else{
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = {ConstantUrl.CREATE_GAME})
    public ResponseEntity<HttpResponse<Game>> createGame(@RequestBody Game game) throws GameExistException, MalformedURLException {
        HttpResponse<Game> createdGame = gameService.createGame(game);
        if (createdGame != null){
            //return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
            return ResponseEntity.created(
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/games/all").toUriString())
            ).body(createdGame);
        }else{
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = {ConstantUrl.UPDATE_GAME})
    public ResponseEntity<HttpResponse<Game>> updateGame(@RequestBody Game game) throws GameNotFoundException {
        ResponseEntity<HttpResponse<Game>> result;
        HttpResponse<Game> updatedGame = gameService.updateGame(game);
        if (updatedGame != null){
            result = ResponseEntity.status(HttpStatus.CREATED).body(updatedGame);
        }else{
            result = ResponseEntity.noContent().build();
        }
        return result;
    }

    @DeleteMapping(value = {ConstantUrl.DELETE_GAME})
    public ResponseEntity<HttpResponse<Game>> deleteGame(@RequestParam String gameId) throws GameNotFoundException {
        ResponseEntity<HttpResponse<Game>> result;
        if (gameService.deleteGame(gameId) != null) {
            result = ResponseEntity.status(HttpStatus.OK).body(gameService.deleteGame(gameId));
        }else{
            result = ResponseEntity.noContent().build();
        }
        return result;
    }
}

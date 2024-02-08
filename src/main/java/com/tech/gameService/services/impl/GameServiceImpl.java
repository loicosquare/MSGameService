package com.tech.gameService.services.impl;

import com.tech.gameService.common.entities.HttpResponse;
import com.tech.gameService.common.exception.game.GameExistException;
import com.tech.gameService.common.exception.game.GameNotFoundException;
import com.tech.gameService.entities.Game;
import com.tech.gameService.externalEntities.Rating;
import com.tech.gameService.repository.GameRepository;
import com.tech.gameService.services.GameService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.tech.gameService.common.Constant.ConstantUrl.TEMP_IMAGE_BASE_URL;
import static java.util.Collections.singleton;
import static org.springframework.http.HttpStatus.CREATED;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    /**
     * Créer un nouveau jeu.
     *
     * @param game Le jeu à créer.
     * @return Le jeu créé.
     */
    @Override
    public HttpResponse<Game> createGame(Game game) throws GameExistException, MalformedURLException {
        logger.info("Saving new game to the database");
        game.setReleaseDate(LocalDate.now());
        game.setImageUrl(String.valueOf(new URL(TEMP_IMAGE_BASE_URL + game.getTitle().trim())));

        try {
            return HttpResponse.<Game>builder()
                    .timeStamp(new Date())
                    .httpStatusCode(200)
                    .httpStatus(CREATED)
                    .httpStatusCode(CREATED.value())
                    .reason(CREATED.getReasonPhrase())
                    .message("The game is added successfully.")
                    .games(singleton(gameRepository.save(game)))
                    .build();
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la création du jeu : {}", e.getMessage());
            throw new GameExistException("Erreur lors de la création du jeu.");
        }
    }

    /**
     * Mettre à jour un jeu existant.
     *
     * @param game Le jeu à mettre à jour.
     * @return Le jeu mis à jour.
     */
    @Override
    public HttpResponse<Game> updateGame(Game game) throws GameNotFoundException {
        logger.info("Updating game to the database");

        try {
            Optional<Game> optionalGame;
            optionalGame = Optional.of(gameRepository.findById(game.getGameId()))
                    .orElseThrow(() -> new GameNotFoundException("The game was not found on the server"));

            if (optionalGame.isPresent()) {
                logger.info("game found on the database");
                Game updatedGame;
                updatedGame = Game.builder()
                        //.gameId(game.getGameId())
                        .title(game.getTitle())
                        .description(game.getDescription())
                        .releaseDate(game.getReleaseDate())
                        .platform(game.getPlatform())
                        .developer(game.getDeveloper())
                        .publisher(game.getPublisher())
                        //.ratingId(game.getRatingId())
                        .price(game.getPrice())
                        .gameLength(game.getGameLength())
                        .imageUrl(game.getImageUrl())
                        .categoryId(game.getCategoryId()) //TODO : Replace by category object comming from category service (Microservice)
                        .build();
                gameRepository.save(updatedGame);

                logger.info("The game is saved to the database");
                return HttpResponse.<Game>builder()
                        .timeStamp(new Date())
                        .httpStatusCode(200)
                        .httpStatus(HttpStatus.OK)
                        .httpStatusCode(CREATED.value())
                        .reason(HttpStatus.OK.getReasonPhrase())
                        .message("The game is updated successfully.")
                        .games(Collections.singletonList(updatedGame))
                        .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la mise à jour du jeu : {}", e.getMessage());
            throw new GameNotFoundException("Erreur lors de la mise à jour du jeu.");
        }
    }

    /**
     * Retourner un jeu par son ID.
     *
     * @param gameId L'ID du jeu.
     * @return Le jeu correspondant à l'ID spécifié.
     * @throws RuntimeException si aucun jeu n'est trouvé.
     */
    @Override
    public HttpResponse<Game> getGameById(String gameId) {
        logger.info("Fetching game by id from the database");
        Optional<Game> optionalGame;
        optionalGame = Optional.ofNullable(Optional.of(
            gameRepository.findAll().stream()
                    .filter(game -> game.getGameId().equals(gameId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Game with id {} not found" + gameId))
        ).orElseThrow(() -> new RuntimeException("Game with id {} not found" + gameId)));
        Game game = optionalGame.get();

        logger.info("The game is fetched from the database");
        return HttpResponse.<Game>builder()
                .games(singleton(game))
                .message("Game fetched successfully")
                .timeStamp(new Date())
                //.httpStatusCode(CREATED.value())
                .httpStatusCode(200)
                .httpStatus(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    /**
     * Supprimer un jeu par son ID.
     *
     * @param gameId L'ID du jeu à supprimer.
     * @return Un message indiquant que le jeu a été supprimé avec succès.
     */
    @Override
    public HttpResponse<Game> deleteGame(String gameId) throws GameNotFoundException {
        logger.warn("Deleting game by id from the database");
        try {
            Optional<Game> optionalGame;
            optionalGame = Optional.of(gameRepository.findById(gameId))
                    .orElseThrow(() -> new GameNotFoundException("The game was not found on the server"));

            optionalGame.ifPresent(gameRepository::delete);
            logger.warn("The game is deleted from the database");
            return HttpResponse.<Game>builder()
                    .games(singleton(optionalGame.get()))
                    .message("Note deleted successfully")
                    .timeStamp(new Date())
                    .httpStatusCode(200)
                    .httpStatus(HttpStatus.OK)
                    .reason(HttpStatus.OK.getReasonPhrase())
                    .build();
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la suppression du jeu : {}", e.getMessage());
            throw new GameNotFoundException("Erreur lors de la suppression du jeu.");
        }
    }

    /**
     * Retourner tous les jeux.
     * @return List<Game> : All games in database (MongoDB) as a list.
     */
    @Override
    public HttpResponse<Game> getAllGames() throws GameNotFoundException {
        logger.info("Fetching all the Games from the database");
        List<Game> games;
        games = gameRepository.findAll().stream().toList();

        //Rating[] foundedRating = restTemplate.getForObject("http://RATING-SERVICE/ratings/" , Rating[].class);

        //TODO : Faire une méthode pour récupérer les Ratings en fonction de l'ID du jeu. (Regarder la différence entre getForObject et exchange).
        ResponseEntity<Rating[]> response = restTemplate.exchange(
                "http://RATING-SERVICE/ratings/",
                HttpMethod.GET,
                null,
                Rating[].class
        );
        Rating[] ratings = response.getBody();

        try {
            if (ratings != null) {
                logger.info("All the Ratings are fetched from the database");
                for (Game game : games) {
                    for (Rating rating : ratings) {
                        if (rating.getGameId().equals(game.getGameId())) {
                            //game.setRatings(Collections.singletonList(rating));
                            game.setRatings(List.of(rating));
                        }
                    }
                }
            }

        logger.info("All the Games are fetched from the database");

            return HttpResponse.<Game>builder()
                    .games(games)
                    .message(gameRepository.count() > 0 ? gameRepository.count() + " games retrieved" : "No games to display")
                    .timeStamp(new Date())
                    .httpStatusCode(CREATED.value())
                    //.httpStatusCode(200)
                    .httpStatus(HttpStatus.OK)
                    .reason(HttpStatus.OK.getReasonPhrase())
                    .build();
        } catch (Exception e) {
            logger.error("Une erreur est survenue lors de la récupération de tous les jeux : {}", e.getMessage());
            throw new GameNotFoundException("Erreur lors de la récupération de tous les jeux.");
        }
    }
}

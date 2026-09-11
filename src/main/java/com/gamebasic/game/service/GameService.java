package com.gamebasic.game.service;

import com.gamebasic.common.exception.GameFinishedException;
import com.gamebasic.common.exception.GameNotFoundException;
import com.gamebasic.game.dto.*;
import com.gamebasic.game.entity.Game;
import com.gamebasic.game.repository.GameRepository;
import com.gamebasic.runcard.dto.CardResponse;
import com.gamebasic.runcard.dto.DeckCountResponse;
import com.gamebasic.runcard.dto.RunCardRequest;
import com.gamebasic.runcard.entity.RunCard;
import com.gamebasic.runcard.repository.RunCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RunCardRepository runCardRepository;

    @Transactional
    public GameDetailResponse createGame(CreateRequest request) {
        Game game = gameRepository.save(new Game(request.getPlayerName()));
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentHp(),
                game.getCurrentFloor(),
                game.getPhase(),
                game.getStatus(),
                deck,
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }

    private void saveDeck(Game game, List<RunCardRequest> deck) {
        List<RunCard> cards = new ArrayList<>();
        for (RunCardRequest card : deck) {
            cards.add(new RunCard(game, card.getCardType(), card.getAcquiredFloor()));
        }
        runCardRepository.saveAll(cards);
    }

    private Game findGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Transactional
    public GameDetailResponse updateProgress(Long gameId, ProgressRequest request) {
        Game game = findGame(gameId);
        // TODO (Lv 9): 끝난 게임 덮어쓰기 막기(조건문)구현함
        // Game Status가 PLAYING이 아닐 때 Status를 수정하려고 하면 409 응답이 나오면서 Conflict 메세지 표시
        if (game.isFinished()) throw new GameFinishedException(gameId);

        game.updateProgress(
                request.getCurrentHp(),
                request.getCurrentFloor(),
                request.getPhase(),
                request.getStatus()
        );
        // 요청의 deck은 저장할 덱 전체이므로 기존 카드를 모두 지우고 요청 순서대로 다시 저장합니다.
        runCardRepository.deleteAllByGame(game);
        saveDeck(game, request.getDeck());
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = new ArrayList<>();
        for (RunCard card : cards) {
            deck.add(new CardResponse(card.getId(), card.getCardType(), card.getAcquiredFloor()));
        }
        return new GameDetailResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentHp(),
                game.getCurrentFloor(),
                game.getPhase(),
                game.getStatus(),
                deck,
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }

    // TODO (Lv 7): 게임 목록 조회. 주석을 풀고 구현하세요.
    @Transactional(readOnly = true)
    public List<GameSummaryResponse> getGames() {
        List<Game> games = gameRepository.findAllByOrderByIdDesc();
        List<GameSummaryResponse> dtos = new ArrayList<>();
        List<DeckCountResponse> counts = runCardRepository.countByGames(games);

        // TODO (Lv 11):
        Map<Long, Integer> deckSizeMap = counts.stream() // List<DeckCountResponse>를 스트림으로 변환
                .collect(Collectors.toMap( // Collectors.toMap(키를 뽑는 함수, 값을 뽑는 함수)
                        DeckCountResponse::getGameId,
                        DeckCountResponse::getDeckSize
                ));

        for (Game game : games) {
            // 카드가 항상 존재한다면 deckSizeMap.get(game.getId())만 사용해도 됨
            // 만약 카드가 없는 경우 countByGames에 결과가 나오지 않기 때문에 기본값 0을 반환하도록 함
            int deskSize = deckSizeMap.getOrDefault(game.getId(), 0);

            GameSummaryResponse dto = new GameSummaryResponse(
                    game.getId(),
                    game.getPlayerName(),
                    game.getCurrentFloor(),
                    game.getCurrentHp(),
                    game.getPhase(),
                    game.getStatus(),
                    game.getCreatedAt(),
                    game.getUpdatedAt(),
                    deskSize
            );
            dtos.add(dto);
        }

        return dtos;
    }

    // TODO (Lv 7): 게임 상세 조회. 주석을 풀고 구현하세요.
    @Transactional(readOnly = true)
    public GameDetailResponse getGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException(gameId)
        );
        List<RunCard> cards = runCardRepository.findAllByGameOrderByIdAsc(game);
        List<CardResponse> deck = cards.stream()
                .map(card -> new CardResponse(
                        card.getId(),
                        card.getCardType(),
                        card.getAcquiredFloor()))
                .toList();

        return new GameDetailResponse(
                game.getId(),
                game.getPlayerName(),
                game.getCurrentHp(),
                game.getCurrentFloor(),
                game.getPhase(),
                game.getStatus(),
                deck,
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }

    // TODO (Lv 8): 플레이어 이름 변경 — 변경 감지로 수정
    @Transactional
    public void renameGame(Long gameId, RenameRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException(gameId)
        );
        game.rename(request.getPlayerName());
    }

    // TODO (Lv 8): 게임 삭제
    @Transactional
    public void deleteGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new GameNotFoundException(gameId)
        );

        boolean existenceGame = gameRepository.existsById(gameId);
        if (!existenceGame) throw new GameNotFoundException(gameId);

        runCardRepository.deleteAllByGame(game);
        gameRepository.deleteById(gameId);
    }
}

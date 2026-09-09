package com.gamebasic.game.dto;

import com.gamebasic.game.entity.GamePhase;
import com.gamebasic.game.entity.GameStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GameSummaryResponse {

    private final Long id;
    private final String playerName;
    private final int currentFloor;
    private final int currentHp;
    private final GamePhase phase;
    private final GameStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int deckSize;
}

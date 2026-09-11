package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankingEntriesResponse {

    private final int rank;
    private final String playerName;
    private final int clearTimeSeconds;
    private final int remainingHp;
    private final int bossTurns;
    private final int deckSize;
}

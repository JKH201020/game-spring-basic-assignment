package com.gamebasic.runcard.dto;

import lombok.Getter;

// TODO (Lv 11): DeckCountResponse 클래스 생성
@Getter
public class DeckCountResponse {

    private final Long gameId;
    private final int deckSize;

    public DeckCountResponse(Long gameId, Long deckSize) {
        this.gameId = gameId;
        this.deckSize = deckSize.intValue();
    }
}
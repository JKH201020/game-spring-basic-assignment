package com.gamebasic.ranking.dto;

import com.gamebasic.runcard.entity.CardType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Cards {

    private final String cardType;
    private final int acquiredFloor;
}

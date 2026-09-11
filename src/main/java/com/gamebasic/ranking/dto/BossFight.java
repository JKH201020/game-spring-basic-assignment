package com.gamebasic.ranking.dto;

import com.gamebasic.runcard.entity.CardType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BossFight {

    private final List<Phase> phases;
    private final String finishingCard;
    private final int totalTurns;
}

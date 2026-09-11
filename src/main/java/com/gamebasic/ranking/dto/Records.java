package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Records {

    private final Long id;
    private final Player player;
    private final Run run;
    private final BossFight bossFight;
    private final Deck deck;
}

package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Phase {

    private final PhaseType phase;
    private final int turns;
    private final int damageTaken;
}

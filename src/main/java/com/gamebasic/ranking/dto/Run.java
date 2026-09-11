package com.gamebasic.ranking.dto;

import com.gamebasic.game.entity.GameStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Run {

    private final GameStatus status;
    private final int clearedFloor;
    private final int durationSeconds;
    private final int finalHp;
}

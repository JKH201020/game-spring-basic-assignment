package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Floors {

    private final int floor;
//    private final String enemy;
    private final int turns;
    private final int hpAfter;
//    private final List<Rewards> rewards;
}

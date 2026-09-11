package com.gamebasic.ranking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Deck {

    private final int size;
    private final List<Cards> cards;
}

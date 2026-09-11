package com.gamebasic.ranking.service;

import com.gamebasic.game.entity.GameStatus;
import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.dto.*;
import com.gamebasic.runcard.entity.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingClient rankingClient;


    @Transactional(readOnly = true)
    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();

        // 1. 순위 대상
        List<Records> records = source.getRecords().stream()
                .filter(record -> record.getRun().getStatus()
                        == GameStatus.CLEARED && record.getRun().getClearedFloor() == 10)
                .toList();

        // 2. 정상 기록 조건
        List<Records> validRecords = new ArrayList<>();
        for (Records record : records) {
            if (isValidRecord(record)) validRecords.add(record);
        }

        // 3. 정렬
        List<Records> sortedRecords = validRecords.stream()
                .sorted(Comparator.comparing((Records r) -> r.getRun().getDurationSeconds())
                        .thenComparing((Records r) -> r.getRun().getFinalHp(), Comparator.reverseOrder())
                        .thenComparing((Records r) -> r.getId()))
                .toList();

        // 4. 플레이어당 하나
        Set<String> duplicatePlayerIds = new HashSet<>();
        List<Records> result = new ArrayList<>();

        for (Records record : sortedRecords) {
            String playerId = record.getPlayer().getId();
            if (!duplicatePlayerIds.contains(playerId)) {
                duplicatePlayerIds.add(playerId);
                result.add(record);
            }
        }

        String season = source.getMeta().getSeason().getId();
        int totalRecords = source.getRecords().size();
        int excludedCount = records.size() - validRecords.size();
        List<RankingEntriesResponse> entries = IntStream.range(0, result.size())
                .mapToObj(i -> {
                    Records record = result.get(i);
                    int rank = i + 1;
                    return new RankingEntriesResponse(
                            rank,
                            record.getPlayer().getName(),
                            record.getRun().getDurationSeconds(),
                            record.getRun().getFinalHp(),
                            record.getBossFight().getTotalTurns(),
                            record.getDeck().getSize()
                    );
                }).toList();

        return new RankingResponse(
                season,
                totalRecords,
                excludedCount,
                entries
        );
    }

    /// 정상 기록 조건
    private boolean isValidRecord(Records records) {
        return isValidRun(records.getRun())
                && isValidDeck(records.getDeck())
                && isValidDeckCards(records.getDeck())
                && isValidBossPhase(records.getBossFight())
                && isValidFinishingCard(records.getBossFight(), records.getDeck());
    }

    /// 클리어 시간 조건, 남은 HP 조건
    private boolean isValidRun(Run run) {
        return (run.getDurationSeconds() >= run.getClearedFloor() * 30)
                && (run.getFinalHp() >= 1 && run.getFinalHp() <= 99);
    }

    /// 덱 크기 조건
    private boolean isValidDeck(Deck deck) {
        return (deck.getCards().size() >= 9 && deck.getCards().size() <= 20)
                && (deck.getSize() == deck.getCards().size());
    }

    /// 덱 전체 순회 메서드 (카드 타입 조건 + 획득 층 조건)
    private boolean isValidDeckCards(Deck deck) {
        return deck.getCards().stream()
                .allMatch(card -> isValidCardType(card) && isValidAcquiredFloor(card));
    }

    /// 카드 타입 조건
    private boolean isValidCardType(Cards card) {
        try {
            CardType.valueOf(card.getCardType());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /// 획득 층 조건
    private boolean isValidAcquiredFloor(Cards card) {
        return ((card.getAcquiredFloor() >= 0 && card.getAcquiredFloor() <= 9));
    }

    /// 보스 페이즈 조건
    private boolean isValidBossPhase(BossFight bossFight) {
        List<Phase> phases = bossFight.getPhases();
        if (phases.size() != 3) return false;

        Phase phaseTHRONE = phases.get(0);
        Phase phaseUNBOUND = phases.get(1);
        Phase phaseECLIPSE = phases.get(2);

        boolean checkPhaseTypes = // 페이즈 순서를 지키고 있는지
                phaseTHRONE.getPhase() == PhaseType.THRONE
                        && phaseUNBOUND.getPhase() == PhaseType.UNBOUND
                        && phaseECLIPSE.getPhase() == PhaseType.ECLIPSE;
        boolean checkTotalTurns = bossFight.getTotalTurns() // TotalTurns와 페이즈별 더한 Turns가 같은지
                == phaseTHRONE.getTurns() + phaseUNBOUND.getTurns() + phaseECLIPSE.getTurns();
        boolean checkTurns = phaseTHRONE.getTurns() >= 1 // 각 페이즈별 turns가 1 이상인가
                && phaseUNBOUND.getTurns() >= 1
                && phaseECLIPSE.getTurns() >= 1;

        return checkPhaseTypes && checkTotalTurns && checkTurns;
    }

    /// 마무리 카드 조건
    private boolean isValidFinishingCard(BossFight bossFight, Deck deck) {
        return deck.getCards().stream()
                .anyMatch(card -> card.getCardType().equals(bossFight.getFinishingCard()));
    }
}

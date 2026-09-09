package com.gamebasic.runcard.repository;

import com.gamebasic.runcard.dto.DeckCountResponse;
import com.gamebasic.game.entity.Game;
import com.gamebasic.runcard.entity.RunCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunCardRepository extends JpaRepository<RunCard, Long> {
    List<RunCard> findAllByGameOrderByIdAsc(Game game);

    void deleteAllByGame(Game game);

    // TODO (Lv 11): @Query 작성
    @Query("""
            select new com.gamebasic.runcard.dto.DeckCountResponse(rc.game.id, count(rc))
            from RunCard rc
            where rc.game in :games
            group by rc.game.id
            """)
    List<DeckCountResponse> countByGames(@Param("games") List<Game> games);
}

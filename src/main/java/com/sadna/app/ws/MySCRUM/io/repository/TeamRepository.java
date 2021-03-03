package com.sadna.app.ws.MySCRUM.io.repository;

import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  TeamRepository extends PagingAndSortingRepository<TeamEntity, Long> {
    TeamEntity findByTeamId(String teamId);
    TeamEntity findByName(String name);
    Iterable<TeamEntity> findByUsers_UserId(String UserId);
}

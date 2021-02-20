package com.sadna.app.ws.MySCRUM.io.repository;

import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  TeamRepository extends PagingAndSortingRepository<TeamEntity, Long> {
    TeamEntity findByTeamId(String teamId);
    TeamEntity findByName(String name);
}

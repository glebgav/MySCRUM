package com.sadna.app.ws.MySCRUM.io.repository;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity,Long>{
    List<TaskEntity>  findAllByUserDetails(UserEntity userEntity);

    TaskEntity findByTaskId(String taskId);

    Iterable<TaskEntity> findAllByTeamDetails(TeamEntity teamEntity);
}

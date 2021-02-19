package com.sadna.app.ws.MySCRUM.io.repository;

import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity,Long> {
   UserEntity findByEmail(String email);
   UserEntity findByUserId(String userId);

}

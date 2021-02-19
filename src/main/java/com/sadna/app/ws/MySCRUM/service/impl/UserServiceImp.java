package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.Utils;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.response.ErrorMessages;
import com.sadna.app.ws.MySCRUM.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user,userEntity);

        String publicUserId = utils.generateUserId(20);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepo.save(userEntity);

        UserDto returnVal = new UserDto();


        BeanUtils.copyProperties(storedUserDetails,returnVal);

        return returnVal;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepo.findByEmail(email);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        UserDto returnVal = new UserDto();
        BeanUtils.copyProperties(userEntity,returnVal);
        return returnVal;

    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        UserDto returnVal = new UserDto();
        BeanUtils.copyProperties(userEntity,returnVal);
        return returnVal;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnVal = new UserDto();
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setIsManager(user.getIsManager());

        UserEntity updatedUser = userRepo.save(userEntity);
        BeanUtils.copyProperties(updatedUser,returnVal);

        return returnVal;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepo.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnVal = new ArrayList<>();
        if(page > 0) page-=1;

        Pageable pageable =  PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepo.findAll(pageable);
        List<UserEntity> users = usersPage.getContent();

        for(UserEntity userEntity: users)
        {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnVal.add(userDto);

        }
        return returnVal;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByEmail(email);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}

package com.sadna.app.ws.MySCRUM.service.impl;

import com.sadna.app.ws.MySCRUM.io.entity.TaskEntity;
import com.sadna.app.ws.MySCRUM.io.entity.TeamEntity;
import com.sadna.app.ws.MySCRUM.io.entity.UserEntity;
import com.sadna.app.ws.MySCRUM.io.repository.TaskRepository;
import com.sadna.app.ws.MySCRUM.io.repository.TeamRepository;
import com.sadna.app.ws.MySCRUM.io.repository.UserRepository;
import com.sadna.app.ws.MySCRUM.service.UserService;
import com.sadna.app.ws.MySCRUM.shared.Utils;
import com.sadna.app.ws.MySCRUM.shared.dto.TaskDto;
import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;
import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import com.sadna.app.ws.MySCRUM.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    Utils utils;

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    TaskRepository taskRepo;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        ModelMapper modelMapper = new ModelMapper();

        String publicUserId = utils.generateUserId(20);
        user.setUserId(publicUserId);

        List<TaskDto> tasks = user.getTasks();
        if(tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                TaskEntity taskFromRepo = taskRepo.findByTaskId(tasks.get(i).getTaskId());
                TaskDto newTask;
                if (taskFromRepo != null) {
                    newTask = modelMapper.map(taskFromRepo, TaskDto.class);
                } else {
                    newTask = tasks.get(i);
                    newTask.setTaskId(utils.generateTaskId(20));
                }
                newTask.setUserDetails(user);
                tasks.set(i, newTask);
            }
        }


        List<TeamDto> list = user.getTeams();
        if(list != null) {
            for(int i=0; i< list.size();i++){
                TeamEntity teamFromRepo = teamRepo.findByName(list.get(i).getName());
                if(teamFromRepo != null){
                    list.set(i,modelMapper.map(teamFromRepo, TeamDto.class));
                }
                else{
                    TeamDto newTeam = list.get(i);
                    newTeam.setTeamId(utils.generateTaskId(20));
                    list.set(i,newTeam);
                }
            }
        }

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepo.save(userEntity);

        for (TaskEntity task : userEntity.getTasks()) {
            taskRepo.save(task);
        }


        return modelMapper.map(storedUserDetails, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepo.findByEmail(email);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userEntity, UserDto.class);

    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserEntity userEntity = userRepo.findByUserId(userId);
        if(userEntity==null)
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setIsManager(user.getIsManager());

        UserEntity updatedUser = userRepo.save(userEntity);

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(updatedUser, UserDto.class);
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
        ModelMapper modelMapper = new ModelMapper();
        if(page > 0) page-=1;

        Pageable pageable =  PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepo.findAll(pageable);
        List<UserEntity> users = usersPage.getContent();

        for(UserEntity userEntity: users)
        {
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
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

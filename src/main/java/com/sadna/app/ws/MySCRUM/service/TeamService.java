package com.sadna.app.ws.MySCRUM.service;

import com.sadna.app.ws.MySCRUM.shared.dto.TeamDto;

import java.util.List;
/**
 * Interface service for team api.
 */
public interface TeamService {
    TeamDto createTeam(TeamDto teamDto);

    TeamDto getTeamByTeamId(String teamId);

    List<TeamDto> getTeams(int page, int limit);

    TeamDto updateTeam(String id, TeamDto teamDto);

    List<TeamDto> getTeamsByUserId(String userId);

    void deleteTeam(String teamId);
}

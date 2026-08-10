package com.hardlands.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GamePhase {

    LOBBY("Lobby", false),
    SURVIVAL_GRACE_PERIOD("Survival Grace Period", false),
    SURVIVAL_PVP("Survival PvP", true),
    BORDER_SHRINK("Border Shrink", true),
    MEETUP("Meetup", true),
    DEATHMATCH("Deathmatch", true),
    FINISHED("Finished", false);

    private final String displayName;
    private final boolean pvpEnabled;
}
package io.github.pepe3012.hardlands.module.game;

public enum GamePhase {

    IDLE("Idle"),
    PRE_GAME("Pre-Game"),
    SURVIVAL("Survival"),
    MEETUP("Meetup"),
    DEATHMATCH("Deathmatch"),
    POST_GAME("Post-Game");

    private final String displayName;

    GamePhase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isRunning() {
        return this == SURVIVAL || this == MEETUP || this == DEATHMATCH;
    }

    public boolean canAdvance() {
        return this != POST_GAME;
    }
}
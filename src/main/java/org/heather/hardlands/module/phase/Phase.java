package org.heather.hardlands.module.phase;

public enum Phase {

    IDLE("Idle"),
    PRE_GAME("Pre-Game"),
    SURVIVAL("Survival"),
    MEETUP("Meetup"),
    DEATHMATCH("Deathmatch"),
    POST_GAME("Post-Game");

    private final String displayName;

    Phase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isRunning() {
        return switch (this) {
            case SURVIVAL, MEETUP, DEATHMATCH -> true;
            default -> false;
        };
    }
}

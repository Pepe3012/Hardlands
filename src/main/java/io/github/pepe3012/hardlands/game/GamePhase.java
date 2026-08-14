package io.github.pepe3012.hardlands.game;

public enum GamePhase {

    BORDER_SHRINK("Border Shrink", true),
    DEATHMATCH("Deathmatch", true),
    FINISHED("Finished", false),
    LOBBY("Lobby", false),
    MEETUP("Meetup", true),
    SURVIVAL_GRACE_PERIOD("Survival Grace Period", false),
    SURVIVAL_PVP("Survival PvP", true);

    private final String displayName;
    private final boolean pvpEnabled;

    GamePhase(String displayName, boolean pvpEnabled) {
        this.displayName = displayName;
        this.pvpEnabled = pvpEnabled;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isPvpEnabled() {
        return this.pvpEnabled;
    }

    public boolean isRunning() {
        return this != LOBBY && this != FINISHED;
    }

    public boolean canAdvance() {
        return this != LOBBY && this != FINISHED;
    }
}
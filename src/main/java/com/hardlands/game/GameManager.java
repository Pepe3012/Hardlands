package com.hardlands.game;

import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class GameManager {
    private final GameConfig config;
    private final WorldBorder worldBorder;

    private State state = State.LOBBY;

    public GameManager(@NotNull GameConfig config) {
        this.config = config;
        this.worldBorder = config.world().getWorldBorder();
    }

    public void configure() {
        this.worldBorder.setCenter(0, 0);
        this.worldBorder.setSize(this.config.initialBorderDiameter());
    }

    public void shrinkBorder() {
        this.worldBorder.changeSize(this.config.meetupBorderDiameter(), this.config.borderShrinkDurationSeconds());
    }

    public enum State {
        LOBBY("Lobby"),
        SURVIVAL("Survival"),
        MEETUP("Meetup"),
        DEATHMATCH("Deathmatch");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }
}
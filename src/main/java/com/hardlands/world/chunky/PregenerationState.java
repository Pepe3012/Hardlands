package com.hardlands.world.chunky;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PregenerationState {

    IDLE("idle", "Sin iniciar"),
    RUNNING("running", "En progreso"),
    COMPLETED("completed", "Completado");

    private final String key;
    private final String displayName;
}
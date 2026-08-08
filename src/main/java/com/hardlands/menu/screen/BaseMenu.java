package com.hardlands.menu.screen;

import com.hardlands.menu.Menu;
import com.hardlands.menu.MenuInventory;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMenu implements Menu {

    private final String displayName;
    private final Material outline;
    private final MenuInventory.Size size;
    private final @Nullable Menu parent;

    protected BaseMenu(String displayName, Material outline, MenuInventory.Size size, @Nullable Menu parent) {
        this.displayName = displayName;
        this.outline = outline;
        this.size = size;
        this.parent = parent;
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public Material outline() {
        return this.outline;
    }

    @Override
    public MenuInventory.Size size() {
        return this.size;
    }

    @Override
    public @Nullable Menu parent() {
        return this.parent;
    }
}
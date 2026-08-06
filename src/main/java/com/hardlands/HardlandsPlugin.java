package com.hardlands;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.command.HardlandsCommand;
import com.hardlands.inventory.InventoryListener;
import com.hardlands.player.PlayerListener;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.UHC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Stream;

public final class HardlandsPlugin extends JavaPlugin {

    @Getter @Setter private static HardlandsPlugin instance;

    @Getter private final ScenarioManager scenarioManager = new ScenarioManager();

    @Getter @Setter private UHC uhc;

    @Override
    public void onEnable() {
        super.getLogger().info("Initializing plugin...");

        setInstance(this);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new InventoryListener(this ), this);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        this.registerCommandCompletions(paperCommandManager);
        paperCommandManager.registerCommand(new HardlandsCommand());

        super.getLogger().info(System.lineSeparator() + """
          _    _          _____  _____  _               _   _ _____   _____
         | |  | |   /\\   |  __ \\|  __ \\| |        /\\   | \\ | |  __ \\ / ____|
         | |__| |  /  \\  | |__) | |  | | |       /  \\  |  \\| | |  | | (___
         |  __  | / /\\ \\ |  _  /| |  | | |      / /\\ \\ | . ` | |  | |\\___ \\
         | |  | |/ ____ \\| | \\ \\| |__| | |____ / ____ \\| |\\  | |__| |____) |
         |_|  |_/_/    \\_\\_|  \\_\\_____/|______/_/    \\_\\_| \\_|_____/|_____/
        """);
        super.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been successfully disabled.");
    }

    private void registerCommandCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();

        completions.registerAsyncCompletion("registered_scenarios", _ -> ScenarioType.IDS);
        completions.registerAsyncCompletion("active_scenarios", _ -> this.scenarioManager.getActiveScenarioTypes()
                .stream()
                .map(ScenarioType::getId)
                .toList());

        completions.registerAsyncCompletion("uhc_options", _ -> {
            UHC uhc = this.uhc;
            if (uhc == null) return List.of();

            return Stream.concat(
                    uhc.getOptionContainer().getOptions().keySet().stream(),
                    uhc.getWorldBorderManager().getOptionContainer().getOptions().keySet().stream()
            ).toList();
        });
    }
}
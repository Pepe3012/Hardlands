package com.hardlands;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.hardlands.command.HardlandsCommand;
import com.hardlands.listener.PlayerListener;
import com.hardlands.scenario.ScenarioManager;
import com.hardlands.scenario.ScenarioType;
import com.hardlands.uhc.UHC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardlandsPlugin extends JavaPlugin {

    public static final HardlandsPlugin INSTANCE = JavaPlugin.getPlugin(HardlandsPlugin.class);

    @Getter private final ScenarioManager scenarioManager = new ScenarioManager();

    @Getter @Setter private UHC uhc; //store the current uhc game being played, null if there are no games being played

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        this.registerCommandCompletions(paperCommandManager);
        paperCommandManager.registerCommand(new HardlandsCommand());

        super.getLogger().info("The plugin has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("The plugin has been successfully disabled.");
    }

    private void registerCommandCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();

        completions.registerAsyncCompletion("registered_scenarios", _ -> ScenarioType.IDS);
        completions.registerAsyncCompletion("active_scenarios", _ -> this.scenarioManager.getActiveScenarioTypes().stream().map(ScenarioType::getId).toList());
    }

    public static String getPlayerHeadAndName(Player player) {
        return "<white><head:%s></white> %s".formatted(player.getUniqueId(), player.getName());
    }
}
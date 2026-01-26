package dev.zedith.configure;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zedith.configure.commands.ConfigListCommand;
import dev.zedith.configure.commands.ConfigureCommand;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigMetadata;
import dev.zedith.configure.data.ExampleConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configure extends JavaPlugin {

    protected final static Map<String, WrappedConfig<?>> configs = new ConcurrentHashMap<>();

    private final WrappedConfig<ExampleConfig> config;

    public Configure(@NonNullDecl JavaPluginInit init) {
        super(init);

        config = new WrappedConfig<>(
                ExampleConfig.CODEC,
                withConfig(ExampleConfig.CODEC),
                new ConfigMetadata("Configure")
        );
        registerConfig(config);
    }

    public static void registerConfig(WrappedConfig<?> config) {
        configs.put(config.metadata().name().toLowerCase(), config);
    }

    public static void registerConfig(String name, WrappedConfig<?> config) {
        configs.put(name.toLowerCase(), config);
    }

    public static Map<String, WrappedConfig<?>> getConfigs() {
        return configs;
    }

    @Override
    protected void setup() {
        config.save();

        getCommandRegistry().registerCommand(new ConfigureCommand());
        getCommandRegistry().registerCommand(new ConfigListCommand());
    }
}

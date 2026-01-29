package dev.zedith.configure;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zedith.configure.commands.ConfigListCommand;
import dev.zedith.configure.commands.ConfigureCommand;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigMetadata;
import dev.zedith.configure.data.ExampleConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class Main extends JavaPlugin {

    private final WrappedConfig<ExampleConfig> config;

    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);

        config = new WrappedConfig<>(
                ExampleConfig.CODEC,
                withConfig(ExampleConfig.CODEC),
                new ConfigMetadata("Configure")
        );
        Configure.registerConfig(config);
    }

    @Override
    protected void setup() {
        config.save();

        getCommandRegistry().registerCommand(new ConfigureCommand());
        getCommandRegistry().registerCommand(new ConfigListCommand());
    }
}

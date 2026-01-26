package dev.zedith.configure.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.util.Config;
import dev.zedith.configure.data.ConfigMetadata;
import dev.zedith.configure.data.Save;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public final class WrappedConfig<T> {
    private final BuilderCodec<T> codec;
    private final Config<T> config;
    private final ConfigMetadata metadata;

    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    public WrappedConfig(BuilderCodec<T> codec, Config<T> config, ConfigMetadata metadata) {
        this.codec = codec;
        this.config = config;
        this.metadata = metadata;
    }

    public <R> R read(Function<? super T, ? extends R> fn) {
        var r = rw.readLock();
        r.lock();
        try {
            return fn.apply(config.get());
        } finally {
            r.unlock();
        }
    }

    public void saveFrom(T incoming) {
        var w = rw.writeLock();
        w.lock();
        try {
            Save.copyInto(config.get(), incoming);
            config.save();
        } finally {
            w.unlock();
        }
    }

    public void save() {
        var w = rw.writeLock();
        w.lock();
        try {
            config.save();
        } finally {
            w.unlock();
        }
    }

    public ConfigMetadata metadata() {
        return metadata;
    }

    public BuilderCodec<T> codec() {
        return codec;
    }
}

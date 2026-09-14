package dev.l1nd3n.kladronym.source;

public final class CachedSource<T> implements FiasSource<T> {
    private final FiasSource<T> origin;
    private T cache;

    public CachedSource(FiasSource<T> origin) {
        this.origin = origin;
    }

    @Override
    public synchronized T load() throws Exception {
        if (cache == null) {
            cache = origin.load();
        }
        return cache;
    }
}

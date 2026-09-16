package dev.l1nd3n.kladronym.source;

public final class CachedSource<T> implements FiasSource<T> {

  private final FiasSource<T> origin;
  private volatile T cache;

  public CachedSource(FiasSource<T> origin) {
    this.origin = origin;
  }

  @Override
  public T load() throws Exception {
    if (cache == null) {
      synchronized (this) {
          if (cache == null) {
              cache = origin.load();
          }
      }
    }
    return cache;
  }
}

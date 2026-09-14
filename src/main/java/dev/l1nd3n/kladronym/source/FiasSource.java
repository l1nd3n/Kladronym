package dev.l1nd3n.kladronym.source;

public interface FiasSource<T> {
    T load() throws Exception;
}

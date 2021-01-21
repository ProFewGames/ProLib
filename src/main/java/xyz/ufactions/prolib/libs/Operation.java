package xyz.ufactions.prolib.libs;

public interface Operation<K, V> {

    V execute(K k);
}
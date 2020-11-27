package com.machopiggies.famedpanic.util;

public interface Pair<F,S> {
    F getFirst();
    S getSecond();

    void setFirst(F first);
    void setSecond(S second);

    void clear();

    String toString();

    boolean equals(Pair<F,S> pair);
    boolean equalsFirst(F first);
    boolean equalsSecond(S second);
}

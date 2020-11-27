package com.machopiggies.famedpanic.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LinkedPair<F,S> implements Pair<F,S>, Cloneable, Serializable {
    private F first;
    private S second;

    public LinkedPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public F getFirst() {
        return first;
    }

    @Override
    public S getSecond() {
        return second;
    }

    @Override
    public void setFirst(F first) {
        this.first = first;
    }

    @Override
    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public void clear() {
        first = null;
        second = null;
    }

    @Override
    public LinkedPair<F,S> clone() {
        return new LinkedPair<>(first, second);
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("first", first);
        map.put("second", second);
        return map.toString();
    }

    @Override
    public boolean equals(Pair<F, S> pair) {
        return pair.getFirst().equals(first) && pair.getSecond().equals(second);
    }

    @Override
    public boolean equalsFirst(F first) {
        return first.equals(this.first);
    }

    @Override
    public boolean equalsSecond(S second) {
        return second.equals(this.second);
    }
}

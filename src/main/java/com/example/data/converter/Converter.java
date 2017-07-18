package com.example.data.converter;

/**
 *
 */
public interface Converter<T, V> {

    V convertTo(T data);

    T convertFrom(V data);
}

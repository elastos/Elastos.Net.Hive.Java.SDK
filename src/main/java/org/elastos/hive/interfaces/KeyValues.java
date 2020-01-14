package org.elastos.hive.interfaces;

import org.elastos.hive.Callback;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface KeyValues {
    CompletableFuture<Void> putValue(String key, String value);

    CompletableFuture<Void> putValue(String key, String value, Callback<Void> callback);

    CompletableFuture<Void> putValue(String key, byte[] value);

    CompletableFuture<Void> putValue(String key, byte[] value, Callback<Void> callback);

    CompletableFuture<Void> setValue(String key, String value);

    CompletableFuture<Void> setValue(String key, String value, Callback<Void> callback);

    CompletableFuture<Void> setValue(String key, byte[] value);

    CompletableFuture<Void> setValue(String key, byte[] value, Callback<Void> callback);

    CompletableFuture<ArrayList<byte[]>> getValues(String key);

    CompletableFuture<ArrayList<byte[]>> getValues(String key, Callback<ArrayList<byte[]>> callback);

    CompletableFuture<Void> delete(String key);

    CompletableFuture<Void> delete(String key, Callback<Void> callback);
}
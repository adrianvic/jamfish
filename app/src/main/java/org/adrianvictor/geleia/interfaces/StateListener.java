package org.adrianvictor.geleia.interfaces;

public interface StateListener {
    void onStatePolling();

    void onStateOnline();

    void onStateOffline();
}

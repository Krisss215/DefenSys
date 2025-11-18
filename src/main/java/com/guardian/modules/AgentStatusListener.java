package com.guardian.modules;

/**
 * Интерфейс, который реализует UI для получения статуса агента.
 * Здесь можно обновлять текст "Agent running/Agent stopped",
 * счётчики USB и сети и т.д.
 */
public interface AgentStatusListener {

    void onAgentStarted();

    void onAgentStopped();

    void onUsbStatsChanged(int devices, int blocked);

    void onNetworkStatsChanged(int connections);
}



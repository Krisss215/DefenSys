package com.guardian.monitor;

import com.guardian.logging.SecurityEventLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ExtendedMonitoringBootstrap {

    private static volatile boolean started = false;

    private ExtendedMonitoringBootstrap() {
    }

    public static synchronized void start() {
        if (started) {
            return;
        }
        started = true;

        ExecutorService executor = Executors.newCachedThreadPool();

        // наши новые сервисы
        UsbMonitorService usbMonitor = new UsbMonitorService();
        NetworkMonitorService networkMonitor = new NetworkMonitorService();
        InternetConnectivityWatcher internetWatcher = new InternetConnectivityWatcher();
        BitLockerChecker bitLockerChecker = new BitLockerChecker();
        FileActivityMonitor fileActivityMonitor = new FileActivityMonitor();
        BiWeeklyReportScheduler reportScheduler = new BiWeeklyReportScheduler();

        executor.submit(usbMonitor);
        executor.submit(networkMonitor);
        executor.submit(internetWatcher);
        executor.submit(fileActivityMonitor);

        // разовая проверка BitLocker при старте
        executor.submit(() -> {
            boolean enabled = bitLockerChecker.isBitLockerFullyEnabled();
            if (enabled) {
                SecurityEventLogger.log(
                        "BITLOCKER",
                        "BitLocker enabled on all systems tones."
                );
            } else {
                SecurityEventLogger.log(
                        "BITLOCKER",
                        "BitLocker disabled on all systems tones."
                );
            }
        });

        // планировщик отчётов раз в 2 недели
        reportScheduler.start();

        SecurityEventLogger.log("AGENT", "ExtendedMonitoringBootstrap успешно запущен.");
    }
}

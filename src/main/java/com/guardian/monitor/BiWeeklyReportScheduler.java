package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Schedules automatic security reports every 14 days.
 */
public class BiWeeklyReportScheduler {

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    private final ReportGenerator generator = new ReportGenerator();

    public void start() {
        long initialDelaySeconds = 60L; // first report after 1 minute (for testing)
        long periodSeconds = 14L * 24L * 60L * 60L; // every 14 days

        executor.scheduleAtFixedRate(
                this::generate,
                initialDelaySeconds,
                periodSeconds,
                TimeUnit.SECONDS
        );
        SecureLogManager.getInstance().logEvent(
                "REPORT",
                "BiWeeklyReportScheduler",
                "Bi-weekly report scheduler started."
        );
    }

    private void generate() {
        try {
            generator.generateReport();
        } catch (Exception e) {
            SecureLogManager.getInstance().logEvent(
                    "REPORT",
                    "BiWeeklyReportScheduler",
                    "Failed to generate scheduled report: " + e.getMessage()
            );
        }
    }
}

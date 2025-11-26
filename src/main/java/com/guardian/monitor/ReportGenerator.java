package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;
import com.guardian.logging.SecurityEventLogger;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates PDF reports from security event log.
 * - Reads SecurityEventLogger log file
 * - Filters lines for last 14 days
 * - Writes them to security-report-YYYY-MM-DD.pdf
 */
public class ReportGenerator {

    private static final DateTimeFormatter DATE_ONLY =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Generates a PDF report for the last 14 days and returns the file.
     * Returns null if log file does not exist or report failed.
     */
    public File generateReportForLast14Days() {
        File logFile = SecurityEventLogger.getLogFile();
        if (logFile == null || !logFile.exists()) {
            SecureLogManager.getInstance().logEvent(
                    "REPORT",
                    "ReportGenerator",
                    "No log file found, nothing to include in report."
            );
            return null;
        }

        try {
            List<String> allLines = Files.readAllLines(
                    logFile.toPath(), StandardCharsets.UTF_8);

            LocalDate cutoff = LocalDate.now().minusDays(14);
            List<String> filtered = filterLastDays(allLines, cutoff);

            File reportFile = new File(
                    logFile.getParentFile(),
                    "security-report-" + LocalDate.now().format(DATE_ONLY) + ".pdf"
            );

            writePdf(reportFile, filtered);

            SecureLogManager.getInstance().logEvent(
                    "REPORT",
                    "ReportGenerator",
                    "Generated PDF report: " + reportFile.getAbsolutePath()
            );

            return reportFile;
        } catch (Exception e) {
            SecureLogManager.getInstance().logEvent(
                    "REPORT",
                    "ReportGenerator",
                    "Failed to generate PDF report: " + e.getMessage()
            );
            return null;
        }
    }

    /**
     * Used by BiWeeklyReportScheduler – just delegates to generateReportForLast14Days().
     */
    public void generateReport() {
        generateReportForLast14Days();
    }

    private List<String> filterLastDays(List<String> allLines, LocalDate cutoff) {
        List<String> result = new ArrayList<>();

        for (String line : allLines) {
            if (line == null || line.length() < 10) {
                continue;
            }
            // Our log format starts with "YYYY-MM-DD ..."
            String datePart = line.substring(0, 10);
            try {
                LocalDate d = LocalDate.parse(datePart, DATE_ONLY);
                if (!d.isBefore(cutoff)) {
                    result.add(line);
                }
            } catch (Exception ignored) {
                // not a standard log line, just include it
                result.add(line);
            }
        }

        // если вдруг ничего не попало – лучше всё равно что-то написать
        if (result.isEmpty()) {
            result.add("No events in the last 14 days.");
        }

        return result;
    }

    private void writePdf(File file, List<String> lines)
            throws IOException, DocumentException {

        // Ensure folder exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Document document = new Document(PageSize.A4, 36, 36, 48, 48);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph("USB Ninja – Security Report"));
            document.add(new Paragraph("Date: " + LocalDate.now().format(DATE_ONLY)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Event log (last 14 days):"));
            document.add(new Paragraph(" "));

            for (String line : lines) {
                document.add(new Paragraph(line));
            }
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}

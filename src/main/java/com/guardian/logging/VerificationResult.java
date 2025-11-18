package com.guardian.logging;

public class VerificationResult {

    private final boolean valid;
    private final long totalEntries;
    private final Long firstBrokenIndex;
    private final String message;

    public VerificationResult(boolean valid,
                              long totalEntries,
                              Long firstBrokenIndex,
                              String message) {
        this.valid = valid;
        this.totalEntries = totalEntries;
        this.firstBrokenIndex = firstBrokenIndex;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public long getTotalEntries() {
        return totalEntries;
    }

    public Long getFirstBrokenIndex() {
        return firstBrokenIndex;
    }

    public String getMessage() {
        return message;
    }

    public String toReadableString(String logFileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Log file: ").append(logFileName).append(System.lineSeparator());
        sb.append("  valid: ").append(valid).append(System.lineSeparator());
        sb.append("  totalEntries: ").append(totalEntries).append(System.lineSeparator());
        if (firstBrokenIndex != null) {
            sb.append("  firstBrokenIndex: ").append(firstBrokenIndex).append(System.lineSeparator());
        }
        if (message != null) {
            sb.append("  message: ").append(message).append(System.lineSeparator());
        }
        return sb.toString();
    }
}

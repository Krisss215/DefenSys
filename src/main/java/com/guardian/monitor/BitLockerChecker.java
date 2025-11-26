package com.guardian.monitor;

import com.guardian.logging.SecureLogManager;

import java.io.IOException;

/**
 * Checks BitLocker status using PowerShell.
 */
public class BitLockerChecker {

    /**
     * Returns true if BitLocker is fully enabled on all OS volumes.
     */
    public boolean isBitLockerFullyEnabled() {
        try {
            String output = PowerShellHelper.run(
                    "Get-BitLockerVolume | " +
                            "Where-Object { $_.VolumeType -eq 'OperatingSystem' } | " +
                            "Select-Object -ExpandProperty ProtectionStatus"
            );

            // ProtectionStatus: 0 = Off, 1 = On, 2 = On with TPM, etc.
            String[] lines = output.split("\\r?\\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if ("0".equals(trimmed)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            SecureLogManager.getInstance().logEvent(
                    "BITLOCKER",
                    "BitLockerChecker",
                    "Failed to query BitLocker status: " + e.getMessage()
            );
            return false;
        }
    }
}

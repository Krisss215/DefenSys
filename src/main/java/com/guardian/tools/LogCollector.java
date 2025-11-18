package com.guardian.tools;

import com.guardian.logging.SecureLogManager;
import com.guardian.logging.VerificationResult;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogCollector {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java -jar LogCollector.jar <logsDir> <outputEncryptedFile> <password>");
            System.out.println("Example: java -jar LogCollector.jar C:\\SA\\logs C:\\USB\\sa-logs.enc MyStrongPass123");
            return;
        }

        Path logsDir = Paths.get(args[0]);
        Path outputEncryptedFile = Paths.get(args[1]);
        String password = args[2];

        if (!Files.isDirectory(logsDir)) {
            System.err.println("Logs directory does not exist: " + logsDir);
            return;
        }

        Path tempZip = Files.createTempFile("sa-logs-", ".zip");
        System.out.println("Creating temporary zip: " + tempZip);

        try {
            String verificationReport = createZipWithLogsAndReport(logsDir, tempZip);
            encryptFileAes(tempZip, outputEncryptedFile, password);
            System.out.println("Encrypted log archive created: " + outputEncryptedFile);
            System.out.println("----- Verification report -----");
            System.out.println(verificationReport);
        } finally {
            try {
                Files.deleteIfExists(tempZip);
            } catch (IOException ignored) {
            }
        }
    }

    private static String createZipWithLogsAndReport(Path logsDir, Path zipFile) throws IOException {
        StringBuilder reportBuilder = new StringBuilder();

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFile));
             Stream<Path> paths = Files.walk(logsDir)) {

            Iterator<Path> it = paths.iterator();
            while (it.hasNext()) {
                Path path = it.next();
                if (Files.isDirectory(path)) {
                    continue;
                }

                Path relative = logsDir.relativize(path);
                String entryName = relative.toString().replace("\\", "/");

                ZipEntry entry = new ZipEntry(entryName);
                zipOut.putNextEntry(entry);
                Files.copy(path, zipOut);
                zipOut.closeEntry();

                if (entryName.endsWith(".log")) {
                    VerificationResult result = SecureLogManager.verifyLogFile(path);
                    reportBuilder.append(result.toReadableString(entryName));
                    reportBuilder.append(System.lineSeparator());
                }
            }

            ZipEntry reportEntry = new ZipEntry("verification_report.txt");
            zipOut.putNextEntry(reportEntry);
            byte[] reportBytes = reportBuilder.toString().getBytes(StandardCharsets.UTF_8);
            zipOut.write(reportBytes);
            zipOut.closeEntry();
        }

        return reportBuilder.toString();
    }

    private static void encryptFileAes(Path inputFile,
                                       Path outputFile,
                                       String password) throws Exception {
        if (outputFile.getParent() != null) {
            Files.createDirectories(outputFile.getParent());
        }

        byte[] keyBytes = deriveKeyFromPassword(password);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        try (OutputStream fileOut = new BufferedOutputStream(Files.newOutputStream(outputFile));
             FileInputStream fis = new FileInputStream(inputFile.toFile());
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {

            // сначала пишем IV, чтобы в будущем можно было расшифровать
            fileOut.write(iv);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                cipherOut.write(buffer, 0, read);
            }
        }
    }

    private static byte[] deriveKeyFromPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        byte[] key = new byte[16];
        System.arraycopy(hash, 0, key, 0, 16); // AES-128
        return key;
    }
}

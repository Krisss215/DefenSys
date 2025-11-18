package com.guardian.modules;

import java.nio.file.Path;

/**
 * Интерфейс для сканера файлов.
 */
public interface FileScanner {

    void scanPath(Path root);

    void scanFile(Path file);
}

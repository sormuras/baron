package de.sormuras.baron;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Defines a group of modules or a source set.
 *
 * <p>Like {@code main} or {@code test} in Maven.
 */
public interface Group {

  Path destination();

  List<Path> modulePath();

  List<Path> moduleSourcePath();

  String name();

  Map<String, String> mainClass();

  Map<String, List<Path>> patchModule();
}

package de.sormuras.baron;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Source set, like {@code main} or {@code test}. */
public interface ModuleGroup {

  Path destination();

  List<Path> modulePath();

  List<Path> moduleSourcePath();

  String name();

  Map<String, List<Path>> patchModule();
}

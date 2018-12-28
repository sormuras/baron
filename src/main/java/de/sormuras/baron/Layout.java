package de.sormuras.baron;

import static java.lang.System.Logger.Level.WARNING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/** Source directory module tree layout/scheme. */
public enum Layout {
  /** Auto-detect at configuration time. */
  AUTO,

  /** Module folder first, no tests: {@code <module>/module-info.java} */
  BASIC,

  /** Source set folder first, no module name: {@code [main|test|...]/java/module-info.java} */
  MAVEN;

  private static final System.Logger LOG = System.getLogger(Layout.class.getName());

  public static Layout of(Path root) {
    if (Files.notExists(root)) {
      return AUTO;
    }
    try {
      var path =
          Files.find(root, 10, (p, a) -> p.endsWith("module-info.java"))
              .map(root::relativize)
              .findFirst()
              .orElseThrow(() -> new AssertionError("no module descriptor found in " + root));
      var name = readModuleName(Files.readString(root.resolve(path)));
      if (path.getNameCount() == 2) {
        if (!path.startsWith(name)) {
          LOG.log(WARNING, "expected path to start with '%s': %s", name, path);
        }
        return BASIC;
      }
      if (path.getNameCount() == 3) {
        if (!path.getParent().endsWith("java")) {
          LOG.log(WARNING, "expected module-info.java to be directory named 'java': %s", path);
        }
        return MAVEN;
      }
    } catch (Exception e) {
      throw new Error("detection failed " + e, e);
    }

    throw new UnsupportedOperationException("can't detect layout for " + root);
  }

  static String readModuleName(String moduleSource) {
    var namePattern = Pattern.compile("(module)\\s+(.+)\\s*\\{.*");
    var nameMatcher = namePattern.matcher(moduleSource);
    if (!nameMatcher.find()) {
      throw new IllegalArgumentException(
          "expected java module descriptor unit, but got: \n" + moduleSource);
    }
    return nameMatcher.group(2).trim();
  }
}

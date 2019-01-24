package de.sormuras.baron;

import static java.lang.System.Logger.Level.WARNING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/** Source directory module tree layout. */
public enum Layout {
  /**
   * Module descriptor resides in folder with same name as the module.
   *
   * <ul>
   *   <li>Pattern: {@code <root>/<module name>/module-info.java}
   *   <li>Example: {@code src/com.greetings/module-info.java} containing {@code module
   *       com.greetings {...}}
   * </ul>
   */
  BASIC,

  /**
   * Module group folder first and no module name but "java" in the directory hierarchy.
   *
   * <ul>
   *   <li>Pattern: {@code {@code <root>/[main|test|...]/java/module-info.java}}
   *   <li>Example: {@code src/main/java/module-info.java} containing {@code module com.greetings
   *       {...}}
   * </ul>
   */
  MAVEN {
    @Override
    public Path resolveModuleSourcePath(Path root, String groupName) {
      return root.resolve(groupName).resolve("java");
    }
  };

  private static final System.Logger LOG = System.getLogger(Layout.class.getName());

  private static final Pattern MODULE_NAME_PATTERN = Pattern.compile("(module)\\s+(.+)\\s*\\{.*");

  public static Layout of(Path root) {
    if (Files.notExists(root)) {
      throw new IllegalArgumentException("root path must exist: " + root);
    }
    if (!Files.isDirectory(root)) {
      throw new IllegalArgumentException("root path must be a directory: " + root);
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

      throw new UnsupportedOperationException(
          "can't detect layout for " + root + " -- found module " + name + " in " + path);
    } catch (Exception e) {
      throw new Error("detection failed " + e, e);
    }
  }

  static String readModuleName(String moduleSource) {
    var nameMatcher = MODULE_NAME_PATTERN.matcher(moduleSource);
    if (!nameMatcher.find()) {
      throw new IllegalArgumentException(
          "expected java module descriptor unit, but got: \n" + moduleSource);
    }
    return nameMatcher.group(2).trim();
  }

  public Path resolveModuleSourcePath(Path root, String groupName) {
    return root;
  }
}

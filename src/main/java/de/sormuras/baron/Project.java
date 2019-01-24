package de.sormuras.baron;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

/** Project build support. */
public interface Project {

  static ProjectBuilder builder() {
    return new ProjectBuilder(Path.of("."));
  }

  static ProjectBuilder builder(Path root, Path... groups) {
    if (!Files.isDirectory(root)) {
      throw new IllegalArgumentException("root path must be a directory: " + root);
    }
    var builder = new ProjectBuilder(root);
    builder.name(root.getFileName().toString());
    var destination = builder.target().resolve("modules");
    for (var group : groups) {
      var groupName = group.getFileName().toString();
      var groupLayout = Layout.of(root.resolve(group));
      var groupDestination = groups.length == 1 ? destination : destination.resolve(groupName);
      var groupBuilder = builder.groupBuilder(groupName);
      groupBuilder.destination(groupDestination);
      groupBuilder.moduleSourcePath(List.of(groupLayout.resolveModuleSourcePath(group, groupName)));
      groupBuilder.buildGroup();
    }
    return builder;
  }

  static Project of(Path root, Path... groups) {
    return builder(root, groups).buildProject();
  }

  Path root();

  default Group group(String name) {
    if (!groups().containsKey(name)) {
      throw new NoSuchElementException("group with name `" + name + "` not found");
    }
    return groups().get(name);
  }

  Map<String, Group> groups();

  String name();

  Path target();

  String version();

  default Set<String> modules() {
    Set<String> modules = new TreeSet<>();
    for (var group : groups().values()) {
      for (var path : group.moduleSourcePath()) {
        var start = root().resolve(path);
        try {
          Files.find(start, 10, (p, a) -> p.endsWith("module-info.java"))
              .map(Project::readString)
              .map(Layout::readModuleName)
              .forEach(modules::add);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
    return modules;
  }

  private static String readString(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

package de.sormuras.baron;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** Project build support. */
public interface Project {

  static ProjectBuilder builder() {
    return new ProjectBuilder();
  }

  static ProjectBuilder builder(Path root, Path... groups) {
    if (!Files.isDirectory(root)) {
      throw new IllegalArgumentException("root path must be a directory: " + root);
    }
    var builder = new ProjectBuilder();
    builder.name(root.getFileName().toString());
    var destination = builder.target().resolve("modules");
    for (var group : groups) {
      var groupName = group.getFileName().toString();
      var groupLayout = Layout.of(root.resolve(group));
      var groupDestination = groups.length == 1 ? destination : destination.resolve(groupName);
      var groupBuilder = builder.groupBegin(groupName);
      groupBuilder.destination(groupDestination);
      groupBuilder.moduleSourcePath(List.of(groupLayout.resolveModuleSourcePath(group, groupName)));
      groupBuilder.end();
    }
    return builder;
  }

  static Project of(Path root, Path... groups) {
    return builder(root, groups).build();
  }

  default Group moduleGroup(String name) {
    if (!moduleGroups().containsKey(name)) {
      throw new NoSuchElementException("ModuleGroup with name `" + name + "` not found");
    }
    return moduleGroups().get(name);
  }

  Map<String, Group> moduleGroups();

  String name();

  Path target();

  String version();
}

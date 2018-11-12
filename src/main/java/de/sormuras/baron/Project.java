package de.sormuras.baron;

import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;

/** Project build support. */
public interface Project {

  static ProjectBuilder builder() {
    return new ProjectBuilder();
  }

  String entryPoint();

  default ModuleGroup moduleGroup(String name) {
    if (!moduleGroups().containsKey(name)) {
      throw new NoSuchElementException("ModuleGroup with name `" + name + "` not found");
    }
    return moduleGroups().get(name);
  }

  Map<String, ModuleGroup> moduleGroups();

  String name();

  Path target();

  String version();
}

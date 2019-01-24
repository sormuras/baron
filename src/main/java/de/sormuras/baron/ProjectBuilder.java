package de.sormuras.baron;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/** A mutable project. */
public class ProjectBuilder implements Project {

  private final Path root;
  private String name;
  private String version;
  private Path target;
  private Map<String, Group> groups;

  ProjectBuilder(Path root) {
    this.root = root.normalize().toAbsolutePath();
    this.name = this.root.getFileName().toString();
    this.target = this.root.resolve("target").resolve("bach");
    this.version = "1.0.0-SNAPSHOT";
    this.groups = new TreeMap<>();
  }

  public Project buildProject() {
    return this;
  }

  public GroupBuilder groupBuilder(String name) {
    if (groups.containsKey(name)) {
      throw new IllegalArgumentException(name + " already defined");
    }
    return new GroupBuilder(this, name);
  }

  @Override
  public Map<String, Group> groups() {
    return groups;
  }

  @Override
  public String name() {
    return name;
  }

  public ProjectBuilder name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public Path root() {
    return root;
  }

  @Override
  public Path target() {
    return target;
  }

  public ProjectBuilder target(Path target) {
    this.target = target;
    return this;
  }

  @Override
  public String version() {
    return version;
  }

  public ProjectBuilder version(String version) {
    this.version = version;
    return this;
  }
}

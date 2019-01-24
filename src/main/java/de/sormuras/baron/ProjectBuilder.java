package de.sormuras.baron;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/** A mutable project. */
public class ProjectBuilder implements Project {

  private String name = Path.of(".").toAbsolutePath().normalize().getFileName().toString();
  private String version = "1.0.0-SNAPSHOT";
  private Path target = Path.of("target", "bach");
  private Map<String, Group> groups = new TreeMap<>();

  public Project build() {
    return this;
  }

  public GroupBuilder groupBegin(String name) {
    if (groups.containsKey(name)) {
      throw new IllegalArgumentException(name + " already defined");
    }
    return new GroupBuilder(this, name);
  }

  @Override
  public Map<String, Group> moduleGroups() {
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

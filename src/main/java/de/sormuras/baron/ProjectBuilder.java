package de.sormuras.baron;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/** A mutable project. */
public class ProjectBuilder implements Project {

  private String name = Path.of(".").toAbsolutePath().normalize().getFileName().toString();
  private String version = "1.0.0-SNAPSHOT";
  private String entryPoint = "";
  private Path target = Path.of("target", "bach");
  private Map<String, ModuleGroup> moduleGroups = new TreeMap<>();

  public Project build() {
    return this;
  }

  @Override
  public String entryPoint() {
    return entryPoint;
  }

  public ProjectBuilder entryPoint(String entryPoint) {
    this.entryPoint = entryPoint;
    return this;
  }

  public ProjectBuilder entryPoint(String mainModule, String mainClass) {
    return entryPoint(mainModule + '/' + mainClass);
  }

  public ModuleGroupBuilder moduleGroupBegin(String name) {
    if (moduleGroups.containsKey(name)) {
      throw new IllegalArgumentException(name + " already defined");
    }
    return new ModuleGroupBuilder(this, name);
  }

  @Override
  public Map<String, ModuleGroup> moduleGroups() {
    return moduleGroups;
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

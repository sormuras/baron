package de.sormuras.baron;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** A mutable source set. */
public class GroupBuilder implements Group {

  private final String name;
  private Path destination;
  private List<Path> modulePath;
  private List<Path> moduleSourcePath;
  private Map<String, List<Path>> patchModule = Map.of();
  private Map<String, String> mainClass = Map.of();

  private final ProjectBuilder projectBuilder;

  GroupBuilder(ProjectBuilder projectBuilder, String name) {
    this.projectBuilder = projectBuilder;
    this.name = name;
    this.destination = projectBuilder.target().resolve(Path.of(name, "modules"));
    this.modulePath = List.of();
    this.moduleSourcePath = List.of(Path.of("src", name, "java"));
  }

  public ProjectBuilder buildGroup() {
    projectBuilder.groups().put(name, this);
    return projectBuilder;
  }

  @Override
  public Path destination() {
    return destination;
  }

  public GroupBuilder destination(Path destination) {
    this.destination = destination;
    return this;
  }

  @Override
  public List<Path> modulePath() {
    return modulePath;
  }

  public GroupBuilder modulePath(List<Path> modulePath) {
    this.modulePath = modulePath;
    return this;
  }

  @Override
  public List<Path> moduleSourcePath() {
    return moduleSourcePath;
  }

  public GroupBuilder moduleSourcePath(List<Path> moduleSourcePath) {
    this.moduleSourcePath = moduleSourcePath;
    return this;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Map<String, List<Path>> patchModule() {
    return patchModule;
  }

  public GroupBuilder patchModule(Map<String, List<Path>> patchModule) {
    this.patchModule = patchModule;
    return this;
  }

  @Override
  public Map<String, String> mainClass() {
    return mainClass;
  }

  public GroupBuilder mainClass(Map<String, String> mainClass) {
    this.mainClass = mainClass;
    return this;
  }
}

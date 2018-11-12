package de.sormuras.baron;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** A mutable source set. */
public class ModuleGroupBuilder implements ModuleGroup {

  private final String name;
  private Path destination;
  private List<Path> modulePath;
  private List<Path> moduleSourcePath;
  private Map<String, List<Path>> patchModule = Map.of();

  private final ProjectBuilder projectBuilder;

  ModuleGroupBuilder(ProjectBuilder projectBuilder, String name) {
    this.projectBuilder = projectBuilder;
    this.name = name;
    this.destination = projectBuilder.target().resolve(Path.of(name, "modules"));
    this.modulePath = List.of();
    this.moduleSourcePath = List.of(Path.of("src", name, "java"));
  }

  public ProjectBuilder end() {
    projectBuilder.moduleGroups().put(name, this);
    return projectBuilder;
  }

  @Override
  public Path destination() {
    return destination;
  }

  public ModuleGroupBuilder destination(Path destination) {
    this.destination = destination;
    return this;
  }

  @Override
  public List<Path> modulePath() {
    return modulePath;
  }

  public ModuleGroupBuilder modulePath(List<Path> modulePath) {
    this.modulePath = modulePath;
    return this;
  }

  @Override
  public List<Path> moduleSourcePath() {
    return moduleSourcePath;
  }

  public ModuleGroupBuilder moduleSourcePath(List<Path> moduleSourcePath) {
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

  public ModuleGroupBuilder patchModule(Map<String, List<Path>> patchModule) {
    this.patchModule = patchModule;
    return this;
  }
}

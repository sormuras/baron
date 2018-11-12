package de.sormuras.baron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ProjectTests {

  private final Path dependencies = Paths.get("dependencies");
  private final Path target = Paths.get("target");
  private final Path mainDestination = target.resolve(Paths.get("main", "mods"));
  private final Path testDestination = target.resolve(Paths.get("test", "mods"));

  @Test
  void creatingModuleGroupWithSameNameFails() {
    Exception e =
        assertThrows(
            IllegalArgumentException.class,
            () -> Project.builder().moduleGroupBegin("name").end().moduleGroupBegin("name"));
    assertEquals("name already defined", e.getMessage());
  }

  @Test
  void defaults() {
    var project = Project.builder().build();
    assertEquals("baron", project.name());
    assertEquals("1.0.0-SNAPSHOT", project.version());
    assertEquals("", project.entryPoint());
    assertEquals(Paths.get("target", "bach"), project.target());
    assertEquals(0, project.moduleGroups().size());
    assertThrows(NoSuchElementException.class, () -> project.moduleGroup("main"));
    assertThrows(NoSuchElementException.class, () -> project.moduleGroup("test"));
  }

  @Test
  void manual() {
    var project =
        Project.builder()
            .name("Manual")
            .version("II")
            .entryPoint("main.module", "main.module.MainClass")
            .target(target)
            // main
            .moduleGroupBegin("main")
            .destination(mainDestination)
            .moduleSourcePath(List.of(Paths.get("src", "main", "java")))
            .end()
            // test
            .moduleGroupBegin("test")
            .destination(testDestination)
            .moduleSourcePath(List.of(Paths.get("src", "test", "java")))
            .modulePath(List.of(mainDestination, dependencies))
            .patchModule(Map.of("hello", List.of(Paths.get("src/main/java/hello"))))
            .end()
            // done
            .build();
    assertEquals("Manual", project.name());
    assertEquals("II", project.version());
    assertEquals("main.module/main.module.MainClass", project.entryPoint());
    assertEquals(Paths.get("target"), project.target());
    assertEquals("main", project.moduleGroup("main").name());
    assertEquals("test", project.moduleGroup("test").name());
    assertEquals(2, project.moduleGroups().size());

    var main = project.moduleGroup("main");
    assertEquals("main", main.name());
    assertEquals(mainDestination, main.destination());
    assertEquals(List.of(Paths.get("src", "main", "java")), main.moduleSourcePath());
    assertTrue(main.modulePath().isEmpty());
    assertTrue(main.patchModule().isEmpty());

    var test = project.moduleGroup("test");
    assertEquals("test", test.name());
    assertEquals(testDestination, test.destination());
    assertEquals(List.of(Paths.get("src", "test", "java")), test.moduleSourcePath());
    assertEquals(List.of(mainDestination, dependencies), test.modulePath());
    assertEquals(List.of(Paths.get("src/main/java/hello")), test.patchModule().get("hello"));
  }
}

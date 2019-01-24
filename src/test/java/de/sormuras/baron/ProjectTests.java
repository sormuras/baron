package de.sormuras.baron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ProjectTests {

  private final Path dependencies = Path.of("dependencies");
  private final Path target = Path.of("target");
  private final Path mainDestination = target.resolve(Path.of("main", "mods"));
  private final Path testDestination = target.resolve(Path.of("test", "mods"));

  @Test
  void creatingModuleGroupWithSameNameFails() {
    Exception e =
        assertThrows(
            IllegalArgumentException.class,
            () -> Project.builder().groupBegin("name").end().groupBegin("name"));
    assertEquals("name already defined", e.getMessage());
  }

  @Test
  void defaults() {
    var project = Project.builder().build();
    assertEquals("baron", project.name());
    assertEquals("1.0.0-SNAPSHOT", project.version());
    assertEquals(Path.of("target", "bach"), project.target());
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
            .target(target)
            // main
            .groupBegin("main")
            .destination(mainDestination)
            .moduleSourcePath(List.of(Path.of("src", "main", "java")))
            .mainClass(Map.of("foo", "foo.Main"))
            .end()
            // test
            .groupBegin("test")
            .destination(testDestination)
            .moduleSourcePath(List.of(Path.of("src", "test", "java")))
            .modulePath(List.of(mainDestination, dependencies))
            .patchModule(Map.of("hello", List.of(Path.of("src/main/java/hello"))))
            .end()
            // done
            .build();
    assertEquals("Manual", project.name());
    assertEquals("II", project.version());
    assertEquals(Path.of("target"), project.target());
    assertEquals("main", project.moduleGroup("main").name());
    assertEquals("test", project.moduleGroup("test").name());
    assertEquals(2, project.moduleGroups().size());

    var main = project.moduleGroup("main");
    assertEquals("main", main.name());
    assertEquals(mainDestination, main.destination());
    assertEquals("foo.Main", main.mainClass().get("foo"));
    assertEquals(List.of(Path.of("src", "main", "java")), main.moduleSourcePath());
    assertTrue(main.modulePath().isEmpty());
    assertTrue(main.patchModule().isEmpty());

    var test = project.moduleGroup("test");
    assertEquals("test", test.name());
    assertEquals(testDestination, test.destination());
    assertTrue(test.mainClass().isEmpty());
    assertEquals(List.of(Path.of("src", "test", "java")), test.moduleSourcePath());
    assertEquals(List.of(mainDestination, dependencies), test.modulePath());
    assertEquals(List.of(Path.of("src/main/java/hello")), test.patchModule().get("hello"));
  }

  @Test
  void demoJigsawQuickStartGreetings() {
    var expected =
        Project.builder()
            .name("greetings")
            .version("1.0.0-SNAPSHOT")
            .target(Path.of("target", "bach"))
            .groupBegin("src")
            .destination(Path.of("target", "bach", "modules"))
            .moduleSourcePath(List.of(Path.of("src")))
            .end()
            .build();

    var root = Path.of("demo", "jigsaw-quick-start", "greetings");
    var project = Project.of(root, Path.of("src"));

    assertEquals(expected.name(), project.name());
    assertEquals(expected.version(), project.version());
    assertEquals(expected.target(), project.target());
    assertEquals(expected.moduleGroups().size(), project.moduleGroups().size());

    var src = project.moduleGroup("src");
    assertEquals("src", src.name());
    assertEquals(Path.of("target", "bach", "modules"), src.destination());
    assertEquals(List.of(Path.of("src")), src.moduleSourcePath());
    assertTrue(src.modulePath().isEmpty());
    assertTrue(src.patchModule().isEmpty());
  }
}

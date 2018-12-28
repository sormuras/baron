package de.sormuras.baron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class LayoutTests {

  private final Path demo = Path.of("demo");

  @TestFactory
  Stream<DynamicTest> readModuleName() {
    return Stream.of("module a {", "open module a{", "what ever \r\n prefix module    a   \n{")
        .map(
            src ->
                dynamicTest(
                    src,
                    () -> {
                      String actual = Layout.readModuleName(src);
                      assertEquals("a", actual);
                    }));
  }

  @Test
  void checkJigsawQuickStart() {
    var root = demo.resolve("jigsaw-quick-start");
    assertEquals(Layout.BASIC, Layout.of(root.resolve("greetings/src")));
    assertEquals(Layout.BASIC, Layout.of(root.resolve("greetings-world/src")));
    assertEquals(
        Layout.BASIC, Layout.of(root.resolve("greetings-world-with-main-and-test/src/main")));
    assertEquals(
        Layout.BASIC, Layout.of(root.resolve("greetings-world-with-main-and-test/src/test")));
  }

  @Test
  void checkMavenProjects() {
    var root = demo.resolve("maven");
    assertEquals(Layout.MAVEN, Layout.of(root.resolve("maven-archetype-quickstart/src")));
  }
}

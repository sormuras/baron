package de.sormuras.baron;

import static de.sormuras.baron.Layout.BASIC;
import static de.sormuras.baron.Layout.MAVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    return Stream.of(
            "module a {",
            "open module a{",
            "what ever \r\n prefix module    a   \n{",
            "/**\n * Comment with module literal.\n */ module a {")
        .map(src -> dynamicTest(src, () -> assertModuleNameIs("a", src)));
  }

  @Test
  void readModuleNameReturnsWrongNameWithContrivedComment() {
    var src = "/**\n * Some module literal {@code followed} by a curly bracket.\n */ module a {";
    assertModuleNameIs("literal", src);
  }

  @Test
  void readModuleNameFailsForNonModuleDescriptorSourceUnit() {
    String source = "enum E {}";
    Exception e = assertThrows(Exception.class, () -> assertModuleNameIs("b", source));
    assertEquals(IllegalArgumentException.class, e.getClass());
    assertEquals("expected java module descriptor unit, but got: \n" + source, e.getMessage());
  }

  private void assertModuleNameIs(String expected, String source) {
    assertEquals(expected, Layout.readModuleName(source));
  }

  @TestFactory
  Stream<DynamicTest> checkJigsawQuickStartContainsOnlyBasicLayout() {
    var root = demo.resolve("jigsaw-quick-start");
    return Stream.of(
            "greetings/src",
            "greetings-world/src",
            "greetings-world-with-main-and-test/src/main",
            "greetings-world-with-main-and-test/src/test")
        .map(path -> dynamicTest(path, () -> assertEquals(BASIC, Layout.of(root.resolve(path)))));
  }

  @Test
  void checkMavenProjects() {
    var root = demo.resolve("maven");
    assertEquals(MAVEN, Layout.of(root.resolve("maven-archetype-quickstart/src")));
  }
}

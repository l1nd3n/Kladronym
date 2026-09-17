package dev.l1nd3n.kladronym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.Aliases;
import dev.l1nd3n.kladronym.text.PreprocessedAddress;
import dev.l1nd3n.kladronym.text.TokenType;
import dev.l1nd3n.kladronym.text.TypeMatch;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

final class AbbreviationsTest {

  @Test
  void indexesFullAndShortFormsWithTheSameNormalization() throws Exception {
    Abbreviations types = BundledData.ABBREVIATIONS.load();
    assertEquals(List.of("Республика"), types.find("РЕСП.").map(List::copyOf).orElseThrow());
    assertEquals(List.of("Городской округ"), types.find("Г.О.").map(List::copyOf).orElseThrow());
    assertEquals(List.of("Город"), types.find("город").map(List::copyOf).orElseThrow());
    assertEquals(List.of("Поселение", "Поселок"), types.find("п.").map(List::copyOf).orElseThrow());
    assertEquals(List.of("Промзона", "Промышленная зона"),
        types.find("промзона").map(List::copyOf).orElseThrow());
    assertTrue(types.find("неизвестное обозначение").isEmpty());
  }

  @Test
  void ownsAnOrderedSnapshotOfInputCollections() {
    var fullNames = new LinkedHashSet<>(List.of("Поселение", "Поселок"));
    var input = new LinkedHashMap<String, Set<String>>();
    input.put("п", fullNames);
    var types = new Abbreviations(input);
    fullNames.clear();
    input.clear();
    assertEquals(List.of("Поселение", "Поселок"), types.find("п").map(List::copyOf).orElseThrow());
    assertThrows(UnsupportedOperationException.class, () -> types.find("п").orElseThrow().clear());
  }

  @Test
  void recognizesLongestSpellingAndKeepsCanonicalAmbiguity() throws Exception {
    var types = BundledData.ABBREVIATIONS.load();
    var aliases = new Aliases(types);
    assertEquals(new TypeMatch("г.о.", 2),
        aliases.match(List.of("г", "о", "пушкинский"), 0).orElseThrow());
    assertEquals(3, aliases.match(List.of("ж", "д", "ст", "лесная"), 0).orElseThrow().length());
    var tokens = new PreprocessedAddress("поселение Лесной", aliases).get();
    assertEquals(2, tokens.size());
    assertSame(TokenType.ABBREVIATION, tokens.getFirst().type());
    assertEquals("п", tokens.getFirst().value());
    assertEquals(List.of("Поселение", "Поселок"),
        types.find(tokens.getFirst().value()).map(List::copyOf).orElseThrow());
    assertEquals(TokenType.TEXT, tokens.getLast().type());
    assertEquals("лесной", tokens.getLast().value());
    assertEquals(TokenType.TEXT,
        new PreprocessedAddress("неизвестное", aliases).get().getFirst().type());
  }

  @Test
  void supportsConcurrentFirstUseOfTheSameTree() throws Exception {
    var aliases = new Aliases(BundledData.ABBREVIATIONS.load());
    var ready = new CountDownLatch(8);
    var start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(8)) {
      var tasks = new ArrayList<java.util.concurrent.Future<TypeMatch>>();
      for (int index = 0; index < 8; index++) {
        tasks.add(executor.submit(() -> {
          ready.countDown();
          start.await();
          TypeMatch match = null;
          for (int attempt = 0; attempt < 100; attempt++) {
            match = aliases.match(List.of("г", "о", "пушкинский"), 0).orElseThrow();
          }
          return match;
        }));
      }
      boolean allReady = ready.await(5, java.util.concurrent.TimeUnit.SECONDS);
      start.countDown();
      assertTrue(allReady);
      for (var task : tasks) {
        assertEquals(new TypeMatch("г.о.", 2), task.get());
      }
    } finally {
      start.countDown();
    }
  }

  @Test
  void toponymStoresDesignationAndDelegatesComparison() throws Exception {
    var query = new Toponym("Саратов", "г.");
    var candidate = new Toponym("Саратов", "город");
    var match = new StandardToponymMatch(new NormalizedPrefixMatch(),
        BundledData.ABBREVIATIONS.load());
    assertEquals("г.", query.type());
    assertTrue(query.matches(candidate, match));
    assertFalse(query.matches(new Toponym("Саратов", "обл"), match));
    assertTrue(query.matches(new Toponym("Саратов"), match));
    assertTrue(new Toponym("Саратов").matches(candidate, match));
    assertFalse(query.matches(new Toponym("Москва", "г"), match));
    var unknown = new Toponym("Имя", "неизвестный тип");
    assertEquals("неизвестный тип", unknown.type());
    assertTrue(unknown.matches(candidate, (left, right) -> left == unknown && right == candidate));
  }
}

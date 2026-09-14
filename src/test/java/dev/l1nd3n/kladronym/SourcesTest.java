package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.source.CachedSource;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.Aliases;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

final class SourcesTest {
    @TempDir
    Path directory;

    @Test
    void readsCustomSourcesLazilyAndRetainsKladrType() throws Exception {
        Path typesFile = directory.resolve("socr.tsv");
        Path kladrFile = directory.resolve("kladr.tsv");
        var typesSource = new SocrSource(typesFile);
        var kladrSource = new KladrSource(kladrFile);
        var resolver = new LikelyKladronym("город Тестов", new NormalizedPrefixMatch(), typesSource, kladrSource);
        Files.writeString(typesFile, "scname\tsocrname\nг\tГород\nг.\tГород\n");
        Files.writeString(kladrFile, "name\tsocr\tcode\nТестов\tг\t64000001000\n");
        Kladronym result = resolver.find().orElseThrow();
        assertEquals(new KladrCode("64000001000"), result.code());
        assertEquals("г", result.toponym().type());
        assertEquals("Тестов (Город)", new FormattedKladronym(result, typesSource.load()).get());
    }

    @Test
    void cachingIsPerInstanceAndLoadsOnlyOnceAcrossThreads() throws Exception {
        var calls = new AtomicInteger();
        var value = new Object();
        var cached = new CachedSource<>(() -> {
            calls.incrementAndGet();
            return value;
        });
        assertEquals(0, calls.get());
        try (var executor = Executors.newFixedThreadPool(8)) {
            var results = new ArrayList<java.util.concurrent.Future<Object>>();
            for (int index = 0; index < 32; index++) results.add(executor.submit(cached::load));
            for (var result : results) assertSame(value, result.get());
        }
        assertEquals(1, calls.get());
        var otherValue = new Object();
        assertSame(otherValue, new CachedSource<>(() -> otherValue).load());
    }

    @Test
    void sourceFailureKeepsCauseAndCanBeRetried() {
        var calls = new AtomicInteger();
        var failure = new IOException("unavailable");
        var cached = new CachedSource<Abbreviations>(() -> {
            if (calls.incrementAndGet() == 1) throw failure;
            return new Abbreviations(Map.of("г", Set.of("Город")));
        });
        var kladr = new Kladr(List.of(new Kladronym(new Toponym("Тестов", "г"), new KladrCode("64000001000"))));
        var resolver = new LikelyKladronym("г Тестов", new NormalizedPrefixMatch(), cached, () -> kladr);
        assertEquals(0, calls.get());
        var thrown = assertThrows(IllegalStateException.class, resolver::find);
        assertSame(failure, thrown.getCause());
        assertTrue(thrown.getMessage().contains("abbreviations"));
        assertTrue(resolver.find().isPresent());
        assertEquals(2, calls.get());
    }

    @Test
    void preservesCanonicalChoiceAcrossInterleavedRows() throws Exception {
        Path file = directory.resolve("interleaved.tsv");
        Files.writeString(file, "scname\tsocrname\nа\tПервый\nб\tВторой\nа\tВторой\n");
        var types = new SocrSource(file).load();
        assertEquals(List.of("Первый", "Второй"), types.find("а").map(List::copyOf).orElseThrow());
        assertEquals("б", new Aliases(types).match(List.of("второй"), 0).orElseThrow().spelling());
    }

    @Test
    void resolverUsesSuppliedSourcesOnEachCall() {
        var typeCalls = new AtomicInteger();
        var kladrCalls = new AtomicInteger();
        var types = new Abbreviations(Map.of("г", Set.of("Город")));
        var kladr = new Kladr(List.of(new Kladronym(new Toponym("Тестов", "г"), new KladrCode("64000001000"))));
        var resolver = new LikelyKladronym("г Тестов", new NormalizedPrefixMatch(),
                () -> {
                    typeCalls.incrementAndGet();
                    return types;
                },
                () -> {
                    kladrCalls.incrementAndGet();
                    return kladr;
                });
        assertEquals(0, typeCalls.get());
        assertEquals(0, kladrCalls.get());
        assertTrue(resolver.find().isPresent());
        assertTrue(resolver.find().isPresent());
        assertEquals(2, typeCalls.get());
        assertEquals(2, kladrCalls.get());
    }
}

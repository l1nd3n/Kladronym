package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.source.CachedSource;
import dev.l1nd3n.kladronym.source.FiasSource;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.name.NameMatch;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

final class BundledData {
    static final FiasSource<Abbreviations> ABBREVIATIONS = new CachedSource<>(new SocrSource(() -> {
        InputStream stream = BundledData.class.getResourceAsStream("/dev/l1nd3n/kladronym/socrbase.tsv");
        if (stream == null) throw new IOException("SOCR resource not found");
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }));
    static final FiasSource<Kladr> KLADR = new CachedSource<>(new KladrSource(() -> {
        InputStream stream = BundledData.class.getResourceAsStream("/dev/l1nd3n/kladronym/kladr.tsv");
        if (stream == null) throw new IOException("KLADR resource not found");
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }));
    static final NameMatch NAME_MATCH = new NormalizedPrefixMatch();

    private BundledData() {
    }
}

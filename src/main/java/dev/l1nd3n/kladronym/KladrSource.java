package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.source.FiasSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class KladrSource implements FiasSource<Kladr> {
    private final Path path;

    public KladrSource() {
        this(null);
    }

    public KladrSource(Path path) {
        this.path = path;
    }

    @Override
    public Kladr load() throws IOException {
        try (var reader = reader()) {
            String header = reader.readLine();
            if (!"name\tsocr\tcode".equals(header)) throw new IOException("Unexpected KLADR header: " + header);
            return new Kladr(reader.lines()
                    .map(line -> line.split("\t", -1))
                    .map(columns -> new Kladronym(
                            new Toponym(columns[0], columns[1]), new KladrCode(columns[2])))
                    .toList());
        }
    }

    private BufferedReader reader() throws IOException {
        if (path != null) return Files.newBufferedReader(path);
        var stream = KladrSource.class.getResourceAsStream("/dev/l1nd3n/kladronym/kladr.tsv");
        if (stream == null) throw new IOException("KLADR resource not found");
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}

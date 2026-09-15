package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.catalog.code.KladrCode;
import dev.l1nd3n.kladronym.catalog.toponym.Toponym;
import dev.l1nd3n.kladronym.source.FiasSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class KladrSource implements FiasSource<Kladr> {
    private final FiasSource<Reader> input;

    public KladrSource(FiasSource<Reader> input) {
        this.input = input;
    }

    @Override
    public Kladr load() throws Exception {
        try (var reader = new BufferedReader(input.load())) {
            String header = reader.readLine();
            if (!"name\tsocr\tcode".equals(header)) throw new IOException("Unexpected KLADR header: " + header);
            return new Kladr(reader.lines()
                    .map(line -> line.split("\t", -1))
                    .map(columns -> new Kladronym(
                            new Toponym(columns[0], columns[1]), new KladrCode(columns[2])))
                    .toList());
        }
    }

}

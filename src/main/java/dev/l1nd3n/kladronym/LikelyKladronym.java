package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.catalog.Kladronym;
import dev.l1nd3n.kladronym.source.FiasSource;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.Aliases;
import dev.l1nd3n.kladronym.text.PreprocessedAddress;
import dev.l1nd3n.kladronym.text.Token;
import dev.l1nd3n.kladronym.text.name.BwExtension;
import dev.l1nd3n.kladronym.text.name.FwExtension;
import dev.l1nd3n.kladronym.text.name.NameMatch;
import dev.l1nd3n.kladronym.text.name.ReversedNameMatch;

import java.util.List;
import java.util.Optional;

public final class LikelyKladronym {
    private final String source;
    private final FiasSource<Abbreviations> abbreviations;
    private final FiasSource<Kladr> kladr;
    private final NameMatch nameMatch;

    public LikelyKladronym(String source) {
        this(source, BundledData.NAME_MATCH, BundledData.ABBREVIATIONS, BundledData.KLADR);
    }

    public LikelyKladronym(String source, NameMatch nameMatch) {
        this(source, nameMatch, BundledData.ABBREVIATIONS, BundledData.KLADR);
    }

    public LikelyKladronym(String source, NameMatch nameMatch,
                           FiasSource<Abbreviations> abbreviations, FiasSource<Kladr> kladr) {
        this.source = source;
        this.nameMatch = nameMatch;
        this.abbreviations = abbreviations;
        this.kladr = kladr;
    }

    public Optional<Kladronym> find() {
        Abbreviations types = load(abbreviations, "abbreviations");
        Kladr catalog = load(kladr, "KLADR");
        List<Token> tokens = new PreprocessedAddress(source, new Aliases(types)).get();
        return new KladronymResolution(tokens, catalog,
                new StandardToponymMatch(nameMatch, types), new FwExtension()).resolve()
                .or(() -> new KladronymResolution(tokens.reversed(), catalog,
                        new StandardToponymMatch(new ReversedNameMatch(nameMatch), types),
                        new BwExtension()).resolve());
    }

    private <T> T load(FiasSource<T> source, String description) {
        try {
            return source.load();
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot load " + description + " from " + source, exception);
        }
    }
}

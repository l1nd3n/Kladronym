package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.name.BwExtension;
import dev.l1nd3n.kladronym.name.FwExtension;
import dev.l1nd3n.kladronym.name.NameMatch;
import dev.l1nd3n.kladronym.name.ReversedNameMatch;
import dev.l1nd3n.kladronym.preprocess.PreprocessedAddress;
import dev.l1nd3n.kladronym.preprocess.Socrbase;
import dev.l1nd3n.kladronym.preprocess.Token;

import java.util.List;
import java.util.Optional;

public final class LikelyKladronym {
    private final String source;
    private final LoadedSocrbase socrbase;
    private final LoadedKladr kladr;
    private final NameMatch nameMatch;

    public LikelyKladronym(String source) {
        this(source, BundledData.NAME_MATCH, BundledData.SOCRBASE, BundledData.KLADR);
    }

    public LikelyKladronym(String source, NameMatch nameMatch) {
        this(source, nameMatch, BundledData.SOCRBASE, BundledData.KLADR);
    }

    private LikelyKladronym(String source, NameMatch nameMatch, LoadedSocrbase socrbase, LoadedKladr kladr) {
        this.source = source;
        this.nameMatch = nameMatch;
        this.socrbase = socrbase;
        this.kladr = kladr;
    }

    public Optional<Kladronym> find() {
        Socrbase loadedSocrbase = socrbase.get();
        KladrCatalog catalog = kladr.get();
        List<Token> tokens = new PreprocessedAddress(source, loadedSocrbase).get();
        return new ResolutionPass(tokens, catalog, nameMatch, new FwExtension()).resolve()
                .or(() -> new ResolutionPass(tokens.reversed(), catalog, new ReversedNameMatch(nameMatch), new BwExtension()).resolve());
    }
}

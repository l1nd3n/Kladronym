package dev.l1nd3n.kladronym;

import java.util.List;
import java.util.Optional;

public final class LikelyKladronym {
    private final String source;
    private final LoadedSocrbase socrbase;
    private final LoadedKladr kladr;
    private final ToponymNameMatch nameMatch;

    public LikelyKladronym(String source) {
        this(source, BundledData.NAME_MATCH, BundledData.SOCRBASE, BundledData.KLADR);
    }

    public LikelyKladronym(String source, ToponymNameMatch nameMatch) {
        this(source, nameMatch, BundledData.SOCRBASE, BundledData.KLADR);
    }

    private LikelyKladronym(String source, ToponymNameMatch nameMatch, LoadedSocrbase socrbase, LoadedKladr kladr) {
        this.source = source;
        this.nameMatch = nameMatch;
        this.socrbase = socrbase;
        this.kladr = kladr;
    }

    public Optional<Kladronym> get() {
        Socrbase loadedSocrbase = socrbase.get();
        KladrCatalog catalog = kladr.get();
        List<Token> tokens = new PreprocessedAddress(source, loadedSocrbase).get();
        ResolutionPass pass = new ResolutionPass(tokens, catalog, nameMatch);
        return pass.frontwards().or(pass::backwards);
    }
}

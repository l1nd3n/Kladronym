package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.name.NameMatch;
import dev.l1nd3n.kladronym.name.NormalizedPrefixMatch;

final class BundledData {
    static final LoadedSocrbase SOCRBASE = new LoadedSocrbase();
    static final LoadedKladr KLADR = new LoadedKladr();
    static final NameMatch NAME_MATCH = new NormalizedPrefixMatch();

    private BundledData() {
    }
}

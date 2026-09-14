package dev.l1nd3n.kladronym;

import dev.l1nd3n.kladronym.catalog.Kladr;
import dev.l1nd3n.kladronym.source.CachedSource;
import dev.l1nd3n.kladronym.source.FiasSource;
import dev.l1nd3n.kladronym.text.Abbreviations;
import dev.l1nd3n.kladronym.text.name.NameMatch;
import dev.l1nd3n.kladronym.text.name.NormalizedPrefixMatch;

final class BundledData {
    static final FiasSource<Abbreviations> ABBREVIATIONS = new CachedSource<>(new SocrSource());
    static final FiasSource<Kladr> KLADR = new CachedSource<>(new KladrSource());
    static final NameMatch NAME_MATCH = new NormalizedPrefixMatch();

    private BundledData() {
    }
}

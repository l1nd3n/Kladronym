package dev.l1nd3n.kladronym;

final class BundledData {
    static final LoadedSocrbase SOCRBASE = new LoadedSocrbase();
    static final LoadedKladr KLADR = new LoadedKladr();
    static final ToponymNameMatch NAME_MATCH = new NormalizedPrefixMatch();

    private BundledData() {
    }
}

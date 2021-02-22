package org.dulab.adapcompounddb.models.enums;

import java.util.Arrays;

public enum OntologyLevel implements EnumWithLabels {
    OL1("OL_1", 1, true, true, true, true),
    OL2A("OL_2a", 2, true, true, true, false),
    OL2B("OL_2b", 2, true, false, true, true),
    PDA("PD_A", 2, false, false, true, true);

    private final String label;
    private final int priority;
    private final boolean inHouseLibrary;
    private final boolean matchRetTime;
    private final boolean matchMass;
    private final boolean matchMsMs;

    OntologyLevel(String label, int priority, boolean inHouseLibrary, boolean matchRetTime, boolean matchMass, boolean matchMsMs) {
        this.label = label;
        this.priority = priority;
        this.inHouseLibrary = inHouseLibrary;
        this.matchRetTime = matchRetTime;
        this.matchMass = matchMass;
        this.matchMsMs = matchMsMs;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isInHouseLibrary() {
        return inHouseLibrary;
    }

    public boolean isMatchRetTime() {
        return matchRetTime;
    }

    public boolean isMatchMass() {
        return matchMass;
    }

    public boolean isMatchMsMs() {
        return matchMsMs;
    }

    public static int[] priorities() {
        return Arrays.stream(OntologyLevel.values())
                .mapToInt(OntologyLevel::getPriority)
                .distinct()
                .sorted()
                .toArray();
    }

    public static OntologyLevel[] findByPriority(int priority) {
        return Arrays.stream(OntologyLevel.values())
                .filter(l -> l.priority == priority)
                .toArray(OntologyLevel[]::new);
    }
}

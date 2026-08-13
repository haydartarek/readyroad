package com.readyroad.readyroadbackend.marketing.content;

import java.util.List;

public enum ContentLocale {
    AR("Arabic", "Write natural Modern Standard Arabic for a learner in Belgium."),
    NL("Dutch", "Write natural Belgian Dutch for a learner preparing in Belgium."),
    EN("English", "Write clear natural English for a learner in Belgium."),
    FR("French", "Write natural Belgian French for a learner preparing in Belgium.");

    public static final List<ContentLocale> SUPPORTED = List.of(AR, NL, EN, FR);

    private final String displayName;
    private final String brief;

    ContentLocale(String displayName, String brief) {
        this.displayName = displayName;
        this.brief = brief;
    }

    public String displayName() {
        return displayName;
    }

    public String brief() {
        return brief;
    }
}

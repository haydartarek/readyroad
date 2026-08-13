package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class OpenAIStructuredContent {
    @JsonPropertyDescription("Target language code. Must exactly match AR, NL, EN or FR from the request.")
    public String language;

    @JsonPropertyDescription("Exact immutable source reference supplied in the request.")
    public String sourceReference;

    @JsonPropertyDescription("Natural educational title with no emoji or unsupported claim.")
    public String title;

    @JsonPropertyDescription("Concise natural summary of the verified source facts.")
    public String summary;

    @JsonPropertyDescription("Educational content derived only from the supplied verified facts.")
    public String body;

    @JsonPropertyDescription("Natural next action matching the approved primary CTA.")
    public String cta;
}

package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class OpenAIEditorialTranslationContent {

    @JsonPropertyDescription("Canonical source language code. Must exactly match the source language from the request.")
    public String sourceLanguage;

    @JsonPropertyDescription("Requested target language code. Must exactly match the target language from the request.")
    public String targetLanguage;

    @JsonPropertyDescription("Exact canonical source article version id supplied in the request.")
    public long sourceVersionId;

    @JsonPropertyDescription("Natural localized article title preserving the exact meaning of the canonical source.")
    public String title;

    @JsonPropertyDescription("Localized URL slug. No spaces, slash, query string or fragment. Use words and hyphens.")
    public String slug;

    @JsonPropertyDescription("Natural localized summary preserving the canonical source meaning.")
    public String summary;

    @JsonPropertyDescription("Complete localized article body preserving the source facts, legal meaning and Markdown structure.")
    public String body;

    @JsonPropertyDescription("Localized SEO meta title preserving the source meaning.")
    public String metaTitle;

    @JsonPropertyDescription("Localized SEO meta description preserving the source meaning.")
    public String metaDescription;

    @JsonPropertyDescription("Localized call to action preserving the source CTA intent.")
    public String cta;
}
INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES
    ('STRATEGY', 'seo.migration',
     '{
       "sourceDomain":"readyroad.be",
       "candidateDomain":"rijvia.be",
       "activationStatus":"RELEASED",
       "mappingPolicy":"PAGE_TO_PAGE_PRESERVE_PATH",
       "changeOfAddress":"COMPLETED",
       "sitemapSubmission":"ACTIVE",
       "releasedAt":"2026-08-24"
     }'::jsonb,
     'OWNER_APPROVAL_2026_08_24'),
    ('STRATEGY', 'publishing.safety',
     '{
       "contentPublishing":true,
       "socialPublishing":false,
       "outreachSending":false,
       "deployment":true,
       "sitemapSubmission":true
     }'::jsonb,
     'OWNER_APPROVAL_2026_08_24'),
    ('STRATEGY', 'social.official_accounts',
     '{
       "ownerConfirmed":true,
       "confirmedAt":"2026-08-24",
       "publishingPolicy":"PROVIDER_API_OAUTH_REQUIRED",
       "accounts":[
         {
           "platform":"YOUTUBE",
           "url":"https://studio.youtube.com/channel/UCs_IDQXCz6zADuHIdfS2C2w",
           "channelId":"UCs_IDQXCz6zADuHIdfS2C2w",
           "publishingTarget":"YOUTUBE_COMMUNITY"
         },
         {
           "platform":"FACEBOOK",
           "url":"https://www.facebook.com/profile.php?id=61559077906506"
         },
         {
           "platform":"INSTAGRAM",
           "url":"https://www.instagram.com/a.rib.0/"
         },
         {
           "platform":"TIKTOK",
           "url":"https://www.tiktok.com/@trijbewijs"
         }
       ]
     }'::jsonb,
     'OWNER_APPROVAL_2026_08_24'),
    ('YOUTUBE', 'youtube.channel',
     '{
       "handle":"@RijBewijsBe",
       "channelId":"UCs_IDQXCz6zADuHIdfS2C2w",
       "url":"https://www.youtube.com/channel/UCs_IDQXCz6zADuHIdfS2C2w",
       "ownerConfirmed":true,
       "ownerVerificationUrl":"https://studio.youtube.com/channel/UCs_IDQXCz6zADuHIdfS2C2w"
     }'::jsonb,
     'OWNER_APPROVAL_2026_08_24')
ON CONFLICT (agent_type, setting_key) DO UPDATE SET
    setting_value = agent_settings.setting_value || EXCLUDED.setting_value,
    updated_by = EXCLUDED.updated_by,
    updated_at = CURRENT_TIMESTAMP;

UPDATE agent_definitions
SET enabled = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE agent_type IN (
    'ADMIN_PLATFORM', 'ANALYTICS', 'CONTENT', 'EDITORIAL', 'STRATEGY', 'YOUTUBE'
);

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'RIJVIA_MARKETING_OPERATIONS_OWNER_ACTIVATED',
    'OWNER_APPROVAL_2026_08_24',
    'MARKETING_PLATFORM',
    'RIJVIA',
    'rijvia-marketing-operations-owner-activated-v53',
    '{
      "canonicalRelease":"RELEASED",
      "searchConsoleWorkbookImport":true,
      "officialSocialAccountsConfirmed":4,
      "contentPublishing":true,
      "socialPublishing":false,
      "socialProviderConnectionRequired":true
    }'::jsonb
);

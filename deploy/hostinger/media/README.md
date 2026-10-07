# Approved local media inventory

The SHA256 manifests record the media required by the approved local release.
Quiz uploads and editorial assets are stored in persistent production volumes,
rather than in Git or application images. Lesson source assets remain tracked.

| Manifest | Backend directory | Persistent volume | Local files |
| --- | --- | --- | ---: |
| quiz.sha256 | /app/public/images/quiz | readyroad-uploads | 484 |
| lessons.sha256 | /app/public/images/lessons | readyroad-lesson-media | 54 |
| editorial.sha256 | /app/data/editorial-images | readyroad-article-images | 35 |
| signs.sha256 | /app/public/images/signs | Backend image | 187 |

Validate each manifest from its corresponding directory with `sha256sum -c`.
Production may contain additional user uploads and editorial versions. Preserve
these files; this inventory is not a deletion list.

Before deployment, back up the production database and all media volumes. Copy
only missing or verified different files, preserving replaced files in the
release backup. After deployment, verify the manifests, mounts and public URLs.

# Listings

A listing is generated metadata associated with a project.

Fields may include:

- title
- description
- tags
- keywords
- category
- language
- platform
- style profile
- prompt version
- generation ID

Listings should preserve enough metadata to reproduce/debug how they were generated.

Current listings persist title, description, tags, keywords, category, language, project,
generation ID and creation time. The associated generation stores provider/model/prompt
metadata and token usage. The most recent listing is returned per owned project.
Deleting a project removes its listing drafts; generation audit records remain.

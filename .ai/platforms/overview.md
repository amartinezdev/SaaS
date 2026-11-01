# Platform Profiles

Platform-specific listing behavior must be isolated from generic listing generation.

Each platform profile should define:

- title length
- tag limits
- formatting
- required fields
- prohibited/undesired patterns
- language expectations
- export format

Never hard-code one platform's rules into the generic listing generator.

## Assisted publishing

The intended workflow is to publish a creator-provided `.3mf` and AI-generated listing
through each marketplace's browser form. The first target is MakerWorld; other marketplaces
follow with separate adapters. The user's own browser session remains on the marketplace.

A page on our origin cannot edit another site's DOM. A browser extension or local automation
component would fill the external form, then let the creator review and submit. Keep selectors
and field mappings per platform, and expect to update them when a platform changes its UI.
Check current platform rules and upload behavior before implementing each adapter. Do not
store marketplace passwords or cookies in the application backend.

Generating the listing once and reusing it across platforms limits paid AI calls. Browser
automation does not eliminate the need to transfer the `.3mf` or make requests to the
marketplace's upload form.

# Projects

A project represents one creator model/workflow.

Current fields:

- id
- ownerId
- name
- optional source image object key and content type
- createdAt
- updatedAt

A user can own many projects.

Every project access must verify ownership.

The MVP accepts one private source image per project (PNG, JPEG, or WebP, up to 10 MiB).
Replacing or deleting it removes the previous object after the database transaction commits.
The image is served to the signed-in owner through the API; the storage bucket is never public.
Creator-provided `.3mf` files and generated listings belong to later publishing slices.

#!/bin/bash
#
# Commits the given paths to the checked-out branch and pushes them. Exits
# without a commit when they have no changes.

set -euo pipefail

# Listed under gitIgnoredAuthors in .github/renovate.json5, so that Renovate
# keeps updating a branch this script has committed to.
readonly AUTHOR_NAME='github-actions[bot]'
readonly AUTHOR_EMAIL='41898282+github-actions[bot]@users.noreply.github.com'

git add -- "$@"

if git diff --cached --quiet -- "$@"; then
  echo "Lock files already match; nothing to commit."
  exit 0
fi

git -c "user.name=${AUTHOR_NAME}" -c "user.email=${AUTHOR_EMAIL}" \
  commit --message 'Regenerate lock files'
git push origin "HEAD:refs/heads/${GITHUB_REF_NAME}"

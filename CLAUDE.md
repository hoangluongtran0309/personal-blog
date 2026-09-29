# CLAUDE.md - Personal Blog

This file is read by coding agents on every session. Follow everything here before writing any code.

---

## Project overview

A personal blog built to the roadmap.sh **Personal Blog** brief (<https://roadmap.sh/projects/personal-blog>):

- **Guest section**: Home (list of published articles) and Article (content + publication date).
- **Admin section**: Dashboard (article list with add / edit / delete), Add Article form, and Edit Article form. The forms have title, content, and publication date.
- Articles are stored on the **filesystem**, one Markdown file per article. There is **no database**.
- Pages are rendered on the server and forms post back to it. The core flows need no JavaScript.
- The admin logs in through a form login page backed by a session.

Kept beyond the brief: categories/tags (free text in the article file), pagination, i18n (en/vi), and dark mode.

Plan and progress: `ROADMAP.md`. Update it at the end of every phase.

The UI and the `article` package are ported from an earlier project at `~/Documents/devblog/`. Use it as a reference for design tokens, layouts, and fragments. Do not copy its other documentation.

**Tech stack**: Java 17, Spring Boot 3.5.x, Spring MVC, Thymeleaf + Layout Dialect, Spring Security, Bean Validation, SnakeYAML (via Spring Boot), Flexmark, jsoup, Tailwind CSS 3, PostCSS, npm scripts.

**Architecture**: feature-oriented Spring Boot monolith. The `article` feature owns its domain, storage, service, web forms, controllers, and tests. Code that more than one feature needs goes in `infrastructure`.

---

## Module structure

```
personal-blog/
├── src/main/java/com/juliawalker/personalblog/
│   ├── article/          - Article domain, ArticleStore, FileSystemArticleStore, ArticleService
│   │   └── web/          - Client and admin controllers, form data, converters
│   └── infrastructure/   - Security, web helpers, exceptions, validation groups, markdown
├── src/main/resources/
│   ├── i18n/             - messages.properties, messages_vi.properties
│   ├── static/           - source CSS, js/script.js (dark mode / mobile menu only), SVG, icons
│   └── templates/        - Thymeleaf client/admin/shared/error templates
├── src/test/java/        - Unit and MVC tests (no Docker needed)
└── data/articles/        - default local ARTICLES_DIR (not committed)
```

## Package conventions

```
com.juliawalker.personalblog.article                    - Article, ArticleId, ArticleTitle, ArticleContent, ArticleSummary,
                                                                 Slug, ArticleTerm, ArticleStore, FileSystemArticleStore,
                                                                 ArticleFileFormat, ArticleService(Impl), exceptions
com.juliawalker.personalblog.article.web                - ClientArticleController, AdminArticleController, form data,
                                                                 validation group sequences, StringToArticleIdConverter
com.juliawalker.personalblog.infrastructure.web         - MVC config, LoginController, Messages, ResolveMessages, SiteProperties
com.juliawalker.personalblog.infrastructure.security    - SecurityConfiguration, in-memory admin user
com.juliawalker.personalblog.infrastructure.exception   - BusinessException, NotFoundException
com.juliawalker.personalblog.infrastructure.validation  - ValidationGroupOne, ValidationGroupTwo
com.juliawalker.personalblog.infrastructure.markdown    - MarkdownService (with HTML sanitizing)
```

---

## Domain model - article

- `Article` is a plain immutable domain object: id, title, slug, optional summary, markdown content, optional category, tags, `publishedAt`, `createdAt`, `updatedAt`, optional `coverImage` (`ArticleCoverImage`: a `/path` or `http(s)://` URL only).
- Publication rule:
  - `publishedAt == null` means draft.
  - `publishedAt <= now` means published.
  - `publishedAt > now` means scheduled.
  - Guests only see published articles. The rule lives in `ArticleService`, not in templates or controllers.
- Slug is generated from the title (Vietnamese diacritics stripped) and can be edited. Slug and title must be unique.
- Category is one optional string; tags are an optional list of strings. Their URL segments come from `Slug`.
- There is no optimistic locking: a single admin edits articles.
- `ArticleStatus` (`DRAFT`, `SCHEDULED`, `PUBLISHED`) comes from `ArticleService.getStatus`; controllers put it in the model, templates never compute it.
- Create and update both take `ArticleParameters` (slug `null` = generate from title, `publishedAt` `null` = draft).

### Storage

- `ArticleStore` is a feature-local interface: `nextId`, `findAll`, `findById`, `findBySlug`, `save`, `delete`.
- `FileSystemArticleStore` writes one file per article at `${ARTICLES_DIR}/{id}.md` (YAML front matter + Markdown body).
  - Writes go to a temp file, then move into place with `Files.move(ATOMIC_MOVE)`.
  - A `ReentrantReadWriteLock` guards the index and writes.
  - The in-memory index is loaded at startup and refreshed after writes.
  - Malformed files are logged and skipped.
  - YAML is parsed with SnakeYAML `SafeConstructor`.
- The file format is documented in `ROADMAP.md` section 2. Changing it means updating that section too.

---

## Web conventions

```
GET  /                                    Home: published articles, paged
GET  /category/{slug}                     Home filtered by category
GET  /tag/{slug}                          Home filtered by tag
GET  /articles/{slug}                     Article page (404 for drafts / scheduled)
GET  /login, POST /login                  Login
POST /logout                              Logout
GET  /admin                               Dashboard: all articles with add / edit / delete
GET  /admin/articles/new, POST            Add Article
GET  /admin/articles/{id}/edit, POST      Edit Article
GET  /admin/articles/{id}/delete, POST    Delete confirmation page + delete
```

- Admin controllers live under `/admin/**` and are protected by the security filter chain (`ROLE_ADMIN`).
- Admin views live in `templates/admin/...`, client views in `templates/client/...`, shared fragments in `templates/shared/fragments/...`, error pages in `templates/error/...`.
- Form objects live in `article.web`, use validation group sequences, and convert through `toParameters()`.
- Controllers catch `BusinessException` when the user needs a form error or flash message. Flash messages go through `Messages.success/error` and `ResolveMessages`.
- Path variable IDs bind through `StringToArticleIdConverter`.
- There is **no REST API**. Do not add `@RestController` classes.
- Do not put business rules in Thymeleaf templates or controllers when they belong in services.

---

## Exceptions

| Exception | When |
|-----------|------|
| `BusinessException` | User-correctable business rule failure with an i18n message key |
| `NotFoundException` | Requested resource does not exist or is not visible (404) |
| `ArticleNotFoundException` | Article id/slug missing, or a draft/scheduled article requested by a guest |
| `ArticleAlreadyExistsException` | Duplicate slug or title |
| `ArticleStorageException` | Filesystem read/write failure |

Use message keys from both `messages*.properties` files. Do not hardcode UI text in exceptions.

---

## UI conventions

- **Thymeleaf pitfall**: `th:replace`/`th:insert` run before `th:if` on the same element. Put the condition on an outer `th:block`.
- **Server-side rendering, no JavaScript required.** List, read, login, add, edit, and delete (through a confirmation page) all work with JavaScript disabled. JS is only a progressive enhancement for the dark mode toggle (theme class set before paint) and the mobile menu. No htmx, Alpine.js, or SPA frameworks.
- **Reuse fragments** before writing new HTML:
  ```
  templates/
      client/layout/main.html   client/fragments/  (header, hero, footer, cards, details, sidebar)
      admin/layout/main.html    admin/fragments/   (header, sidebar, tables)
      shared/fragments/         (fields, buttons, alerts, paginations)
      error/                    (404, 500)
  ```
- **Thymeleaf**: always use Layout Dialect (`layout:decorate`, `layout:fragment`). Forms use `th:object` / `th:field`; errors use `th:errors` and `#fields.hasErrors()`. Use `th:text` by default; `th:utext` only for sanitized Markdown. Format dates with `#temporals.format(...)` and the i18n date pattern keys.
- **Design reference**: the original DevBlog template (codewithsadee) — hero, cards with cover image, Topics/Tags/Let's Talk aside, 3-column footer. Images live in `static/images/`.
- **Tailwind CSS 3**: use the existing color tokens (`light-*`, `dark-*`, `accent`), the Inter font, and `prose` for article bodies. No ad hoc colors. Dark mode is class-based: every element needs both light and `dark:` variants. Prefer utilities over `@apply` and inline styles.
- **Responsive**: mobile-first; check mobile, tablet, desktop.
- **i18n**: all user-facing text uses `#{...}` keys present in both `messages.properties` and `messages_vi.properties`.
- **Accessibility**: form fields have labels; icon-only buttons have `title`/`aria-label` from i18n keys; destructive actions go through a confirmation page.

```bash
npm install
npm run build      # compile CSS/JS/templates into target/classes
npm run watch      # BrowserSync on http://localhost:3000 against localhost:8080
```

For production packaging, the Maven profile `release` runs `npm run build-prod`.

---

## Configuration and secrets

- Articles: `ARTICLES_DIR` (default `./data/articles`)
- Admin: `ADMIN_USERNAME`, `ADMIN_PASSWORD_HASH` (BCrypt hash, never a plain password). Startup fails if either is missing.
- Site: `SITE_NAME`, `SITE_DESCRIPTION`, `SITE_AUTHOR`, `SITE_AUTHOR_ROLE`, `SITE_TAGLINE`, `SITE_HERO_IMAGE`, `SITE_AUTHOR_AVATAR`, `SITE_EMAIL`
- Social links (blank = hidden): `SOCIAL_GITHUB`, `SOCIAL_FACEBOOK`, `SOCIAL_LINKEDIN`, `SOCIAL_X`
- Security: `REMEMBER_ME_KEY`, `SESSION_COOKIE_SECURE`
- Local development reads an optional `.env` file (`spring.config.import=optional:file:.env[.properties]`). `.env` is not committed; `.env.example` is.

Deployment must mount persistent storage for `ARTICLES_DIR`, and only one instance may run. Never hardcode credentials in code, tests, templates, or docs.

---

## Testing conventions

- JUnit 5, AssertJ, Mockito, Spring MVC Test, spring-security-test. No Testcontainers and no Docker.
- `FileSystemArticleStore` tests use `@TempDir`.
- Test names follow `method_condition_expectedResult`.
- Add tests at the same layer as the change:
  - Business rule -> service/domain test
  - Storage/file format -> `FileSystemArticleStore` / `ArticleFileFormat` test
  - Controller/form behavior -> MVC test
  - Security rule -> MVC/security test

```bash
mvn test
mvn -Dtest=ClassName test
mvn -P release package
```

---

## Commit convention

Conventional Commits: `<type>(<scope>): <subject>`

Types: `feat` `fix` `refactor` `test` `docs` `chore` `perf` `style` `build` `ci`

Scopes: `article` `security` `web` `templates` `assets` `config` `build` `docker` `deps` `tests` `docs`

Rules: imperative verb, lowercase subject, no period, max 72 chars, English.

```
feat(article): store articles as markdown files
feat(security): add in-memory admin login
docs(docs): update roadmap after phase 2
```

---

## What NOT to do

- Do not add a database, JPA, or Flyway.
- Do not add REST APIs, htmx, Alpine.js, or other client-side frameworks.
- Do not show drafts or scheduled articles to guests. Keep that rule in the service.
- Do not render unsanitized Markdown HTML with `th:utext`.
- Do not hardcode credentials or plain-text admin passwords. Use `ADMIN_PASSWORD_HASH`.
- Do not hardcode user-facing text. Always update both `messages.properties` and `messages_vi.properties`.
- Do not edit generated/build output in `target/` as source.
- Do not revert unrelated user changes.

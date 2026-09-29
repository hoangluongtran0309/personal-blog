# Roadmap: Personal Blog (roadmap.sh)

> Cập nhật: 2026-09-28 · Trạng thái: **Giai đoạn 0–9 xong**; còn lại các mục chưa tick trong checklist nghiệm thu (mục 7).
>
> Đề bài: <https://roadmap.sh/projects/personal-blog>
>
> Tham chiếu: dự án cũ `~/Documents/devblog/` (giao diện, package `article`, fragments Thymeleaf). Chỉ tái dùng code và giao diện; **không** chép tài liệu (ADR, standards, `ai/*`).
>
> Quyết định đã chốt:
> - **Stack giống devblog**: Java 17, Spring Boot 3.5.x, Thymeleaf + Layout Dialect, Spring Security, Bean Validation, Flexmark, Tailwind CSS 3.
> - **Lưu bằng filesystem**: mỗi bài là một file Markdown có YAML front matter. Không có database.
> - **Port package `article` từ devblog và đơn giản hóa**: bỏ optimistic lock (`version`) vì chỉ có một admin.
> - **Giữ thêm ngoài đề**: dark mode, i18n (en/vi), category/tag (chuỗi trong front matter), phân trang.
> - **Không cần JavaScript** cho luồng chính. JS chỉ dùng cho toggle dark mode và menu mobile.

## 1. Phạm vi

| Trang | Route | Quyền |
|---|---|---|
| Home: danh sách bài đã publish, mới nhất trước, phân trang | `GET /` | Công khai |
| Home lọc theo category / tag | `GET /category/{slug}`, `GET /tag/{slug}` | Công khai |
| Article: nội dung + ngày publish | `GET /articles/{slug}` | Công khai (404 nếu là nháp hoặc hẹn giờ) |
| Login / Logout | `GET/POST /login`, `POST /logout` | Công khai / Admin |
| Dashboard: mọi bài + Add / Edit / Delete | `GET /admin` | Admin |
| Add Article | `GET/POST /admin/articles/new` | Admin |
| Edit Article | `GET/POST /admin/articles/{id}/edit` | Admin |
| Delete Article (trang xác nhận, không cần JS) | `GET/POST /admin/articles/{id}/delete` | Admin |

Trường của form:
- **title** (bắt buộc)
- **content** (bắt buộc, Markdown, `<textarea>`)
- **publishedAt** (`datetime-local`; để trống = nháp)
- **slug** (tùy chọn; trống thì sinh từ title)
- **summary** (tùy chọn; trống thì lấy ~200 ký tự đầu của content)
- **category** (tùy chọn), **tags** (tùy chọn, cách nhau bằng dấu phẩy)

Quy tắc publish:
- `publishedAt == null` → nháp
- `publishedAt <= now` → đã publish
- `publishedAt > now` → hẹn giờ

Guest chỉ thấy bài đã publish. Luật này nằm trong `ArticleService`, không nằm trong controller hay template.

## 2. Thiết kế lưu trữ

- File `${ARTICLES_DIR}/{id}.md`, `id` là UUID. Dùng id thay vì slug để đổi slug không phải đổi tên file.
  ```text
  ---
  id: 7f3c0b9e-...
  title: Hello world
  slug: hello-world
  summary: Mô tả ngắn (tùy chọn)
  coverImage: /images/blog-1.png   # tùy chọn: đường dẫn bắt đầu bằng / hoặc URL http(s)
  category: Java
  tags: [spring, thymeleaf]
  publishedAt: 2026-09-28T10:00   # trống = nháp
  createdAt: 2026-09-20T08:00
  updatedAt: 2026-09-28T10:00
  ---
  Nội dung Markdown...
  ```
- `FileSystemArticleStore`:
  - ghi file tạm rồi `Files.move(ATOMIC_MOVE)`
  - `ReentrantReadWriteLock` cho ghi
  - index trong bộ nhớ (id / slug → `Article`), nạp khi khởi động, cập nhật sau mỗi lần ghi
  - file hỏng thì log cảnh báo và bỏ qua, không làm sập ứng dụng
  - SnakeYAML dùng `SafeConstructor`
- `ArticleService`:
  - chặn trùng slug và title
  - phân trang, lọc category/tag trong bộ nhớ, trả `Page`
  - `Clock` được inject để test được luật publish
- Category/tag: chuỗi tự do, URL segment lấy từ `Slug`. Danh sách cho sidebar suy ra từ các bài đã publish.
- Chỉ chạy **một** instance (index nằm trong bộ nhớ). Khi deploy phải mount volume cho `ARTICLES_DIR`.

## 3. Bảo mật

- Một `SecurityFilterChain`: `/admin/**` yêu cầu `ROLE_ADMIN`, còn lại `permitAll`.
- Form login `/login`, logout bằng POST, CSRF bật, remember-me.
- Admin là `InMemoryUserDetailsManager`, lấy từ `ADMIN_USERNAME` + `ADMIN_PASSWORD_HASH` (BCrypt). Không hardcode mật khẩu. Thiếu biến thì ứng dụng dừng khi khởi động.
- `MarkdownService` sanitize HTML đầu ra bằng jsoup `Safelist.relaxed()` trước khi in bằng `th:utext`.
- Trang lỗi riêng `error/404.html`, `error/500.html`.

## 4. Giao diện (tái dùng từ devblog)

Nguồn: `~/Documents/devblog/src/main/resources/` và các file build ở root devblog.

| Thành phần | Cách tái dùng |
|---|---|
| `tailwind.config.js`, `postcss.config.js` | Chép; thêm token `light-border` / `dark-border` (devblog dùng nhưng chưa khai báo) |
| `package.json` (npm scripts build/watch) | Chép, đổi tên dự án |
| `static/css/application.css` | Chép, bỏ phần CSS của ToastUI |
| `static/svg/logo-*.svg`, `static/icons/favicon.ico` | Chép |
| `static/js/script.js`, `admin.script.js` | Gộp thành một `script.js`: chỉ dark mode + menu mobile, chạy an toàn khi thiếu phần tử |
| `templates/client/layout/main.html` | Giữ; bỏ `seoSettings`, dùng `SITE_NAME` / `SITE_DESCRIPTION` |
| `templates/client/fragments/header, footer, cards, details, sidebar` | Giữ bố cục; bỏ ảnh, project/contact/about, social links; category/tag là chuỗi |
| `templates/admin/layout/main.html` | Giữ; bỏ ToastUI CDN |
| `templates/admin/fragments/sidebar, header, tables` | Sidebar chỉ còn Dashboard / New article / Về trang web; header bỏ dropdown JS, logout là form POST |
| `templates/shared/fragments/fields, buttons, alerts, paginations` | Giữ |
| `i18n/messages*.properties` | Chỉ chép key đang dùng, thêm key `article.*`; hai file luôn đồng bộ |

Thêm mới: `admin/articles/form.html` (dùng chung Add/Edit), `admin/articles/delete.html`, `error/404.html`, `error/500.html`.

## 5. Giai đoạn thực hiện

| # | Trạng thái | Nội dung | Commit scope |
|---|---|---|---|
| 0 | ✅ | `ROADMAP.md`, `CLAUDE.md`, `.gitignore` | `docs` |
| 1 | ✅ | Khung dự án: `pom.xml`, `package.json`, tailwind/postcss, `application.properties`, `.env.example`; app khởi động được | `build`, `config` |
| 2 | ✅ | Port `article`: domain, `ArticleStore`, `FileSystemArticleStore`, `ArticleFileFormat`, `ArticleService` + test (bỏ `version`) | `article` |
| 3 | ✅ | `MarkdownService` + sanitize jsoup + test | `web` |
| 4 | ✅ | Assets, layout, fragments, i18n (tái dùng giao diện devblog) | `templates`, `assets` |
| 5 | ✅ | Guest: `ClientArticleController`, Home, Article, lọc category/tag, phân trang, 404 + MVC test | `article`, `templates` |
| 6 | ✅ | Security: in-memory admin từ env (fail-fast), form login, CSRF, remember-me, logout POST + test | `security` |
| 7 | ✅ | Admin: Dashboard, Add, Edit, Delete xác nhận, flash message + MVC test | `article`, `templates` |
| 8 | ✅ | Hoàn thiện: trang lỗi, kiểm tra tắt JS / dark mode / mobile / en-vi, bài mẫu, `Dockerfile`, `README.md` | `templates`, `docker`, `docs` |
| 9 | ✅ | UI theo template DevBlog gốc (codewithsadee): hero, card có ảnh bìa, sidebar, footer 3 cột; trường `coverImage`; dữ liệu demo | `templates`, `assets`, `article`, `docs` |

Nguyên tắc: sau mỗi giai đoạn `mvn test` phải pass, cập nhật bảng trên và thêm nhật ký ngắn ở mục 6.

## 6. Nhật ký

### Giai đoạn 0 (2026-09-28)

- Tạo `ROADMAP.md`, `CLAUDE.md`, `.gitignore`.

### Giai đoạn 1 (2026-09-28)

- `pom.xml` (Spring Boot 3.5.14 như devblog; không có JPA/DB; thêm `spring-data-commons` chỉ để dùng `Page`, jsoup cho sanitize), `package.json`, `tailwind.config.js` (thêm `light-border`/`dark-border`), `postcss.config.js`, `application.properties` (tiền tố `blog.*`), `.env.example`.
- Chép thư mục `node/` từ devblog để `frontend-maven-plugin` không phải tải lại (đã gitignore).

### Giai đoạn 2 (2026-09-28)

- Port package `article` từ devblog, đổi package sang `com.juliawalker.personalblog`.
- Bỏ optimistic lock: xóa `version`, `ArticleVersionConflictException`; gộp `Create/UpdateArticleParameters` thành `ArticleParameters`. File cũ có dòng `version:` vẫn đọc được (key thừa bị bỏ qua).
- Thêm `ArticleStatus` (DRAFT / SCHEDULED / PUBLISHED) và `ArticleService.getStatus` cho Dashboard.
- Excerpt bỏ thẻ HTML thô trong nội dung.

### Giai đoạn 3 (2026-09-28)

- `MarkdownService` (Flexmark) + sanitize jsoup `Safelist.relaxed()` mở rộng: giữ `id` heading/anchor, class `language-*` của code, checkbox task list (luôn `disabled`), link tương đối và `#anchor`; loại `<script>`, `on*`, `javascript:`.

### Giai đoạn 4 (2026-09-28)

- Tái dùng giao diện devblog: token màu, Inter, `prose`, layout client/admin, header/footer/cards/details/sidebar, fields/buttons/alerts/paginations.
- Khác devblog: không ToastUI (content là `<textarea>` Markdown), admin header không dropdown JS (logout là form POST), link thao tác trong bảng có chữ (không chỉ icon), phân trang dùng ký tự ‹ › thay icon để chạy khi không có JS; theme lưu chung key `theme`, mặc định theo `prefers-color-scheme`.
- Layout dùng `site.*` (`SITE_NAME`, `SITE_DESCRIPTION`, `SITE_AUTHOR`); chuyển ngôn ngữ bằng link `?lang=vi|en` (cookie `locale`, mặc định `vi`).
- i18n: viết lại từ đầu, chỉ các key đang dùng; hai file đồng bộ.

### Giai đoạn 5 (2026-09-28)

- `ClientArticleController`: `/`, `/category/{slug}`, `/tag/{slug}` (6 bài/trang), `/articles/{slug}`. Category/tag không có bài đã publish → 404; slug sai định dạng → 404.

### Giai đoạn 6 (2026-09-28)

- `SecurityConfiguration`: `/admin/**` cần `ROLE_ADMIN`, form login, logout POST `/logout`, CSRF, remember-me 14 ngày. `AdminProperties` fail-fast khi thiếu `ADMIN_USERNAME` hoặc hash không phải BCrypt. Thiếu `REMEMBER_ME_KEY` chỉ cảnh báo (key ngẫu nhiên mỗi lần khởi động).

### Giai đoạn 7 (2026-09-28)

- `AdminArticleController`: Dashboard (10 bài/trang, kèm trạng thái), Add, Edit, Delete qua trang xác nhận; flash message; trùng slug/title báo lỗi ngay trên ô tương ứng. ID sai định dạng → 404.
- Lưu ý: `th:if` và `th:replace` trên cùng phần tử thì `th:replace` chạy trước — luôn tách `th:if` ra block ngoài.

### Giai đoạn 8 (2026-09-28)

- Trang lỗi `error/404.html`, `error/5xx.html`, `error.html`; `Dockerfile` (volume `/data/articles`), `.dockerignore`, `README.md`.
- Test: 112 test pass (`mvn test`, không cần Docker/DB), gồm MVC test cho guest/admin và test đăng nhập với context đầy đủ.
- Chạy thử thật (curl, không JS): login đúng/sai, tạo bài → sinh file `{uuid}.md`, sửa bài thành nháp → guest nhận 404, xóa cần CSRF → file bị xóa, logout. Xem giao diện trên Chrome (desktop, dark mode): Home, Article, Login, Dashboard, form Add.

### Giai đoạn 9 (2026-09-28)

- Tham chiếu template gốc `devblog-personal-blog-website-master` (HTML/CSS thuần) và chuyển sang Tailwind, giữ nguyên token màu (trùng với devblog).
- Chép toàn bộ ảnh của template vào `static/images/` (hero, author, pattern, blog-1..10) và `favicon.ico`. Thêm script `build:images` cho npm.
- Hero 2 cột: lời chào + tên + chức danh + tagline + 2 nút; ảnh chân dung trên 2 hình tròn xoay -20° và họa tiết `pattern.png` (ẩn ảnh trên mobile như template). Chỉ hiện ở trang 1 không lọc.
- Card bài viết: nền trắng, bóng, hover nâng lên, ảnh bìa bên trái (`3fr/4fr`, ẩn trên mobile), chip chủ đề, excerpt 3 dòng, hàng tác giả (avatar, tên, ngày, thời gian đọc). Bài không có ảnh dùng ô gradient accent + icon chủ đề.
- Sidebar: Chủ đề (ô icon 70px, icon theo từ khóa: database→server, performance→rocket, …), Thẻ (hashtag), Trò chuyện (`#contact`, link email/mạng xã hội từ env). Không làm Newsletter (cần backend lưu email).
- Trang bài viết: ảnh bìa lớn, hộp tác giả; sửa padding dòng đầu khối code và màu tiêu đề (anchor link).
- Footer 3 cột (logo + mô tả + mạng xã hội | chủ đề | liên kết nhanh). Phân trang dạng nút tròn “Mới hơn / Cũ hơn”.
- Domain: `ArticleCoverImage` (chỉ nhận `/path` hoặc `http(s)://`), `Article.getReadingMinutes()` (200 từ/phút). Admin: ô ảnh bìa + thư viện ảnh có sẵn + ảnh xem trước; Dashboard có thumbnail.
- Cấu hình mới: `SITE_AUTHOR_ROLE`, `SITE_TAGLINE`, `SITE_HERO_IMAGE`, `SITE_AUTHOR_AVATAR`, `SITE_EMAIL`, `SOCIAL_GITHUB|FACEBOOK|LINKEDIN|X`.
- `demo/articles/`: 10 bài demo (8 đã đăng, 1 hẹn giờ, 1 nháp) dùng ảnh `blog-1..10`.
- 128 test pass. Đã xem trên Chrome: Home (dark/light, desktop/mobile), trang bài viết, Dashboard, form Edit.

## 7. Checklist nghiệm thu

- [x] `/` liệt kê bài đã publish, mới nhất trước; không hiện bài nháp hay hẹn giờ
- [x] `/articles/{slug}` hiện nội dung và ngày publish; bài nháp / hẹn giờ trả 404
- [x] `/admin/**` khi chưa đăng nhập bị chuyển về `/login`; đăng nhập bằng thông tin từ env
- [x] Dashboard liệt kê mọi bài (kèm trạng thái), có Add / Edit / Delete
- [x] Form Add / Edit có title, content, ngày publish; lỗi validate hiện ngay trên form
- [x] Mỗi bài là một file `.md` trong `ARTICLES_DIR`; xóa bài thì xóa file
- [x] `mvn test` chạy được mà không cần Docker hay database
- [x] Mọi luồng chính chạy không cần JavaScript (đã thử bằng curl)
- [ ] Kiểm tra trực quan: light mode, màn hình mobile, trình duyệt tắt JS (icon ionicons sẽ không hiện, chữ vẫn hiện)
- [ ] Build và chạy thử Docker image
- [x] Hai file `messages*.properties` đồng bộ
- [ ] Thay logo SVG ("DevBlog") và ảnh hero/avatar của template bằng ảnh của bạn nếu muốn thương hiệu riêng

## 8. Làm sau (ngoài phạm vi)

Search, comments, trang About, RSS, upload ảnh, đổi mật khẩu, trình soạn thảo Markdown có preview, chuyển sang DB khi số bài lớn.

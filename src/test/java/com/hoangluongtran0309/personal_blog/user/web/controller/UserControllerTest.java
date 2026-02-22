package com.hoangluongtran0309.personal_blog.user.web.controller;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hoangluongtran0309.personal_blog.infrastructure.web.EditMode;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserFilter;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;
import com.hoangluongtran0309.personal_blog.user.application.usecase.ChangePasswordUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.CreateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.DeleteUserUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.GetUserQueryUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.UpdateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.web.form.ChangePasswordFormData;
import com.hoangluongtran0309.personal_blog.user.web.form.CreateUserFormData;
import com.hoangluongtran0309.personal_blog.user.web.form.UpdateUserFormData;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private GetUserQueryUseCase getUserQueryUseCase;
    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private DeleteUserUseCase deleteUserUseCase;
    @Mock
    private ChangePasswordUseCase changePasswordUseCase;

    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UserView userView;
    private Page<UserView> userPage;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userView = new UserView(
                userId, "john_doe", "John", "Doe",
                "john@mail.com", null, "MALE", "2000-01-01",
                "0123456789", "Developer", "Bio text",
                "123 Main St", "New York", "USA",
                "https://github.com/john", "https://x.com/john",
                "https://facebook.com/john", "https://linkedin.com/in/john");
        userPage = new PageImpl<>(List.of(userView));
    }

    @Nested
    @DisplayName("GET /admin/users - index()")
    class Index {

        @Test
        @DisplayName("trả về full view khi không có HX-Request header")
        void index_noHxRequest_returnsFullView() {
            when(getUserQueryUseCase.getAllUsers(any(UserFilter.class), any(PageRequest.class)))
                    .thenReturn(userPage);

            String view = userController.index(null, "firstname", "asc", 0, 10, model, null);

            assertThat(view).isEqualTo("admin/user/index");
            verify(model).addAttribute("users", userPage);
        }

        @Test
        @DisplayName("trả về fragment khi có HX-Request header")
        void index_withHxRequest_returnsFragment() {
            when(getUserQueryUseCase.getAllUsers(any(UserFilter.class), any(PageRequest.class)))
                    .thenReturn(userPage);

            String view = userController.index(null, "firstname", "asc", 0, 10, model, "true");

            assertThat(view).isEqualTo("admin/user/index :: page-content");
        }

        @Test
        @DisplayName("sort descending khi sortDir = desc")
        void index_sortDesc_useDescSort() {
            when(getUserQueryUseCase.getAllUsers(any(UserFilter.class),
                    argThat(pr -> pr.getSort().equals(Sort.by("email").descending()))))
                    .thenReturn(userPage);

            String view = userController.index("alice", "email", "desc", 0, 5, model, null);

            assertThat(view).isEqualTo("admin/user/index");
            verify(getUserQueryUseCase).getAllUsers(
                    argThat(f -> "alice".equals(f.search())),
                    argThat(pr -> pr.getSort().equals(Sort.by("email").descending())));
        }

        @Test
        @DisplayName("model nhận đủ attribute cần thiết")
        void index_modelAttributesPopulated() {
            when(getUserQueryUseCase.getAllUsers(any(), any())).thenReturn(userPage);

            userController.index("test", "firstname", "asc", 0, 10, model, null);

            verify(model).addAttribute(eq("searchValue"), eq("test"));
            verify(model).addAttribute(eq("currentSort"), eq("firstname"));
            verify(model).addAttribute(eq("currentDir"), eq("asc"));
            verify(model).addAttribute(eq("actionUrl"), eq("/admin/users"));
            verify(model).addAttribute(eq("resetUrl"), eq("/admin/users"));
            verify(model).addAttribute(eq("baseUrl"), eq("/admin/users"));
        }
    }

    @Nested
    @DisplayName("GET /admin/users/{id} - detail()")
    class Detail {

        @Test
        @DisplayName("trả về full view khi không có HX-Request")
        void detail_noHxRequest_returnsFullView() {
            when(getUserQueryUseCase.getUserById(userId)).thenReturn(userView);

            String view = userController.detail(userId, model, null);

            assertThat(view).isEqualTo("admin/user/detail");
            verify(model).addAttribute("user", userView);
        }

        @Test
        @DisplayName("trả về fragment khi có HX-Request")
        void detail_withHxRequest_returnsFragment() {
            when(getUserQueryUseCase.getUserById(userId)).thenReturn(userView);

            String view = userController.detail(userId, model, "true");

            assertThat(view).isEqualTo("admin/user/detail :: page-content");
        }
    }

    @Nested
    @DisplayName("GET /admin/users/new - createUserForm()")
    class CreateUserForm {

        @Test
        @DisplayName("trả về full form view")
        void createForm_noHxRequest_returnsFullView() {
            String view = userController.createUserForm(model, null);

            assertThat(view).isEqualTo("admin/user/form");
            verify(model).addAttribute("editMode", EditMode.CREATE);
            verify(model).addAttribute(eq("form"), any(CreateUserFormData.class));
        }

        @Test
        @DisplayName("trả về fragment khi có HX-Request")
        void createForm_withHxRequest_returnsFragment() {
            String view = userController.createUserForm(model, "true");

            assertThat(view).isEqualTo("admin/user/form :: page-content");
        }
    }

    @Nested
    @DisplayName("POST /admin/users/new - doCreateUser()")
    class DoCreateUser {

        @Test
        @DisplayName("validation lỗi → trả lại form (no HX)")
        void doCreate_validationErrors_returnsForm() {
            when(bindingResult.hasErrors()).thenReturn(true);
            CreateUserFormData form = new CreateUserFormData();

            String view = userController.doCreateUser(form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("admin/user/form");
            verifyNoInteractions(createUserUseCase);
        }

        @Test
        @DisplayName("validation lỗi + HX-Request → trả lại fragment")
        void doCreate_validationErrors_hxRequest_returnsFragment() {
            when(bindingResult.hasErrors()).thenReturn(true);
            CreateUserFormData form = new CreateUserFormData();

            String view = userController.doCreateUser(form, bindingResult, model, redirectAttributes, "true");

            assertThat(view).isEqualTo("admin/user/form :: page-content");
        }

        @Test
        @DisplayName("thành công (no HX) → redirect đến danh sách")
        void doCreate_success_noHx_redirects() {
            when(bindingResult.hasErrors()).thenReturn(false);
            CreateUserFormData form = mock(CreateUserFormData.class);
            when(form.toRequest()).thenReturn(null);

            String view = userController.doCreateUser(form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("redirect:/admin/users");
            verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        }

        @Test
        @DisplayName("thành công (HX) → trả về index fragment")
        void doCreate_success_hxRequest_returnsIndexFragment() {
            when(bindingResult.hasErrors()).thenReturn(false);
            CreateUserFormData form = mock(CreateUserFormData.class);
            when(form.toRequest()).thenReturn(null);
            when(getUserQueryUseCase.getAllUsers(any(), any())).thenReturn(userPage);

            String view = userController.doCreateUser(form, bindingResult, model, redirectAttributes, "true");

            assertThat(view).isEqualTo("admin/user/index :: page-content");
        }

        @Test
        @DisplayName("usecase ném exception → trả lại form với errorMessage")
        void doCreate_usecaseThrows_returnsFormWithError() {
            when(bindingResult.hasErrors()).thenReturn(false);
            CreateUserFormData form = mock(CreateUserFormData.class);
            when(form.toRequest()).thenReturn(null);
            doThrow(new RuntimeException("Email đã tồn tại")).when(createUserUseCase).createUser(any());

            String view = userController.doCreateUser(form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("admin/user/form");
            verify(model).addAttribute("errorMessage", "Email đã tồn tại");
        }
    }

    @Nested
    @DisplayName("GET /admin/users/{id}/update - updateUserForm()")
    class UpdateUserForm {

        @Test
        @DisplayName("trả về form với editMode UPDATE")
        void updateForm_noHxRequest_returnsFullView() {
            when(getUserQueryUseCase.getUserById(userId)).thenReturn(userView);

            String view = userController.updateUserForm(userId, model, null);

            assertThat(view).isEqualTo("admin/user/form");
            verify(model).addAttribute("editMode", EditMode.UPDATE);
        }

        @Test
        @DisplayName("trả về fragment khi có HX-Request")
        void updateForm_withHxRequest_returnsFragment() {
            when(getUserQueryUseCase.getUserById(userId)).thenReturn(userView);

            String view = userController.updateUserForm(userId, model, "true");

            assertThat(view).isEqualTo("admin/user/form :: page-content");
        }
    }

    @Nested
    @DisplayName("POST /admin/users/{id}/update - doUpdateUser()")
    class DoUpdateUser {

        @Test
        @DisplayName("validation lỗi → trả lại form")
        void doUpdate_validationErrors_returnsForm() {
            when(bindingResult.hasErrors()).thenReturn(true);
            UpdateUserFormData form = new UpdateUserFormData();

            String view = userController.doUpdateUser(userId, form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("admin/user/form");
            verifyNoInteractions(updateUserUseCase);
        }

        @Test
        @DisplayName("thành công (no HX) → redirect đến detail")
        void doUpdate_success_noHx_redirectsToDetail() {
            when(bindingResult.hasErrors()).thenReturn(false);
            UpdateUserFormData form = mock(UpdateUserFormData.class);
            when(form.toRequest()).thenReturn(null);

            String view = userController.doUpdateUser(userId, form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("redirect:/admin/users/" + userId);
        }

        @Test
        @DisplayName("thành công (HX) → trả về index fragment")
        void doUpdate_success_hxRequest_returnsIndexFragment() {
            when(bindingResult.hasErrors()).thenReturn(false);
            UpdateUserFormData form = mock(UpdateUserFormData.class);
            when(form.toRequest()).thenReturn(null);
            when(getUserQueryUseCase.getAllUsers(any(), any())).thenReturn(userPage);

            String view = userController.doUpdateUser(userId, form, bindingResult, model, redirectAttributes, "true");

            assertThat(view).isEqualTo("admin/user/index :: page-content");
        }

        @Test
        @DisplayName("usecase ném exception → trả lại form với errorMessage")
        void doUpdate_usecaseThrows_returnsFormWithError() {
            when(bindingResult.hasErrors()).thenReturn(false);
            UpdateUserFormData form = mock(UpdateUserFormData.class);
            when(form.toRequest()).thenReturn(null);
            doThrow(new RuntimeException("Username đã tồn tại")).when(updateUserUseCase).update(any(), any());

            String view = userController.doUpdateUser(userId, form, bindingResult, model, redirectAttributes, null);

            assertThat(view).isEqualTo("admin/user/form");
            verify(model).addAttribute("errorMessage", "Username đã tồn tại");
        }
    }

    @Nested
    @DisplayName("POST /admin/users/{id}/delete - doDeleteUser()")
    class DoDeleteUser {

        @Test
        @DisplayName("thành công (no HX) → redirect đến danh sách")
        void doDelete_success_noHx_redirects() {
            String view = userController.doDeleteUser(userId, model, redirectAttributes, null);

            assertThat(view).isEqualTo("redirect:/admin/users");
            verify(deleteUserUseCase).delete(userId);
            verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        }

        @Test
        @DisplayName("thành công (HX) → trả về index fragment")
        void doDelete_success_hxRequest_returnsFragment() {
            when(getUserQueryUseCase.getAllUsers(any(), any())).thenReturn(userPage);

            String view = userController.doDeleteUser(userId, model, redirectAttributes, "true");

            assertThat(view).isEqualTo("admin/user/index :: page-content");
        }

        @Test
        @DisplayName("usecase ném exception (no HX) → redirect với errorMessage")
        void doDelete_usecaseThrows_noHx_redirectWithError() {
            doThrow(new RuntimeException("Không thể xóa admin")).when(deleteUserUseCase).delete(userId);

            String view = userController.doDeleteUser(userId, model, redirectAttributes, null);

            assertThat(view).isEqualTo("redirect:/admin/users");
            verify(redirectAttributes).addFlashAttribute("errorMessage", "Không thể xóa admin");
        }

        @Test
        @DisplayName("usecase ném exception (HX) → index fragment với errorMessage")
        void doDelete_usecaseThrows_hxRequest_returnsFragmentWithError() {
            doThrow(new RuntimeException("Lỗi xóa")).when(deleteUserUseCase).delete(userId);
            when(getUserQueryUseCase.getAllUsers(any(), any())).thenReturn(userPage);

            String view = userController.doDeleteUser(userId, model, redirectAttributes, "true");

            assertThat(view).isEqualTo("admin/user/index :: page-content");
            verify(model).addAttribute("errorMessage", "Lỗi xóa");
        }
    }

    @Nested
    @DisplayName("GET /admin/users/{id}/change-password - changePasswordForm()")
    class ChangePasswordForm {

        @Test
        @DisplayName("trả về full view")
        void changePasswordForm_noHxRequest_returnsFullView() {
            String view = userController.changePasswordForm(userId, model, null);

            assertThat(view).isEqualTo("admin/user/change-password");
            verify(model).addAttribute("userId", userId);
            verify(model).addAttribute(eq("form"), any(ChangePasswordFormData.class));
        }

        @Test
        @DisplayName("trả về fragment khi có HX-Request")
        void changePasswordForm_withHxRequest_returnsFragment() {
            String view = userController.changePasswordForm(userId, model, "true");

            assertThat(view).isEqualTo("admin/user/change-password :: page-content");
        }
    }

    @Nested
    @DisplayName("POST /admin/users/{id}/change-password - doChangePassword()")
    class DoChangePassword {

        @Test
        @DisplayName("validation lỗi → trả lại form")
        void doChangePassword_validationErrors_returnsForm() {
            when(bindingResult.hasErrors()).thenReturn(true);
            ChangePasswordFormData form = new ChangePasswordFormData();

            String view = userController.doChangePassword(userId, form, bindingResult, model, redirectAttributes);

            assertThat(view).isEqualTo("admin/user/change-password");
            verify(model).addAttribute("userId", userId);
            verifyNoInteractions(changePasswordUseCase);
        }

        @Test
        @DisplayName("thành công → redirect đến detail")
        void doChangePassword_success_redirectsToDetail() {
            when(bindingResult.hasErrors()).thenReturn(false);
            ChangePasswordFormData form = mock(ChangePasswordFormData.class);
            when(form.toRequest()).thenReturn(null);

            String view = userController.doChangePassword(userId, form, bindingResult, model, redirectAttributes);

            assertThat(view).isEqualTo("redirect:/admin/users/" + userId);
            verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        }

        @Test
        @DisplayName("usecase ném exception → trả lại form với errorMessage")
        void doChangePassword_usecaseThrows_returnsFormWithError() {
            when(bindingResult.hasErrors()).thenReturn(false);
            ChangePasswordFormData form = mock(ChangePasswordFormData.class);
            when(form.toRequest()).thenReturn(null);
            doThrow(new RuntimeException("Mật khẩu cũ không đúng")).when(changePasswordUseCase).change(any(), any());

            String view = userController.doChangePassword(userId, form, bindingResult, model, redirectAttributes);

            assertThat(view).isEqualTo("admin/user/change-password");
            verify(model).addAttribute("errorMessage", "Mật khẩu cũ không đúng");
        }
    }

}
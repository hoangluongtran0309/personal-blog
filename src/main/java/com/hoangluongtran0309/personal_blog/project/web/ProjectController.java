package com.hoangluongtran0309.personal_blog.project.web;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoangluongtran0309.personal_blog.project.Project;
import com.hoangluongtran0309.personal_blog.project.ProjectId;
import com.hoangluongtran0309.personal_blog.project.ProjectService;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

@Controller
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public String projects(Model model,
            @SortDefault.SortDefaults({
        @SortDefault(sort = "publishDate", direction = Sort.Direction.DESC)
    }) Pageable pageable) {
        model.addAttribute("activePage", "projects");
        model.addAttribute("projects", projectService.getAllPublishedProjects(pageable));
        return "projects/projects";
    }

    @GetMapping("/projects/{id}")
    public String detail(@PathVariable ProjectId id, Model model) {
        model.addAttribute("activePage", "projects");
        model.addAttribute("project", projectService.getProjectById(id));
        return "projects/detail";
    }

    @GetMapping("/admin/projects")
    public String admin(Model model,
            @SortDefault.SortDefaults({
        @SortDefault(sort = "publishDate", direction = Sort.Direction.DESC)
    }) Pageable pageable) {
        model.addAttribute("activePage", "projects");
        model.addAttribute("projects", projectService.getAllProjects(pageable));
        return "admin/projects/projects";
    }

    @GetMapping("/projects/new")
    @HxRequest
    public String createProjectForm(Model model) {
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("project", new CreateProjectFormData());
        return "admin/projects/form :: form";
    }

    @PostMapping("/projects")
    @HxRequest
    public String createProject(@Validated @ModelAttribute("project") CreateProjectFormData data,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.CREATE);
            return "admin/projects/form :: form";
        }

        Project savedProject = projectService.createProject(data.toParameters());
        model.addAttribute("project", savedProject);
        return "admin/projects/item :: item";
    }

    @GetMapping("/projects/{id}/edit")
    @HxRequest
    public String editProjectForm(@PathVariable ProjectId id, Model model) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("project", EditProjectFormData.fromProject(project));
        return "admin/projects/form :: form";
    }

    @PostMapping("/projects/{id}")
    @HxRequest
    public String editProject(@PathVariable ProjectId id,
            @Validated @ModelAttribute("project") EditProjectFormData data,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.UPDATE);
            return "admin/projects/form :: form";
        }

        projectService.updateProject(id, data.toParameters());
        Project updatedProject = projectService.getProjectById(id);
        model.addAttribute("project", updatedProject);
        return "admin/projects/item :: item";
    }

    @DeleteMapping("/projects/{id}")
    @HxRequest
    @ResponseBody
    public String deleteProject(@PathVariable ProjectId id, Model model) {
        projectService.deleteProject(id);
        return "";
    }

    @GetMapping("/projects/{id}")
    @HxRequest
    public String item(@PathVariable ProjectId id, Model model) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "admin/projects/item :: item";
    }

}

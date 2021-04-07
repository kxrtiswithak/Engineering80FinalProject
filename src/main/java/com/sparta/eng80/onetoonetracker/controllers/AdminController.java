package com.sparta.eng80.onetoonetracker.controllers;

import com.sparta.eng80.onetoonetracker.entities.GroupEntity;
import com.sparta.eng80.onetoonetracker.entities.TrainerEntity;
import com.sparta.eng80.onetoonetracker.entities.UserEntity;
import com.sparta.eng80.onetoonetracker.security.PasswordEncryptor;
import com.sparta.eng80.onetoonetracker.services.AdminService;
import com.sparta.eng80.onetoonetracker.services.GroupService;
import com.sparta.eng80.onetoonetracker.services.SecurityService;
import com.sparta.eng80.onetoonetracker.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * The Admin controller. Used for all methods available to the user with the role admin.
 *
 */
@Controller
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final GroupService groupService;
    private final SecurityService securityService;

    public AdminController(AdminService adminService, UserService userService, GroupService groupService, SecurityService securityService) {
        this.adminService = adminService;
        this.userService = userService;
        this.groupService = groupService;
        this.securityService = securityService;
    }

    @GetMapping("/trainers")
    public String getAllTrainers(ModelMap modelMap){
        if (securityService.requiresPasswordChange()) {
            return "redirect:/change-password";
        }
        Iterable<TrainerEntity> trainers = adminService.findAllTrainers();
        modelMap.addAttribute("trainers", trainers);
        Iterable<GroupEntity> groups = groupService.findAllUnassigned();
        modelMap.addAttribute("groups", groups);
        TrainerEntity trainer = new TrainerEntity();
        modelMap.addAttribute("newTrainer", trainer);
        return "redirect:/";
    }

    @PostMapping("/add-trainer")
    public String addTrainer(@RequestParam Integer groupId, @RequestParam String firstName, @RequestParam String lastName){
        TrainerEntity trainer = new TrainerEntity();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        Optional<GroupEntity> group = groupService.findById(groupId);
        if(group.isPresent()){
            trainer.setGroup(group.get());
        }
        UserEntity user = new UserEntity();
        user.setRole("ROLE_TRAINER");
        PasswordEncryptor passwordEncryptor = new PasswordEncryptor();
        PasswordEncoder passwordEncoder = passwordEncryptor.getBCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        String email = firstName.toLowerCase().toCharArray()[0] +
                lastName.trim().toLowerCase();
        Set<String> emails = userService.getAllEmails();
        if(!emails.contains(email +"@sparta.com")){
            user.setEmail(email +"@sparta.com");
        } else {
            boolean valid = false;
            int i = 1;
            while(!valid){
                if (!emails.contains(email + i + "@sparta.com")) {
                    user.setEmail(email + i + "@sparta.com");
                    valid = true;
                } else i++;
            }
        }
        trainer.setUser(user);
        adminService.saveTrainer(trainer);
        return "redirect:/trainers";
    }

    @PostMapping("/remove-trainer")
    public String removeTrainer(@RequestParam int trainerId, boolean confirmation){
        if(confirmation){
            adminService.deleteTrainerById(trainerId);
        }
        return "redirect:/trainers";
    }

    @GetMapping("/trainers/{trainerId}")
    public String findTrainer(Model model, @PathVariable(name = "trainerId") Integer trainerId){
        if (securityService.requiresPasswordChange()) {
            return "redirect:/change-password";
        }
        Optional<TrainerEntity> trainer = adminService.findTrainerById(trainerId);
        if(trainer.isEmpty()){
            //Return trainer not found
            return "trainer-details";
        }else {
            model.addAttribute("trainer", trainer.get());
            return "trainer-details";
        }
    }

    @PostMapping("/edit-trainer")
    public String editTrainer(@RequestParam int trainerId, @RequestParam String firstName, @RequestParam String lastName){
        adminService.editTrainer(trainerId, firstName, lastName);
        return "redirect:/trainers";
    }
}

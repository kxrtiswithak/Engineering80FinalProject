package com.sparta.eng80.onetoonetracker.controllers;

import com.sparta.eng80.onetoonetracker.entities.GroupEntity;
import com.sparta.eng80.onetoonetracker.entities.TraineeEntity;
import com.sparta.eng80.onetoonetracker.entities.TrainerEntity;
import com.sparta.eng80.onetoonetracker.services.GroupService;
import com.sparta.eng80.onetoonetracker.services.TraineeService;
import com.sparta.eng80.onetoonetracker.services.TrainerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class TrainerController {

    private final TrainerService trainerService;
    private final TraineeService traineeService;

    public TrainerController(TrainerService trainerService, TraineeService traineeService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
    }

    @GetMapping("trainer/{trainerId}")
    public ModelMap getTrainees(ModelMap model, int trainerId) {
        Iterable<TraineeEntity> trainees = new ArrayList<>();
        Optional<TrainerEntity> trainer = trainerService.findById(trainerId);
        if (trainer.isEmpty()) {
            //trainer id isn't valid, return empty list
        } else {
            GroupEntity groupEntity = trainer.get().getGroup();
            trainees = traineeService.findByGroupId(groupEntity.getGroupId());

        }
        model.addAttribute("traineesInTrainerGroup", trainees);
        return model;
    }
}
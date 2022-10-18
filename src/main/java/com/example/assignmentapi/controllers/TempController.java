package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.Views;
import com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.service.TempService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/temps")
public class TempController {
    @Autowired
    TempService tempService;

    // POST /temps
        // Create a temp from data in request body
    @JsonView(Views.TempWithSubsAndJobs.class)
    @PostMapping
    public ResponseEntity<Object> createTemp(@Valid @RequestBody TempCreateData data) {
        TempReturnDTO temp = tempService.createTemp(data);
        if (temp != null) {
            return ResponseEntity.ok(temp);
        } else {
            return ResponseEntity.badRequest().body(
                    "Bad request - there was likely an issue with the provided data."
            );
        }

    }

    // GET /temps
        // List all temps
    // GET /temps?jobId={id}
        // List temps that are available for the job with jobId based on the jobs date range
    @JsonView(Views.TempWithJobs.class)
    @GetMapping
    public ResponseEntity<List<TempReturnDTO>> getTemps(
            @RequestParam(name = "jobId", required = false) Integer jobId
    ) {
        List<TempReturnDTO> temps;
        if (jobId != null) {
            temps = tempService.getAvailable(jobId);
        } else {
            temps = tempService.getAllTemps();
        }
        return ResponseEntity.ok(temps);
    }


    // GET /temps/{tempId}
        // Get temp by id
    @JsonView(Views.TempWithSubsAndJobs.class)
    @GetMapping(path = "/{tempId}")
    public ResponseEntity<Object> getTempById(@NotNull @PathVariable(name = "tempId") Integer tempId) {
        TempReturnDTO temp = tempService.getTempById(tempId);
        if (temp != null) {
            return ResponseEntity.ok(temp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /temps/tree
        // Get full hierarchy tree
    @JsonView(Views.TempWithSubs.class)
    @GetMapping(path = "/tree")
    public ResponseEntity<List<TempReturnDTO>> getTempHierarchy() {
        List<TempReturnDTO> tree = tempService.getTempHierarchy();
        return ResponseEntity.ok(tree);
    }
}
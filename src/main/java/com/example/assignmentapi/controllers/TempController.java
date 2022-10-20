package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.Views;
import com.example.assignmentapi.dto.temp.*;
import com.example.assignmentapi.service.TempService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> createTemp(@Valid @RequestBody TempCreateData data) {
        TempReturnDTO temp = tempService.createTemp(data);
        return ResponseEntity.ok(temp);
    }

    // GET /temps
        // List all temps
    // GET /temps?jobId={id}
        // List temps that are available for the job with jobId based on the jobs date range
    @JsonView(Views.TempWithJobs.class)
    @GetMapping
    @PreAuthorize("hasAuthority('user') or hasAuthority('admin')")
    public ResponseEntity<List<TempReturnDTO>> getTemps(@RequestParam(name = "jobId", required = false) Integer jobId) {
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
    @PreAuthorize("hasAuthority('user') or hasAuthority('admin')")
    public ResponseEntity<Object> getTempById(@NotNull @PathVariable(name = "tempId") Integer tempId) {
        TempReturnDTO temp = tempService.getTempById(tempId);
        return ResponseEntity.ok(temp);
    }

    // GET /temps/tree
        // Get full hierarchy tree
    @JsonView(Views.TempWithSubs.class)
    @GetMapping(path = "/tree")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<TempReturnDTO>> getTempHierarchy() {
        List<TempReturnDTO> tree = tempService.getTempHierarchy();
        return ResponseEntity.ok(tree);
    }
}

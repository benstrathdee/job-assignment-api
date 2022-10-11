package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.temp.TempCreateData;
import com.example.assignmentapi.dto.temp.TempWithJobs;
import com.example.assignmentapi.dto.temp.TempWithNestedSubs;
import com.example.assignmentapi.dto.temp.TempWithSubsAndJobs;
import com.example.assignmentapi.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path="/temps")
public class TempController {
    @Autowired
    TempService tempService;

    // POST /temps
        // Create a temp from data in request body
    @PostMapping
    public ResponseEntity<Object> createTemp (@Valid @RequestBody TempCreateData data) {
        TempWithJobs temp = tempService.createTemp(data);
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
    @GetMapping
    public ResponseEntity<List<TempWithJobs>> getTemps (
            @RequestParam(name = "jobId", required = false) Integer jobId
    ) {
        List<TempWithJobs> temps;
        if (jobId != null) {
            temps = tempService.getAvailable(jobId);
        } else {
            temps = tempService.getAllTemps();
        }
        return ResponseEntity.ok(temps);
    }


    // GET /temps/{tempId}
        // Get temp by id
    @GetMapping(path = "/{tempId}")
    public ResponseEntity<Object> getTempById (@NotNull @PathVariable(name = "tempId") Integer tempId) {
        TempWithSubsAndJobs temp = tempService.getTempById(tempId);
        if (temp != null) {
            return ResponseEntity.ok(temp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /temps/tree
        // Get full hierarchy tree
    @GetMapping(path = "/tree")
    public ResponseEntity<List<TempWithNestedSubs>> getTempHierarchy () {
        List<TempWithNestedSubs> tree = tempService.getTempHierarchy();
        return ResponseEntity.ok(tree);
    }
}

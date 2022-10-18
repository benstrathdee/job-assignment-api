package com.example.assignmentapi.controllers;

import com.example.assignmentapi.dto.job.JobCreateData;
import com.example.assignmentapi.dto.job.JobReturnDTO;
import com.example.assignmentapi.dto.job.JobUpdateData;
import com.example.assignmentapi.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path="/jobs")
public class JobController {
    @Autowired
    JobService jobService;

    // POST /jobs
        // Creates a job from data in request body
    @PostMapping
    public ResponseEntity<Object> createJob (@Valid @RequestBody JobCreateData data ) {
        JobReturnDTO job = jobService.createJob(data);
        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.badRequest().body(
                    "Bad request - there was likely an issue with the provided data."
            );
        }
    }

    // PATCH /jobs/{id}
        // Updates job with id using data from request body
    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateJob (@PathVariable Integer id, @RequestBody JobUpdateData data ) {
        JobReturnDTO job = jobService.updateJob(id, data);
        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.badRequest().body(
                    "Bad request - there was likely an issue with the provided data."
            );
        }
    }

    // GET /jobs
        // Fetch all jobs
    // GET /jobs?assigned={true|false}
        // Filter by whether a job is assigned to a temp or not
    @GetMapping
    public ResponseEntity<List<JobReturnDTO>> getJobs (@RequestParam(required = false) Boolean assigned ) {
        List<JobReturnDTO> jobs;
        if (assigned != null) {
            jobs = jobService.getJobsByAssigned(assigned);
        } else {
            jobs = jobService.getAllJobs();
        }
        return ResponseEntity.ok(jobs);
    }

    // GET /jobs/{id}
        // get the job with id
    @GetMapping(path = "/{id}")
    public ResponseEntity<JobReturnDTO> getJobById (@PathVariable Integer id ) {
        JobReturnDTO job = jobService.getJobById(id);
        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
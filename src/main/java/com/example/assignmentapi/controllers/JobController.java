package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.Job.JobCreateData;
import com.example.assignmentapi.DTOs.Job.JobWithTemp;
import com.example.assignmentapi.DTOs.Job.JobUpdateData;
import com.example.assignmentapi.Services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path="/jobs")
public class JobController {
    @Autowired
    JobService service;

    // POST /jobs
        // Creates a job from data in request body
    @PostMapping
    public ResponseEntity<JobWithTemp> createJob (@Valid @RequestBody JobCreateData data ) {
        JobWithTemp job = this.service.create(data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // PATCH /jobs/{id}
        // Updates job with id using data from request body
    @PatchMapping(path = "/{id}")
    public ResponseEntity<JobWithTemp> updateJob (@PathVariable Integer id, @RequestBody JobUpdateData data ) {
        JobWithTemp job = this.service.update(id, data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // GET /jobs
        // Fetch all jobs
    // GET /jobs?assigned={true|false}
        // Filter by whether a job is assigned to a temp or not
    @GetMapping
    public ResponseEntity<List<JobWithTemp>> getJobs (@RequestParam(required = false) Boolean assigned ) {
        List<JobWithTemp> jobs;
        if (assigned != null) {
            jobs = this.service.getByAssigned(assigned);
        } else {
            jobs = this.service.getAll();
        }
        return new ResponseEntity<>(jobs, jobs != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // GET /jobs/{id}
        // get the job with id
    @GetMapping(path = "/{id}")
    public ResponseEntity<JobWithTemp> getJob (@PathVariable Integer id ) {
        JobWithTemp job = this.service.getById(id);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
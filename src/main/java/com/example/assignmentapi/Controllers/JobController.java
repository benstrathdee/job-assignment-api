package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.Job.JobCreateDTO;
import com.example.assignmentapi.DTOs.Job.JobGetDTO;
import com.example.assignmentapi.DTOs.Job.JobUpdateDTO;
import com.example.assignmentapi.Services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping(path="/jobs")
public class JobController {
    @Autowired
    JobService service;

    // POST /jobs
    @PostMapping
    public ResponseEntity<JobGetDTO> createJob (@Valid @RequestBody JobCreateDTO data ) {
        // Creates a job from data in request body
        JobGetDTO job = this.service.create(data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // PATCH /jobs/{id}
    @PatchMapping(path = "/{id}")
    public ResponseEntity<JobGetDTO> updateJob ( @PathVariable Integer id, @RequestBody JobUpdateDTO data ) {
        // Updates job with id using data from request body
        // TODO : Add alternative 404 response when job with id not found
        JobGetDTO job = this.service.update(id, data);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // GET /jobs
    // GET /jobs?assigned={true|false}
    @GetMapping
    public ResponseEntity<ArrayList<JobGetDTO>> getJobs ( @RequestParam(required = false) Boolean assigned ) {
        ArrayList<JobGetDTO> jobs;
        if (assigned != null) {
            // Filter by whether a job is assigned to a temp or not
            jobs = this.service.getByAssigned(assigned);
        } else {
            // Fetch all jobs
            jobs = this.service.all();
        }
        return new ResponseEntity<>(jobs, jobs != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // GET /jobs/{id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<JobGetDTO> getJob ( @PathVariable Integer id ) {
        // get the job with id
        JobGetDTO job = this.service.getById(id);
        return new ResponseEntity<>(job, job != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.Temp.TempCreateData;
import com.example.assignmentapi.DTOs.Temp.TempWithJobs;
import com.example.assignmentapi.DTOs.Temp.TempWithNestedSubs;
import com.example.assignmentapi.DTOs.Temp.TempWithSubsAndJobs;
import com.example.assignmentapi.Services.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path="/temps")
public class TempController {
    @Autowired
    TempService service;


    // POST /temps
        // Create a temp from data in request body
    @PostMapping
    public ResponseEntity<TempWithJobs> createTemp (@Valid @RequestBody TempCreateData data) {
        TempWithJobs temp = this.service.create(data);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // GET /temps
        // List all temps
    // GET /temps?jobId={jobId}
        // List temps that are available for the job with jobId based on the jobs date range
    @GetMapping
    public ResponseEntity<List<TempWithJobs>> getTemps (@Valid @RequestParam(required = false) Integer jobId) {
        List<TempWithJobs> temps;
        if (jobId != null) {
            temps = this.service.getAvailable(jobId);
        } else {
            temps = this.service.getAll();
        }
        return new ResponseEntity<>(temps, temps != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // GET /temps/{id}
        // Get temp by id
    @GetMapping(path = "/{id}")
    public ResponseEntity<TempWithSubsAndJobs> getTemp (@PathVariable Integer id) {
        TempWithSubsAndJobs temp = this.service.getIndividual(id);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // GET /temps/tree
        // Get full hierarchy tree
    @GetMapping(path = "/tree")
    public ResponseEntity<List<TempWithNestedSubs>> getTree () {
        List<TempWithNestedSubs> tree = this.service.getHierarchy();
        return new ResponseEntity<>(tree, tree != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}

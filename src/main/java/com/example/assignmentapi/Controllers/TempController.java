package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.TempCreateDTO;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;
import com.example.assignmentapi.Services.JobService;
import com.example.assignmentapi.Services.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path="/temps")
public class TempController {
    @Autowired
    TempService service;


    // POST /temps
    @PostMapping
    public ResponseEntity<Temp> createTemp (@Valid @RequestBody TempCreateDTO data) {
        // Create a temp from data in request body
        Temp temp = this.service.create(data);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // GET /temps
    // GET /temps?jobId={jobId}
    @GetMapping
    public ResponseEntity<ArrayList<Temp>> getTemps (@Valid @RequestParam(required = false) Integer jobId) {
        ArrayList<Temp> temps;
        if (jobId != null) {
            // List temps that are available for the job with jobId based on the jobs date range
            temps = this.service.getAvailable(jobId);
        } else {
            // List all temps
            temps = this.service.all();
        }
        return new ResponseEntity<>(temps, HttpStatus.OK);
    }

    // GET /temps/{id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<Temp> getTemp (@PathVariable Integer id) {
        // Get temp by id
        Temp temp = this.service.getById(id);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}

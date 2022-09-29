package com.example.assignmentapi.Controllers;

import com.example.assignmentapi.DTOs.Temp.TempCreateDTO;
import com.example.assignmentapi.DTOs.Temp.TempGetDTO;
import com.example.assignmentapi.DTOs.Tree.TreeGetDTO;
import com.example.assignmentapi.Services.TempService;
import com.example.assignmentapi.Services.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping(path="/temps")
public class TempController {
    @Autowired
    TempService service;
    @Autowired
    TreeService treeService;


    // POST /temps
    @PostMapping
    public ResponseEntity<TempGetDTO> createTemp (@Valid @RequestBody TempCreateDTO data) {
        // Create a temp from data in request body
        TempGetDTO temp = this.service.create(data);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // GET /temps
    // GET /temps?jobId={jobId}
    @GetMapping
    public ResponseEntity<ArrayList<TempGetDTO>> getTemps (@Valid @RequestParam(required = false) Integer jobId) {
        ArrayList<TempGetDTO> temps;
        if (jobId != null) {
            // List temps that are available for the job with jobId based on the jobs date range
            temps = this.service.getAvailable(jobId);
        } else {
            // List all temps
            temps = this.service.all();
        }
        return new ResponseEntity<>(temps, temps != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    // GET /temps/{id}
    @GetMapping(path = "/{id}")
    public ResponseEntity<TempGetDTO> getTemp (@PathVariable Integer id) {
        // Get temp by id
        TempGetDTO temp = this.service.getById(id);
        return new ResponseEntity<>(temp, temp != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

//    // GET /temps/tree
//    @GetMapping(path = "/tree")
//    public ResponseEntity<TreeGetDTO> getTree () {
//        // Get full hierarchy tree
//        TreeGetDTO tree = this.treeService.getTree();
//        return new ResponseEntity<>(tree, tree != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
//    }
}

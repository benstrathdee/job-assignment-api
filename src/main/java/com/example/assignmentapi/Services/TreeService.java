package com.example.assignmentapi.Services;

import com.example.assignmentapi.DTOs.Tree.TreeGetDTO;
import com.example.assignmentapi.Repositories.TempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class TreeService {
    @Autowired
    TempRepository repository;

//    public TreeGetDTO getTree () {
//        Optional<TreeGetDTO> tree = this.repository.getTree();
//        return tree.orElse(null);
//    }

//
}

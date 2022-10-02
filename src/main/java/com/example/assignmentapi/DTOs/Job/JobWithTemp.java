package com.example.assignmentapi.DTOs.Job;

import com.example.assignmentapi.DTOs.Temp.TempAsChild;
import com.example.assignmentapi.Entities.Job;

public class JobWithTemp extends JobAsChild {
    private final TempAsChild temp;

    public TempAsChild getTemp() {
        return temp;
    }

    public JobWithTemp(Job job, TempAsChild temp) {
        super(job);
        this.temp = temp;
    }
}

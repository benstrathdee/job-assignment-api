package com.example.assignmentapi.dto.temp;

import com.example.assignmentapi.dto.Views;
import com.example.assignmentapi.dto.job.JobReturnDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import java.util.List;

@Value // all fields are private and final by default
@Builder(builderClassName = "Builder")
public class TempReturnDTO {
    @JsonView(Views.TempDefault.class) // JSON views define what fields are shown when returned to client
    @NonNull Integer id;

    @JsonView(Views.TempDefault.class)
    @NonNull String firstName;

    @JsonView(Views.TempDefault.class)
    @NonNull String lastName;

    @JsonView({Views.TempWithJobs.class, Views.TempWithSubsAndJobs.class})
    @Singular List<JobReturnDTO> jobs; // builder can add one item at a time

    @JsonView(Views.TempWithSubsAndJobs.class)
    @JsonIgnoreProperties({"jobs", "subordinates"})
    @Singular List<TempReturnDTO> subordinates;

    @JsonView(Views.TempWithSubs.class)
    @JsonIgnoreProperties({"jobs"})
    @Singular List<TempReturnDTO> directSubordinates;
}

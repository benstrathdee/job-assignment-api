package com.example.assignmentapi.dto.job;

import com.example.assignmentapi.dto.Views;
import com.example.assignmentapi.dto.temp.TempReturnDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value // all fields are private and final by default
@Builder(builderClassName = "Builder")
public class JobReturnDTO {
    @JsonView({Views.TempWithJobs.class, Views.TempWithSubsAndJobs.class}) // make sure these fields are shown when returning a temp's jobs
    @NonNull Integer id;

    @JsonView({Views.TempWithJobs.class, Views.TempWithSubsAndJobs.class})
    @NonNull String name;

    @JsonView({Views.TempWithJobs.class, Views.TempWithSubsAndJobs.class})
    @NonNull Long startDate;

    @JsonView({Views.TempWithJobs.class, Views.TempWithSubsAndJobs.class})
    @NonNull Long endDate;

    @JsonIgnoreProperties({"jobs", "subordinates", "directSubordinates"}) // Don't show these fields from the temp when returned to client
    TempReturnDTO temp;
}

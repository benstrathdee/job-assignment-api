# Job Assignment API

## Summary
You task is to build a Resourcing API using the Java Spring Boot framework, that allows consumers to assign temps to jobs.

## Endpoints

* `POST /jobs`   - Creates a job
* `PATCH /jobs/{id}` - Updates job, endpoint should be used to assign temps to jobs
* `GET /jobs` - Fetch all jobs
* `GET /jobs?assigned={true|false}` - Filter by whether a job is assigned to a temp or not
* `GET /jobs/{id}` - (id, name, tempId, startDate, endDate)
* `POST /temps` - Create a temp
* `GET /temps` - List all temps
* `GET /temps?jobId={jobId}` - List temps that are available for a job based on the jobs date range
* `GET /temps/{id}` - get temp by id (should also display jobs they’ve been assigned to)

## Example Payloads

```
// GET /jobs/{id}
{
	id: ...,
	name: ...,
	startDate: ...,
	endDate: ...,
	temp: {
		id: ...,
		firstName: ...,
		lastName: ...,
	} // temp can also be null if a temp hasn't been assigned to the job
}

// GET /temps/{id}
{
	id: ...,
	firstName: ...,
	lastName: ...,
	jobs: [{
		id: ...,
		name: ...,
		startDate: ...,
		endDate: ...,
	}, ...] // can be empty if temp hasn't been assigned to jobs
}
```

## Assumptions

* Temps can only have one job at a time (can’t be doing 2 jobs on the same date)
* Temps can have many jobs, and job can have 1 temp assigned
* Should be able to assign existing temps to jobs via the POST /jobs & PATCH /jobs/{id}
* You must use a relational database

## Bonus

* Temps should be able to manage other temps (will require an additional field)
* When you request a temp record it should display the reports of that temp
* Should be represented in the database as a nested set
* `GET /temps/tree` - should display the whole tree of temps
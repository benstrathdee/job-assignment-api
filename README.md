# Job Assignment API

## Summary
Your task is to build a Resourcing API using the Java Spring Boot framework, that allows consumers to assign temps to jobs.

## Endpoints

### Auth

* `POST` `/auth/register` - Registers a new user in the DB
* `POST` `/auth/login` - When a user posts a valid username + password in the request body, 
a JWT Set_Cookie is returned to the client containing a JWT necessary for all other calls
* `POST` `/auth/logout` - Log out a currently logged-in user

### Jobs

* `POST` `/jobs` - Creates a job
* `PATCH` `/jobs/{id}` - Updates job, endpoint should be used to assign temps to jobs
* `GET` `/jobs` - Fetch all jobs
* `GET` `/jobs?assigned={true|false}` - Filter by whether a job is assigned to a temp or not
* `GET` `/jobs/{id}` - (id, name, tempId, startDate, endDate)

### Temps

* `POST` `/temps` - Create a temp
* `GET` `/temps` - List all temps
* `GET` `/temps?jobId={jobId}` - List temps that are available for a job based on the jobs date range
* `GET` `/temps/{id}` - get temp by id (should also display jobs they’ve been assigned to)

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
* Should be able to assign existing temps to jobs via `POST` `/jobs` & `PATCH` `/jobs/{id}`
* You must use a relational database
* Temps should be able to manage other temps (will require an additional field)
* When you request a temp record it should display the reports of that temp
* Should be represented in the database as a nested set
* `GET` `/temps/tree` - should display the whole tree of temps
* Users should have to authenticate with a JWT when making requests

## Notes

### Security

* All requests other than those to `/auth/**` endpoints require JWT for authentication. This JWT is passed
in a `HttpOnly` cookie to the client upon successful authentication through `/auth/login`, so it is not
accessible through JS
* All requests that modify data (`POST` or `PATCH` in this case) other than those to `/auth/**`
require an `X-XSRF-TOKEN` header to prevent CSRF attacks. This token is initially passed to the client 
in a `XSRF-TOKEN` cookie upon a successful request to any of the `/auth/**` endpoints, and a fresh one is sent with each
subsequent request. The client must implement a means of taking this token from the cookie and sending it as a header with 
each modifying request. Ideally even requests to the `/auth/**` endpoints should require this token, but this
requires a pre-session implementation

### RSA Keys
* JWT implementation requires a public and private RSA KeyPair, rather than use a tool like OpenSSL
I made a little utility class for generating a keypair and writing them to files.
* Utility class also has a loader method to load the keys from files - used in WebSecurityConfig
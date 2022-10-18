# Job Assignment API

## Summary
Your task is to build a Resourcing API using the Java Spring Boot framework, that allows consumers to assign temps to jobs.

## Endpoints

* `POST` `/authenticate` - When a user posts a valid username + password using Basic Auth 
(User: "user", Password: "password"), server will return a JWT valid for 10 minutes. 
This JWT needs to be included in an Authorization - Bearer token by the client for all other calls 
* `POST` `/jobs` - Creates a job
* `PATCH` `/jobs/{id}` - Updates job, endpoint should be used to assign temps to jobs
* `GET` `/jobs` - Fetch all jobs
* `GET` `/jobs?assigned={true|false}` - Filter by whether a job is assigned to a temp or not
* `GET` `/jobs/{id}` - (id, name, tempId, startDate, endDate)
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

## Bonus

* Temps should be able to manage other temps (will require an additional field)
* When you request a temp record it should display the reports of that temp
* Should be represented in the database as a nested set
* `GET` `/temps/tree` - should display the whole tree of temps

## Extra Bonus

* JWT implementation - users should have to authenticate with a JWT when making requests

## Notes

### JWT
* Benefit of JWT implementation is that it's stateless: there's no need to store stuff in a DB
and retrieve it each time, so requests are pretty quick
* It's disadvantageous in that a JWT can not be revoked once it's been issued without making 
a stateful solution, which then defeats the purpose
* Most useful for situations like a single sign-on: e.g. an application that makes calls to a 
separate, secured API. The application can issue the client a JWT which gets passed to the API 
with each request.
* Need to be stored by the client in a way that doesn't leave it vulnerable to XSS attacks 
(so localStorage and cookies are no good). If your Auth and API servers are hosted on the same 
domain, a SameSite cookie is a good solution. 
  * Recommended storage solution is:
    * Store the token using the browser sessionStorage container.
    * Add it as a Bearer HTTP Authentication header with JavaScript when calling services.
    * Add some sort of fingerprint information (e.g. a claim that has to be verified by the server) 
    to the token.
  * Storing in sessionStorage exposes the token to XSS attacks, but adding a fingerprint of some 
  sort prevents the re-use of any exposed token

### General Security
* As mentioned in comments, passing Basic Auth over HTTP is not secure as base64 is only encoded, 
not encrypted. Ideally you should be using HTTPS but that would require a TLS cert which felt a 
little too far for this project

### RSA Keys
* JWT implementation requires a public and private RSA KeyPair, rather than use a tool like OpenSSL
I made a little utility class for generating a keypair and writing them to files.
* Utility class also has a loader method to load the keys from files - used in WebSecurityConfig
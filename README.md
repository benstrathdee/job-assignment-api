# Job Assignment API

## Description

### Original

Your task is to build a Resourcing API using the Java Spring Boot framework, that allows consumers to assign temps to jobs.

### Additional

This task started as a small project from a simple MVP with just a few endpoints but has grown to become my repo for trying
out all the various things available in Spring Boot + Spring Security. It features a full JWT implementation with
refresh token rotation, CSRF protection, custom exception handling, and my first attempt at using the Builder 
design pattern to simplify the construction of DTOs to the client.

## Table of Contents

* [Usage](#usage)
  + [Running from Project Files](#running-from-project-files)
  + [Standalone Runnable](#standalone-runnable)
* [Techs](#techs)
* [Endpoints](#endpoints)
  + [Auth](#auth)
  + [Jobs](#jobs)
  + [Temps](#temps)
* [Notes](#notes)
  + [Security](#security)
  + [RSA Keys](#rsa-keys)
  + [General](#general)

## Usage

* Requires JDK17+

1. `git clone` this repo
2. An RSA KeyPair (with names `key.pub` and `key.priv`) is required for JWT encoding/decoding. 
The app will look for these under a `certificates` directory in the location it is being run from.
   * I wrote a little CLI for generating these using Java which can be found
     [here](https://github.com/benstrathdee/rsa-key-utility)

### Running from Project Files

1. In a new command line window within the cloned directory, run `./mvnw spring-boot:run`

### Standalone Runnable

1. In a new command line window within the cloned directory, run `./mvnw clean install`
2. A runnable `.jar` is generated in the `/target` directory
3. This `.jar` can be run with the command `java -jar [filename.jar]`

Some sample data is initialised for testing purposes (such as with the provided Postman collection).

## Techs 

* JDK 17+
* Spring from Spring Initializr with the following dependencies:
  * Spring Boot 2.7.5
  * Spring Web
  * Spring Security
  * Spring Data JPA 
  * Spring Boot Actuator
  * Spring Boot DevTools
  * MySQL Driver
  * OAuth2 Resource Server
  * Lombok
  * Validation
* Postman for API testing
* OpenAPI for documentation

I've attempted to keep updating the provided Postman Collection and OpenAPI docs, but they might
be a bit behind due to the constantly-changing nature of this repo as I learn more.

## Endpoints

### Auth

* `POST` `/auth/register` - Registers a new user
* `POST` `/auth/login` - Log in a user
* `POST` `/auth/logout` - Log out a user
* `POST` `/auth/refresh` - Refreshes the user's auth tokens

### Jobs

* `POST` `/jobs` - Creates a job (requires admin role)
* `PATCH` `/jobs/{id}` - Updates job
* `GET` `/jobs` - Lists all jobs
* `GET` `/jobs?assigned={true|false}` - List all jobs, filtered by whether a job is assigned to a temp or not
* `GET` `/jobs/{id}` - Returns a specific job

### Temps

* `POST` `/temps` - Create a temp (requires admin role)
* `GET` `/temps` - List all temps
* `GET` `/temps?jobId={jobId}` - List temps that are available for a job based on the jobs date range
* `GET` `/temps/{id}` - Returns a specific temp
* `GET` `/temps/tree` - Returns a tree representation of the temp hierarchy (requires admin role)
## Notes

### Security

* All requests other than those to `/auth/**` endpoints require an `AuthToken` cookie for authentication. This is a JWT 
valid for 10 minutes and is passed in a `HttpOnly` cookie (so it is not accessible through JS) to the client upon 
successful authentication through `/auth/login`. A `RefreshToken` cookie valid for 24 hours is also provided in the same 
way which allows a user to refresh their tokens through a post to `/auth/refresh`
  * Both of these JWTs contain a fingerprint claim which is randomly generated any time a new token is generated and 
  sent to the client. 
  * The fingerprint is checked upon authentication/refresh - if the fingerprint does not match the one stored against 
  the user in the DB, the user will not have their token authenticated/refreshed as it is assumed to be an old/invalid 
  token.
  * This is my implementation of refresh token rotation. A lot of resources I found recommended storing information 
  about previous refresh tokens as a means to blacklist them but this solution struck me as simpler and stores
  less data, which felt appropriate for a stateless authentication method. 
  * The fingerprint is also randomised upon logout (at the same time the user's token cookies are cleared), 
  invalidating the old tokens in case they are somehow persisted.
  * This implementation unfortunately would not work well for a user who needs to be logged in on multiple devices at 
  the same time, as that would lead to the fingerprint being randomised, essentially ending the other 'session'. 
* All requests that modify data (`POST` or `PATCH` in this case) other than those to `/auth/**`
require an `X-XSRF-TOKEN` header to prevent CSRF attacks. This token is initially passed to the client 
in a `XSRF-TOKEN` cookie upon a successful request to any of the `/auth/**` endpoints, and a fresh one is sent with each
subsequent request. 
  * The client must implement a means of taking this token from the cookie and sending it as a header with 
  each modifying request. 
  * Ideally even requests to the `/auth/**` endpoints should require this token, but this requires a pre-session 
  implementation

### RSA Keys

* JWT implementation requires a public and private RSA KeyPair, rather than use a tool like OpenSSL
I made a little CLI for generating a keypair and writing them to files.
* This served as a nice little side-project to explore things like passing arguments in through the command line and
formatting terminal output.

### General

* A lot of the information I used came from various guides/tutorials found on [BezKoder](https://www.bezkoder.com/),
which I adapted for my own structure and usage.

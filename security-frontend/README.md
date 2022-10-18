# Assignment API Frontend

## Pre-requisites

* You will need to add spring security to your backends to allow temps to log in (you’ll need to add credentials to each temp record too)
* API should only return jobs that are unassigned or are assigned to the reports of the currently logged in temp
* API should only return temps that are reporting to the currently logged in user in the hierarchy

## Assumptions

* All endpoints need to be authorized, apart from `/login`
* All pages other than `/login` should redirect to `/login` if no temp is logged in
* Currently logged in users should not be able to see or update temps or jobs assigned to temps that are not reporting to them in the hierarchy

## Requirements

* React (specifically using Vite to create React app)
* Typescript
* styled-components 
* react-router-dom
* Make sure to use proper theming in your application via styled-components
* Make sure to decouple state and styling

***

## Pages

### `/login`

* Allows user to log in
* Each temp should be allowed to log in (You will need to add credentials for each temp)

### `/profile`

* Display the current logged in user
* Should be able to edit all fields

### `/temps`

* Display all temps, each entry should link to /temps/:id
* You should only be able to see temps that report to currently logged in temp

### `/temps/:id`

*Display temp record by id
* You should only be able to see temps that report to currently logged in temp
* Attempting to get a temp that doesn’t report to the currently logged in user should return a `404` page

### `/jobs`

* Display all unassigned jobs
* Display jobs that are assigned either to the currently logged in temp, or reports of the currently logged in temp

### `/jobs/:id`

* Display job information, with a drop down to assign temps
* Dropdown should be populated by the API
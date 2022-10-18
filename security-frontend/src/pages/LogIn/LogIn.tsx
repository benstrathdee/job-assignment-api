import React, { useContext, useState } from "react";
import { AuthContext } from "../../context/AuthContext";
import { AUTH_ENDPOINT, BASE_URL } from "../../data/constants";

const LogIn = () => {
	type userDetails = {
		username: string;
		password: string;
	};

	const {setToken} = useContext(AuthContext);
	const [userDetails, setUserDetails] = useState({} as userDetails);
	const [authenticationFailed, setAuthenticationFailed] = useState(false);

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const {username, password} = userDetails;
		const basicAuth = btoa(`${username}:${password}`);

		fetch(`${BASE_URL}/${AUTH_ENDPOINT}`, {
			method: "POST",
			headers: {
				"Authorization": `Basic ${basicAuth}`
			}
		})
			.then(async (response) => {
				if (response.status !== 200) {
					setAuthenticationFailed(true);
				} else {
					const json = await response.json();
					setToken(json.token);
					setAuthenticationFailed(false);
				}
			})
			.catch(() => {
				setAuthenticationFailed(true);
			});
	};

	const onChangeUsername = (event: React.ChangeEvent<HTMLInputElement>) => {
		const tempDetails = userDetails;
		tempDetails.username = event.target.value;
		setUserDetails(tempDetails);
	};

	const onChangePassword = (event: React.ChangeEvent<HTMLInputElement>) => {
		const tempDetails = userDetails;
		tempDetails.password = event.target.value;
		setUserDetails(tempDetails);
	};

	return (
		<>
			{authenticationFailed && (<span>Authentication failed.</span>)}
			<form onSubmit={handleSubmit}>
				<label htmlFor="username">Username:</label>
				<input type="text" name="username" onChange={onChangeUsername}></input>
				<label htmlFor="password">Password:</label>
				<input type="password" name="password" onChange={onChangePassword}></input>
				<input type="submit" value="Log In"/>
			</form>
		</>
	);
};

export default LogIn;
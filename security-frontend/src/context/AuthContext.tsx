import React, { createContext, useEffect, useState } from "react";
import jwt_decode from "jwt-decode";

interface ContextState {
    token: string;
    setToken: React.Dispatch<React.SetStateAction<string>>;
    username: string | null;
}

export const AuthContext = createContext({} as ContextState);

type Props = {
    children: JSX.Element | JSX.Element[],
}

type JwtClaims = {
    iss : string;
    sub: string;
    exp: number;
    iat: number;
    scope: string;
}

export const AuthProvider = ({children}: Props) => {
	const [token, setToken] = useState("");
	const [username, setUsername] = useState<string | null>(null);

	useEffect(() => {
		try {
			if (token !== undefined) {
				const decodedToken: JwtClaims = jwt_decode(token);
				setUsername(decodedToken.sub);
			}
		} catch (error) {
			if (error instanceof Error) {
				if (!error.message.includes("Invalid token specified")) {
					console.log(error.message);
				}
			}
		}
	}, [token]);

	const data = {token, setToken, username};
    
	return <AuthContext.Provider value={data}>{children}</AuthContext.Provider>;
};
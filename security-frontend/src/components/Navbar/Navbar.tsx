import React, { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

const Navbar = () => {
	const {username} = useContext(AuthContext);
    
	return (
		<div>Navbar username = {username}</div>
	);
};

export default Navbar;
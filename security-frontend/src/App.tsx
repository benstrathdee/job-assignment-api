import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import { AuthProvider } from "./context/AuthContext";
import JobInfo from "./pages/JobInfo";
import Jobs from "./pages/Jobs";
import LogIn from "./pages/LogIn";
import Profile from "./pages/Profile";
import TempInfo from "./pages/TempInfo";
import Temps from "./pages/Temps";

const App = () => {
	return (
		<Router>
			<AuthProvider>
				<Navbar/>
				<Routes>
					<Route path="/login" element={<LogIn />} />
					<Route path="/temps" element={<Temps />} />
					<Route path="/temps/:tempId" element={<TempInfo />} />
					<Route path="/jobs" element={<Jobs />} />
					<Route path="/jobs/:jobId" element={<JobInfo />} />
					<Route path="/profile" element={<Profile />} />
				</Routes>
			</AuthProvider>
		</Router>
	);
};

export default App;

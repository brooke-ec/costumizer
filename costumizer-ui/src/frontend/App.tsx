import Unauthorized from "./routes/error/Unauthorized";
import { Routes, Route } from "@solidjs/router";
import Header from "./components/Header";
import Library from "./routes/Library";
import Login from "./routes/error/Login";
import NotFound from "./routes/error/NotFound";
import { createResource } from "solid-js";
import { useIdentity } from "./components/Identity";
import { fetchCostumes } from "./utils/api/costume";

export default function App() {
	const identity = useIdentity();

	return (
		<>
			<Routes>
				<Route path="*" component={NotFound} />
				<Route path="/unauthorized/" component={Unauthorized} />
				<Route path="/login/" component={Login} />
				<Route path="/" component={Header}>
					<Route
						path="/"
						component={Library}
						data={() =>
							createResource(identity.token, fetchCostumes)[0]
						}
					/>
				</Route>
			</Routes>
		</>
	);
}

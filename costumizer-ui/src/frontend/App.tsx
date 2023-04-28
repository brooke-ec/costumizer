import ExistingCostume from "./routes/costume/ExistingCostume";
import Unauthorized from "./routes/system/Unauthorized";
import NewCostume from "./routes/costume/NewCostume";
import { Routes, Route } from "@solidjs/router";
import NotFound from "./routes/system/NotFound";
import Login from "./routes/system/Login";
import Library from "./routes/Library";
import Header from "./global/Header";

export default function App() {
	return (
		<>
			<Routes>
				<Route path="*" component={NotFound} />
				<Route path="/unauthorized/" component={Unauthorized} />
				<Route path="/login/" component={Login} />
				<Route path="/" component={Header}>
					<Route path="/" component={Library} />
					<Route path="/costume/:name" component={ExistingCostume} />
					<Route path="/new" component={NewCostume} />
				</Route>
			</Routes>
		</>
	);
}

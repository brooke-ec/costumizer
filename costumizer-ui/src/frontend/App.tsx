import Unauthorized from "./routes/error/Unauthorized";
import { Routes, Route } from "@solidjs/router";
import NotFound from "./routes/error/NotFound";
import Header from "./components/Header";
import Login from "./routes/error/Login";
import Library from "./routes/Library";
import Costume from "./routes/Costume";

export default function App() {
	return (
		<>
			<Routes>
				<Route path="*" component={NotFound} />
				<Route path="/unauthorized/" component={Unauthorized} />
				<Route path="/login/" component={Login} />
				<Route path="/" component={Header}>
					<Route path="/" component={Library} />
					<Route path="/costume/:name" component={Costume} />
				</Route>
			</Routes>
		</>
	);
}

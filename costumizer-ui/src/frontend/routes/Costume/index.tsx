import { Match, Show, Switch, createResource } from "solid-js";
import { fetchCostumeInfo } from "../../utils/api/costume";
import { useParams } from "@solidjs/router";
import NotFound from "../error/NotFound";

export default function Costume() {
	const parameters = useParams();
	const [info] = createResource(() => parameters.name, fetchCostumeInfo);
	return (
		<Show when={!info.loading && info()}>
			<Switch>
				<Match when={info()!.status == 404}>
					<NotFound />
				</Match>
				<Match when={info()!.status == 200}>
					<h1>{info()!.data!.name}</h1>
				</Match>
			</Switch>
		</Show>
	);
}

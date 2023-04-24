import { Match, Show, Switch, createResource } from "solid-js";
import { fetchCostumeInfo } from "../../utils/api/costume";
import SkinPreview from "../../components/SkinPreview";
import { useParams } from "@solidjs/router";
import styles from "./styles.module.scss";
import NotFound from "../error/NotFound";

export default function Costume() {
	const [info, { refetch }] = createResource(() => useParams().name, fetchCostumeInfo);
	return (
		<Show when={!info.loading && info()}>
			<Switch>
				<Match when={info()!.status == 404}>
					<NotFound />
				</Match>
				<Match when={info()!.status == 200}>
					<h1>{info()!.data!.name}</h1>
					<hr />
					<div class={styles.grid}>
						<div class={styles.preview}>
							<SkinPreview
								src={info()!.data!.skin.url}
								slim={info()!.data!.skin.slim}
							/>
						</div>
						<div class={styles.form}>Hello world!</div>
					</div>
				</Match>
			</Switch>
		</Show>
	);
}

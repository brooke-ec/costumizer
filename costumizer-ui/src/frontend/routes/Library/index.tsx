import { CostumesListType } from "../../utils/api/costume";
import { useRouteData } from "@solidjs/router";
import { For, Resource, Show } from "solid-js";
import styles from "./styles.module.scss";
import Card from "../../components/Card";

export default function Library() {
	const costumes = useRouteData<() => Resource<CostumesListType>>();

	return (
		<Show when={!costumes.loading && costumes()}>
			<h1>Costumes</h1>
			<div class={styles.list}>
				<For each={costumes()}>
					{(costume) => (
						<Card
							class={styles.entry}
							title={costume.name}
							href={"/costume/" + costume.name}
						>
							<div
								class={styles.preview}
								style={{ "background-image": `url(${costume.preview})` }}
							/>
						</Card>
					)}
				</For>
			</div>
		</Show>
	);
}

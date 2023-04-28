import { fetchCostumes } from "../../utils/api/costume";
import { For, Show, createResource } from "solid-js";
import { faCirclePlus } from "@fortawesome/free-solid-svg-icons";
import styles from "./styles.module.scss";
import Card from "../../global/Card";
import { A } from "@solidjs/router";
import Fa from "solid-fa";

export default function Library() {
	const [costumes] = createResource(fetchCostumes);

	return (
		<>
			<Show
				when={!costumes.loading && costumes()?.data}
				fallback={
					<div class={styles.list}>
						<For each={new Array(8)}>{() => <div class={styles.loading_entry} />}</For>
					</div>
				}
			>
				<Show
					when={costumes()!.data!.length > 0}
					fallback={
						<div class={styles.empty}>
							<span class={styles.icon}>✨</span>
							<p>You do not have any costumes yet</p>
							<A href="/new">Create New Costume</A>
						</div>
					}
				>
					<h1>Costumes</h1>
					<div class={styles.list}>
						<For each={costumes()!.data}>
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
						<Card class={styles.new} href="/new">
							<Fa icon={faCirclePlus} class={styles.icon} />
							New Costume
						</Card>
					</div>
				</Show>
			</Show>
		</>
	);
}

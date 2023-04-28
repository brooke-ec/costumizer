import { fetchCostumes } from "../../utils/api/costume";
import { For, Show, createResource } from "solid-js";
import { faCirclePlus } from "@fortawesome/free-solid-svg-icons";
import styles from "./styles.module.scss";
import Card from "../../global/Card";
import Fa from "solid-fa";

export default function Library() {
	const [costumes] = createResource(fetchCostumes);

	return (
		<>
			<h1>Costumes</h1>
			<div class={styles.list}>
				<Show
					when={!costumes.loading && costumes()?.data}
					fallback={
						<For each={new Array(8)}>{() => <div class={styles.loading_entry} />}</For>
					}
				>
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
						<Fa icon={faCirclePlus} class={styles.plus} />
						New Costume
					</Card>
				</Show>
			</div>
		</>
	);
}

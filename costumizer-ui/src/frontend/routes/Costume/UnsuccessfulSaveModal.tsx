import { useModal } from "../../global/Modal";
import styles from "./modal.module.scss";
import { Show } from "solid-js";

export default function UnsuccessfulSaveModal(props: { message?: string }) {
	const modal = useModal();

	return (
		<div class={styles.modal}>
			<h1>Error Saving Costume</h1>
			<Show when={props.message} fallback={<br />}>
				<hr />
				<p>{props.message}</p>
			</Show>
			<br />
			<div class={styles.controls}>
				<button onClick={modal.close} class={styles.close}>
					Dismiss
				</button>
			</div>
		</div>
	);
}

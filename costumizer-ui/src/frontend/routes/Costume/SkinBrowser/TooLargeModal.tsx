import { MAX_FILE_SIZE } from ".";
import { useModal } from "../../../global/Modal";
import styles from "../modal.module.scss";

export default function TooLargeModal() {
	const modal = useModal();

	return (
		<div class={styles.modal}>
			<h1>File Too Large</h1>
			<hr />
			<p>
				Skins files must be no larger than <code>{MAX_FILE_SIZE / 1024} KiB</code> in size.
			</p>
			<br />
			<div class={styles.controls}>
				<button onClick={modal.close} class={styles.close}>
					Dismiss
				</button>
			</div>
		</div>
	);
}

import { useModal } from "../../../../global/Modal";
import styles from "../modal.module.scss";

export default function InvalidResolutionModal() {
	const modal = useModal();

	return (
		<div class={styles.modal}>
			<h1>Invalid Skin File</h1>
			<hr />
			<p>
				Skins must be <code>64x64</code> or <code>64x32</code> pixels in size.
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

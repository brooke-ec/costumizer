import { useNavigate, useParams } from "@solidjs/router";
import { deleteCostume } from "../../utils/api/costume";
import LoadingModal from "../../global/LoadingModal";
import { useModal } from "../../global/Modal";
import styles from "./modal.module.scss";

export default function ConfirmDeleteModal(props: { name: string }) {
	const navigate = useNavigate();
	const modal = useModal();

	async function confirmDelete() {
		modal.close();
		modal.open(LoadingModal);
		await deleteCostume(props.name);
		modal.close();
		navigate("/");
	}

	return (
		<div class={styles.modal}>
			<h1>Delete Costume?</h1>
			<hr />
			<p>Are you sure you want to delete this costume? This action is irreversible.</p>
			<br />
			<div class={styles.controls}>
				<button onClick={modal.close} class={styles.close}>
					Cancel
				</button>
				<button onClick={confirmDelete} classList={{ red: true, [styles.close]: true }}>
					Delete
				</button>
			</div>
		</div>
	);
}

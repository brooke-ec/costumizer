import { fetchCostumeInfo, updateCostume } from "../../../utils/api/costume";
import { useNavigate, useParams } from "@solidjs/router";
import ConfirmDeleteModal from "./ConfirmDeleteModal";
import CostumeEditor from "../common/CostumeEditor";
import styles from "../common/styles.module.scss";
import { useModal } from "../../../global/Modal";
import { FormValues } from "../../../utils/Form";
import { Show, createResource } from "solid-js";
import NotFound from "../../system/NotFound";

export default function ExistingCostume() {
	const params = useParams();
	const [info] = createResource(() => params.name, fetchCostumeInfo);
	const navigate = useNavigate();
	const modal = useModal();

	async function submit(data: FormValues) {
		const response = await updateCostume(params.name, data);
		if (response.status != 200!) return response.data?.error;
		if (params.name != data.name) navigate(`../${data.name}`);
	}

	async function deleteCostume() {
		modal.open(() => ConfirmDeleteModal({ name: params.name }));
	}

	return (
		<Show when={info()?.status != 404} fallback={<NotFound />}>
			<h1 class={styles.title}>{params.name}</h1>
			<hr />
			<CostumeEditor data={info()?.data} submit={submit} onDelete={deleteCostume} />
		</Show>
	);
}

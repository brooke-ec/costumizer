import { createCostume, fetchCostumeDefaults } from "../../utils/api/costume";
import { IdentityType, useIdentity } from "../../global/Identity";
import { ModalType, useModal } from "../../global/Modal";
import CostumeEditor from "./common/CostumeEditor";
import styles from "./common/styles.module.scss";
import { Show, createResource } from "solid-js";
import { useNavigate } from "@solidjs/router";
import { FormValues } from "../../utils/Form";
import NotFound from "../system/NotFound";

export default function NewCostume() {
	const modal = useModal();
	const identity = useIdentity();
	const navigate = useNavigate();
	const [info] = createResource(
		() => [identity, modal] as [IdentityType, ModalType],
		fetchCostumeDefaults,
	);

	async function submit(data: FormValues) {
		const response = await createCostume([data, identity, modal]);
		if (response.status != 200!) return response.data?.error;
		navigate(`/costume/${data.name}`);
	}

	return (
		<Show when={info()?.status != 404} fallback={<NotFound />}>
			<h1 class={styles.title}>Create New Costume</h1>
			<hr />
			<CostumeEditor data={info()?.data} submit={submit} />
		</Show>
	);
}

import { CostumeInfoType } from "../../../utils/api/costume";
import UnsuccessfulSaveModal from "./UnsuccessfulSaveModal";
import LoadingModal from "../../../global/LoadingModal";
import Form, { FormValues } from "../../../utils/Form";
import SkinPreview from "../../../global/SkinPreview";
import { useModal } from "../../../global/Modal";
import loadingStyle from "./loading.module.scss";
import { Show, createSignal } from "solid-js";
import styles from "./styles.module.scss";
import CostumeForm from "./CostumeForm";

export default function CostumeEditor(props: {
	submit: (data: FormValues) => Promise<string | undefined>;
	data?: CostumeInfoType;
	onDelete?: () => void;
}) {
	const [newSkin, setNewSkin] = createSignal<string | null>(null);
	const [newModel, setNewModel] = createSignal<boolean>();
	const modal = useModal();
	const form = new Form();

	async function submit() {
		modal.open(LoadingModal);
		const skin = newSkin()?.substring(22)! || null;
		const data = { ...form.value(), skin: skin };
		const message = await props.submit(data);
		modal.close();
		if (message) modal.open(() => UnsuccessfulSaveModal({ message: message }));
	}

	return (
		<Show
			when={props.data}
			fallback={
				<>
					<div class={styles.grid}>
						<div class={loadingStyle.preview}></div>
						<div class={styles.form}>
							<div class={loadingStyle.input} />
							<div class={loadingStyle.input} />
							<div class={loadingStyle.radio} />
							<div class={loadingStyle.browse} />
						</div>
					</div>
				</>
			}
		>
			<div class={styles.grid}>
				<div class={styles.preview}>
					<SkinPreview
						slim={newModel() ?? props.data!.skin.slim}
						src={newSkin() ?? props.data!.skin.url}
					/>
				</div>
				<CostumeForm
					onModelChange={setNewModel}
					onSkinChange={setNewSkin}
					onDelete={props.onDelete}
					data={props.data!}
					onSubmit={submit}
					form={form}
				/>
			</div>
		</Show>
	);
}

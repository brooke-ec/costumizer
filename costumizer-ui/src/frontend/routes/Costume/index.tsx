import { fetchCostumeExistence, fetchCostumeInfo, updateCostume } from "../../utils/api/costume";
import { Match, Show, Switch, createResource, createSignal } from "solid-js";
import { useNavigate, useParams } from "@solidjs/router";
import LoadingButton from "../../global/LoadingButton";
import LoadingModal from "../../global/LoadingModal";
import Form, { ValueTypes } from "../../utils/Form";
import SkinPreview from "../../global/SkinPreview";
import RadioButton from "../../global/RadioButton";
import { useModal } from "../../global/Modal";
import styles from "./styles.module.scss";
import NotFound from "../system/NotFound";
import UnsuccessfulSaveModal from "./UnsuccessfulSaveModal";
import SkinBrowser from "./SkinBrowser";
import Input from "../../global/Input";

export default function Costume() {
	const [info] = createResource(() => useParams().name, fetchCostumeInfo);
	const [newSkin, setNewSkin] = createSignal<string | null>(null);
	const [newSlim, setNewSlim] = createSignal<boolean>();
	const [loading, setLoading] = createSignal(false);
	const navigate = useNavigate();
	const modal = useModal();
	const form = new Form();

	const validators = [
		{
			pattern: /^[a-zA-Z0-9_]*$/,
			message: "Field must contain only alphanumeric characters and underscores.",
		},
	];

	const options = [
		{ label: "Classic", value: false },
		{ label: "Slim", value: true },
	];

	async function validateNameUnique(value: string) {
		if (info()!.data!.name.toLowerCase() == value.toLowerCase()) return;
		setLoading(true);
		const response = await fetchCostumeExistence(value);
		setLoading(false);
		if (!response.data!.exists) return;
		return "There is already a costume registered with this name.";
	}

	async function submit() {
		modal.open(LoadingModal);
		const skin = newSkin()?.substring(22)! || null;
		const data: { [name: string]: ValueTypes } = { ...form.value(), skin: skin };
		const response = await updateCostume(info()!.data!.name, data);
		modal.close();

		if (response.status != 200!)
			modal.open(() => UnsuccessfulSaveModal({ message: response.data?.error }));
		else if (info()!.data!.name != data.name) navigate(`../${data.name}`);
	}

	return (
		<Show when={!info.loading && info()}>
			<Switch>
				<Match when={info()!.status == 404}>
					<NotFound />
				</Match>
				<Match when={info()!.status == 200}>
					<h1 class={styles.title}>{info()!.data!.name}</h1>
					<hr />
					<div class={styles.grid}>
						<div class={styles.preview}>
							<SkinPreview
								slim={newSlim() ?? info()!.data!.skin.slim}
								src={newSkin() ?? info()!.data!.skin.url}
							/>
						</div>
						<div class={styles.form}>
							<div class={styles.field}>
								<label class="smallcaps">Name</label>
								<Input
									required
									form={form}
									name="name"
									maxlength="32"
									class={styles.input}
									validators={validators}
									value={info()!.data?.name}
									validator={validateNameUnique}
								/>
							</div>
							<div class={styles.field}>
								<label class="smallcaps">Display Name</label>
								<Input
									required
									form={form}
									minlength="3"
									name="display"
									maxlength="16"
									class={styles.input}
									validators={validators}
									value={info()!.data?.display}
								/>
							</div>
							<div class={styles.field}>
								<label class="smallcaps">Player Model</label>
								<RadioButton
									form={form}
									name="slim"
									options={options}
									onChange={setNewSlim}
									value={info()!.data!.skin.slim}
								/>
							</div>
							<div class={styles.field}>
								<label class="smallcaps">Skin File</label>
								<SkinBrowser onChange={setNewSkin} />
							</div>
							<div class={styles.controls}>
								<button class="danger">Delete</button>
								<LoadingButton
									class="green"
									onClick={submit}
									loading={loading()}
									disabled={!form.valid()}
								>
									Save
								</LoadingButton>
							</div>
						</div>
					</div>
				</Match>
			</Switch>
		</Show>
	);
}

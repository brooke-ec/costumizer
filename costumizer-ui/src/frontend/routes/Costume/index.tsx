import { fetchCostumeExistence, fetchCostumeInfo } from "../../utils/api/costume";
import { Match, Show, Switch, createResource, createSignal } from "solid-js";
import LoadingButton from "../../components/LoadingButton";
import SkinPreview from "../../components/SkinPreview";
import { useParams } from "@solidjs/router";
import styles from "./styles.module.scss";
import NotFound from "../error/NotFound";
import Input from "../../components/Input";
import Form from "../../utils/Form";

export default function Costume() {
	const [info, { refetch }] = createResource(() => useParams().name, fetchCostumeInfo);
	const [loading, setLoading] = createSignal(false);
	const form = new Form();

	const validators = [
		{
			pattern: /^[a-zA-Z0-9_]*$/,
			message: "Field must contain only alphanumeric characters and underscores.",
		},
	];

	async function validateNameUnique(value: string) {
		if (info()!.data!.name == value) return;
		setLoading(true);
		const response = await fetchCostumeExistence(value);
		setLoading(false);
		if (!response.data!.exists) return;
		return "There is already a costume registered with this name.";
	}

	return (
		<Show when={!info.loading && info()}>
			<Switch>
				<Match when={info()!.status == 404}>
					<NotFound />
				</Match>
				<Match when={info()!.status == 200}>
					<h1>{info()!.data!.name}</h1>
					<hr />
					<div class={styles.grid}>
						<div class={styles.preview}>
							<SkinPreview
								src={info()!.data!.skin.url}
								slim={info()!.data!.skin.slim}
							/>
						</div>
						<div class={styles.form}>
							<label for="name">Name</label>
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
							<label for="display">Display Name</label>
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
							<div class={styles.controls}>
								<button class="danger">Delete</button>
								<LoadingButton
									class="green"
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

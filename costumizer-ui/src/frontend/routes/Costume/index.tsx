import { fetchCostumeExistence, fetchCostumeInfo } from "../../utils/api/costume";
import { Match, Show, Switch, createResource } from "solid-js";
import SkinPreview from "../../components/SkinPreview";
import { useParams } from "@solidjs/router";
import styles from "./styles.module.scss";
import NotFound from "../error/NotFound";
import Input from "../../components/Input";

export default function Costume() {
	const [info, { refetch }] = createResource(() => useParams().name, fetchCostumeInfo);
	let form: HTMLFormElement | undefined;

	const validators = [
		{
			pattern: /^[a-zA-Z0-9_]*$/,
			message: "Field must contain only alphanumeric characters and underscores.",
		},
	];

	async function validateNameUnique(value: string) {
		if (info()!.data!.name == value) return;
		const response = await fetchCostumeExistence(value);
		if (!response.data!.exists) return;
		return "There is already a costume registered with this name.";
	}

	function submit(e: Event) {
		const fd = new FormData(form);
		for (const [key, value] of fd) console.log(`${key}: ${value}\n`);
		e.preventDefault();
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
						<form ref={form} class={styles.form} onSubmit={submit}>
							<label for="name">Name</label>
							<Input
								required
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
								minlength="3"
								name="display"
								maxlength="16"
								class={styles.input}
								validators={validators}
								value={info()!.data?.display}
							/>
							<div class={styles.controls}>
								<button class="danger">Delete</button>
								<button class="green" type="submit">
									Save
								</button>
							</div>
						</form>
					</div>
				</Match>
			</Switch>
		</Show>
	);
}

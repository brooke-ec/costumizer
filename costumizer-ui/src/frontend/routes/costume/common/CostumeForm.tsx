import { CostumeInfoType, fetchCostumeExistence } from "../../../utils/api/costume";
import LoadingButton from "../../../global/LoadingButton";
import Form, { ValueTypes } from "../../../utils/Form";
import RadioButton from "../../../global/RadioButton";
import { Show, createSignal } from "solid-js";
import styles from "./styles.module.scss";
import Input from "../../../global/Input";
import SkinBrowser from "./SkinBrowser";

export default function CostumeForm(props: {
	onModelChange: (value: ValueTypes) => void;
	onSkinChange: (value: string) => void;
	data: CostumeInfoType;
	onDelete?: () => void;
	onSubmit: () => void;
	form: Form;
}) {
	const [validating, setValidating] = createSignal(false);

	const validators = [
		{
			pattern: /^[a-zA-Z0-9_]*$/,
			message: "Field must contain only alphanumeric characters and underscores.",
		},
	];

	const modelOptions = [
		{ label: "Classic", value: false },
		{ label: "Slim", value: true },
	];

	async function validateNameUnique(value: string) {
		if (props.data.name.toLowerCase() == value.toLowerCase()) return;
		setValidating(true);
		const response = await fetchCostumeExistence(value);
		setValidating(false);
		if (!response.data!.exists) return;
		return "There is already a costume registered with this name.";
	}

	return (
		<div class={styles.form}>
			<div class={styles.field}>
				<label class="smallcaps">Name</label>
				<Input
					required
					form={props.form}
					name="name"
					maxlength="32"
					class={styles.input}
					validators={validators}
					value={props.data.name}
					validator={validateNameUnique}
				/>
			</div>
			<div class={styles.field}>
				<label class="smallcaps">Display Name</label>
				<Input
					required
					form={props.form}
					minlength="3"
					name="display"
					maxlength="16"
					class={styles.input}
					validators={validators}
					value={props.data.display}
				/>
			</div>
			<div class={styles.field}>
				<label class="smallcaps">Player Model</label>
				<RadioButton
					form={props.form}
					name="slim"
					onChange={props.onModelChange}
					options={modelOptions}
					value={props.data.skin.slim}
				/>
			</div>
			<div class={styles.field}>
				<label class="smallcaps">Skin File</label>
				<SkinBrowser onChange={props.onSkinChange} />
			</div>
			<div class={styles.controls}>
				<Show when={props.onDelete}>
					<button class="danger" onClick={props.onDelete}>
						Delete
					</button>
				</Show>
				<LoadingButton
					class="green"
					onClick={props.onSubmit}
					loading={validating()}
					disabled={!props.form.valid()}
				>
					Save
				</LoadingButton>
			</div>
		</div>
	);
}

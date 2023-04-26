import Form, { ValueTypes } from "../../utils/Form";
import styles from "./styles.module.scss";
import { For, createSignal } from "solid-js";

export type RadioButtonOption = {
	value: ValueTypes;
	label: string;
};

export default function RadioButton(props: {
	onChange?: (value: ValueTypes) => void;
	options: RadioButtonOption[];
	value: ValueTypes;
	name: string;
	form?: Form;
}) {
	const [value, setValue] = createSignal(props.value);

	if (props.form) {
		props.form.register(props.name, { valid: () => true, value: value });
	}

	function click(value: ValueTypes) {
		if (props.onChange) props.onChange(value);
		setValue(value);
	}

	return (
		<div class={styles.container}>
			<For each={props.options}>
				{(option, i) => (
					<div class={styles.option} onClick={() => click(option.value)}>
						<div
							classList={{
								[styles.check]: true,
								[styles.checked]: value() == option.value,
							}}
						/>
						<label>{option.label}</label>
					</div>
				)}
			</For>
		</div>
	);
}

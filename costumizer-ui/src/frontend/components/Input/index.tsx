import { Show, createMemo, createSignal } from "solid-js";
import styles from "./styles.module.scss";
import Form from "../../utils/Form";

export type Validator = {
	pattern: RegExp;
	message: string;
	inverse?: boolean;
};

export default function Input(props: {
	form?: Form;
	name: string;
	class?: string;
	value?: string;
	required?: boolean;
	validators?: Validator[];
	maxlength?: number | string;
	minlength?: number | string;
	validator?: (value: string) => string | void | Promise<string | void>;
}) {
	const [message, setMessage] = createSignal<string | undefined>();
	let input: HTMLInputElement | undefined;

	if (props.form) {
		props.form.register(props.name, {
			valid: () => message() === undefined,
			value: () => input!.value,
		});
	}

	const validators = createMemo(() => {
		const result: Validator[] = new Array();
		if (props.required)
			result.push({
				message: "This field is required.",
				pattern: /^$/,
				inverse: true,
			});
		if (props.minlength)
			result.push({
				message: `Field must be at least ${props.minlength} characters in length.`,
				pattern: new RegExp(`.{${props.minlength},}`),
			});
		result.push(...(props.validators ?? new Array()));
		return result;
	});

	function validate() {
		const value = input!.value;
		if (!props.validators) return;
		for (const validator of validators()) {
			if (validator.pattern.test(value) == Boolean(validator.inverse)) {
				input!.setCustomValidity(validator.message);
				setMessage(validator.message);
				return;
			}
		}
		input!.setCustomValidity("");
		setMessage();
	}

	async function blur() {
		if (props.validator) {
			if (message()) return;
			let result = props.validator(input!.value);
			if (result instanceof Promise) result = await result;
			if (result) {
				input!.setCustomValidity(result);
				setMessage(result);
			}
		}
	}

	return (
		<div class={props.class} classList={{ [styles.no_margin]: Boolean(message()) }}>
			<input
				ref={input}
				onBlur={blur}
				name={props.name}
				onInput={validate}
				value={props.value}
				required={props.required}
				maxlength={props.maxlength}
				autocomplete="off"
			/>
			<Show when={message()}>
				<div class={styles.tooltip_container}>
					<div class={styles.tooltip}>{message()}</div>
				</div>
			</Show>
		</div>
	);
}

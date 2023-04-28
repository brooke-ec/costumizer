import { SetStoreFunction, createStore } from "solid-js/store";
import { Accessor, createMemo } from "solid-js";

export type ValueTypes = string | number | boolean | null;

export type FormValues = { [name: string]: ValueTypes };

export type FormInput = {
	valid: () => boolean;
	value: () => ValueTypes;
};

export type FormInputs = { [name: string]: FormInput };

export default class Form {
	inputs: FormInputs;
	setInputs: SetStoreFunction<FormInputs>;
	valid: Accessor<boolean>;

	constructor() {
		[this.inputs, this.setInputs] = createStore<FormInputs>({});
		this.valid = createMemo(() => {
			for (const input of Object.values(this.inputs)) {
				if (!input.valid()) return false;
			}
			return true;
		});
	}

	register(name: string, settings: FormInput) {
		this.setInputs(name, settings);
	}

	value() {
		const result: FormValues = {};
		Object.entries(this.inputs).forEach(([name, input]) => (result[name] = input.value()));
		return result;
	}
}

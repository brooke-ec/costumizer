import UnexpectedError from "../../routes/system/UnexpectedError";
import { IdentityType, useIdentity } from "../../global/Identity";
import { ModalType, useModal } from "../../global/Modal";

export type ResponseData<T> = {
	status: number;
	data?: T;
};

// The way contexts are passed around here is terrible.
// Needs refactoring.
export async function request<T>(
	input: RequestInfo | URL,
	identity: IdentityType,
	modal: ModalType,
	init: RequestInit = {},
): Promise<ResponseData<T>> {
	let response;
	try {
		response = await fetch(input, {
			...init,
			headers: {
				...init.headers,
				Accept: "application/json",
				Authorization: `Bearer ${identity.token()}`,
			},
		});
	} catch {
		modal.open(UnexpectedError);
		throw new Error("Error fetching.");
	}

	let json;
	switch (response.status) {
		case 401:
			json = await response.json();
			identity.invalidate(json.error);
			throw new Error(json.error);
		case 500:
			const error = await getError(response);
			modal.open(UnexpectedError);
			console.error(error);
			throw new Error("500 Error");
		default:
			return {
				status: response.status,
				data: await response.json(),
			};
	}
}

async function getError(response: Response) {
	const text = await response.text();
	try {
		return JSON.parse(text).error;
	} catch {
		return text;
	}
}

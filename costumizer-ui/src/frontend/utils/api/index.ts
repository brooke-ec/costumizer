import UnexpectedError from "../../routes/system/UnexpectedError";
import { useIdentity } from "../../components/Identity";
import { useModal } from "../../components/Modal";

export type ResponseData<T> = {
	status: number;
	data?: T;
};

export async function request<T>(
	input: RequestInfo | URL,
	init: RequestInit = {},
): Promise<ResponseData<T>> {
	const identity = useIdentity();
	const modal = useModal();

	const response = await fetch(input, {
		...init,
		headers: {
			...init.headers,
			Accept: "application/json",
			Authorization: `Bearer ${identity.token()}`,
		},
	});

	let json;
	switch (response.status) {
		case 401:
			json = await response.json();
			identity.invalidate(json.error);
			throw new Error(json.error);
		case 500:
			json = await response.json();
			modal.open(UnexpectedError);
			throw new Error(json.error);
		default:
			return {
				status: response.status,
				data: await response.json(),
			};
	}
}

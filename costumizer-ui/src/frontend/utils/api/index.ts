import UnexpectedError from "../../modals/UnexpectedError";
import { useIdentity } from "../../components/Identity";
import { useModal } from "../../components/Modal";

export async function request<T>(
	input: RequestInfo | URL,
	init: RequestInit,
): Promise<T> {
	const identity = useIdentity();
	const modal = useModal();

	const response = await fetch(input, init);

	let json;
	switch (response.status) {
		case 200:
			return await response.json();
		case 401:
			json = await response.json();
			identity.invalidate(json.error);
			throw new Error(json.error);
		case 500:
			json = await response.json();
			modal.open(UnexpectedError());
			throw new Error(json.error);
		default:
			throw new Error(await response.text());
	}
}

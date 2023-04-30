import { IdentityType } from "../../global/Identity";
import { ModalType } from "../../global/Modal";
import { request } from ".";

export type CostumesListType = {
	name: string;
	preview: string;
}[];

export async function fetchCostumes([identity, modal]: [IdentityType, ModalType]) {
	return await request<CostumesListType>("/api/costume/list", identity, modal);
}

export type CostumeInfoType = {
	name: string;
	display: string;
	skin: {
		url: string;
		slim: boolean;
	};
};

export async function fetchCostumeInfo([name, identity, modal]: [string, IdentityType, ModalType]) {
	return await request<CostumeInfoType>(
		"/api/costume/info?" + new URLSearchParams({ name: name }),
		identity,
		modal,
	);
}

export async function fetchCostumeDefaults([identity, modal]: [IdentityType, ModalType]) {
	return await request<CostumeInfoType>("/api/costume/defaults", identity, modal);
}

export async function fetchCostumeExistence([name, identity, modal]: [
	string,
	IdentityType,
	ModalType,
]) {
	return await request<{ exists: boolean }>(
		"/api/costume/exists?" + new URLSearchParams({ name: name }),
		identity,
		modal,
	);
}

export type UpdateCostumeType = {
	successful?: true;
	error?: string;
};

export async function updateCostume([name, data, identity, modal]: [
	string,
	object,
	IdentityType,
	ModalType,
]) {
	return await request<UpdateCostumeType>(
		"/api/costume/update?" + new URLSearchParams({ name: name }),
		identity,
		modal,
		{
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data),
			method: "POST",
		},
	);
}

export async function createCostume([data, identity, modal]: [object, IdentityType, ModalType]) {
	return await request<UpdateCostumeType>("/api/costume/create", identity, modal, {
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(data),
		method: "POST",
	});
}

export async function deleteCostume([name, identity, modal]: [string, IdentityType, ModalType]) {
	return await request(
		"/api/costume/delete?" + new URLSearchParams({ name: name }),
		identity,
		modal,
		{
			method: "POST",
		},
	);
}

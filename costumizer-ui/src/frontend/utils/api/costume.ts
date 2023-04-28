import { request } from ".";

export type CostumesListType = {
	name: string;
	preview: string;
}[];

export async function fetchCostumes() {
	return await request<CostumesListType>("/api/costume/list");
}

export type CostumeInfoType = {
	name: string;
	display: string;
	skin: {
		url: string;
		slim: boolean;
	};
};

export async function fetchCostumeInfo(name: string) {
	return await request<CostumeInfoType>(
		"/api/costume/info?" + new URLSearchParams({ name: name }),
	);
}

export async function fetchCostumeDefaults() {
	return await request<CostumeInfoType>("/api/costume/defaults");
}

export async function fetchCostumeExistence(name: string) {
	return await request<{ exists: boolean }>(
		"/api/costume/exists?" + new URLSearchParams({ name: name }),
	);
}

export type UpdateCostumeType = {
	successful?: true;
	error?: string;
};

export async function updateCostume(name: string, data: object) {
	return await request<UpdateCostumeType>(
		"/api/costume/update?" + new URLSearchParams({ name: name }),
		{
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data),
			method: "POST",
		},
	);
}

export async function createCostume(data: object) {
	return await request<UpdateCostumeType>("/api/costume/create", {
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(data),
		method: "POST",
	});
}

export async function deleteCostume(name: string) {
	return await request("/api/costume/delete?" + new URLSearchParams({ name: name }), {
		method: "POST",
	});
}

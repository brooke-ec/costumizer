import { request } from ".";

export type CostumesListType = {
	name: string;
	preview: string;
}[];

export async function fetchCostumes() {
	return await request<CostumesListType>("/api/costume/list/");
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
	return await request<CostumeInfoType>("/api/costume/info/" + name);
}

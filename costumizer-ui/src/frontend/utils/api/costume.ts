import { request } from ".";

export type CostumesListType = {
	name: string;
	preview: string;
}[];

export async function fetchCostumes(token: string): Promise<CostumesListType> {
	return await request("/api/costume/list/", {
		headers: new Headers({
			Accept: "application/json",
			Authorization: `Bearer ${token}`,
		}),
	});
}

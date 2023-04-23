import { request } from ".";

export type UserInfoType = {
	id: string;
	name: string;
	skin: string;
};

export async function fetchUserInfo(token: string): Promise<UserInfoType> {
	return await request("/api/user/info/", {
		headers: {
			Accept: "application/json",
			Authorization: `Bearer ${token}`,
		},
	});
}

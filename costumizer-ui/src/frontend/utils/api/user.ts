import { request } from ".";

export type UserInfoType = {
	id: string;
	name: string;
	skin: string;
};

export async function fetchUserInfo() {
	return await request<UserInfoType>("/api/user/info/");
}

import { IdentityType } from "../../global/Identity";
import { ModalType } from "../../global/Modal";
import { request } from ".";

export type UserInfoType = {
	id: string;
	name: string;
	skin: string;
};

export async function fetchUserInfo([identity, modal]: [IdentityType, ModalType]) {
	return await request<UserInfoType>("/api/user/info", identity, modal);
}

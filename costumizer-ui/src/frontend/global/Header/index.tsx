import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { Show, createEffect, createResource } from "solid-js";
import { A, Outlet, useLocation } from "@solidjs/router";
import { fetchUserInfo } from "../../utils/api/user";
import FaceRenderer from "../FaceRenderer";
import { IdentityType, useIdentity } from "../Identity";
import styles from "./styles.module.scss";
import { ModalType, useModal } from "../Modal";
import Fa from "solid-fa";

export default function Header() {
	const identity = useIdentity();
	const location = useLocation();
	const modal = useModal();

	const [info] = createResource(
		() => [identity, modal] as [IdentityType, ModalType],
		fetchUserInfo,
	);

	createEffect(() => {
		if (!identity.token()) identity.invalidate();
	});

	return (
		<>
			<header>
				<Show when={location.pathname != "/"}>
					<A href="/" class={styles.back}>
						<Fa icon={faArrowLeft} /> Back
					</A>
				</Show>
				<div class={styles.user}>
					<Show
						when={!info.loading && info()?.data}
						fallback={
							<>
								<div
									classList={{
										[styles.pfp]: true,
										[styles.loading]: true,
									}}
								/>
								<span
									classList={{
										[styles.username]: true,
										[styles.loading]: true,
									}}
								/>
							</>
						}
					>
						<FaceRenderer class={styles.pfp} src={info()!.data!.skin} />
						<span class={styles.username}>{info()!.data!.name}</span>
					</Show>
				</div>
			</header>
			<main>
				<Outlet />
			</main>
		</>
	);
}

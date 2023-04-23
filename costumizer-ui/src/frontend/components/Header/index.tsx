import { fetchUserInfo } from "../../utils/api/user";
import { Show, createEffect, createResource } from "solid-js";
import FaceRenderer from "../FaceRenderer";
import { useIdentity } from "../Identity";
import styles from "./styles.module.scss";
import { Outlet } from "@solidjs/router";

export default function Header() {
	const identity = useIdentity();
	const [info] = createResource(identity.token, fetchUserInfo);

	createEffect(() => {
		if (!identity.token()) identity.invalidate();
	});

	return (
		<>
			<header>
				<div class={styles.user}>
					<Show
						when={!info.loading && info()}
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
						<FaceRenderer class={styles.pfp} src={info()!.skin} />
						<span class={styles.username}>{info()!.name}</span>
					</Show>
				</div>
			</header>
			<main>
				<Outlet />
			</main>
		</>
	);
}

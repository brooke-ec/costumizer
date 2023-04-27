import { useSearchParams, Navigate } from "@solidjs/router";
import { useIdentity } from "../../global/Identity";
import styles from "./styles.module.scss";
import { Show } from "solid-js";

export default function Login() {
	const [params] = useSearchParams();
	const identity = useIdentity();

	if (params.token) identity.login(params.token);

	return (
		<Show
			when={params.token}
			fallback={
				<>
					<h1 class={styles.title}>Bad Request</h1>
					<p>
						Required search parameter <code>token</code> was not found.
					</p>
				</>
			}
		>
			<Navigate href="/" />
		</Show>
	);
}

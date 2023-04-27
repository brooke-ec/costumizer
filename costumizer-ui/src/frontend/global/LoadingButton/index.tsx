import { JSX, Show, children } from "solid-js";
import styles from "./styles.module.scss";

export default function LoadingButton(props: {
	onClick?: (e: MouseEvent) => void;
	children: JSX.Element;
	disabled?: boolean;
	loading?: boolean;
	class?: string;
}) {
	const c = children(() => props.children);

	return (
		<button
			class={props.class}
			disabled={props.loading || props.disabled}
			onClick={props.onClick}
		>
			<Show when={props.loading} fallback={c}>
				<div class={styles.spinner_container}>
					<div class={styles.spinner} />
				</div>
			</Show>
		</button>
	);
}

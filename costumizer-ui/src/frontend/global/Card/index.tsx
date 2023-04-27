import { JSX, Show, children } from "solid-js";
import styles from "./styles.module.scss";
import { useNavigate } from "@solidjs/router";

export default function Card(props: {
	onClick?: (e: MouseEvent) => void;
	children: JSX.Element;
	reverse?: boolean;
	title?: string;
	class?: string;
	href?: string;
}) {
	const c = children(() => props.children);
	const navigate = useNavigate();

	function onClick(e: MouseEvent) {
		if (props.onClick) props.onClick(e);
		if (props.href) navigate(props.href);
	}

	return (
		<div
			onClick={onClick}
			classList={{
				[styles.card]: true,
				[styles.reverse]: props.reverse,
				[styles.clickable]: Boolean(props.href) || Boolean(props.onClick),
			}}
		>
			<Show when={props.title}>
				<div class={styles.title}>{props.title}</div>
			</Show>
			<div class={props.class}>{c()}</div>
		</div>
	);
}

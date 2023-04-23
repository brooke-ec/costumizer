import styles from "./styles.module.scss";
import { createEffect } from "solid-js";

export default function FaceRenderer(props: { src: string; class?: string }) {
	let canvas: HTMLCanvasElement | undefined;

	createEffect(async () => {
		const ctx = canvas!.getContext("2d");

		ctx!.fillStyle = "#353544";
		ctx?.fillRect(0, 0, 8, 8);

		const skin = new Image();
		skin.src = props.src;
		await skin.decode(); // wait until loaded

		ctx?.drawImage(skin, 8, 8, 8, 8, 0, 0, 8, 8);
		ctx?.drawImage(skin, 40, 8, 8, 8, 0, 0, 8, 8);
	});

	return (
		<canvas
			class={props.class}
			classList={{ [styles.canvas]: true }}
			ref={canvas}
			height="8"
			width="8"
		/>
	);
}

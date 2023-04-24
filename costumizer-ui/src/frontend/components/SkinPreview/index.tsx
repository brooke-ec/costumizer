import { Show, createEffect, createSignal, onCleanup, untrack } from "solid-js";
import { faPause, faPlay } from "@fortawesome/free-solid-svg-icons";
import { WalkingAnimation, SkinViewer } from "skinview3d";
import styles from "./styles.module.scss";
import Card from "../Card";
import Fa from "solid-fa";

const STORAGE_KEY = "skin_preview_animate";

export default function SkinPreview(props: { src: string; slim: boolean }) {
	const [animate, setAnimate] = createSignal(localStorage.getItem(STORAGE_KEY) != "false");
	let animation: WalkingAnimation | undefined;
	let canvas: HTMLCanvasElement | undefined;
	let skinveiwer: SkinViewer | undefined;

	createEffect(() => {
		const model = props.slim ? "slim" : "default";

		if (skinveiwer) {
			skinveiwer.loadSkin(props.src, { model });
		} else {
			observer.observe(canvas!);
			animation = new WalkingAnimation();
			animation.paused = !untrack(animate);
			animation.headBobbing = false;
			animation.speed = 0.5;

			skinveiwer = new SkinViewer({
				model,
				canvas,
				width: 1,
				height: 1,
				animation,
				skin: props.src,
			});

			skinveiwer.cameraLight.intensity = 0.2;
			skinveiwer.globalLight.intensity = 0.8;
			skinveiwer.controls.enableZoom = false;

			skinveiwer.camera.position.set(-20, 20, 35);
			skinveiwer.controls.update();
		}
	});

	const observer = new ResizeObserver(() => {
		const rect = canvas!.getBoundingClientRect();
		skinveiwer!.height = rect.height;
		skinveiwer!.width = rect.width;
	});

	onCleanup(() => {
		observer.disconnect();
	});

	function toggleAnimate() {
		const newValue = !animate();
		localStorage.setItem(STORAGE_KEY, newValue.toString());
		animation!.paused = !newValue;
		setAnimate(newValue);
	}

	return (
		<Card class={styles.card}>
			<canvas class={styles.canvas} ref={canvas} />
			<div class={styles.controls_container}>
				<button class={styles.controls} onClick={toggleAnimate}>
					<Show when={animate()} fallback={<Fa icon={faPlay} />}>
						<Fa icon={faPause} />
					</Show>
				</button>
			</div>
		</Card>
	);
}

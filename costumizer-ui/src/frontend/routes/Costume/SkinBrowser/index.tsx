import { useModal } from "../../../components/Modal";
import InvalidSizeModal from "./InvalidSizeModal";
import styles from "./styles.module.scss";

export default function SkinBrowser(props: { onChange: (value: string) => void }) {
	let input: undefined | HTMLInputElement;
	const modal = useModal();

	function openBrowser() {
		input!.value = "";
		input!.click();
	}

	async function submit() {
		const files = input!.files!;
		if (files.length == 0) return;
		const url = URL.createObjectURL(files.item(0)!);
		if (await valid(url)) props.onChange(url);
		else modal.open(InvalidSizeModal);
	}

	async function valid(url: string): Promise<boolean> {
		const im = new Image();
		im.src = url;

		await im.decode();
		return im.width == 64 && [64, 32].includes(im.height);
	}

	return (
		<>
			<button class={styles.button} onClick={openBrowser}>
				Browse
			</button>
			<input
				type="file"
				ref={input}
				class={styles.input}
				accept="image/png"
				onChange={submit}
			/>
		</>
	);
}

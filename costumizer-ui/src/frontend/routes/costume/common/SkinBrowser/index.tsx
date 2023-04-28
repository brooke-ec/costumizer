import InvalidResolutionModal from "./InvalidResolutionModal";
import { useModal } from "../../../../global/Modal";
import TooLargeModal from "./TooLargeModal";
import styles from "./styles.module.scss";

export const MAX_FILE_SIZE = 1024 * 10;

export default function SkinBrowser(props: { onChange: (value: string) => void }) {
	let input: undefined | HTMLInputElement;
	const modal = useModal();

	function openBrowser() {
		input!.value = "";
		input!.click();
	}

	function getDataUrl(file: File): Promise<string> {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();
			reader.readAsDataURL(file);
			reader.addEventListener("load", () => {
				if (typeof reader.result != "string") reject();
				else resolve(reader.result);
			});
		});
	}

	async function submit() {
		if (input!.files!.length == 0) return;
		const file = input!.files![0];

		if (file.size > MAX_FILE_SIZE) {
			modal.open(TooLargeModal);
			return;
		}

		const url = await getDataUrl(file);
		if (await isResolutionValid(url)) props.onChange(url);
		else modal.open(InvalidResolutionModal);
	}

	async function isResolutionValid(url: string): Promise<boolean> {
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

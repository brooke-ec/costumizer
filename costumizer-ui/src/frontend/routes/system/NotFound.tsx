import styles from "./styles.module.scss";
import { A } from "@solidjs/router";

export default function NotFound() {
	return (
		<>
			<h1 class={styles.title}>Not Found</h1>
			<p>
				No resource was found at this url. <A href="/">Return Home.</A>
			</p>
		</>
	);
}

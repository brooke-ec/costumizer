import { useIdentity } from "../../components/Identity";
import { useNavigate } from "@solidjs/router";
import styles from "./styles.module.scss";

export default function Unauthorized() {
	const navigate = useNavigate();
	const identity = useIdentity();

	if (identity.token()) navigate("/", { replace: true });

	return (
		<>
			<h1 class={styles.title}>Unauthorized</h1>
			<p>
				Please log in by following the link returned by the{" "}
				<code>/costumizer ui</code> command in-game.
			</p>
		</>
	);
}

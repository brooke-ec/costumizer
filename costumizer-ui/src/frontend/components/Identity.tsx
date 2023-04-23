import { createSignal, createContext, useContext } from "solid-js";
import { useNavigate, Navigator } from "@solidjs/router";

function makeIdentityContext(navigate?: Navigator) {
	const previous = window.sessionStorage.getItem("token");
	const [token, setToken] = createSignal<null | string>(previous);

	return {
		token,
		login(token: string) {
			window.sessionStorage.setItem("token", token);
			setToken(token);
		},
		invalidate(reason?: string) {
			if (reason) console.info(`Invalidating token: '${reason}'`);
			if (navigate) navigate("/unauthorized/", { replace: true });
			window.sessionStorage.removeItem("token");
			setToken(null);
		},
	} as const;
}

const IdentityContext = createContext(makeIdentityContext());

export default function Identity(props: { children: any }) {
	const navigate = useNavigate();

	return (
		<IdentityContext.Provider value={makeIdentityContext(navigate)}>
			{props.children}
		</IdentityContext.Provider>
	);
}

export function useIdentity() {
	return useContext(IdentityContext);
}

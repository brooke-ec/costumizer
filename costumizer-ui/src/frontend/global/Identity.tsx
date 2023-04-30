import { createSignal, createContext, useContext } from "solid-js";
import { useNavigate, Navigator } from "@solidjs/router";

const STORAGE_KEY = "token";

export type IdentityType = ReturnType<typeof makeIdentityContext>;

function makeIdentityContext(navigate?: Navigator) {
	const previous = window.sessionStorage.getItem(STORAGE_KEY);
	const [token, setToken] = createSignal<null | string>(previous);

	return {
		token,
		login(token: string) {
			window.sessionStorage.setItem(STORAGE_KEY, token);
			setToken(token);
		},
		invalidate(reason?: string) {
			if (reason) console.info(`Invalidating token: '${reason}'`);
			if (navigate) navigate("/unauthorized/", { replace: true });
			window.sessionStorage.removeItem(STORAGE_KEY);
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

import { defineConfig } from "vite";
import solidPlugin from "vite-plugin-solid";

export default defineConfig({
	plugins: [solidPlugin()],
	root: "./src/frontend",
	server: {
		port: 3000,
		proxy: {
			"/api/": {
				target: "http://127.0.0.1:3001",
				changeOrigin: true,
				secure: false,
				ws: false,
			},
		},
	},
	build: {
		target: "esnext",
		outDir: "../../build/static",
	},
});

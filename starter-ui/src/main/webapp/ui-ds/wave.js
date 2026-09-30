/*
 * Decorative halftone wave behind the Starter page title.
 * Taken from payara/observability-tool 89faddc,
 * infrastructure/keycloak/themes/azul-payara/login/resources/js/insights-wave.js;
 * the only change is the element it mounts into (.hero instead of .insights-brand-panel).
 * Geometry is in CSS pixels; color is resolved by CSS from the payara-ds tokens (starter.css).
 */
(() => {
	'use strict';

	const MAX_POINTS = 12000;
	const MAX_DPR = 2;
	const SHADES = 32;
	const PULSE_TRAVEL = 8;
	const PULSE_PERIOD = 10;
	const PULSE_HALF_WIDTH = 0.12;
	const TAU = Math.PI * 2;

	function smoothstep(start, end, value) {
		const t = Math.max(0, Math.min(1, (value - start) / (end - start)));
		return t * t * (3 - 2 * t);
	}

	function init() {
		const panel = document.querySelector('.hero');
		if (!panel || panel.querySelector('.insights-wave')) return;

		const canvas = document.createElement('canvas');
		const context = canvas.getContext('2d');
		if (!context) return;

		canvas.className = 'insights-wave';
		canvas.setAttribute('aria-hidden', 'true');
		panel.prepend(canvas);

		const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');
		const buckets = Array.from({ length: SHADES }, () => []);
		let columns = [];
		let rows = [];
		let width = 0;
		let height = 0;
		let dpr = 0;
		let color;
		let frame = null;
		let previousTime = null;
		let elapsed = 0;
		let disposed = false;

		function readColor() {
			// Read a real color property: this also resolves token aliases and color functions.
			color = getComputedStyle(canvas).color;
		}

		function resize() {
			const bounds = canvas.getBoundingClientRect();
			const nextDpr = Math.min(window.devicePixelRatio || 1, MAX_DPR);
			if (width === bounds.width && height === bounds.height && dpr === nextDpr) return;
			width = bounds.width;
			height = bounds.height;
			dpr = nextDpr;
			canvas.width = Math.round(width * dpr);
			canvas.height = Math.round(height * dpr);
			context.setTransform(dpr, 0, 0, dpr, 0, 0);

			// Keep a fine regular mesh, reducing both axes together on very large displays.
			const wantedColumns = Math.max(2, Math.ceil(width * 1.16 / 7));
			const wantedRows = Math.max(2, Math.ceil(height / 7));
			const density = Math.min(1, Math.sqrt(MAX_POINTS / (wantedColumns * wantedRows)));
			const columnCount = Math.max(2, Math.min(MAX_POINTS / 2, Math.floor(wantedColumns * density)));
			const rowCount = Math.max(2, Math.min(Math.floor(wantedRows * density), Math.floor(MAX_POINTS / columnCount)));
			columns = Array.from({ length: columnCount }, (_, index) => {
				const x = (index / (columnCount - 1) * 1.16 - 0.08) * width;
				// On narrow screens, crop the same broad folds rather than squeezing them.
				return { x, u: (x - width / 2) / Math.max(900, width) + 0.5 };
			});
			rows = Array.from({ length: rowCount }, (_, index) => {
				const v = index / (rowCount - 1);
				const depth = Math.pow(v, 1.35);
				return {
					v,
					depth,
					fade: smoothstep(0, 0.12, v) * (1 - smoothstep(0.78, 1, v)),
				};
			});
		}

		function draw() {
			context.clearRect(0, 0, width, height);
			if (!width || !height) return;
			const time = elapsed / 1000;
			const cycle = time % PULSE_PERIOD;
			// The band starts and ends entirely offscreen, then waits for two seconds.
			const pulseCenter = -PULSE_HALF_WIDTH + (1 + 2 * PULSE_HALF_WIDTH) * cycle / PULSE_TRAVEL;
			const pulseActive = !reducedMotion.matches && cycle < PULSE_TRAVEL;
			for (const bucket of buckets) bucket.length = 0;

			for (const { v, depth, fade } of rows) {
				for (const { x: baseX, u } of columns) {
					// Two crossing swells and a longer fold make one continuous fabric surface.
					const a = u * TAU * 1.35 - v * 3.2 - time * 0.24;
					const b = u * TAU * 2.15 + v * 3.8 - time * 0.17;
					const c = u * TAU * 0.65 - v * 5.2 + time * 0.13;
					const swell = Math.sin(a) * 0.56 + Math.sin(b) * 0.27 + Math.cos(c) * 0.17;
					const x = baseX + Math.sin(a + v * 2) * Math.sin(v * Math.PI) * Math.min(width, 1400) * 0.018;
					const y = height * (0.16 + depth * 0.7 - swell * (0.12 + depth * 0.08));
					const lighting = 0.5 + 0.5 * Math.cos(a - 0.7 + Math.sin(b) * 0.45);
					const distance = Math.abs(x / width - pulseCenter) / PULSE_HALF_WIDTH;
					const pulse = pulseActive && distance < 1 ? Math.pow(Math.cos(distance * Math.PI / 2), 2) : 0;
					const edgeFade = smoothstep(0, 0.06, y / height) * (1 - smoothstep(0.88, 1, y / height));
					const radius = (0.3 + depth * 1.8) * (0.7 + lighting * 0.5) * (1 + pulse * 0.5);
					const opacity = fade * edgeFade * (0.25 + lighting * 0.5 + pulse * 0.24);
					const shade = Math.min(SHADES - 1, Math.round(opacity * (SHADES - 1)));
					if (!shade || x < -radius || x > width + radius) continue;
					buckets[shade].push(x, y, radius);
				}
			}

			// Batch circles by opacity instead of issuing thousands of individual fills.
			context.fillStyle = color;
			for (let shade = 1; shade < SHADES; shade++) {
				const bucket = buckets[shade];
				if (!bucket.length) continue;
				context.globalAlpha = shade / (SHADES - 1);
				context.beginPath();
				for (let index = 0; index < bucket.length; index += 3) {
					const x = bucket[index];
					const y = bucket[index + 1];
					const radius = bucket[index + 2];
					context.moveTo(x + radius, y);
					context.arc(x, y, radius, 0, TAU);
				}
				context.fill();
			}
			context.globalAlpha = 1;
		}

		function tick(timestamp) {
			frame = null;
			if (disposed || document.hidden || reducedMotion.matches || !width || !height) return;
			if (previousTime !== null) elapsed += Math.min(timestamp - previousTime, 100);
			previousTime = timestamp;
			draw();
			frame = requestAnimationFrame(tick);
		}

		function stop() {
			if (frame !== null) cancelAnimationFrame(frame);
			frame = null;
			previousTime = null;
		}

		function refresh() {
			stop();
			if (disposed) return;
			resize();
			readColor();
			if (document.hidden || !width || !height) return;
			draw();
			if (!reducedMotion.matches) frame = requestAnimationFrame(tick);
		}

		const themeObserver = new MutationObserver(refresh);
		for (const element of [document.documentElement, document.body]) {
			themeObserver.observe(element, { attributes: true, attributeFilter: ['class', 'style'] });
		}
		const sizeObserver = new ResizeObserver(refresh);
		sizeObserver.observe(canvas);
		window.addEventListener('resize', refresh);
		document.addEventListener('visibilitychange', refresh);
		reducedMotion.addEventListener('change', refresh);

		// Preserve the animation on bfcache restores, and release observers on navigation.
		window.addEventListener('pagehide', (event) => {
			stop();
			if (event.persisted) return;
			disposed = true;
			themeObserver.disconnect();
			sizeObserver.disconnect();
			window.removeEventListener('resize', refresh);
			document.removeEventListener('visibilitychange', refresh);
			reducedMotion.removeEventListener('change', refresh);
		});
		window.addEventListener('pageshow', refresh);
		refresh();
	}

	// Keycloak's inherited template loads theme scripts in <head>, without defer.
	if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init, { once: true });
	else init();
})();

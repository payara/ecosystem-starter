/*
 *
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
/*
 * ER Diagram Builder: the "ER Diagram Designer" step and its "Diagram Builder & Live Preview"
 * dialog. Loaded by index.html as a module; the markup is in index.html, the styles in
 * starter.css.
 *
 * - The step: the diagram search field (combobox.js over the select the form submits), the
 *   "Generate Code For" layers (each needs the ones before it), and the button that opens the
 *   dialog.
 * - The dialog (a native <dialog>): a diagram picker, Code / Preview tabs, the version history,
 *   the Mermaid code in CodeMirror (coloured by the ER grammar below), the preview drawn by
 *   Mermaid with zoom, and the composer that asks the AI for a new diagram or a change.
 * - One diagram at a time. Its code goes into the hidden erDiagram field, which the page's form
 *   submits (assets/main.js).
 *
 * Backend (starter-ui, unchanged): GET erDiagram/<name>.mmd for the samples; under
 * resources/er-diagram: GET generate?request=, and POST update?request=, enlarge?name= and
 * shrink?name= with the current code as the body.
 */
import { initCombobox } from './combobox.js';

const SAMPLES = [
	'Auction Management System',
	'Customer Relationship Management',
	'Employee Management',
	'Energy Management System',
	'Event Management',
	'Flight Reservation System',
	'Healthcare System',
	'Insurance Claim Management System',
	'Inventory System',
	'Laboratory Information Management System',
	'Law Firm Management System',
	'Legal Case Management System',
	'Library Management System',
	'Membership Management System',
	'Music Streaming Platform',
	'Product Catalog',
	'Property Management System',
	'Recruitment Management System',
	'Sales Tracking',
	'School Management',
	'Video Streaming Platform',
	'Waste Management System',
];

const MERMAID_URL = 'https://cdn.jsdelivr.net/npm/mermaid@10.9.8/dist/mermaid.esm.min.mjs';

const MESSAGES = {
	busy: 'Updating the diagram…',
	tooManyRequests:
		'Too many requests in a short time: requests are limited per hour. Please wait a few moments and try again.',
	failed: 'The diagram couldn’t be updated. Please try again.',
	offline: 'The diagram couldn’t be updated. Check your connection and try again.',
	noPreview: 'The diagram preview couldn’t load. You can still edit the diagram in Code and generate the application.',
};

const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');

// ---------- Mermaid ER code highlighting ----------
// CodeMirror 5 has no Mermaid mode; this ER-diagram grammar runs on its Simple Mode addon (loaded
// in index.html). Tokens, coloured in starter.css: keyword (erDiagram), operator (cardinality and
// the label colon), def (entity), type and variable (an attribute's type and name), attribute
// (PK / FK / UK), string (labels and quoted comments), comment (%%).
function defineMermaidMode(CodeMirror) {
	const quoted = /"(?:[^"\\]|\\.)*"/;
	CodeMirror.defineSimpleMode('mermaid', {
		start: [
			{ regex: /%%.*/, token: 'comment' },
			{ regex: /erDiagram\b/, token: 'keyword' },
			{ regex: /(?:\|\||\|o|\}o|\}\|)(?:--|\.\.)(?:\|\||o\||o\{|\|\{)/, token: 'operator' },
			{ regex: /(:)(\s*)(.*)/, token: ['operator', null, 'string'] },
			{ regex: /\{/, token: 'bracket', next: 'entity' },
			{ regex: quoted, token: 'string' },
			{ regex: /[A-Za-z_][\w-]*/, token: 'def' },
		],
		entity: [
			{ regex: /%%.*/, token: 'comment' },
			{ regex: /\}/, token: 'bracket', next: 'start' },
			{ regex: /(\s*)([A-Za-z_][\w-]*(?:\[\])?)(\s+)([A-Za-z_*][\w-]*)/, sol: true, token: [null, 'type', null, 'variable'] },
			{ regex: /\b(?:PK|FK|UK)\b/, token: 'attribute' },
			{ regex: quoted, token: 'string' },
		],
		meta: { lineComment: '%%' },
	});
}

// Without CodeMirror (its CDN failed), the plain textarea stands in, with the same small surface.
function plainEditor(textarea) {
	const listeners = [];
	const changed = () => listeners.forEach((listener) => listener());
	textarea.addEventListener('input', changed);
	return {
		getValue: () => textarea.value,
		setValue(value) {
			textarea.value = value;
			changed();
		},
		on: (event, listener) => listeners.push(listener),
		refresh() {},
	};
}

function createEditor(textarea) {
	const { CodeMirror } = window;
	if (!CodeMirror?.fromTextArea) return plainEditor(textarea);
	if (CodeMirror.defineSimpleMode) defineMermaidMode(CodeMirror);
	return CodeMirror.fromTextArea(textarea, {
		lineNumbers: true,
		mode: 'mermaid',
		theme: 'default',
		screenReaderLabel: textarea.getAttribute('aria-label'),
	});
}

// ---------- Backend ----------
class RequestError extends Error {
	constructor(status) {
		super(`Request failed with status ${status}`);
		this.status = status;
	}
}

async function request(url, options) {
	const response = await fetch(url, options);
	if (!response.ok) throw new RequestError(response.status);
	return response.text();
}

// The AI answers may wrap the diagram in a Markdown code fence.
const unfence = (text) => text.replace(/```mermaid|```/g, '').trim();

const post = (path, code) =>
	request(`resources/er-diagram/${path}`, {
		method: 'POST',
		headers: { 'Content-Type': 'text/plain' },
		body: encodeURIComponent(code),
	}).then(unfence);

const backend = {
	sample: (name) => request(`erDiagram/${encodeURIComponent(name)}.mmd`),
	generate: (description) =>
		request(`resources/er-diagram/generate?request=${encodeURIComponent(description)}`).then(unfence),
	update: (code, change) => post(`update?request=${encodeURIComponent(change)}`, code),
	enlarge: (code, name) => post(`enlarge?name=${encodeURIComponent(name)}`, code),
	shrink: (code, name) => post(`shrink?name=${encodeURIComponent(name)}`, code),
};

const messageFor = (error) =>
	error instanceof RequestError
		? error.status === 429
			? MESSAGES.tooManyRequests
			: MESSAGES.failed
		: MESSAGES.offline;

// ---------- Tabs ----------
// WAI-ARIA tabs: a click, or Arrow Left / Right, Home and End, selects a tab and shows its panel.
function initTabs(tablist, onSelect) {
	const tabs = [...tablist.querySelectorAll('[role="tab"]')];
	const select = (tab, focus) => {
		for (const other of tabs) {
			const selected = other === tab;
			other.setAttribute('aria-selected', String(selected));
			other.tabIndex = selected ? 0 : -1;
			document.getElementById(other.getAttribute('aria-controls')).hidden = !selected;
		}
		if (focus) tab.focus();
		onSelect?.(tab);
	};
	tablist.addEventListener('click', (event) => {
		const tab = event.target.closest('[role="tab"]');
		if (tab) select(tab, false);
	});
	tablist.addEventListener('keydown', (event) => {
		const index = tabs.indexOf(document.activeElement);
		const next = { ArrowRight: index + 1, ArrowLeft: index - 1, Home: 0, End: tabs.length - 1 }[event.key];
		if (index < 0 || next === undefined) return;
		event.preventDefault();
		select(tabs[(next + tabs.length) % tabs.length], true);
	});
}

// ---------- "Generate Code For" ----------
// JPA → Repository → REST → Web: turning a layer on turns on the ones before it, turning it off
// turns off the ones after it. Web is a select, off when "None".
function initLayers(layers) {
	const isOn = (layer) => (layer.type === 'checkbox' ? layer.checked : layer.value !== 'none');
	const setOn = (layer, on) => {
		if (layer.type === 'checkbox') layer.checked = on;
		else if (!on) layer.value = 'none';
	};
	layers.forEach((layer, index) =>
		layer.addEventListener('change', () => {
			if (isOn(layer)) layers.slice(0, index).forEach((before) => setOn(before, true));
			else layers.slice(index + 1).forEach((after) => setOn(after, false));
		}),
	);
}

// ---------- The builder ----------
function initErBuilder() {
	const byId = (id) => document.getElementById(id);
	const dialog = byId('previewModal');
	const stepSelect = byId('mermaidErDiagramList');
	if (!dialog || !stepSelect) return;

	const dialogSelect = byId('mermaidErDiagramListPreview');
	const formField = byId('erDiagram');
	const openButton = byId('erBuilderOpen');
	const closeButton = dialog.querySelector('.close');
	const progress = byId('loadingBarAI');
	const status = byId('erStatus');
	const versionText = byId('erVersion');
	const previousButton = byId('prevERDiagram');
	const nextButton = byId('nextERDiagram');
	const preview = byId('livePreview');
	const emptyState = byId('erEmpty');
	const composer = byId('erComposer');
	const input = byId('queryBox');
	const inputLabel = byId('queryBoxLabel');
	const send = byId('erSend');
	const chips = byId('erSuggestions');
	const more = byId('erMoreSuggestions');

	const editor = createEditor(byId('sourceCode'));
	const state = {
		name: '', // the chosen diagram: a sample name, or the description a diagram was generated from
		saved: new Map(), // latest code by name, so a diagram comes back as it was left
		versions: [], // this diagram's versions: { code, change }
		version: -1,
		busy: false,
		pending: 0, // requests still running
	};
	const hasDiagram = () => editor.getValue().trim() !== '';

	// ----- Status line and progress -----
	function setStatus(message, isError = false) {
		status.textContent = message;
		status.classList.toggle('er-builder__status--error', isError);
	}

	function setBusy(busy) {
		state.busy = busy;
		dialog.classList.toggle('er-builder--busy', busy);
		progress.hidden = !busy;
		if (busy) setStatus(MESSAGES.busy);
		else if (!status.classList.contains('er-builder__status--error')) setStatus('');
		syncComposer();
	}

	// A request with progress and any error shown in the dialog. Requests can overlap (a sample
	// loading while the AI answers), so the dialog stays busy until the last one ends.
	async function run(task) {
		state.pending++;
		setBusy(true);
		try {
			return await task();
		} catch (error) {
			console.error('ER diagram request failed:', error);
			setStatus(messageFor(error), true);
			return null;
		} finally {
			if (--state.pending === 0) setBusy(false);
		}
	}

	// ----- Pickers: the step's search field and the dialog's, over two selects kept in step -----
	const selects = [stepSelect, dialogSelect];
	for (const select of selects) {
		select.replaceChildren(new Option('', ''), ...SAMPLES.map((name) => new Option(name, name)));
	}
	const pickers = selects.map((select) => initCombobox(select));

	function showPicked(name) {
		for (const select of selects) select.value = name;
		pickers.forEach((picker) => picker.refresh());
	}

	// A generated diagram joins the list, first after the empty choice.
	function addChoice(name) {
		for (const select of selects) {
			if (![...select.options].some((option) => option.value === name)) {
				select.add(new Option(name, name), 1);
			}
		}
	}

	async function pick(name) {
		const previous = state.name;
		state.name = name;
		showPicked(name);
		let code = name ? state.saved.get(name) : '';
		if (code === undefined) {
			code = await run(() => backend.sample(name));
			if (state.name !== name) return;
			if (code === null) {
				// The sample did not load: the editor still holds the previous diagram, so keep its name.
				state.name = previous;
				showPicked(previous);
				return;
			}
		}
		startVersions(code);
	}

	selects.forEach((select) => select.addEventListener('change', () => pick(select.value)));

	// ----- Versions -----
	function renderVersions() {
		previousButton.disabled = state.version <= 0;
		nextButton.disabled = state.version >= state.versions.length - 1;
		versionText.textContent =
			state.versions.length > 1 ? `Version ${state.version + 1} of ${state.versions.length}` : '';
	}

	function startVersions(code, change) {
		state.versions = code ? [{ code, change }] : [];
		state.version = state.versions.length - 1;
		editor.setValue(code);
		renderVersions();
	}

	function addVersion(code, change) {
		state.versions.push({ code, change });
		state.version = state.versions.length - 1;
		editor.setValue(code);
		renderVersions();
	}

	function goToVersion(step) {
		const version = state.versions[state.version + step];
		if (!version) return;
		state.version += step;
		editor.setValue(version.code);
		renderVersions();
	}

	previousButton.addEventListener('click', () => goToVersion(-1));
	nextButton.addEventListener('click', () => goToVersion(1));

	// ----- AI requests -----
	// The picker stays usable while a request runs. If another diagram is picked meanwhile, the
	// answer no longer applies to what is shown: a new diagram only joins the list, and a change
	// to the old diagram is dropped.
	async function generate(description) {
		const name = state.name;
		const code = await run(() => backend.generate(description));
		if (!code) return;
		addChoice(description);
		state.saved.set(description, code);
		if (state.name !== name) return;
		state.name = description;
		showPicked(description);
		startVersions(code, description);
		clearComposer();
	}

	async function change(request) {
		const name = state.name;
		const code = await run(() => backend.update(editor.getValue(), request));
		if (!code || state.name !== name) return;
		addVersion(code, request);
		clearComposer();
	}

	async function resize(kind) {
		const name = state.name;
		const code = await run(() => backend[kind](editor.getValue(), name));
		if (code && state.name === name) addVersion(code);
	}

	byId('enlargeERDiagram').addEventListener('click', () => resize('enlarge'));
	byId('shrinkERDiagram').addEventListener('click', () => resize('shrink'));

	// ----- Preview -----
	// Mermaid comes from a CDN and is loaded on its own: if it fails, only the preview is lost.
	// The diagram code can come from the AI, so it is drawn with Mermaid's strict security level.
	// It is only fetched when a diagram is first drawn, so visitors who never open the builder
	// do not pay for it.
	// A failed import() of a URL is remembered by the browser for the life of the page, so a retry
	// asks for the same file with a throwaway query string.
	let mermaidPromise = null;
	let mermaidAttempts = 0;
	function loadMermaid() {
		mermaidPromise ??= import(mermaidAttempts++ ? `${MERMAID_URL}?retry=${mermaidAttempts}` : MERMAID_URL)
			.then(({ default: mermaid }) => {
				mermaid.initialize({ startOnLoad: false, securityLevel: 'strict' });
				return mermaid;
			})
			.catch((error) => {
				console.error('Mermaid could not be loaded:', error);
				mermaidPromise = null; // let the next render try again
				return null;
			});
		return mermaidPromise;
	}
	let renderCount = 0;
	let renderId = 0;

	async function render(code) {
		const ticket = ++renderCount;
		const mermaid = await loadMermaid();
		if (ticket !== renderCount) return;
		if (!mermaid) {
			const message = document.createElement('p');
			message.className = 'er-builder__empty';
			message.textContent = MESSAGES.noPreview;
			preview.replaceChildren(message);
			return;
		}
		if (!code.trim()) {
			preview.replaceChildren();
			return;
		}
		const id = `er-diagram-preview-${++renderId}`;
		try {
			const { svg, bindFunctions } = await mermaid.render(id, code);
			if (ticket !== renderCount) return;
			preview.innerHTML = svg;
			// One image for assistive technology.
			const drawing = preview.querySelector('svg');
			drawing?.setAttribute('role', 'img');
			drawing?.setAttribute('aria-label', 'ER diagram preview');
			drawing?.removeAttribute('aria-roledescription');
			bindFunctions?.(drawing);
		} catch {
			// Code being edited is often not valid Mermaid yet: keep the last drawing, and drop the
			// error drawing Mermaid may leave in the page.
			document.getElementById(`d${id}`)?.remove();
			document.getElementById(id)?.remove();
		}
	}

	editor.on('change', () => {
		const code = editor.getValue();
		formField.value = code;
		if (state.name) state.saved.set(state.name, code);
		render(code);
		syncComposer();
	});

	// ----- Zoom -----
	// Fit (the default) shows the whole diagram, never above its natural size; Zoom in and out
	// step through fixed levels of the natural size, and the preview scrolls. The level holds
	// across re-renders.
	const zoomBar = byId('erZoom');
	const zoomIn = byId('erZoomIn');
	const zoomOut = byId('erZoomOut');
	const zoomFit = byId('erZoomFit');
	const zoomLevel = byId('erZoomLevel');
	const zoomSteps = [0.25, 0.5, 0.75, 1, 1.5, 2, 3, 4];
	let zoom = null;

	const drawing = () => {
		const svg = preview.querySelector('svg');
		return svg?.viewBox.baseVal.width ? svg : null;
	};

	function fitScale(svg) {
		const style = getComputedStyle(preview);
		const width = preview.clientWidth - parseFloat(style.paddingLeft) - parseFloat(style.paddingRight);
		const height = preview.clientHeight - parseFloat(style.paddingTop) - parseFloat(style.paddingBottom);
		const box = svg.viewBox.baseVal;
		return width > 0 && height > 0 ? Math.min(1, width / box.width, height / box.height) : null;
	}

	function applyZoom() {
		const svg = drawing();
		const scale = svg && (zoom ?? fitScale(svg));
		if (!scale) return;
		svg.style.maxWidth = 'none';
		svg.style.width = `${svg.viewBox.baseVal.width * scale}px`;
		zoomLevel.textContent = `${Math.round(scale * 100)}%`;
		zoomFit.setAttribute('aria-pressed', String(zoom === null));
		zoomOut.disabled = scale <= zoomSteps[0];
		zoomIn.disabled = scale >= zoomSteps.at(-1);
	}

	function stepZoom(direction) {
		const svg = drawing();
		const current = svg && (zoom ?? fitScale(svg));
		if (!current) return;
		const next =
			direction > 0
				? zoomSteps.find((step) => step > current + 0.01)
				: zoomSteps.findLast((step) => step < current - 0.01);
		if (next) zoom = next;
		applyZoom();
	}

	zoomIn.addEventListener('click', () => stepZoom(1));
	zoomOut.addEventListener('click', () => stepZoom(-1));
	zoomFit.addEventListener('click', () => {
		zoom = null;
		applyZoom();
	});
	new MutationObserver(() => {
		applyZoom();
		syncComposer();
	}).observe(preview, { childList: true });
	new ResizeObserver(() => zoom === null && applyZoom()).observe(preview);

	// ----- Composer -----
	// Grows with its text, Enter sends and Shift+Enter starts a new line; Send and the suggestions
	// are enabled only when they can act; the suggestion row scrolls with "More" when it
	// overflows. With no diagram yet, the preview says how to start and the composer asks for a
	// description of the application.
	function fit() {
		input.style.height = 'auto';
		input.style.height = `${input.scrollHeight}px`;
	}

	function syncComposer() {
		const empty = !hasDiagram();
		input.placeholder = empty
			? 'Describe the application, e.g. “Online bookstore with orders and reviews”'
			: 'Describe a change, e.g. “Add customer address”';
		inputLabel.textContent = empty ? 'Describe the application to generate a diagram' : 'Describe a change to the diagram';
		send.disabled = state.busy || !input.value.trim();
		chips.querySelectorAll('.chip').forEach((chip) => (chip.disabled = state.busy || empty));
		emptyState.hidden = !empty;
		preview.hidden = empty;
		zoomBar.hidden = empty || !drawing();
	}

	function syncSuggestions() {
		const hidden = chips.scrollWidth - chips.clientWidth - chips.scrollLeft > 1;
		chips.classList.toggle('composer__chips--more', hidden);
		more.hidden = !hidden;
	}

	function clearComposer() {
		input.value = '';
		fit();
		syncComposer();
	}

	input.addEventListener('input', () => {
		fit();
		syncComposer();
	});
	input.addEventListener('keydown', (event) => {
		if (event.key !== 'Enter' || event.shiftKey || event.isComposing) return;
		event.preventDefault();
		if (!send.disabled) composer.requestSubmit();
	});
	composer.addEventListener('submit', (event) => {
		event.preventDefault();
		const text = input.value.trim();
		if (!text || state.busy) return;
		if (hasDiagram()) change(text);
		else generate(text);
	});
	chips.addEventListener('scroll', syncSuggestions, { passive: true });
	new ResizeObserver(syncSuggestions).observe(chips);
	more.addEventListener('click', () =>
		chips.scrollBy({ left: chips.clientWidth * 0.8, behavior: reducedMotion.matches ? 'auto' : 'smooth' }),
	);

	// ----- The dialog -----
	// A native modal <dialog>: the browser keeps focus inside, dims the page and restores focus
	// on close. Escape closes it, except that an open tooltip or suggestion list closes first,
	// and it never clears the picker (the search field clears on Escape by itself).
	initTabs(dialog.querySelector('[role="tablist"]'), (tab) => {
		if (tab.id === 'erTabCode') editor.refresh();
	});

	openButton.addEventListener('click', () => {
		dialog.showModal();
		editor.refresh();
		requestAnimationFrame(() => {
			fit();
			syncComposer();
			syncSuggestions();
			applyZoom();
		});
	});
	closeButton.addEventListener('click', () => dialog.close());
	// A click on the dimmed page around the card.
	dialog.addEventListener('click', (event) => {
		if (event.target === dialog) dialog.close();
	});

	let escapeClosesSomethingElse = false;
	dialog.addEventListener(
		'keydown',
		(event) => {
			if (event.key !== 'Escape') return;
			const tooltip = dialog.querySelector('.help:is(:hover, :focus-within):not(.help--dismissed)');
			const suggestions = dialog.querySelector('.combobox__input[aria-expanded="true"]');
			escapeClosesSomethingElse = Boolean(tooltip || suggestions);
			if (!escapeClosesSomethingElse && event.target.closest('.combobox')) event.stopPropagation();
		},
		true,
	);
	dialog.addEventListener('cancel', (event) => {
		if (escapeClosesSomethingElse) event.preventDefault();
		escapeClosesSomethingElse = false;
	});

	syncComposer();
	renderVersions();
}

initLayers(['generateJpa', 'generateRepository', 'generateRest', 'generateWeb'].map((id) => document.getElementById(id)).filter(Boolean));
initErBuilder();

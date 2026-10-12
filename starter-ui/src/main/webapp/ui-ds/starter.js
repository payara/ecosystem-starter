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
 * Payara Starter page behaviour. Replaces the blinky interface script (ui/js/interface.min.js):
 * the step-by-step form, section scrolling between the title and the steps, the "Full MP"
 * checkbox group, the help tooltips, the "steps below" cue, the phone menu, and "Privacy
 * Settings" reopening the HubSpot cookie banner. starter.css styles the states it sets:
 * .js__form-steps on the form, .form-steps__step--active on the open step, and the
 * .wizard-progress bar and its segments. The ER Diagram Designer step and its dialog are in
 * er-builder.js.
 */
(() => {
	'use strict';

	const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');

	// Java's reserved words, which can't name a package.
	const JAVA_KEYWORDS = new Set(
		('abstract assert boolean break byte case catch char class const continue default do double else enum ' +
			'extends final finally float for goto if implements import instanceof int interface long native new ' +
			'package private protected public return short static strictfp super switch synchronized this throw ' +
			'throws transient try void volatile while true false null _').split(' '),
	);

	// ---------- Step-by-step form ----------
	// One step at a time under the step bar and "Step N of M"; one action row with Back, Download
	// project (the submit button: a secondary action until the last step, where it is the primary)
	// and Next.
	function initSteps(form) {
		const steps = [...form.querySelectorAll('.form-steps__step')];
		const back = form.querySelector('.form__row--footer .form__step__button-back');
		const next = form.querySelector('.form__row--footer .form__step__button');
		const download = form.querySelector('.form__row--footer button[type="submit"]');
		if (!steps.length || !back || !next || !download) return;
		const total = steps.length;
		const titles = steps.map((step) => step.querySelector('legend')?.textContent.trim() ?? '');
		const labels = steps.map((step, index) => step.dataset.stepLabel || titles[index]);
		let active = 0;

		// "Step N of M" shows above each title; the step's legend (its name when focus lands on it)
		// starts with the same words.
		steps.forEach((step, index) => {
			step.tabIndex = -1;
			step.querySelector('div.form-steps__title')?.setAttribute('data-step', `Step ${index + 1} of ${total}`);
			step.querySelector(':scope > legend')?.prepend(`Step ${index + 1} of ${total}: `);
		});

		// The step bar: one segment per step, filled up to the open one. Each segment is a button
		// that opens its step, with the step's short name (data-step-label) under it; one Tab stop,
		// Arrow keys move between the segments.
		function createBar() {
			const nav = document.createElement('nav');
			nav.className = 'wizard-progress';
			nav.setAttribute('aria-label', 'Steps');
			const list = document.createElement('ol');
			list.className = 'wizard-progress__list';
			steps.forEach((step, index) => {
				const button = document.createElement('button');
				button.type = 'button';
				button.className = 'wizard-progress__segment';
				button.setAttribute('aria-label', `Step ${index + 1} of ${total}: ${titles[index]}`);
				const label = document.createElement('span');
				label.className = 'wizard-progress__label';
				label.textContent = labels[index];
				button.append(label);
				button.addEventListener('click', () => go(index));
				const item = document.createElement('li');
				item.append(button);
				list.append(item);
			});
			list.addEventListener('keydown', (event) => {
				const buttons = [...list.querySelectorAll('button')];
				const index = buttons.indexOf(document.activeElement);
				const target = { ArrowRight: index + 1, ArrowLeft: index - 1, Home: 0, End: total - 1 }[event.key];
				if (index < 0 || target === undefined) return;
				event.preventDefault();
				buttons[Math.max(0, Math.min(target, total - 1))].focus();
			});
			nav.append(list);
			return nav;
		}

		const bar = createBar();
		form.prepend(bar);

		// While the steps scroll under the header, a second bar rides the header's lower edge at full
		// width; only one of the two is shown (and reachable) at a time.
		const header = document.querySelector('.page__header');
		const headerBar = header ? createBar() : null;
		if (headerBar) {
			headerBar.hidden = true;
			header.append(headerBar);
		}
		const bars = headerBar ? [bar, headerBar] : [bar];

		function render() {
			steps.forEach((step, index) => step.classList.toggle('form-steps__step--active', index === active));
			for (const each of bars) {
				each.querySelectorAll('.wizard-progress__segment').forEach((segment, index) => {
					segment.classList.toggle('wizard-progress__segment--reached', index <= active);
					segment.tabIndex = index === active ? 0 : -1;
					if (index === active) segment.setAttribute('aria-current', 'step');
					else segment.removeAttribute('aria-current');
				});
			}
			back.hidden = active === 0;
			next.hidden = active === total - 1;
			download.classList.toggle('button--ghost', active !== total - 1);
			fillSummary();
		}

		// Focus moves to the target (a step, named by its legend, or a result title); the page
		// scrolls only if it is out of view, bringing the whole screen-high block under the
		// header — one smooth scroll, no anchor jump.
		function reveal(target) {
			target.focus({ preventScroll: true });
			const { top } = target.getBoundingClientRect();
			const edge = parseFloat(getComputedStyle(form).scrollMarginTop) || 0;
			if (top < edge || top > window.innerHeight * 0.75) {
				form.scrollIntoView({ block: 'start', behavior: reducedMotion.matches ? 'auto' : 'smooth' });
			}
		}

		function go(index) {
			if (fading) return;
			const leftResult = closeResult();
			if ((index === active && !leftResult) || index < 0 || index >= total) return;
			active = index;
			render();
			reveal(steps[active]);
		}

		// ---------- Field checks ----------
		// Text fields carry their rule in index.html: data-check="name" (Maven coordinates: letters,
		// digits, dots, hyphens and underscores) or "package" (a Java package name), and
		// data-check-when="<checkbox id>" when the field counts only while that box is ticked. The
		// rule becomes the field's validity, so the browser won't send an invalid form. A field's
		// message shows once the field has been left, then follows what is typed; Next checks the
		// open step, and Download project opens the first step with a problem.
		const checkedFields = [...form.querySelectorAll('[data-check]')];
		const flagged = new Set();
		let firstInvalid = null;

		function nameOf(field) {
			const ids = field.getAttribute('aria-labelledby');
			const text = ids
				? ids.split(/\s+/).map((id) => document.getElementById(id)?.textContent.trim()).join(' ')
				: field.labels?.[0]?.textContent.trim();
			return text || 'This field';
		}

		function problemOf(field) {
			const when = field.dataset.checkWhen && document.getElementById(field.dataset.checkWhen);
			if (field.disabled || (when && (!when.checked || when.disabled))) return '';
			const { value } = field;
			if (!value.trim()) return `${nameOf(field)} can’t be empty.`;
			if (field.dataset.check === 'name' && !/^[A-Za-z0-9_.-]+$/.test(value)) {
				return 'Use only letters, digits, dots (.), hyphens (-) and underscores (_), with no spaces.';
			}
			if (field.dataset.check === 'package') {
				if (!/^[A-Za-z_][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)*$/.test(value)) {
					return 'Use a Java package name: words of letters, digits and underscores, separated by dots, each starting with a letter.';
				}
				const reserved = value.split('.').find((word) => JAVA_KEYWORDS.has(word));
				if (reserved) return `“${reserved}” is a reserved word in Java, so it can’t be part of a package name.`;
			}
			return '';
		}

		function showProblem(field, message) {
			const id = `${field.id}-problem`;
			let note = document.getElementById(id);
			if (!message) {
				note?.remove();
				field.removeAttribute('aria-invalid');
				if (field.getAttribute('aria-describedby') === id) field.removeAttribute('aria-describedby');
				return;
			}
			if (!note) {
				note = document.createElement('span');
				note.className = 'form__error';
				note.id = id;
				field.after(note);
			}
			note.textContent = message;
			field.setAttribute('aria-invalid', 'true');
			field.setAttribute('aria-describedby', id);
		}

		// Brings the field's validity up to date; says whether it is fine.
		function check(field) {
			const message = problemOf(field);
			field.setCustomValidity(message);
			if (flagged.has(field)) showProblem(field, message);
			return !message;
		}

		// From now on the field shows its message.
		function flag(field) {
			flagged.add(field);
			check(field);
		}

		// Checks the fields of the open step, flags the problems and focuses the first; says
		// whether the step is fine.
		function checkStep(step) {
			const problems = checkedFields.filter((field) => step.contains(field) && !check(field));
			problems.forEach(flag);
			problems[0]?.focus();
			return !problems.length;
		}

		for (const field of checkedFields) {
			field.addEventListener('input', () => check(field));
			field.addEventListener('blur', () => flag(field));
			check(field);
		}
		// Other choices can switch a rule on or off (a layer box, Core Profile), sometimes by script.
		form.addEventListener('change', () => checkedFields.forEach(check));
		download.addEventListener('click', () => checkedFields.forEach(check));
		// Download project with a problem: the browser reports each invalid field here instead of
		// sending the form; the page shows its own messages and opens the first one's step.
		form.addEventListener(
			'invalid',
			(event) => {
				event.preventDefault();
				flag(event.target);
				if (firstInvalid) return;
				firstInvalid = event.target;
				// After the browser has reported every invalid field (a task, not a microtask: those
				// run between the reports).
				setTimeout(() => {
					go(steps.findIndex((step) => step.contains(firstInvalid)));
					firstInvalid.focus();
					firstInvalid = null;
				});
			},
			true,
		);
		// Start over: after assets/main.js has put the fields back, no field shows a message.
		form.addEventListener('reset', () =>
			setTimeout(() => {
				flagged.clear();
				for (const field of checkedFields) {
					showProblem(field, '');
					check(field);
				}
			}),
		);

		// ---------- Review ----------
		// The last step lists every choice with a way back to each step. index.html marks what each
		// line shows: data-summary names a field (an id) or a radio group (a name), data-summary-any
		// lists boxes to name the ticked ones of, data-summary-label copies a field's current label.
		// It is filled whenever the steps change.
		const labelOf = (input) => input.labels?.[0]?.textContent.trim() ?? input.value;

		function describe(key) {
			const field = document.getElementById(key);
			if (!field) {
				const picked = form.querySelector(`input[name="${key}"]:checked`);
				return picked ? labelOf(picked) : 'None';
			}
			if (field.disabled) return 'Not available';
			if (field.type === 'checkbox') return field.checked ? 'Yes' : 'No';
			if (field.tagName === 'SELECT') return field.selectedOptions[0]?.textContent.trim() || 'None';
			return field.value.trim() || 'Not set';
		}

		function describeAny(ids) {
			const boxes = ids.split(/\s+/).map((id) => document.getElementById(id)).filter(Boolean);
			if (boxes.every((box) => box.disabled)) return 'Not available';
			return boxes.filter((box) => box.checked).map(labelOf).join(', ') || 'None';
		}

		function fillSummary() {
			form.querySelectorAll('[data-summary]').forEach((line) => {
				const field = document.getElementById(line.dataset.summary);
				line.textContent = describe(line.dataset.summary);
				if (field?.validationMessage) {
					const problem = document.createElement('span');
					problem.className = 'summary__problem';
					problem.textContent = 'Needs a fix';
					line.append(' ', problem);
				}
			});
			form.querySelectorAll('[data-summary-any]').forEach((line) => {
				line.textContent = describeAny(line.dataset.summaryAny);
			});
			form.querySelectorAll('[data-summary-label]').forEach((term) => {
				const field = document.getElementById(term.dataset.summaryLabel);
				if (field) term.textContent = labelOf(field);
			});
		}

		form.querySelectorAll('[data-summary-edit]').forEach((button) =>
			button.addEventListener('click', () => go(steps.indexOf(document.getElementById(button.dataset.summaryEdit)))),
		);

		// ---------- Download project: generating, then ready or failed ----------
		// assets/main.js fetches the project and reports on the form (starter:generating, then
		// starter:generated or starter:failed). The open step and the action row fade out and the
		// result panel takes their place, its content centred; the step bar waits while the
		// project is generated. Ready offers Back to options (the same step, every choice kept)
		// and Start over (a reset button: every choice back to its default, then step 1); failed
		// offers Back to options and Try again. Focus moves to each state's title, so screen
		// readers read it out.
		const result = document.getElementById('formResult');
		const footer = form.querySelector('.form__row--footer');
		let resultState = '';
		let fading = false;
		let saveAgain = () => {};
		let switching = Promise.resolve();

		function fadeOut(elements) {
			const style = getComputedStyle(form);
			const duration = reducedMotion.matches ? 0 : parseFloat(style.getPropertyValue('--motion-duration-base')) || 0;
			if (!duration) return Promise.resolve([]);
			const easing = style.getPropertyValue('--motion-easing-standard').trim() || 'ease';
			const animations = elements.map((element) =>
				element.animate([{ opacity: 1 }, { opacity: 0 }], { duration, easing, fill: 'forwards' }),
			);
			return Promise.all(animations.map((animation) => animation.finished)).then(
				() => animations,
				() => animations,
			);
		}

		function showResult(state) {
			// While the step fades out its fields can't be changed and the step bar and Back / Next
			// wait (go() stops); focus stays on Download project until the panel's title takes it.
			if (!resultState && !fading) {
				fading = true;
				steps[active].inert = true;
				for (const each of bars) each.inert = true;
			}
			switching = switching.then(async () => {
				if (!resultState) {
					const leaving = steps[active];
					const faded = await fadeOut([leaving, footer]);
					form.classList.add('form-steps--result');
					faded.forEach((animation) => animation.cancel());
					leaving.inert = false;
					fading = false;
				}
				resultState = state;
				result.hidden = false;
				for (const panel of result.querySelectorAll('[data-result]')) panel.hidden = panel.dataset.result !== state;
				for (const each of bars) each.inert = state === 'generating';
				reveal(result.querySelector(`[data-result="${state}"] .form-result__title`));
			});
		}

		// Back to the steps; says whether the result panel was open.
		function closeResult() {
			if (!resultState) return false;
			resultState = '';
			result.hidden = true;
			form.classList.remove('form-steps--result');
			for (const each of bars) each.inert = false;
			return true;
		}

		if (result && footer) {
			form.addEventListener('starter:generating', () => showResult('generating'));
			form.addEventListener('starter:generated', (event) => {
				result.querySelector('.form-result__file').textContent = event.detail.filename;
				saveAgain = event.detail.download;
				showResult('generated');
			});
			form.addEventListener('starter:failed', () => showResult('failed'));
			result.addEventListener('click', (event) => {
				const action = event.target.closest('[data-result-action]')?.dataset.resultAction;
				if (action === 'download') saveAgain();
				// Download project is hidden while the result panel shows, but it is still the form's submit button.
				if (action === 'retry') form.requestSubmit(download);
				if (action === 'back') {
					closeResult();
					reveal(steps[active]);
				}
			});
			// Start over: the browser resets the fields after this event, and assets/main.js
			// works out the options that depend on others again.
			form.addEventListener('reset', () => go(0));
		}

		// Docking: once the form's bar rises past the middle of the screen, the header bar flies up
		// out of it and stretches to full width along the header, and the action row spreads to
		// full width too; scrolling back down below the middle returns both into the column, and
		// scrolling past the form fades the header bar out. No motion under prefers-reduced-motion.
		function initDocking() {
			let docked = false;
			let ready = false;
			let frame = 0;

			function timing() {
				const style = getComputedStyle(form);
				const duration = parseFloat(style.getPropertyValue('--motion-duration-slow')) || 0;
				const easing = style.getPropertyValue('--motion-easing-standard').trim() || 'ease';
				return { duration: ready && !reducedMotion.matches ? duration : 0, easing };
			}

			// The transform that lays the full-width header bar over the form's bar.
			function overFormBar() {
				const from = bar.getBoundingClientRect();
				const to = headerBar.getBoundingClientRect();
				const shiftX = from.left + from.width / 2 - (to.left + to.width / 2);
				const shiftY = from.top - to.top;
				return `translate(${shiftX}px, ${shiftY}px) scaleX(${from.width / to.width})`;
			}

			// How far the form's column sits from the viewport edges, for the action row that spreads
			// to full width while docked (starter.css).
			function measureBleed() {
				const rect = form.getBoundingClientRect();
				form.style.setProperty('--bleed-start', `${rect.left}px`);
				form.style.setProperty('--bleed-end', `${document.documentElement.clientWidth - rect.right}px`);
			}

			function setDocked(next, pastForm) {
				docked = next;
				measureBleed();
				form.classList.toggle('form-steps--docked', next);
				headerBar.getAnimations().forEach((animation) => animation.cancel());
				const { duration, easing } = timing();
				if (next) {
					headerBar.hidden = false;
					handOver(bar, headerBar);
					bar.classList.add('wizard-progress--docked');
					if (duration) fly([{ transform: overFormBar() }, { transform: 'none' }], { duration, easing });
					return;
				}
				const done = () => {
					bar.classList.remove('wizard-progress--docked');
					handOver(headerBar, bar);
					headerBar.hidden = true;
				};
				if (!duration) {
					done();
					return;
				}
				const keyframes = pastForm
					? [{ opacity: 1 }, { opacity: 0 }]
					: [{ transform: 'none' }, { transform: overFormBar() }];
				fly(keyframes, { duration, easing }).finished.then(done, () => {});
			}

			// Focus on a segment moves to the same segment of the bar that takes over, rather than
			// dropping to the page when its bar hides.
			function handOver(from, to) {
				const index = [...from.querySelectorAll('.wizard-progress__segment')].indexOf(document.activeElement);
				if (index >= 0) to.querySelectorAll('.wizard-progress__segment')[index].focus({ preventScroll: true });
			}

			// The step names stay hidden while the bar stretches, so the text never squeezes.
			function fly(keyframes, options) {
				headerBar.classList.add('wizard-progress--flying');
				const animation = headerBar.animate(keyframes, options);
				// A cancelled flight settles after the next one has started; only the last one lands.
				const land = () => {
					if (!headerBar.getAnimations().length) headerBar.classList.remove('wizard-progress--flying');
				};
				animation.finished.then(land, land);
				return animation;
			}

			function update() {
				frame = 0;
				const edge = header.getBoundingClientRect().bottom;
				const middle = edge + (window.innerHeight - edge) / 2;
				const formBottom = form.getBoundingClientRect().bottom;
				const next = bar.getBoundingClientRect().top < middle && formBottom > edge;
				if (next !== docked) setDocked(next, formBottom <= edge);
				ready = true;
			}

			const schedule = () => {
				frame ||= requestAnimationFrame(update);
			};
			window.addEventListener('scroll', schedule, { passive: true });
			window.addEventListener('resize', () => {
				measureBleed();
				schedule();
			});
			update();
		}

		back.addEventListener('click', () => go(active - 1));
		next.addEventListener('click', () => {
			if (checkStep(steps[active])) go(active + 1);
		});
		form.classList.add('js__form-steps');
		render();
		if (headerBar) initDocking();
	}

	// ---------- Section scrolling (as on apple.com) ----------
	// Between the title and the step screen the page does not rest halfway: a scroll that
	// starts on the title side and goes down enters the step screen (it stops there even when
	// flung past it), and one that starts on the step screen and goes up returns to the top.
	// From the step screen down, and around the footer, scrolling is ordinary; coming back up
	// past the steps stops on the step screen. Only wheel, touch and scroll keys count, so the
	// scrollbar, find-in-page and the page's own scrolling are left alone.
	function initSectionScroll(form) {
		const SCROLL_KEYS = new Set(['PageDown', 'PageUp', 'ArrowDown', 'ArrowUp', ' ', 'Home', 'End']);
		let lastInput = -Infinity;
		let auto = false;
		let start = null;
		let lastY = window.scrollY;
		let timer = 0;

		const stepScreen = () => form.getBoundingClientRect().top + window.scrollY - (parseFloat(getComputedStyle(form).scrollMarginTop) || 0);
		const near = (a, b) => Math.abs(a - b) < 2;

		const markUser = () => {
			lastInput = performance.now();
		};
		window.addEventListener('wheel', markUser, { passive: true });
		window.addEventListener('touchmove', markUser, { passive: true });
		window.addEventListener('keydown', (event) => {
			if (SCROLL_KEYS.has(event.key) && !event.target.closest('input, textarea, select, [contenteditable]')) markUser();
		});

		function settle() {
			timer = 0;
			const from = start;
			start = null;
			if (auto) {
				auto = false;
				return;
			}
			if (performance.now() - lastInput > 1000) return;
			lastInput = -Infinity;
			const screen = stepScreen();
			const at = window.scrollY;
			let target = null;
			if (from < screen - 1) target = at > from ? screen : 0; // began on the title side
			else if (near(from, screen)) target = at < screen ? 0 : null; // began on the step screen
			else target = at < screen ? screen : null; // began below the steps
			if (target === null || near(at, target)) return;
			auto = true;
			window.scrollTo({ top: target, behavior: reducedMotion.matches ? 'auto' : 'smooth' });
			if (reducedMotion.matches) auto = false;
		}

		window.addEventListener(
			'scroll',
			() => {
				if (start === null) start = lastY;
				lastY = window.scrollY;
				window.clearTimeout(timer);
				timer = window.setTimeout(settle, 140);
			},
			{ passive: true },
		);
	}

	const icon = (name) =>
		`<svg class="icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false"><use href="ui-ds/icons.svg#${name}"></use></svg>`;

	// ---------- Checkbox with sub-options ----------
	// The parent names its sub-options in aria-controls and shows the mixed state while only some
	// of them are checked. The page's own script keeps `checked` in step both ways.
	function initCheckboxGroup(parent) {
		const options = parent
			.getAttribute('aria-controls')
			.split(/\s+/)
			.map((id) => document.getElementById(id))
			.filter(Boolean);
		const update = () => {
			const checked = options.filter((option) => option.checked).length;
			parent.indeterminate = checked > 0 && checked < options.length;
		};
		for (const input of [parent, ...options]) input.addEventListener('change', update);
		update();
	}

	// ---------- Step help tooltips ----------
	// starter.css shows the tooltip on hover and keyboard focus; here it is shifted to stay inside
	// the step's column, and Escape hides it until the pointer or focus leaves.
	// Each help is written in index.html as <span class="help" data-docs="anchor" data-label="Field">tooltip
	// text</span>; this builds its link to that anchor of the Payara Starter documentation (new tab)
	// and the tooltip. The documentation address lives only here.
	const docsUrl = 'https://docs.azul.com/payara-community/technical-documentation/ecosystem/project-management-tools/payara-starter.html';
	let helpCount = 0;

	function buildHelp(help) {
		const id = `help-${++helpCount}`;
		const text = help.textContent.trim();
		help.innerHTML =
			`<a class="help__link" target="_blank" rel="noopener" aria-describedby="${id}">${icon('help')}` +
			`<span class="visually-hidden"></span></a><span class="help__tooltip" role="tooltip" id="${id}"></span>`;
		help.querySelector('.help__link').href = `${docsUrl}#${help.dataset.docs}`;
		help.querySelector('.visually-hidden').textContent = `${help.dataset.label} documentation (opens in a new tab)`;
		help.querySelector('.help__tooltip').textContent = text;
	}

	function initHelp(help) {
		buildHelp(help);
		const tooltip = help.querySelector('.help__tooltip');
		const column = help.closest('.form-steps__step') ?? document.body;
		const place = () => {
			help.style.removeProperty('--help-shift');
			const box = tooltip.getBoundingClientRect();
			if (!box.width) return;
			const bounds = column.getBoundingClientRect();
			const shift = Math.max(bounds.left - box.left, 0) + Math.min(bounds.right - box.right, 0);
			help.style.setProperty('--help-shift', `${shift}px`);
		};
		const reset = () => help.classList.remove('help--dismissed');
		help.addEventListener('mouseenter', place);
		help.addEventListener('focusin', place);
		help.addEventListener('mouseleave', reset);
		help.addEventListener('focusout', reset);
	}

	// ---------- Phone menu ----------
	function initMenu(toggle) {
		const menu = document.getElementById(toggle.getAttribute('aria-controls'));
		if (!menu) return;
		const icon = toggle.querySelector('use');

		function setOpen(open) {
			toggle.setAttribute('aria-expanded', String(open));
			icon?.setAttribute('href', 'ui-ds/icons.svg#' + (open ? 'x' : 'menu-2'));
		}

		toggle.addEventListener('click', () => setOpen(toggle.getAttribute('aria-expanded') !== 'true'));
		document.addEventListener('keydown', (event) => {
			if (event.key === 'Escape' && toggle.getAttribute('aria-expanded') === 'true') {
				setOpen(false);
				toggle.focus();
			}
		});
		// Leaving the phone layout closes the menu, so it is never left open behind the bar.
		window.matchMedia('(width > 768px)').addEventListener('change', (event) => {
			if (event.matches) setOpen(false);
		});
	}

	function init() {
		// The header's height, for layouts that fill the screen under it (starter.css).
		const header = document.querySelector('.page__header');
		if (header) {
			const measure = () => document.documentElement.style.setProperty('--page-header-height', `${header.offsetHeight}px`);
			new ResizeObserver(measure).observe(header);
			measure();
		}

		const form = document.querySelector('.form-steps');
		if (form) initSteps(form);
		if (form?.classList.contains('js__form-steps')) initSectionScroll(form);

		document.querySelectorAll('input[type="checkbox"][aria-controls]').forEach(initCheckboxGroup);

		document.querySelectorAll('.help').forEach(initHelp);

		document.addEventListener('keydown', (event) => {
			if (event.key !== 'Escape') return;
			document.querySelectorAll('.help:is(:hover, :focus-within)').forEach((help) => help.classList.add('help--dismissed'));
		});

		const toggle = document.querySelector('.menu__toggle');
		if (toggle) initMenu(toggle);

		document.querySelector('.scroll-cue')?.addEventListener('click', (event) => {
			const target = document.getElementById(event.currentTarget.hash.slice(1));
			if (!target) return;
			event.preventDefault();
			target.scrollIntoView({ block: 'start', behavior: reducedMotion.matches ? 'auto' : 'smooth' });
		});

		document.getElementById('privacySettings')?.addEventListener('click', () => {
			const hsp = (window._hsp = window._hsp || []);
			hsp.push(['showBanner']);
		});
	}

	if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init, { once: true });
	else init();
})();

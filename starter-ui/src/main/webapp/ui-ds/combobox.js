/*
 * Search field with suggestions (one value), in the payara-ds SearchField and Combobox look.
 * Used by er-builder.js for the ER diagram pickers.
 *
 * Built over a native <select>, which stays the value holder for the page's own
 * scripts: its options are the suggestions, and picking one sets select.value and fires
 * `change`. The field looks like the payara-ds SearchField (search glyph, clear button);
 * suggestions open in a dropdown panel like the Combobox's. One value, shown as the field's
 * text: typing filters, Arrow keys move, Enter picks, Escape closes (or clears when closed).
 */
const icon = (name) =>
	`<svg class="icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false"><use href="ui-ds/icons.svg#${name}"></use></svg>`;
let comboboxCount = 0;

export function initCombobox(select) {
	const id = `combobox-${++comboboxCount}`;
	const label = select.id ? document.querySelector(`label[for="${select.id}"]`) : null;
	const placeholder = select.dataset.comboboxPlaceholder || 'Search...';
	const emptyText = select.dataset.comboboxEmpty || 'No results';

	const root = document.createElement('div');
	root.className = 'combobox';
	root.innerHTML = `
		<div class="combobox__field">
			${icon('search')}
			<input type="text" class="combobox__input" id="${id}-input" role="combobox"
				aria-autocomplete="list" aria-expanded="false" aria-controls="${id}-listbox"
				autocomplete="off" spellcheck="false">
			<button type="button" class="combobox__clear" hidden>${icon('x')}</button>
		</div>
		<div class="combobox__dropdown" hidden>
			<ul class="combobox__options" role="listbox" id="${id}-listbox"></ul>
		</div>
		<span class="visually-hidden" aria-live="polite"></span>`;
	select.after(root);
	select.hidden = true;

	const input = root.querySelector('.combobox__input');
	const clear = root.querySelector('.combobox__clear');
	const dropdown = root.querySelector('.combobox__dropdown');
	const list = root.querySelector('.combobox__options');
	const status = root.querySelector('[aria-live]');
	input.placeholder = placeholder;

	if (label) {
		label.htmlFor = input.id;
		label.id ||= `${id}-label`;
		list.setAttribute('aria-labelledby', label.id);
		clear.setAttribute('aria-label', `Clear ${label.textContent.trim()}`);
	}

	let open = false;
	let typing = false;
	let filtered = [];
	let highlight = -1;

	const choices = () =>
		[...select.options].filter((option) => option.value !== '').map((option) => ({ value: option.value, label: option.textContent.trim() }));
	const current = () => choices().find((choice) => choice.value === select.value);

	// The field shows the chosen label, except while the user is typing a search.
	function renderValue() {
		if (!typing) input.value = current()?.label ?? '';
		input.disabled = select.disabled;
		clear.hidden = select.disabled || input.value === '';
		root.classList.toggle('combobox--disabled', select.disabled);
		if (select.disabled && open) close();
	}

	function renderList() {
		const query = typing ? input.value.trim().toLowerCase() : '';
		filtered = choices().filter((choice) => choice.label.toLowerCase().includes(query));
		list.replaceChildren(
			...filtered.map((choice, index) => {
				const option = document.createElement('li');
				option.className = 'combobox__option';
				option.id = `${id}-option-${index}`;
				option.setAttribute('role', 'option');
				option.setAttribute('aria-selected', String(choice.value === select.value));
				const text = document.createElement('span');
				text.textContent = choice.label;
				option.append(text);
				if (choice.value === select.value) option.insertAdjacentHTML('beforeend', icon('check'));
				option.addEventListener('mousedown', (event) => event.preventDefault());
				option.addEventListener('click', () => choose(choice.value));
				option.addEventListener('mousemove', () => setHighlight(index));
				return option;
			}),
		);
		if (!filtered.length) {
			const empty = document.createElement('li');
			empty.className = 'combobox__empty';
			empty.setAttribute('role', 'presentation');
			empty.textContent = emptyText;
			list.append(empty);
		}
		status.textContent = query ? `${filtered.length} ${filtered.length === 1 ? 'result' : 'results'}` : '';
	}

	function setHighlight(index) {
		highlight = index;
		list.querySelectorAll('.combobox__option').forEach((option, i) => option.toggleAttribute('data-highlighted', i === index));
		const active = list.children[index];
		if (index >= 0 && active) {
			input.setAttribute('aria-activedescendant', active.id);
			active.scrollIntoView({ block: 'nearest' });
		} else {
			input.removeAttribute('aria-activedescendant');
		}
	}

	function openList() {
		if (open || select.disabled) return;
		open = true;
		root.classList.add('combobox--open');
		dropdown.hidden = false;
		input.setAttribute('aria-expanded', 'true');
		renderList();
		setHighlight(filtered.findIndex((choice) => choice.value === select.value));
	}

	function close() {
		if (!open) return;
		open = false;
		root.classList.remove('combobox--open');
		dropdown.hidden = true;
		input.setAttribute('aria-expanded', 'false');
		input.removeAttribute('aria-activedescendant');
		highlight = -1;
		status.textContent = '';
	}

	function choose(value) {
		typing = false;
		if (select.value !== value) {
			select.value = value;
			select.dispatchEvent(new Event('change', { bubbles: true }));
		}
		close();
		renderValue();
	}

	// Leaving the field: an exact name picks it, an emptied field clears, anything else
	// falls back to the chosen value.
	function settle() {
		if (typing) {
			const text = input.value.trim().toLowerCase();
			const match = choices().find((choice) => choice.label.toLowerCase() === text);
			if (text === '') choose('');
			else if (match) choose(match.value);
			else {
				typing = false;
				renderValue();
			}
		}
		close();
	}

	input.addEventListener('focus', () => {
		input.select();
		openList();
	});
	input.addEventListener('click', openList);
	input.addEventListener('input', () => {
		typing = true;
		clear.hidden = input.value === '';
		openList();
		renderList();
		setHighlight(filtered.length ? 0 : -1);
	});
	input.addEventListener('keydown', (event) => {
		switch (event.key) {
			case 'ArrowDown':
				event.preventDefault();
				if (!open) openList();
				else setHighlight(Math.min(highlight + 1, filtered.length - 1));
				break;
			case 'ArrowUp':
				event.preventDefault();
				if (open) setHighlight(Math.max(highlight - 1, 0));
				break;
			case 'Enter':
				// Never let Enter submit the form from here.
				event.preventDefault();
				if (open && highlight >= 0 && filtered[highlight]) choose(filtered[highlight].value);
				else openList();
				break;
			case 'Escape':
				if (open) {
					event.preventDefault();
					typing = false;
					renderValue();
					close();
				} else if (select.value !== '') {
					choose('');
				}
				break;
			case 'Tab':
				settle();
				break;
		}
	});

	root.querySelector('.combobox__field').addEventListener('mousedown', (event) => {
		if (select.disabled || event.target === input || event.target.closest('.combobox__clear')) return;
		event.preventDefault();
		input.focus();
	});
	clear.addEventListener('mousedown', (event) => event.preventDefault());
	clear.addEventListener('click', () => {
		choose('');
		input.focus();
	});
	root.addEventListener('focusout', (event) => {
		if (!root.contains(event.relatedTarget)) settle();
	});

	// Other scripts change the select directly (options added, value set, disabled toggled).
	select.addEventListener('change', renderValue);
	new MutationObserver(() => {
		renderValue();
		if (open) renderList();
	}).observe(select, { childList: true, subtree: true, attributes: true, attributeFilter: ['disabled'] });
	renderValue();
	return { refresh: renderValue };
}

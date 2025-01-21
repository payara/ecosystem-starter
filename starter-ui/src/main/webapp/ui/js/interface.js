/*! Payara Pattern Library version: 0.72.1 */
/*! DO NOT MODIFY THIS FILE, CHANGES WILL BE OVERWRITTEN! */

// Always set a top level class to indicate if we have JS available.

document.getElementsByTagName("html")[0].className = document.getElementsByTagName("html")[0].className.replace(/no-js/,'').trim() + ' js';



function dispatch_resize_end() {
	var ev;
	try {
		ev = new Event('resize-end');
	} catch (e) {
		ev = document.createEvent('CustomEvent');
		ev.initCustomEvent('resize-end', true, true, {});
	}
	window.dispatchEvent(ev);
}

var create_resize_end_event = function() {
/*
	Creates an event to use instead of just using resize which is a massive resource hog.
	Just dump this once into some code: 'create_resize_end_event();'.
	Then use thusly: 
	window.addEventListener('resize-end', function (e) {
		console.log('resized!');
	});
*/
	var resize_end_timeout;
	window.addEventListener('resize', function () {
		clearTimeout(resize_end_timeout);
		resize_end_timeout = setTimeout(function () {
			dispatch_resize_end();
		}, 100);
	});
};

create_resize_end_event();



var blinky_addIcon = function(icon, t) {

	if (!icon) return false;

	// Create the SVG wrapper for the button icon.
	const i = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
	i.setAttribute('class', 'icon');
	i.setAttribute('width', '1em');
	i.setAttribute('height', '1em');
	i.setAttribute('viewbox', '0 0 16 16');
	i.setAttribute('aria-hidden', 'true');
	i.setAttribute('focusable', 'false');

	// Add the icon to the SVG.
	if (typeof icon === 'string') {
		const i_use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
		i_use.setAttributeNS('http://www.w3.org/1999/xlink', 'href', ui_path({el: t})+'/ui/images/icons.svg#icon-'+icon);
		i.append(i_use);
	} else {
		for (const [k, v] of icon.entries()) {
			const k = document.createElementNS('http://www.w3.org/2000/svg', 'use');
			k.setAttributeNS('http://www.w3.org/1999/xlink', 'href', ui_path({el: t})+'/ui/images/icons.svg#icon-'+v.icon);
			k.setAttribute('class', v.css);
			i.append(k);
		}
	}

	return i;
};
const Blinky_cookie = function() {

	// A constructor for getting and setting cookies.

	this.get_eTLD1 = (suffixes) => {
		const hostname = document.location.hostname;

		// // Check against the specified suffixes.
		for (let suffix of suffixes) {
			if (/^[a-z.]*$/.test(suffix)) {

				// Add a dot to the front of the suffix if one hasn't been sent.
				if (suffix.substring(0,1) !== '.') suffix= '.'+suffix;

				// Chop the suffix off the hostname, if the reulting array
				// contains more than a single item we found an allowed suffix.
				const suffix_end = new RegExp(suffix+"$");
				const hostbits = hostname.split(suffix_end);
				if (hostbits.length > 1) {
					// Send back the string from the last occurance . to the end of the string, plus the suffix.
					// e.g. billing.payara.cloud or even bill.ing.payara.cloud will return .payara.cloud
					return hostbits[0].substr(hostbits[0].lastIndexOf('.'), hostbits[0].length) + suffix;
				}
			}
		}

		// Nothing matched.
		return false;
	};


	this.set = (args) => {
		// Required args: name.
		// Optional args: val, expiry_days, path, domain, hostname.

		// Check name and val only contain allowed characters, and that they exist.
		if (!args.name || !/^[A-Za-z0-9-_]*$/.test(args.name) ||
			!/^[A-Za-z0-9-_!#$&()\[\]*+.:<>?@^`{}|~]*$/.test(args.val)) return false;

		let cookie = args.name+'=';
		if (args.val) cookie+= args.val;

		const expiry = new Date();

		// Check expiry_days is greater than 0.
		if (!isNaN(args.expiry_days) && args.expiry_days > 0) {
			expiry.setTime(expiry.getTime() + (args.expiry_days*24*60*60*1000));
			cookie+= ';expires='+expiry.toUTCString();

		// If expiry is less then 0 we take this as an intent to delete the cookie so set the expiry 1
		// day behind the current time(ish). Not dealing with BST here so could be 23-24 hours behind.
		} else if (!isNaN(args.expiry_days) && args.expiry_days < 0) {
			expiry.setTime(expiry.getTime() - 86400000);
			cookie+= ';expires='+expiry.toUTCString();
		}
		// If the expiry value was 0 the we set no expiry value and assume a session cookie.

		// Add the path if supplied and only contains allowed characters.
		cookie+= ';path=';
		cookie+= (args.path && /^[A-Za-z0-9-_\/]*$/.test(args.path)) ? args.path : '/';

		// For the sake of simplicity we're running on the theory that if there's
		// a properly formatted domain set then set Samesite and Secure as a
		// matter of course. We could break these out as separate args but for
		// the moment ¯\_(ツ)_/¯
		if (args.hostname) {
			// If anything was sent in the hostname parameter we will always
			// try and grab the eTLD+1 and ignore any domain parameter.

			// If we got the hostname as a string change it to an array because we can check for multiple suffixes.
			let suffixes = (typeof args.hostname == 'string') ? new Array(args.hostname) : args.hostname;

			const domain_from_hostname = this.get_eTLD1(suffixes);
			if (domain_from_hostname) {
				cookie+= ';Domain='+domain_from_hostname+';SameSite=None;Secure';
			} else {
				cookie+= ';SameSite=Lax';
			}

		} else if (args.domain && /^[A-Za-z0-9-.]*$/.test(args.domain)) {
			// Use the domain sent, this makes no judgement as to the correctness
			// of the domain compared to the actual domain in use so if they don't
			// match the cookie will fail to be set.
			cookie+= ';Domain='+args.domain+';SameSite=None;Secure';
		} else {
			cookie+= ';SameSite=Lax';
		}

		// Write the cookie.
		document.cookie = cookie;
	};


	this.get = (args) => {
		// Required args: name or a single string that will be interpretted as name.

		// Is there anything that might be a cookie name and are there actually any cookies?
		if (!args || !document.cookie) return false;

		// Set the name to either the arg.name object or if sommeone
		// shorthanded it did they sent the cookie name as a string?
		const name = args.name || args;

		// Split the cookie string, then look for a cookie that starts with the name=.
		let cookie = document.cookie
			.split('; ')
			.find((c) => c.startsWith(name+'='));

		if(!cookie) return false;
		return cookie.split('=')[1];
	};
};



const Blinky_wrap_content = function(args) {
	// Take an element and wrap its contents in another element, turns out this is painful to do in vanilla JS because we want to make sure to move both children HTML elements and any text nodes. Since we can't grab text nodes with querySelector we have to rely on childNodes, this is promlematic as it's a live set of data. Directly moving the child elements messes with the live data so we have to move them out into a temporary array and then reattach them into the new wrapper.

	let els = [];
	for (let c of args.el.childNodes) {
		els.push(c);
	}
	for (let e of els) {
		args.wrapper.append(e);
	}

	// If there's a separate target to wrap the contents into...
	let target = (args.target) ? args.target : args.el;
	target.append(args.wrapper);
};



// Scroll bars

class BlinkyScrollHints extends HTMLElement {
	
	constructor() {
		super();
	}
	
	
	__scroll_pos_checker(e) {
		// The styles for the shadows are set using data values, this is so 
		// the mutation observer we use later on can ignore the shadow changes.
		window.requestAnimationFrame(function() {
			
			// If the scroll position is at all scrolled from the top, show the top shadow.
			e.dataset.shadow_top = (e.scrollTop > 0) ? '1' : '0';
			
			// Deduct the clientHeight and scrollTop from the scrollHeight. ScrollHeight 
			// is non-rounded whilst the others are so we check here that the bottom of 
			// the scroll is somewhere near 0.
			e.dataset.shadow_bottom = (Math.abs(e.scrollHeight - e.clientHeight - e.scrollTop) >= 1) ? '1' : '0';
		});
	}
	
	
	__scrolling(e) {
		// We're only doing this to standardise what's sent to the next function, 
		// otherwise we could just call this directly from the eventlistener.
		this.__scroll_pos_checker(e.target);
	}
	
	
	connectedCallback() {
		// Setup the component.
		
		// Shadow CSS hangs off this.
		this.classList.add('js__scroll');
		
		// Because we can't add two elements as :before and :after (only :before works 
		// in conjunction with sticky) we need something to apply one of the shadows to.
		const shadow = document.createElement('span');
		shadow.classList.add('js__shadow');
		this.prepend(shadow);
		
		const debounce = new WeakMap();
		
		this.resize_observer = new ResizeObserver((entries) => {
			const entry = entries[0];
			
			// Remove the old timeout, we'll be adding another below.
			clearTimeout( debounce.get( entry.target ) );
			
			// To minimise layout thrashing we need to throttle running code too quickly in 
			// sucsession so add a timeout, set to 150 ms as that's generally quick enough 
			// not to look janky.
			debounce.set( entry.target, setTimeout(() => {
				
				// Remove the current eventlistener since we'll be rechecking if we still need 
				// one next and adding it back if we do.
				this.removeEventListener('scroll', this.__scrolling);
				
				// Compare the total height of the content with the current height of its 
				// containing box, if the total height is larger than the space available the 
				// content will be scrollable so we add the eventlistener.
				if (this.scrollHeight >= entry.contentBoxSize[0].blockSize) {
					this.addEventListener('scroll', this.__scrolling);
					this.__scroll_pos_checker(this);
				}
			}, 150) );
			
		});
		this.resize_observer.observe(this);
		
		// Observe pretty much everything, we're adding the shadows to the scrollable 
		// element via some data attributes so they aren't detected in this because we're 
		// only checking class changes.
		const observables = { 
			attributes: true, 
			attributeFilter: ['class'],
			childList: true, 
			subtree: true 
		};
		
		// Callback function runs when any changes specified in the observables is detected.
		const mutation_cb = (mutations, mut_observer) => {
			for (const mutation of mutations) {
				this.__scroll_pos_checker(this);
			}
		};
		
		// Create instance and link to the callback.
		this.mut_observer = new MutationObserver(mutation_cb);
		
		// Start the mutation observer.
		this.mut_observer.observe(this, observables);
		
		// Run on initial setup just to set any shadows on first load.
		this.__scroll_pos_checker(this);
	}
	
	
	disconnectedCallback() {
		this.removeEventListener('scroll', this.__scrolling);
		this.resize_observer.disconnect();
		this.mut_observer.disconnect();
	}
}

customElements.define('blinky-scroll-hints', BlinkyScrollHints);



var grid_to_column = (function() {

	// Forces a CSS grid to shift to a single column stack in one go rather than have something like the following:
	// 
	// Wide screen:
	// | item1 | item2 | item3 |
	// 
	// Narrower screen:
	// | item1 | item2 |
	// |     item3     |
	// 
	// This script avoids the above layout, the layout would go straight to:
	// | item1 |
	// | item2 |
	// | item3 |
	
	var __init = function(el) {
		
		// Check for the items to check and the class to toggle.
		if (!el || !el.selector || !el.toggleClass) return false;
		
		// Check the there are some items to be found with selector.
		var els = document.querySelectorAll(el.selector);
		if (els.length === 0) return;
		el.items = els;
		
		for (var i=0; i<el.items.length; i++) {
			__action({item: el.items[i], class: el.toggleClass});
		}
		
	};
	
	// Slow down the ResizeObserver a bit.
	var __debounce = function(fn, ms) {
		var timer = 0;
		return function(bits) {
			var args = Array.prototype.slice.call(bits);
			args.unshift(this);
			clearTimeout(timer);
			timer = setTimeout(fn.bind.apply(fn, args), ms);
		};
	};
	
	var __action = function(el) {
		var looky = new ResizeObserver(__debounce(function(entries) {
				
			// Always start by resetting the layout.
			entries.target.classList.remove(el.class);

			// Compare the main element offsetTop with that of the last child offsetTop, 
			// if there's been no wrapping they should be the same.
			if (entries.target.offsetTop < entries.target.lastElementChild.offsetTop) {

				// The offsetTops were not the same so add the adjustment class.
				entries.target.classList.add(el.class);
			}
		}, 50));
		looky.observe(el.item);
		
	};
	
	return {
		init: __init
	};
	
})();




var ui_path = function(bits) {

	// Default settings.
	var defaults = {
		el: document.getElementsByTagName('html')[0],
		tree: false
	};

	// If we were passed any vars...
	// If an element possibly containing data-ui-path was attached...
	if (typeof bits !== 'undefined') {

		// Element to check for data-ui-path.
		if ('el' in bits) {

			// If we got sent a CSS class or ID then bail out.
			if (typeof bits.el === 'string') return '';

			defaults.el = bits.el;

			// We only want the option to check the entire tree from el outwards if we were sent el, otherwise assume no tree.
			if ('tree' in bits) {
				defaults.tree = bits.tree;
			}
		}
	}

	bits = defaults;

	// This is where we store the elements to check for data-ui-path;
	var els = [];

	// If we need to search from the element outwards...
	if (bits.tree) {
		parents = bits.el;
		while(parents.parentNode) {
			if (parents.parentNode.tagName) {
				els.push(parents.parentNode);
			}
			parents = parents.parentNode;
		}
	} else {
		els.push(document.getElementsByTagName('html')[0]);
	}

	// Add the original element to the elements to check.
	if (bits.el) els.unshift(bits.el);

	var ui_path = '';

	// Loop through the elements and look for data-ui-path, we return the first one found and the search starts at the innermost element.
	for(var i=0; i<els.length; i++) {
		if (els[i].hasAttribute('data-ui-path')) {
			ui_path = els[i].getAttribute('data-ui-path');
			break;
		}
	}
	
	return ui_path;
};



// Main menu mobile wrapping.

var menu_wrap = (function() {

	var event_listener = false;

	var __init = function(el) {
		el = (el) ? el : '.js__main-menu';
		var main_menu = document.querySelectorAll(el);
		if (main_menu.length === 0) return;
		for (var i=0; i<main_menu.length; i++) {
			__setup(main_menu[i]);
		}
	};

	var __setup = function(el) {
		if (!el.tagName) el = document.querySelector(el);

		window.addEventListener('load', function () {
			__action(el);
		});
		if (!event_listener) {
			window.addEventListener('resize-end', function (e) {
				__action(el);
			});
			event_listener = true;
		}
	};

	var __action = function(el) {

		// Check there are at least two items to compare.
		if (!el.children[el.children.length-2]) return false;

		el.classList.remove('js__main-menu--wrapped');

		// Compare the left offset of the last and previous to last items, 
		// If the last item has an offset equal to or less than the previous one apply the mobile class.
		if (el.children[el.children.length-1].offsetLeft <= el.children[el.children.length-2].offsetLeft) {
			el.classList.add('js__main-menu--wrapped');
		}
	};
	
	return {
		init: __init,
		setup: __setup,
		action: __action
	};
	
})();



class BlinkySidebar extends HTMLElement {
	
	static get observedAttributes() {
		// Watched component attributes.
		return ['minimized'];
	}
	

	constructor() {
		super();
	}


	__wrap_contents() {
		// Take an element and wrap its contents in another element, turns out this is painful to do in vanilla JS because we want to make sure to move both children HTML elements and any text nodes. Since we can't grab text nodes with querySelector we have to rely on childNodes, this is promlematic as it's a live set of data. Directly moving the child elements messes with the live data so we have to move them out into a temporary array and then reattach them into the new wrapper.

		// Create a wrapper to wrap all the content of the shoutbox.
		let wrapper = document.createElement('aside');
		wrapper.setAttribute('class', 'sidebar');

		let els = [];
		for (let c of this.childNodes) {
			els.push(c);
		}
		for (let e of els) {
			wrapper.append(e);
		}
		this.appendChild(wrapper);
	}


	__add_toggle() {
		// Is there a .sidebar__header? Get it or create and attach it.
		let sidebar_header = this.querySelector('.sidebar__header');
		if (!sidebar_header) {
			sidebar_header = document.createElement('header');
			sidebar_header.setAttribute('class', 'sidebar__header');
		}
		this.querySelector('.sidebar').prepend(sidebar_header);

		// Create button.
		const button = document.createElement('button');
		button.setAttribute('type', 'button');
		button.setAttribute('class', 'button sidebar__toggle');

		// Button icon.
		const icon_svg = blinky_addIcon('sidebar', this);
		button.append(icon_svg);

		// Button label.
		let label = this.getAttribute('toggle-label') || 'Toggle Sidebar';
		const button_label = document.createElement('span');
		button_label.innerText = label;
		button_label.setAttribute('class', 'visually-hidden');
		button.append(button_label);

		// Set the initial value of the aria-expanded based on the minimized attribute on load/creation.
		button.setAttribute('aria-expanded', !Number(this.getAttribute('minimized')));

		sidebar_header.prepend(button);
		button.addEventListener('click', this.__do_toggle.bind(this));
	}


	__do_toggle() {
		// Update the minimized attribute, we only need to update this single value as the rest is handled by watching for changes on this attribute.
		this.setAttribute('minimized', Number(!JSON.parse(this.getAttribute('minimized'))));
	}


	__set_mimimized_state() {

		// Update the aria on the toggle button.
		this.querySelector('.sidebar__toggle').setAttribute('aria-expanded', !JSON.parse(this.getAttribute('minimized')));

		// Update persistent storage.
		this.cookie.set({
			name: this.id,
			val: this.getAttribute('minimized'),
			expiry_days: 365,
			hostname: ['.fish', '.cloud']
		});
	}


	__is_large_screen() {
		const cssmq = window.matchMedia('(min-width: 60rem)');
		let thing = this;
		function test_cssmq(e) {
			if (!e.matches) {

				// Force minimize the sidebar on window resize to small width'
				thing.setAttribute('minimized', '1');
			}
		}
		cssmq.addEventListener('change', test_cssmq);
		return cssmq.matches;
	}


	__get_mimimized_state() {

		// If we're a small screen we need to show the sidebar minimized.
		// Using a media query, it's a little janky but it does work.
		// The code below handles forcing minimized on load, the code in __is_large_screen() deals 
		// with the transision of resizing the window and minimizing there. I did say it was janky.

		if (this.__is_large_screen()) {

			// Check for any stored previous setting in browser localStorage, 
			// everything runs off the minimized attribute so we only need this one line.
			if (this.cookie.get(this.id)) {
				this.setAttribute('minimized', this.cookie.get(this.id));
			}
		} else {
			this.setAttribute('minimized', 1);

			// I'd have rather this got picked up in the attributeChangedCallback() 
			// but I think it's happening before the guard can pass.
			this.cookie.set({
				name: this.id,
				val: this.getAttribute('minimized'),
				expiry_days: 365,
				hostname: '.fish'
			});
		}
	}


	__set_sidebar_height() {

		// If we detected a change to the height of the .page--sticky-header .page__header 
		// we need to adjust the CSS for the height of 100% - header height.

		let sidebar = this.querySelector('.sidebar');
		if (sidebar) {

			let header_height = 0;
			try {
				header_height = document.querySelector('.page--sticky-header .page__header').offsetHeight;
			} catch(e) {}
			
			sidebar.setAttribute('style', 'top: '+header_height+'px; max-height: calc(100vh - '+header_height+'px);');
		}
	}


	__sidebar_height() {
		if (!document.querySelector('.page--sticky-header .page__header')) return false;

		const debounce = new WeakMap();
		let old_height = 0;
		this.resize_observer = new ResizeObserver((entries) => {
			const entry = entries[0];
			clearTimeout( debounce.get( entry.target ) );
			debounce.set( entry.target, setTimeout(() => {
				if (entry.contentBoxSize[0].blockSize !== old_height) {
					this.__set_sidebar_height();
				}
				old_height = entry.contentBoxSize[0].blockSize;
			}, 150) );
		});
		this.resize_observer.observe(document.querySelector('.page--sticky-header .page__header'));
	}
	

	connectedCallback() {
		if (!this.classList.contains('js__sidebar')) {

			// Use the Blinky cookie utility constructor.
			this.cookie = new Blinky_cookie();

			// Wrap the entire contents of the sidebar.
			this.__wrap_contents();
			
			// Because we store the minimized state in a cookie and
			// it needs a name we need to make sure there's an ID set.
			if (this.getAttribute('toggle') && this.id) {
				this.__get_mimimized_state();
				this.__add_toggle();
			}
			
			// This is only run if the page has a layout using sticky header only.
			this.__sidebar_height();
		}
		
		// We use this to guard actions in attributeChangedCallback() but 
		// also to apply any CSS that may only be needed on the JSed element
		this.classList.add('js__sidebar');
	}
	

	attributeChangedCallback(att, old_v, new_v) {
		
		// Because this method fires purely on the element being inited we 
		// guard the actions by checking to make sure it's not the first run.
		// Also, only run if the attribute was actually updated.
		if ((!this.classList.contains('js__sidebar')) || (old_v === new_v)) return false;
		
		// If the value of attribute minimized is updated, either by using the WC 
		// toggle button or an outside action (button elsewhere on the page) update 
		// the aria values and the localStorage.
		if (att === 'minimized') {
			this.__set_mimimized_state();
		}
	}

}

customElements.define('blinky-sidebar', BlinkySidebar);



var tab_group = (function() {

	var __key_focus_tab = function(e) {
		// Left key.
		if (e.keyCode == 37) {
			if (this.previousElementSibling) {
				this.previousElementSibling.focus();
			} else {
				this.parentElement.lastElementChild.focus();
			}
		}
		// Right key.
		if (e.keyCode == 39) {
			if (this.nextElementSibling) {
				this.nextElementSibling.focus();
			} else {
				this.parentElement.firstElementChild.focus();
			}
		}
	};

	var __switch_tab = function() {

		var tabs = this.closest('.tab-group').querySelectorAll('.tab-group__tab');
		var content = this.closest('.tab-group').querySelectorAll('.tab-group__content');

		// Unset any active tabs.
		for (i=0; i<tabs.length; i++) {
			tabs[i].setAttribute('aria-selected', 'false');
			tabs[i].setAttribute('tabindex', '-1');
			content[i].classList.remove('tab-group__content--active');
		}

		// Set the new active tab.
		this.setAttribute('aria-selected', 'true');
		this.removeAttribute('tabindex');
		this.closest('.tab-group').querySelector('[aria-labelledby="'+this.getAttribute('id')+'"]').classList.add('tab-group__content--active');
	};

	var __create_tab_group = function(el) {

		var tab_group_id = Date.now()+'-'+el.tab_num;
		var tabs = el.tab_group.querySelectorAll('.tab-group__tab');
		var content = el.tab_group.querySelectorAll('.tab-group__content');
		var show_tab = (el.tab_group.hasAttribute('data-show-tab') ? el.tab_group.getAttribute('data-show-tab') : 0);
		if (show_tab >= tabs.length) show_tab = 0;

		el.tab_group.querySelector('.tab-group__tabs').setAttribute('role','tablist');

		for (x=0; x<tabs.length; x++) {

			// Add the a11y attributes, if we have too many tabs vs content hide any extra, we don't need to do this for content as it's set to hidden by default.
			if (content[x] === undefined) {
				tabs[x].setAttribute('hidden', 'hidden');
			} else {
				tabs[x].setAttribute('role', 'tab');
				tabs[x].setAttribute('aria-controls', 'tab-'+tab_group_id+'-'+x+'-content');
				tabs[x].setAttribute('id', 'tab-'+tab_group_id+'-'+x+'-label');

				content[x].setAttribute('role', 'tabpanel');
				content[x].setAttribute('id', 'tab-'+tab_group_id+'-'+x+'-content');
				content[x].setAttribute('aria-labelledby', 'tab-'+tab_group_id+'-'+x+'-label');

				// Open a tab by default, if there's a data-show-tab attribute we use that otherwise it's set to the first tab.
				if (x === parseInt(show_tab)) {
					tabs[x].setAttribute('aria-selected', 'true');
					tabs[x].removeAttribute('tabindex');
					content[x].classList.add('tab-group__content--active');
				} else {
					tabs[x].setAttribute('aria-selected', 'false');
					tabs[x].setAttribute('tabindex', '-1');
					content[x].classList.remove('tab-group__content--active');
				}

				tabs[x].addEventListener('click', __switch_tab);
				tabs[x].addEventListener('keydown', __key_focus_tab);
			}
		}
	};

	var __init = function() {
		var tab_groups = document.querySelectorAll('.tab-group');

		for (i=0; i<tab_groups.length; i++) {
			__create_tab_group({tab_group: tab_groups[i], tab_num: i});
		}
	};

	return {
		init: __init
	};

})();



var accordion = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	
	// Finds all accordions and kickstarts the setup process.
	var __init = function(el) {
		
		el = (el) ? el : '.accordion';
		var accordions = document.querySelectorAll(el);
		
		if (accordions.length === 0) return;
		
		for (var i=0; i<accordions.length; i++) {
			var label = (accordions[i].getAttribute('data-details-btn')) ? accordions[i].getAttribute('data-details-btn') : '';
			__setup({el: accordions[i], icon: ui_path({el: accordions[i]}), label: label});
		}
	};

	
	var __setup = function(acc) {

		var items = acc.el.querySelectorAll('.accordion__content');

		if (items.length === 0) return;
		
		var c = 1;
		for (var i=0; i<items.length; i++) {
			if (items[i].parentElement.querySelector('.accordion__toggle') == null) {
				items[i].insertAdjacentHTML('beforebegin', '<button class="accordion__toggle button button--clear" aria-expanded="false" aria-controls="acc'+c+'" type="button"><svg class="icon icon--nav" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true"><use href="'+acc.icon+'/ui/images/icons.svg#icon-menu-arrow"></use></svg><span class="visually-hidden">'+acc.label+'</span></button>');
			} else {
				items[i].parentElement.querySelector('.accordion__toggle').setAttribute('aria-controls', 'acc'+c);
			}
			items[i].setAttribute('id', 'acc'+c);
			items[i].setAttribute('aria-hidden', 'true');
			c++;
		}
		
		// Add the event listener only once.
		if (!event_listener) {
			document.addEventListener('click', __action);
			// We only ever want to add one event listener.
			event_listener = true;
		}
	};

	
	var __action = function(e) {
		if (!e.target.closest('.accordion__toggle')) return false;
		
		toggle = e.target.getAttribute('aria-expanded') === 'true';
		e.target.setAttribute('aria-expanded', String(!toggle));

		content = e.target.parentElement.querySelector('.accordion__content').getAttribute('aria-hidden') === 'true';
		e.target.parentElement.querySelector('.accordion__content').setAttribute('aria-hidden', String(!content));
	};
	
	
	
	return {
		init: __init,
		setup: __setup
	};
	
})();
class BlinkyCopybox extends HTMLElement {
	
	constructor() {
		super();
	}

	__create_button() {

		let label = this.getAttribute('button-label') || 'Copy';

		const button = document.createElement('button');
		button.setAttribute('type', 'button');
		button.setAttribute('class', 'button');

		// icon-and-text (default), icon, text.
		let button_type = this.getAttribute('button-type') || 'icon-and-text';

		if (button_type !== 'text') {
			const icon_svg = blinky_addIcon('copy', this);
			button.append(icon_svg);
		}

		let button_class = 'button__text';
		if (button_type == 'icon') {
			button_class = 'visually-hidden';
		}

		// Button label.
		const button_label = document.createElement('span');
		button_label.innerText = label;
		button_label.setAttribute('class', button_class);
		button.append(button_label);

		return button;
	}

	__upgrade_text() {
		const text = this.firstElementChild;
		text.setAttribute('tabIndex', '0');
		text.classList.add('copy-box__content', 'scroll');
	}
	__copy() {
		const text = this.firstElementChild;

		// We can't directly select something that's not an input but being an input wouldn't be great for accessibility, so we create a temporary visually hidden textarea, add the content to copy, copy that into the clipboard then nuke the temporary textarea.
		const ta = document.createElement('textarea');
		ta.setAttribute('style', 'position: absolute; left: 9999px;');
		ta.textContent = text.textContent;
		this.append(ta);
		ta.select();

		// CSS animation for text content.
		text.classList.add('copy-box__content--copied');
		setTimeout(function() {
			text.classList.remove('copy-box__content--copied');
		}, 500);

		document.execCommand("copy");

		ta.remove();

		let icon = this.querySelector('use');
		let icon_href = icon.getAttribute('href').split('-');
		icon.setAttribute('href', icon_href[0] + '-' + 'tick');
		setTimeout(function() {
			icon.setAttribute('href', icon_href[0] + '-' + icon_href[1]);
		}, 1000);
	}
	
	connectedCallback() {
		// Setup the component.
		this.__upgrade_text();
		const button = this.__create_button();
		this.append(button);
		button.addEventListener('click', this.__copy.bind(this));
	}
}

customElements.define('blinky-copybox', BlinkyCopybox);



// Notification toggle.

var notification_toggle = (function() {
	
	var event_listener = false;
	
	// Finds all instances of the default .notification__button.
	var __init = function(el) {
		el = (el) ? el : '.notification__button';
		var notification_button = document.querySelectorAll(el);
		if (notification_button.length === 0) return;
		for (var i=0; i<notification_button.length; i++) {
			__setup(notification_button[i]);
		}
	};
	
	var __setup = function(el) {
		if (!el.tagName) el = document.querySelector(el);

		el.classList.add('js__notification__button');
		if (!el.hasAttribute('aria-haspopup')) {
			el.setAttribute('aria-haspopup', 'true');
		}
		if (!el.hasAttribute('aria-expanded')) {
			el.setAttribute('aria-expanded', 'false');
		}
		
		if (!event_listener) {
			document.addEventListener('mousedown', __action);
			event_listener = true;
		}
	};
	
	
	var __action = function(e) {

		if (e.target.closest('.js__notification__button')) {

			// Close any other open notication panels.
			e.target.closest('.js__notification__button').classList.add('temp');
			var others = document.querySelectorAll('.js__notification__button:not(.temp)');
			if (others) {
				for (var i=0; i<others.length; i++) {
					others[i].setAttribute('aria-expanded', 'false');
				}
			}
			e.target.closest('.js__notification__button').classList.remove('temp');

			e.target.closest('[aria-haspopup]').setAttribute('aria-expanded', 
				(e.target.closest('[aria-haspopup]').getAttribute('aria-expanded') == 'true') ? 'false' : 'true'
			);
			return true;


		} else {
			if (!e.target.closest('.notifications--popup .notifications__panel')) {
				if (document.querySelector('.js__notification__button[aria-expanded=true]')) {
					document.querySelector('.js__notification__button[aria-expanded=true]').setAttribute('aria-expanded', 'false');
				}
			}
		}
	};
	
	
	
	return {
		init: __init,
		setup: __setup
	};
	
})();



// Closeable Card Custom Element

class BlinkyShoutbox extends HTMLElement {
	static get observedAttributes() {
		return ['b-close'];
	}

	constructor() {
		super();
	}

	__create_button(bits) {

		// Creates a button, passes it back so we can attach it to the DOM and add the eventListener.

		// Create button and attach it.
		const button = document.createElement('button');
		button.setAttribute('class', 'button '+bits.class);
		button.setAttribute('type', 'button');

		if (bits.icon) {
			// Create the SVG wrapper for the button icon.
			const icon = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
			icon.setAttribute('class', 'icon');
			icon.setAttribute('width', '1em');
			icon.setAttribute('height', '1em');
			icon.setAttribute('viewbox', '0 0 16 16');
			icon.setAttribute('aria-hidden', 'true');
			icon.setAttribute('focusable', 'false');
			button.append(icon);

			// Add the icon to the SVG.
			const icon_use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
			icon_use.setAttributeNS('http://www.w3.org/1999/xlink', 'href', ui_path({el: this})+'/ui/images/icons.svg#icon-'+bits.icon);
			icon.append(icon_use);
		}

		// Button label.
		const button_label = document.createElement('span');
		button_label.setAttribute('class', (bits.type == 'icon') ? 'visually-hidden' : 'button__text');
		button_label.innerText = bits.label;
		button.append(button_label);

		return button;
	}

	__close_card() {
		// this.classList.add('shoutbox--closing');
		// This sets the animation speed and waits for the speed before removing the element.
		let speed = this.__set_speed();
		this.querySelector('.shoutbox__outer').setAttribute('style', '--transision-duration: '+speed+'ms');
		this.classList.add('shoutbox--closing');
		setTimeout(() => {
			if (this.querySelector('.shoutbox__undo')) {
				this.__show_undo();
			} else {
				this.__remove_card();
			}
		}, speed);
	}

	__show_undo() {
		this.querySelector('.shoutbox__undo').classList.add('shoutbox__undo--show1');
		// Needs a small delay because CSS can't animate display states, so we add the class containing the display block above, wait a tiny amount and then we can animate the opacity.
		setTimeout(() => {
			this.querySelector('.shoutbox__undo').classList.add('shoutbox__undo--show2');
		}, 1);
	}

	__remove_card() {
		// Can be called to immediately remove the element.
		// When the cookie is tested on page load is when this needs to be run quickly without the animation.
		this.remove();
	}

	__reopen_shoutbox() {
		this.classList.remove('shoutbox--closing');
		this.querySelector('.shoutbox__undo').classList.remove('shoutbox__undo--show2');
		this.querySelector('.shoutbox__undo').classList.remove('shoutbox__undo--show1');
		// Remove the cookie.
		this.cookieObj.set({
			name: this.getAttribute('b-cookie'),
			expiry_days: -1,
			path: '/',
			hostname: ['.fish', '.cloud']
		});
	}

	__wrap_contents(wrapper) {
		// Take an element and wrap its contents in another element, turns out this is painful to do in vanilla JS because we want to make sure to move both children HTML elements and any text nodes. Since we can't grab text nodes with querySelector we have to rely on childNodes, this is promlematic as it's a live set of data. Directly moving the child elements messes with the live data so we have to move them out into a temporary array and then reattach them into the new wrapper.
		let els = [];
		for (let c of this.childNodes) {
			els.push(c);
		}
		for (let e of els) {
			wrapper.append(e);
		}
	}

	__set_speed() {
		// We want a consistent transition speed but this depends on the amount of content we need to close so measure the height of the shoutout and double it, then if it falls outside of the range of 100-300 push it back into range.
		let speed = Math.floor(this.offsetHeight * 1.5);
		if (speed < 100) {
			speed = 100;
		} else if (speed > 300) {
			speed = 300;
		}
		return speed;
	}

	connectedCallback() {

		// This sets up the custom element bits and pieces, since we can have connectedCallback or attributeChangedCallback put a guard around the code because we're also callign this function from attributeChangedCallback to sest things up ASAP.
		if (!this.classList.contains('js__shoutbox')) {

			// If there's no cookie name value attached to the card then kill off any shoutbox custom element because if we definitely can't save the close state it's more trouble to keep it.
			if (!this.getAttribute('b-cookie') || this.getAttribute('b-cookie') == '') {
				this.classList.add('shoutbox--disabled');
				return false;
			}
			
			// Initiate the cookie object.
			this.cookieObj = new Blinky_cookie();

			// Get the cookie.
			let cookie = this.cookieObj.get({
				name: this.getAttribute('b-cookie')
			});

			// If the cookie has a value of 'closed' the we get rid of the shoutbox ASAP, no animation.
			if (cookie === 'closed') this.__remove_card();

			// If we got here then we didn't remove the shoutbox so now set it up visually.
			// As we're animating the closing of the shoutbox we need for the outer element to not have any padding as this causes jankiness when animating the height, it doesn't count and causes a jump.
			// Create a wrapper to wrap all the content of the shoutbox.
			let wrapper = document.createElement('div');
			wrapper.setAttribute('class', 'shoutbox__inner');

			// Send the wrapper over to the wrapping function (because it turns out this isn't a one-liner).
			this.__wrap_contents(wrapper);

			let outer_wrapper = document.createElement('div');
			outer_wrapper.setAttribute('class', 'shoutbox__outer');
			this.appendChild(outer_wrapper);

			// Now the wrapper contains the child elements from the shoutbox we need to attach it to the shoutbox.
			outer_wrapper.appendChild(wrapper);

			// Add the close button.
			let close_button = this.__create_button({
				type: 'icon',
				icon: 'cross',
				class: 'button--clear shoutbox__close layout-hidden',
				label: 'Close'
			});
			this.querySelector('.shoutbox__inner').prepend(close_button);

			// Click handler.
			close_button.addEventListener('click', () => {
				this.__close_card();
				this.cookieObj.set({
					name: this.getAttribute('b-cookie'),
					val: 'closed',
					expiry_days: this.getAttribute('b-expiry') || 0,
					path: '/',
					hostname: '.fish'
				});
			});

			// Check for any in-content cookie setting.
			let close_in_content = this.querySelectorAll('.shoutbox__close-in-content');
			if (close_in_content) {
				for (let el of close_in_content) {
					el.addEventListener('click', () => {
						this.__close_card();
						this.cookieObj.set({
							name: this.getAttribute('b-cookie'),
							val: 'closed',
							expiry_days: this.getAttribute('b-expiry') || 0,
							path: '/',
							hostname: '.fish'
						});
					});
				}
			}

			// If we want the show an undo button...
			if (this.getAttribute('b-undo')) {
				let undo_button = this.__create_button({
					type: 'text',
					class: 'button--text shoutbox__undo',
					label: this.getAttribute('b-undo')
				});
				this.append(undo_button);
				undo_button.addEventListener('click', () => {
					this.__reopen_shoutbox();
				});
			}
		}

		// All the interactiviy is targeted via this class so we completely separate any non-JS CSS.
		this.classList.add('js__shoutbox');
	}
	
	disconnectedCallback() {}

	attributeChangedCallback(att, oldValue, newValue) {
		
		// Force connectedCallback() to run before doing anything with the attribute values.
		if (!this.classList.contains('js__shoutbox')) {
			this.connectedCallback();
		}

		// If we see the b-close attribute set to 1, close the shoutbox.
		if ((att === 'b-close') && (newValue === '1')) {
			this.__close_card(this, this.speed);
		}
	}
}

customElements.define('blinky-shoutbox', BlinkyShoutbox);



// Charts JS

var chart_colors = {
	tango: 'rgb(240, 152, 27)',
	midnight: 'rgb(0, 44, 62)',
	smurf: 'rgb(5, 152, 214)',
	basalt: 'rgb(67, 68, 69)',
	concrete: 'rgb(218, 224, 226)',
	coal: 'rgb(23, 24, 24)'
};



class BlinkyToggleButton extends HTMLElement {

	static get observedAttributes() {
		// Watched component attributes.
		return ['toggled'];
	}

	constructor() {
		super();
	}

	__toggle_clicked() {
		this.setAttribute('toggled', Number(!JSON.parse(this.getAttribute('toggled'))));
	}

	__equalise_label_width() {
		// Under normal circumstances a button takes its width from the length of the text label, because we have two text labels the button can suffer width changes when it's clicked. This quickly sets the label to each state's text, grabs the width, then we take the highest value, convert it to rem and add it to the button as a min-width style.
		let label_widths = [];
		this.querySelector('.toggle__text').innerHTML = this.getAttribute('text-0');
		label_widths.push(this.querySelector('.toggle__text').offsetWidth);
		this.querySelector('.toggle__text').innerHTML = this.getAttribute('text-1');
		label_widths.push(this.querySelector('.toggle__text').offsetWidth);
		if (Math.max(...label_widths) > 1) {
			this.querySelector('.toggle__text').setAttribute('style', 'min-width: '+(Math.max(...label_widths)/10)+'rem');
		}
	}

	__build_toggle() {

		if (!this.getAttribute('toggled')) this.setAttribute('toggled', '0');

		// Create the actual toggle button.
		let btn = document.createElement('button');

		// This is where we store the button attributes.
		let btn_atts = {
			id: '',
			class: ''
		};

		// Get the attributes from the WC.
		if (this.getAttribute('button-class')) {
			btn_atts.class = this.getAttribute('button-class');
		}
		if (this.getAttribute('button-id')) {
			btn_atts.id = this.getAttribute('button-id');
		}

		// If there's an A fallback specified we can check it's attributes, if there are no WC attributes we get a second go at setting them from this A tag.
		if (this.querySelector('a') && this.querySelector('a').tagName === 'A') {
			let a = this.querySelector('a');
			if (!btn_atts.class && a.getAttribute('class')) {
				btn_atts.class = a.getAttribute('class');
			}
			if (!btn_atts.id && a.id) {
				btn_atts.id = a.id;
			}
			// Remove the fallback.
			a.remove();
		}

		// Add the class and ID attributes to the new button.
		if (btn_atts.id) btn.id = btn_atts.id;
		btn.setAttribute('class', btn_atts.class+' button--toggle');

		if (!btn.classList.contains('button')) {
			btn.classList.add('button');
		}

		// This stops the toggle from submitting if it's inside a form.
		btn.setAttribute('type', 'button');

		this.append(btn);

		const text = document.createElement('span');
		text.classList.add('toggle__text');

		// Check of we have text labels, if we don't then add some.
		if (!this.getAttribute('text-0')) this.setAttribute('text-0', 'Off');
		if (!this.getAttribute('text-1')) this.setAttribute('text-1', 'On');

		if (btn.classList.contains('button--small')) {

			// Small buttons have their contents wrapped.
			const span = document.createElement('span');
			span.classList.add('button__text');
			Blinky_wrap_content({
				el: btn,
				wrapper: span
			});

			// Because we know we're dealing with a small button change our 'shortcut' to the button content wrapper, everything we add after this point needs to go inside that.
			btn = span;
		} else {
			text.classList.add('button__text');
		}

		if (this.getAttribute('type') === 'icon-only') {
			text.classList.add('visually-hidden');
		}

		Blinky_wrap_content({
			el: btn,
			wrapper: text
		});

		// Under normal circumstances a button takes its width from the length of the text label, because we have two text labels the button can suffer width changes when it's clicked.
		this.__equalise_label_width();

		// Add the icons, there should be two, if there isn't we force some.
		const icon0 = this.getAttribute('icon-0') || 'dot_outline';
		const icon1 = this.getAttribute('icon-1') || 'dot';
		const icon_svg = blinky_addIcon([{icon: icon0, css: 'toggle__0'},{icon: icon1, css: 'toggle__1'}], this);
		btn.prepend(icon_svg);
	}

	__update_toggle() {
		// Get the toggled value or force to the off state.
		const toggled = this.getAttribute('toggled') || '0';
		this.querySelector('.toggle__text').innerHTML = this.getAttribute('text-'+toggled);
	}

	connectedCallback() {
		// Setup the component.
		this.__build_toggle();

		// We use this to guard actions in attributeChangedCallback() but
		// also to apply any CSS that may only be needed on the JSed element
		this.classList.add('js__button--toggle');

		// Do an initial grabbing of the toggle state and set.
		// This is also called on clicking the toggle.
		this.__update_toggle();

		this.querySelector('.button').addEventListener('click', () => {this.__toggle_clicked();});
	}

	disconnectedCallback() {}

	attributeChangedCallback(att, old_v, new_v) {

		// Because this method fires purely on the element being inited we
		// guard the actions by checking to make sure it's not the first run.
		// Also, only run if the attribute was actually updated.
		if ((!this.classList.contains('js__button--toggle')) || (old_v === new_v)) return false;

		// Changes to watched attributes.
		if (att === 'toggled') {
			this.__update_toggle();
		}
	}
}

customElements.define('blinky-toggle-button', BlinkyToggleButton);



var menu = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	
	// Finds all menus and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default .menu, note this is only to init stuff, 
		// menus still need to use the .menu class after this point.
		el = (el) ? el : '.menu';
		var menus = document.querySelectorAll(el);
		
		if (menus.length === 0) return;
		
		for (var i=0; i<menus.length; i++) {
			__menu(menus[i]);
		}
	};




	// Set up single menu element
	function __menu(element) {

		// Get the menu type.
		var menu_type = (element.getAttribute('data-menu-type')) ? element.getAttribute('data-menu-type') : 'full';

		__setup({el: element, icon: ui_path({el: element}), type: menu_type});

		// Add the event listener only once.
		if (!event_listener) {
			document.addEventListener('click', __action);
			// We only ever want to add one event listener.
			event_listener = true;
		}
	}
	
	
	
	// Setup each menu instance.
	var __setup = function(menu) {
		
		// Horizontal menus can only use the toggle behaviour, change any behaviours on these to toggle.
		if (menu.el.classList.contains('menu--horizontal')) {
			menu.el.setAttribute('data-menu-behaviour', 'toggle');
		}
		
		// Create the CSS selector for grabbing all sub menu groups.
		// There's no :has equivalent so grab the child, later we'll use parentNode.
		var menu_items_selector = '';
		
		// Add any instances of the main menu toggle on dropdowns.
		if (menu.el.contains(menu.el.querySelector('.menu__main-toggle'))) {
			menu_items_selector = '.menu__main-toggle + .menu__group';
		}
		
		// If it isn't a nested list of links, add the sub menu groups, adding a CSS separator if needed.
		if (menu.el.getAttribute('data-menu-behaviour') !== 'open') {
			if (menu_items_selector !== '') menu_items_selector += ', ';
			menu_items_selector += '.menu__item > .menu__group';
		}
		
		// Grab the menu groups.
		var menu_items;
		if (menu_items_selector !== '') {
			menu_items = menu.el.querySelectorAll(menu_items_selector);
		}
		if (!menu_items || menu_items.length === 0) return;
		
		// If we found any menu groups now setup each one.
		for (var i=0; i<menu_items.length; i++) {
			__setup_submenus({el: menu_items[i], icon: menu.icon, type: menu.type});
		}
		menu.el.classList.add('js__menu');
	};
	
	
	
	// Setup each menu group instance.
	var __setup_submenus = function(menu_item) {
		
		// Cache the parent toggle rather than keep refering to it directly: .menu__item > a
		var toggle = menu_item.el.parentElement.querySelector('*:first-child');
		
		// We only add the icon and replicate the toggle item on things that are not the main button toggle.
		if (menu_item.el.parentNode.classList.contains('menu__item')) {
			
			// Duplicate the item toggle link.
			if (menu_item.type !== 'split') __replicate_toggle_item(toggle);
			
			// Add the submenu icons.
			var icon_code = '<svg class="icon icon--nav" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true"><use href="'+menu_item.icon+'/ui/images/icons.svg#icon-menu-arrow"></use></svg>';
			if (menu_item.type !== 'split') {
				toggle.insertAdjacentHTML('beforeend', icon_code);
			} else {
				toggle.insertAdjacentHTML('afterend', '<button class="button menu__split-button">'+icon_code+'<span class="visually-hidden">Toggle Submenu</span></button>');
			}
			
		
		// This must be a main button toggle beacuse there's no .menu__item in the parentNode.
		// Add a CSS class to make it easier to find these later on.
		} else {
			menu_item.el.parentNode.classList.add('menu--dropdown');
		}
		
		// Aria attributes serve two purposes: 1 - a11y. 2 - they control the interaction.
		// This is either a split menu or fulle link menu.
		// Split menus we need to use the nexct item along which is typically a toggle button.
		var add_aria = (menu_item.type === 'split' && toggle.tagName === 'A') ? toggle.nextSibling : toggle;
		add_aria.setAttribute('aria-haspopup', 'true');
		add_aria.setAttribute('aria-expanded', (toggle.classList.contains('menu--expanded')) ? 'true' : 'false');
	};
	
	
	
	// Because we're retasking the parent link of a sub menu group we lose access to it as a link.
	// Clone this link and add it as the first item in it's own sub group.
	var __replicate_toggle_item = function(toggle) {
		
		// Get the label content, if there's a specific data-label on this menu item use that otherwise look for a menu-wide one.
		var label = '';
		if (toggle.hasAttribute('data-clone-label')) {
			label = toggle.getAttribute('data-clone-label');
		} else if (toggle.closest('.menu').hasAttribute('data-clone-label')) {
			label = toggle.closest('.menu').getAttribute('data-clone-label');
		}
		
		if (label !== '') {
			
			// Clone the A and its content, clone the LI without the content.
			var copy_a = toggle.cloneNode(true);
			var copy_li = toggle.parentNode.cloneNode(false);

			// This is to remove the attribute if we wanted a menu open on load.
			if (copy_a.hasAttribute('aria-expanded')) copy_a.removeAttribute('aria-expanded');
			if (copy_a.hasAttribute('aria-current')) copy_a.removeAttribute('aria-current');
			copy_a.classList.remove('menu__current-parent');

			copy_a.classList.add('menu--clone');

			// This fixes an issue where line breaks in the code (not actual visual ones mind) would get turned into BRs and mess up the clone.
			var cloned_text = copy_a.querySelector('.menu__text').innerText;
			cloned_text = cloned_text.replace(/\r?\n|\r/g, '');

			// It won't make much sense to simply duplicate the link text, so we can add some wrapper text.
			// If we have "All ." the text will become "All <duplicated text>".
			// Add the text from data-label, place the current label where the . is.
			if (label.indexOf('.') > -1) {
				copy_a.querySelector('.menu__text').innerText = label.replace(/\./, cloned_text);
			} else {
				copy_a.querySelector('.menu__text').innerText = label;
			}

			copy_li.appendChild(copy_a);

			toggle.parentNode.querySelector('.menu__group > *:first-child').insertAdjacentElement('beforebegin', copy_li);
		}
	};
	
	
	
	// Called by the listener.
	var __action = function(e) {
		
		// We only ever want to add one event listener.
		event_listener = true;

		// The menu_close is for a bug where because the menus are display flex it makes them a block level element, 
		// this means they can be clicked on accidentally and then the action to close the dropdowns doesn't happen.
		var menu_close = e.target;
		if (e.target.closest('.menu') && !e.target.closest('[aria-haspopup]')) {
			if (e.target.classList.contains('menu')) {
				menu_close = e.target.parentNode;
			} else {
				return false;
			}
		}
		
		// Don't follow the href link, this will only kick in if the user followed a link rather than a button.
		if (e.target.closest('.menu__content[aria-haspopup]')) {
			e.preventDefault();
		}
		
		// Close any open menu items, this list depends on what was clicked.
		var other_menus = __close_menu_items_list(menu_close);
		if (other_menus.length !== 0) {
			for (var i=0; i<other_menus.length; i++) {
				other_menus[i].setAttribute('aria-expanded', 'false');
				other_menus[i].classList.remove('menu__group--boundary');
				
				if (other_menus[i].closest('.menu__item')) {
					other_menus[i].closest('.menu__item').querySelector('.menu__content').classList.remove('menu--expanded');
				}
			}
		}
		
		// If the user clicked on a toggle then update the aria-expanded.
		if (e.target.closest('.menu [aria-haspopup]')) {
			e.target.closest('[aria-haspopup]').setAttribute('aria-expanded', 
				(e.target.closest('[aria-haspopup]').getAttribute('aria-expanded') == 'true') ? 'false' : 'true'
			);
			
			// Toggle the class on the link, this is only for styling and doesn't drive the functionality.
			if (e.target.closest('.menu__item')) {
				e.target.closest('.menu__item').querySelector('.menu__content').classList.toggle('menu--expanded');
			}
			
			e.target.closest('[aria-haspopup]').classList.remove('menu__group--boundary');
			
			// Compare the right edge of the menu group to open with the right edge of the menu container, 
			// if the menu is going to overhang we shift it left.
			if (e.target.closest('[aria-haspopup]').nextElementSibling.getBoundingClientRect().right > e.target.closest('.menu').parentNode.getBoundingClientRect().right) {
				e.target.closest('[aria-haspopup]').classList.add('menu__group--boundary');
			}
		}
	};
	
	
	
	var __close_menu_items_list = function(clicked) {
		
		// If the user clicked on something that isn't a link or a toggle we don't want to close the menu.
		if (clicked.classList.contains('menu__item')) return false;
		
		var other_menus = '';
		
		// We set some scopes if the user clicked on a menu and not any other element on the page.
		if (clicked.closest('.menu')) {
			clicked.closest('.menu').setAttribute('menu-current', '');

			if (clicked.closest('.menu').getAttribute('data-menu-behaviour') == 'toggle') {
				if (clicked.closest('.menu__group') !== null) {
					clicked.closest('.menu__group').setAttribute('menu-scope', '');
				}
				if (clicked.closest('[aria-haspopup]') !== null) {
					clicked.closest('[aria-haspopup]').setAttribute('menu-ignore', '');
				}
			}
		}
		
		// Selector for closing other menus...
		// 1 - Get all dropdowns but not current menu.
		// 2 - All horizontals but not current.
		// 3 - All verticals from the current level downwards but not current.
		var css_query = '.menu--dropdown:not([menu-current]) [aria-expanded="true"], ';
		css_query+= '.menu--horizontal:not([menu-current]) [aria-expanded="true"], ';
		css_query+= '[menu-scope] [aria-expanded="true"]:not([menu-ignore])';
		
		other_menus = document.querySelectorAll(css_query);
		
		// Remove all the scoping stuff, we don't need to bother with a load of 'if' statements as 
		// they were added using that, if they exist we want rid of these temporary attributes.
		try {
			clicked.closest('.menu').removeAttribute('menu-current');
			clicked.closest('.menu__group').removeAttribute('menu-scope');
			clicked.closest('[aria-haspopup]').removeAttribute('menu-ignore');
		}
		catch(e) {}
		
		return other_menus;
	};
	
	
	
	return {
		init: __init,
		menu: __menu
	};
	
})();
var form_sections = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	// Finds all switch toggles and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default class.
		el = (el) ? el : '.form--sections';
		var form_section = document.querySelector(el);

		if (!form_section) return;
		
		__setup(form_section);
	};
	
	
	var __setup = function(el) {

		// Because we're setting an aria-current in unpoly we need to check for instances of aria-current="page" and remove them, there shouldn't be any instances of this inside a form section nav anyway.
		var forms = el.querySelectorAll('[aria-current]');
		if (forms.length !== 0) {
			for (var i=0; i<forms.length; i++) {
				if (forms[i].getAttribute('aria-current') == 'page') forms[i].removeAttribute('aria-current');
			}
		}

		if (!event_listener) {
			document.addEventListener('click', __action);
			event_listener = true;
		}
	};
	
	
	// Called by the listener.
	var __action = function(e) {

		// Ignore anything that isn't a click on a link in the section menu or a fieldset.
		if (!e.target.closest('.form__section-list a, .form--sections fieldset')) return false;
		
		var clicky = '';
		try {
			clicky = e.target.closest('.form__section-list a').hash;
			clicky = clicky.substring(1);
		} catch (error) {
			clicky = e.target.closest('.form--sections fieldset').id;
		}
		var current_form = e.target.closest('.form--sections');
		
		// Remove previous current markers.
		// Put this in try/catch because if this is the first click no markers will cause an error.
		try {
			current_form.querySelector('[aria-current]').removeAttribute('aria-current');
			current_form.querySelector('.fieldset--active').classList.remove('fieldset--active');
		} catch (error) {}
		
		// Add new markers, as long as the Back to Top button wasn't clicked.
		if (!e.target.closest('.form--sections__menu-anchor')) {
			current_form.querySelector('a[href="#'+clicky+'"]').setAttribute('aria-current', 'step');
			document.getElementById(clicky).classList.add('fieldset--active');
		}
	};
	
	
	return {
		init: __init,
		setup: __setup
	};
})();



var step_through = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	

	
	// Finds all step-throughs and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default .form--step-through, note this is only to init stuff, menus still need to use the .menu class after this point.
		el = (el) ? el : '.form--step-through';
		var step_throughs = document.querySelectorAll(el);
		
		if (step_throughs.length === 0) return;
		
		for (var i=0; i<step_throughs.length; i++) {
			__step_through(step_throughs[i]);
		}
	};



	// Set up
	var __step_through = function(el) {

		var steps = el.querySelectorAll('.form__step');
		if (steps === 0) return;

		// Add an ID to each fieldset, this is so the user can "bookmark" where they are. If there's no "next button" (probably because it's the last fieldset we create a generic hash.)
		var active_added = false;
		var step_hash = '';
		for (var i=0; i<steps.length; i++) {

			// Add the button label from the previous iteration as the id of the current step.
			if (step_hash) {
				steps[i].setAttribute('id', step_hash);

				// If the current hash matches the browser URL we mark this iteraction as the active step.
				if ('#'+step_hash == window.location.hash) {
					if (steps[i].classList.contains('form__step')) {
						steps[i].classList.add('form__step--active');

						// Make sure we don't set step 1 as the default active step.
						active_added = true;
					}
				}
			}

			// Grab the current button label.
			var step_title = steps[i].querySelector('.form__step__button');
			if (step_title) {
				// We use this in the next iteration of the loop to set the anchor destination.
				step_hash = __anchor(step_title);
				steps[i].querySelector('.form__step__button').setAttribute('href', '#'+step_hash);
			}
		}

		// If we found no active step via the loop then set the first step as active.
		if (!active_added) {
			el.querySelector('.form__step').classList.add('form__step--active');
		}

		// The last item doesn't have a left border, there's no native CSS way to target so we add a class whilst we're here.
		steps[steps.length-1].classList.add('form__step--last');

		// Add the main class, we do it this way so we don't get a flash of HTML before the JS has had a chance to set things up.

		// Add the event listener only once.
		if (!event_listener) {
			document.addEventListener('click', __action);
			// We only ever want to add one event listener.
			event_listener = true;
		}


		el.classList.add('form--step-through-css');
	};



	var __anchor = function(step_title) {
		step_hash = step_title.textContent.replace(/\s+/g, '_');
		step_hash = step_hash.replace(/\W/g, '');
		return step_hash;
	};
	
	
	
	// Called by the listener.
	var __action = function(e) {
		if (e.target.closest('.form__step__button')) {

			var invalid_fields = e.target.closest('.form__step').querySelector('[aria-invalid=true]');

			var error_message = e.target.parentNode.querySelector('.form__step__noproceedmsg');

			// If there are any fields that have aria-invalid=true.
			if (invalid_fields) {

				// We don't want the native HTML anchor to fire.
				e.preventDefault();
				
				// The error message is set to visible when the role=alert is attached.
				// We use role=alert so the browser announces the error if assistive tech is being used.
				error_message.setAttribute('role', 'alert');
				error_message.querySelector('span').innerHTML = e.target.closest('.form--step-through').getAttribute('data-no-advance');

			// Fields all look good, go to the next step.
			} else {
				try {
					error_message.removeAttribute('role');
					error_message.querySelector('span').innerHTML = '';
				} catch(err){}

				e.target.closest('.form--step-through').querySelector('.form__step--active').classList.add('form__step--completed');
				e.target.closest('.form--step-through').querySelector('.form__step--active').classList.remove('form__step--active');
				e.target.closest('.form--step-through').querySelector(e.target.getAttribute('href')).classList.add('form__step--active');
			}
		}
	};


	
	return {
		init: __init,
		step_through: __step_through
	};

})();
var form_steps = (function() {
	
	// The value that ensures we only ever add one event listener.
	let event_listener = false;
	let counter = -1;
	

	
	// Finds all step-throughs and kickstarts the setup process.
	let __init = (el) => {
		
		// If nothing was sent across then use the default .form--step-through, 
		// note this is only to init stuff, menus still need to use the .menu class after this point.
		el = (el) ? el : '.form-steps';
		let form_steps_els = document.querySelectorAll(el);
		
		if (form_steps_els.length === 0) return;
		
		// Loop through each form-step component.
		for (let form_step_el of form_steps_els) {
			__form_steps(form_step_el);
		}
	};



	let __link_legends = (step) => {
		alink = document.createElement('a');
		alink.setAttribute('href', step.href);
		let legends = step.step.querySelectorAll('.form-steps__title');
		if (!legends) return false;
		alink.innerHTML = legends[0].innerHTML;
		for (let l of legends) {
			// Clear the old content as we've moved it to the link.
			l.textContent = '';
			// Clone the created button because we want to use it twice.
			l.appendChild(alink.cloneNode(true));
		}
	};



	let __add_connections = (bits) => {

		// Get the step through ID, we use this to build the IDs for the steps.
		let stb_id = bits.step.closest('.form-steps').getAttribute('data-id');
		
		// IDs can't be just a number to set a prefix to use.
		let prefix = 'st';
		if (stb_id) prefix = stb_id+'-'+prefix;

		// Add an ID to the step, this is based on the index of the step itself.
		bits.step.setAttribute('id',prefix+bits.i);

		// If we're running the option to hide previous fields 
		// check for if we want the legends to be clickable too.
		if (bits.step.closest('.form-steps--hide-previous-fields.form-steps--clickable-legends')) {
			__link_legends({step: bits.step, href: '#'+prefix+bits.i});
		}

		// Set the next and previous href value to +1 and -1 of the ID from above.
		let next = bits.i+1;
		let previous = bits.i-1;

		// If the previous value is less than 0 then we must be dealing with the first step.
		if (previous >= 0) {
			let previous_btn = bits.step.querySelector('.form__step__button-back');
			if (previous_btn) previous_btn.setAttribute('href','#'+prefix+previous);
		}
		// If the next value is higher than the total number of steps then we're dealing with the last step.
		if (next < bits.length) {
			let next_btn = bits.step.querySelector('.form__step__button');
			if (next_btn) next_btn.setAttribute('href','#'+prefix+next);
		}
	};



	let __set_step_states = (steps) => {
		// Loops through the form steps and sets the complete and incomplete styles, 
		// these are used for displaying the correct icon and border type.

		// The CSS classes for complete and incomplete.
		let css = ['form-steps__step--complete','form-steps__step--incomplete'];

		// Loop through the steps, check to see if the current step is the active one, 
		// if it isn't then by default we assume the step must be above the active step and so 
		// it must be complete. Once we find the active step update the found_active pointer so 
		// that we apply the incomplete class to steps after the active one.
		let found_active = 0;
		for (let step of steps) {
			step.classList.remove('form-steps__step--complete', 'form-steps__step--incomplete');
			if (step.classList.contains('form-steps__step--active')) {
				found_active = 1;
			} else {
				step.classList.add(css[found_active]);
			}
		}
	};



	// Set up
	let __form_steps = (el) => {

		// Give each form-step a unique identifier, this is used when we generate the connections between the steps.
		counter++;
		el.setAttribute('data-id','stb'+counter);
		el.classList.add('js__form-steps');

		// Find all sections of component.
		let steps = el.querySelectorAll('.form-steps__step');
		if (steps === 0) return;

		// Loop through each component section.
		for (const [i, step] of steps.entries()) {
			// Add an ID, next and previous links to the step.
			__add_connections({step,i,'length': steps.length});
		}

		// If there's a hash in the URL.
		// And the hash can be used to find an HTML element by its ID.
		// Otherwise find the first step in the current step through component.
		let active_item;
		if (window.location.hash && document.querySelector(window.location.hash)) {
			active_item = document.querySelector(window.location.hash);
		} else {
			active_item = document.querySelector('[data-id="stb'+counter+'"] .form-steps__step:first-child');
		}
		active_item.classList.add('form-steps__step--active');

		// Set the complete / incomplete CSS classes on steps.
		__set_step_states(steps);

		// The last item doesn't have a left border, there's no native CSS way to target so we add a class whilst we're here.
		steps[steps.length-1].classList.add('form-steps__step--last');

		// Add the main class, we do it this way so we don't get a flash of HTML before the JS has had a chance to set things up.

		// Add the event listener only once.
		if (!event_listener) {
			document.addEventListener('click', __action);
			event_listener = true;
		}
	};



	let __focus_field = (f) => {
		// Grab the first input that isn't a hidden field,
		// Wait the length of time the fieldset animation is set to (300ms) and focus this field.
		// Because our focus styles are set on focus-visible we need to 
		// trick the browser into applying focus-visible withcontentEditable.
		let first_field = f.querySelector('input:not([type=hidden]), textarea, select, button');
		if (first_field) {
			setTimeout(() => {
				first_field.contentEditable = true;
				first_field.focus();
				first_field.contentEditable = false;
			}, 300);
		}
	};
	
	
	
	// Called by the listener.
	let __action = (e) => {

		// If either a next or previous button was clicked...
		if (e.target.closest('.form__step__button') || e.target.closest('.form__step__button-back') || e.target.closest('.form-steps__title a')) {

			// Check for any fields in the current step that are marked as aria-invalid.
			let invalid_fields = e.target.closest('.form-steps__step').querySelector('[aria-invalid=true]');
			
			// Get the link to the error message element, do it here because we need it in both sides of the below if statement.
			let error_message = e.target.parentNode.querySelector('.form-steps__error');

			// If any aria-invalid fields were found...
			if (invalid_fields) {

				// We don't want the native HTML anchor to fire.
				e.preventDefault();
				
				// The error message is set to visible when the role=alert is attached.
				// We use role=alert so the browser announces the error if assistive tech is being used.
				error_message.setAttribute('role', 'alert');
				error_message.querySelector('span').innerHTML = e.target.closest('.form-steps').getAttribute('data-step-error');

			// Fields all look good, go to the next step.
			} else {
				try {
					error_message.removeAttribute('role');
					error_message.querySelector('span').innerHTML = '';
				} catch(err){}

				// Move the "active" class to the new step.
				e.target.closest('.form-steps').querySelector('.form-steps__step--active').classList.remove('form-steps__step--active');
				e.target.closest('.form-steps').querySelector(e.target.getAttribute('href')).classList.add('form-steps__step--active');

				// Because the form fieldsets that aren't active can be collapsed the browser is having difficulty
				// keeping track of the vertical location, so we force a scroll to the new fieldset after the
				// animation to hide / show the fields is done.
				const next = e.target.closest('.form-steps').querySelector(e.target.getAttribute('href'));
				setTimeout(function() {
					next.scrollIntoView();
				}, 300);

				// Set the first field that isn't a hidden field to focus.
				// Because we're not setting up a specific keyup eventListener we just check the x/y coords of the interaction, 
				// if they're 0 then assume it's a keypress.
				if ((e.x+e.y) === 0) {
					__focus_field(e.target.closest('.form-steps').querySelector(e.target.getAttribute('href')));
				}

				// Reset the steps that are complete and incomplete.
				let steps = e.target.closest('.form-steps').querySelectorAll('.form-steps__step');
				__set_step_states(steps);
			}
		}
	};


	
	return {
		init: __init,
		form_steps: __form_steps
	};

})();
var help_toggle = (function() {

	/*
		This can either be run to grab all of the .form__help-toggle elements by 
		default a group of elements, or in a more specific per element way.

		Find all default: help_toggle.init();
		Find all: help_toggle.init('.CSS-class-goes-here');
		Target specific element: help_toggle.toggle(el);

		el in toggle can either be an actual element or an ID.
	*/

	var __init = function(els) {

		// Find all instances of help toggles and loop through them. If nothing is passed across then we search for the default .form__help-toggle
		var help_toggles;
		if (els) {
			help_toggles = document.querySelectorAll(els);
		} else {
			help_toggles = document.querySelectorAll('.form__help-toggle');
		}

		for (i=0; i<help_toggles.length; i++) {
			__toggle(help_toggles[i]);
		}
	};

	var __toggle = function(el) {
		if (el) {

			// Check if we've been passed a CSS ID or class rather than an element and grab it as an element.
			if (!el.tagName) el = document.querySelector(el);
			
			if (el) {
				el.addEventListener('click', function(e) {
					if (e.target.getAttribute('aria-expanded') == 'false') {
						e.target.setAttribute('aria-expanded', true);
					} else {
						e.target.setAttribute('aria-expanded', false);
					}
					e.target.parentElement.classList.toggle('form__help-text--open');
					e.preventDefault();
				}, false);
			}
		}
	};

	return {
		init: __init,
		toggle: __toggle
	};
	
})();



// Multi-suggest on textfield

var multi_suggest = (function() {
	
	var event_listener = false;
	var select_bounds = {
		up: 0,
		down: 0
	};
	
	// Finds all instances of the default .multilist__field.
	var __init = function(el) {
		el = (el) ? el : '.form__multilist';
		var multilists = document.querySelectorAll(el);
		if (multilists.length === 0) return;
		for (var i=0; i<multilists.length; i++) {
			__setup(multilists[i]);
		}
	};
	
	var __setup = function(el) {

		if (!el.tagName) el = document.querySelector(el);
		
		// Safer to never even try to deal with anything that hasn't been setup via this script.
		el.classList.add('js__multilist');
		__set_datalist_height(el.querySelector('.multilist__list'));
		
		if (!event_listener) {
			// Prevent using left/right arrows and enter keys when inside an instance of this component.
			// This is on keydown to take effect before the key is registered.
			document.addEventListener('keydown', __action_keydown);
			// The main work searching when in the field.
			document.addEventListener('keyup', __action);
			// Adds the clicked or keyed suggestion from the select options to the field.
			document.addEventListener('click', __action_click);
			event_listener = true;
		}

		// When the user moves away from the select suggestions we reset the currently highlighted option in that select.
		el.querySelector('.multilist__list').addEventListener('blur', __clear_selected_option);
	};
	
	
	var __clear_selected_option = function(e) {
		e.target.selectedIndex = -1;
	};
	
	
	// When keying on selects the default behaviour of left/right arrows is to move up and down the list, we need to stop this.
	var __action_keydown = function(e) {
		if (!e.target.closest('.js__multilist')) return;
		if ((e.keyCode === 37) || (e.keyCode === 39) || (e.keyCode === 13)) {
			e.preventDefault();
		}
	};
	
	
	var __action = function(e) {
		if (!e.target.closest('.js__multilist')) return;
		
		if (e.target.classList.contains('multilist__field')) {
			
			// If down arrow pressed...
			if (e.keyCode === 40) {
				// Don't allow the user to move to the suggested items if there aren't any available options.
				if (e.target.nextElementSibling.classList.contains('multilist__list--empty')) return;

				e.target.nextElementSibling.selectedIndex = -1;
				e.target.nextElementSibling.focus();
				select_bounds.up = 1;
				select_bounds.down = 0;
				e.target.nextElementSibling.querySelector('option:not(.hide)').selected = true;
				__detect_last_option(e.target.nextElementSibling);
				
			} else if (e.keyCode === 38) {
				// Don't allow the user to move to the suggested items if there aren't any available options.
				if (e.target.nextElementSibling.classList.contains('multilist__list--empty')) return;

				e.target.nextElementSibling.selectedIndex = -1;
				e.target.nextElementSibling.focus();
				select_bounds.up = 0;
				select_bounds.down = 1;
				e.target.nextElementSibling.querySelector('option:not(.hide):last-child').selected = true;
				__detect_last_option(e.target.nextElementSibling);
				
			} else {
				// Check whether entered text matches anything found in the button list.
				var current_str = e.target.value.split(' ');
				var last_word = current_str[current_str.length - 1];

				var datalist = document.getElementById(e.target.getAttribute('aria-owns')).children;
				var datalist_container = document.getElementById(e.target.getAttribute('aria-owns'));
				
				// Loop through all OPTIONS in SELECT dropdown.
				for(i=0; i<datalist.length; i++) {
					// Remove all the hiding classes and start from scratch.
					datalist[i].classList.remove('hide');
					// If there's currently a word being typed and it doesn't match with letters in the OPTIONS list...
					if ( (last_word !== undefined) && (!datalist[i].innerText.toLowerCase().includes(last_word)) ) {
						// Hide the unmatched OPTIONS.
						datalist[i].classList.add('hide');
					}
				}
				__set_datalist_height(datalist_container);
			}
			
		} else if (e.target.tagName == 'SELECT') {
			if ((e.keyCode === 37) || (e.keyCode === 39) || (e.keyCode === 13)) {
				__add_to_field(e, e.target[e.target.selectedIndex].value);
				e.preventDefault();
				return false;
			}
			if (e.keyCode === 40) {
				select_bounds.up = 0;
				if (select_bounds.down) {
					e.target.previousElementSibling.focus();
					select_bounds.down = 0;
				} else {
					__detect_last_option(e.target);
				}
			}
			if (e.keyCode === 38) {
				select_bounds.down = 0;
				if (select_bounds.up) {
					e.target.previousElementSibling.focus();
					select_bounds.up = 0;
				} else {
					__detect_first_option(e.target);
				}
			}
		}
	};
	
	
	// Compare the text values of the current option and the last visible option.
	// We have to compare the text because we can't use the selectedIndex directly as we might get 1,4,8 out of the list when filtering.
	var __detect_first_option = function(e) {
		var options = e.querySelectorAll('option:not(.hide)');
		select_bounds.up = (e[e.selectedIndex].value === options[0].value) ? 1 : 0;
	};
	
	var __detect_last_option = function(e) {
		var options = e.querySelectorAll('option:not(.hide)');
		select_bounds.down = (e[e.selectedIndex].value === options[options.length-1].value) ? 1 : 0;
	};
	
	
	var __set_datalist_height = function(select) {
		var datalist_size = select.querySelectorAll('option:not(.hide)').length;
		if (datalist_size > 0)  {
			select.setAttribute('size', datalist_size);
			select.classList.remove('multilist__list--empty');
		} else {
			select.setAttribute('size', 1);
			select.classList.add('multilist__list--empty');
		}
	};
	
	
	var __add_to_field = function(e, word) {
		
		var input_field = e.target.closest('.form__multilist').querySelector('.multilist__field');
			
		var current_str = input_field.value.split(' ');
		current_str.pop();
		current_str.push(word);
		input_field.value = current_str.join(' ') + ' ';

		var datalist = e.target.closest('.form__multilist').querySelectorAll('.hide');
		for (i=0; i<datalist.length; i++) {
			datalist[i].classList.remove('hide');
		}
		input_field.focus();
		
		// Reset datalist height to max.
		__set_datalist_height(e.target.closest('.multilist__list'));
	};
	
	// Add word on click.
	var __action_click = function(e) {
		if (e.target.closest('.multilist__list')) {
			__add_to_field(e, e.target.textContent);
		}
	};
	
	
	return {
		init: __init,
		setup: __setup
	};
	
})();



// Obfuscated field

class BlinkyObfuscatedField extends HTMLElement {
	
	static get observedAttributes() {
		// Watched component attributes.
		return ['state'];
	}
	
	constructor() {
		super();
	}

	__add_toggle() {
		const toggle = document.createElement('blinky-toggle-button');
		toggle.setAttribute('type', 'icon-only');
		toggle.setAttribute('button-class', 'button button--small form__obfuscated__toggle');

		// Add the toggle button settings, we either use attributes passed
		// on this component down to the toggle or some basic defaults.
		toggle.setAttribute('icon-0', this.getAttribute('icon-0') || 'view-hidden');
		toggle.setAttribute('text-0', this.getAttribute('text-0') || 'Show');
		toggle.setAttribute('icon-1', this.getAttribute('icon-1') || 'view');
		toggle.setAttribute('text-1', this.getAttribute('text-1') || 'Hide');
		toggle.setAttribute('toggled', this.getAttribute('state') || '0');
		this.append(toggle);

		
		// Add an event listener for exposing the toggle change to this web component,
		// we name it so we can remove the event handler if we remove the WC from the page.
		toggle.querySelector('.button').addEventListener('click', this.toggle_state = () => {
			this.setAttribute('state', toggle.getAttribute('toggled'));
		});

		// When we add an evenListener later on to monitor for form submits the only way to do this is to go outside
		// this component and attach it to the parent form. But if the disconnectedCallback() is used we lose track of
		// the parent form so we record any added evenListeners into this array.
		this.event_items = [];
		this.event_items.push({
			ev: 'click',
			el: toggle.querySelector('.button'),
			fn: this.toggle_state
		});
	}

	__add_aria() {
		// Because we're updating elements on the page that deal with potentially
		// sensitive data make doubly sure the user is aware, even if they can't see it.
		const aria = document.createElement('span');
		aria.classList.add('visually-hidden');
		aria.setAttribute('aria-live', 'polite');
		this.append(aria);
	}

	__change_state() {
		// Runs once on component setup and then whenever the state attribute changes.

		// The state and toggle attributes on the main WC and the toggle are binary,
		// so we can reference the field type via the array key.
		const states = ['password', 'text'];
		
		this.querySelector('input').setAttribute('type', states[this.getAttribute('state')]);

		// Make sure to keep the toggle attibute in sync, this is needed
		// for when we force fields back to passwords on form submit.
		this.querySelector('blinky-toggle-button').setAttribute('toggled', this.getAttribute('state'));
	}

	__update_aria() {
		// Runs whenever the state attribute changes and on component
		// setup if the default state is to show the field content.
		this.querySelector('[aria-live]').innerText = this.aria_text[this.getAttribute('state')];
	}

	__submit_pass() {
		// Ideally we don't want to submit password fields as regular text fields, so add an eventListener to the
		// parent form. If the form is submitted it is highjecked for a moment to rest the fields back to passwords,
		// then we proceed with the submit.
		const submit_pass = this.getAttribute('submit-pass') || '0';
		if (submit_pass === '0') {
			const form = this.closest('form');
			if (form) {
				form.addEventListener('submit', this.submit_form = (e) => {
					this.setAttribute('state', '0');
				});
				// Store the eventListener info because we will lose access to it if disconnectedCallback is fired.
				this.event_items.push({
					ev: 'submit',
					el: form,
					fn: this.submit_form
				});
			}
		}
	}
	
	connectedCallback() {
		// Setup the component.

		// Add toggle button.
		this.__add_toggle();

		// Add aria status, we add this separately to the toggle as if the
		// field is set to password we don't need to alert the user for this.
		this.__add_aria();

		// Setup the aria text content, either from attributes set on the web component or some sensible defaults.
		// We set up this array so the keys correspond to the content of the state attribute.
		this.aria_text = [];
		this.aria_text.push(this.getAttribute('aria-0') || 'Your field content is hidden from view.');
		this.aria_text.push(this.getAttribute('aria-1') || 'Your field content is being displayed in plain text.');

		// Check for a state attribute, if none exists set it.
		if (!this.hasAttribute('state')) this.setAttribute('state', '0');

		// Set the initial state of the field.
		this.__change_state();
		
		// If the obsufacted field is set to display in plain text on initial appearance we need to let the user know.
		if (this.getAttribute('state') === '1') this.__update_aria();
		
		// We use this to guard actions in attributeChangedCallback() but 
		// also to apply any CSS that may only be needed on the JSed element
		this.classList.add('js__form__obfuscated');

		// Swap any visible fields back to passwords before submit.
		this.__submit_pass();
	}
	
	disconnectedCallback() {
		// When disconnectedCallback() we lose access to the parent data of the component (probably bacue it's already
		// been yeeted from the DOM) so we need to keep a log when we set an eventListener so we can find them and
		// remove them if the WC is removed.
		for (let item of this.event_items) {
			item.el.removeEventListener(item.ev, item.fn);
		}
	}
	
	attributeChangedCallback(att, old_v, new_v) {
		
		// Because this method fires purely on the element being inited we guard the actions by checking
		// to make sure it's not the first run. Also, only run if the attribute was actually updated.
		if ((!this.classList.contains('js__form__obfuscated')) || (old_v === new_v)) return false;
		
		// Changes to watched attributes.
		if (att === 'state') {
			this.__change_state();
			this.__update_aria();
		}
	}
}

customElements.define('blinky-obfuscated-field', BlinkyObfuscatedField);



var switch_toggle = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	// Finds all switch toggles and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default class.
		el = (el) ? el : '.form__switch';
		var switches = document.querySelectorAll(el);

		if (switches.length === 0) return;
		
		for (var i=0; i<switches.length; i++) {
			__setup(switches[i]);
		}
	};
	
	
	var __setup = function(el) {

		el.classList.add('js__form__switch');
		
		// Add the event listener only once.
		if (!event_listener) {
			document.addEventListener('click', __action);
			// We only ever want to add one event listener.
			event_listener = true;
		}
	};
	
	
	// Called by the listener.
	var __action = function(e) {

		// Ignore anything that isn't a click on a switch toggle.
		if (!e.target.closest('.js__form__switch')) return false;

		switched = e.target.getAttribute('aria-pressed') === 'true';
		e.target.setAttribute('aria-pressed', String(!switched));
	};
	
	
	return {
		init: __init,
		setup: __setup
	};
	
})();
// Nested linked fieldsets

	class BlinkyChildFields extends HTMLElement {
		
		static get observedAttributes() {
			return ['expanded'];
		}

		constructor() {
			super();
		}


		__add_connection(parent) {
			// Does .children have an id? If it doesn't then we can generate one based on the parent's ID. If the
			// parent doesn't have an ID we can generate soething based on the current time in milliseconds.
			let connection = '';
			if (this.querySelector(':scope > .children').id) {
				connection = this.querySelector(':scope > .children').id;
			} else {
				// No ID on .children, look for something on parent.
				connection = (parent.id) ? 'connection-'+parent.id : 'connection-'+Date.now();
			}

			// Add aria-controls to parent.
			parent.setAttribute('aria-controls', connection);

			// Add ID to .children.
			this.querySelector(':scope > .children').id = connection;
		}


		__update_aria_expanded() {
			// This keeps the aria-expanded on the .children in sync with the expanded attribute on the instance.
			this.querySelector(':scope > .children').setAttribute('aria-expanded', this.getAttribute('expanded'));

			this.__toggle();
			this.__set_disabled_attribute();
		}


		__set_disabled_attribute() {
			// Sets the fields as disabled or enabled. Because there's no nice way in CSS to get .el :not(.el) input
			// without also unsing the restrictive immediate child selector of > we need to grab all the fields in the
			// instance, then get a second group that will contain only the grandchildren. When we loop thorugh the
			// main group we check to see if the current field is actually a grandchild, if it is we ignore it.

			// Add fields under this instance.
			let fields = this.querySelectorAll(`
				:scope .children input:not([type=submit]),
				:scope .children textarea,
				:scope .children select
			`);

			// Fields to ignore.
			let fields_to_ignore = this.querySelectorAll(`
				:scope .children .children input:not([type=submit]),
				:scope .children .children textarea,
				:scope .children .children select
			`);

			// We can't use includes() on a nodeList so we
			// duplicate the ignore fields into a normal array.
			let arr_fields_to_ignore = Array.from(fields_to_ignore);

			// This instance's expanded value.
			let expanded = this.getAttribute('expanded');

			for (let field of fields) {

				// Make sure the current field isn't a grandchild.
				if (!arr_fields_to_ignore.includes(field)) {

					// Set the field to the needed disabled value.
					if (expanded == 'true') {
						field.removeAttribute('disabled');
					} else {
						field.setAttribute('disabled', 'disabled');
					}
				}
			}
		}


		__toggle() {
			// Gets all nested component instances and sets the expanded attributes.

			const bcfs = this.querySelectorAll(':scope blinky-child-fields');

			// The top level instance is not expanded then it follows that all nested should also be closed.
			if (this.getAttribute('expanded') === 'false') {
				if (bcfs.length > 0) {
					for (let i of bcfs) {
						i.setAttribute('expanded', 'false');
					}
				}

			} else {
				if (bcfs.length > 0) {
					for (let i of bcfs) {

						// Is this instance's parent field checked?
						const input = i.querySelector(':scope .js__parent');
						if (input && input.checked) {

							// Just because this instance's parent field is checked doesn't mean we just go ahead
							// and expand this instance because we also need the parent instance to be expanded.
							// To get the parent instance, we have to use parentNode otherwise closest() just
							// returns the current instance.
							const parent = i.parentNode.closest('blinky-child-fields');

							// Only if the parent instance is expanded do we
							// actually set the curruent instance to expanded.
							if (parent && parent.getAttribute('expanded') == 'true') {
								i.setAttribute('expanded', 'true');
							}
						}
					}
				}
			}
		}


		connectedCallback() {
			// Find parent field, should be first checkbox or radio but not a child.
			const parent = this.querySelector(':scope > input[type="radio"], :scope > input[type="checkbox"]');
			if (!parent) return false;

			// Check for children group, we don't need to find them all, just check for one field.
			if (!this.querySelector(`
				:scope > .children input,
				:scope > .children textarea,
				:scope > .children select`)) return false;

			// Setup the aria-controls / id relationship between parent and children.
			this.__add_connection(parent);

			// We found an aria-controls/id pairing...
			const children = this.querySelector(':scope > .children');
			children.setAttribute('aria-expanded', 'false');
			this.setAttribute('expanded', 'false');
			parent.classList.add('js__parent');

			// We use this to guard actions in attributeChangedCallback() but 
			// also to apply any CSS that may only be needed on the JSed element
			this.classList.add('js__form__child-fields');

			// Add the CSS class to the .children container to apply the indent.
			children.classList.add('form__child-fields');

			// Add the inner span to apply the overflow, this allows the fields to be animated closed.
			const inner = document.createElement('span');
			inner.classList.add('form__child-fields__inner');
			Blinky_wrap_content({
				el: children,
				wrapper: inner
			});

			// Store any added eventListeners for removal in disconnectedCallback();
			this.event_items = [];

			// As there is no eventListener that will detect a radio being unchecked we have to add some other means
			// to clear the active children. We add eventListeners to poke the current radio children to an uncecked
			// state by monitoring all other radio buttons that have the same name attribute. It's not nice but it
			// does work ¯\_(ツ)_/¯.
			if (parent.type === 'radio') {
				const radios = document.querySelectorAll('input[type="radio"][name="'+parent.getAttribute('name')+'"]');
				for (let r of radios) {
					r.addEventListener('change', this.untoggle_children = (e) => {
						this.setAttribute('expanded', 'false');
						children.setAttribute('aria-expanded', 'false');
					});

					// Save event.
					this.event_items.push({
						ev: 'change',
						el: r,
						fn: this.untoggle_children
					});
				}
			}

			// Add the eventListener to the aria-controls. We need to save it so we can remove it if the
			// component is removed from the page since it will be added outside the scope of this.
			parent.addEventListener('change', this.toggle_children = (e) => {
				this.setAttribute('expanded', (e.target.checked) ? 'true' : 'false' );
				children.setAttribute('aria-expanded', (e.target.checked) ? 'true' : 'false' );
			});

			// Save event.
			this.event_items.push({
				ev: 'change',
				el: parent,
				fn: this.toggle_children
			});

			this.__update_aria_expanded();

			// If the component has a field that's checked by default we need to make the children visible.
			if (parent.getAttribute('checked')) {
				children.setAttribute('aria-expanded', 'true');
				this.setAttribute('expanded', 'true');
			}
		}


		disconnectedCallback() {
			// Remove all previously set eventListeners.
			for (let item of this.event_items) {
				item.el.removeEventListener(item.ev, item.fn);
			}
		}


		attributeChangedCallback(att, old_v, new_v) {
			
			// Because this method fires purely on the element being inited we 
			// guard the actions by checking to make sure it's not the first run.
			// Also, only run if the attribute was actually updated.
			if ((!this.classList.contains('js__form__child-fields')) || (old_v === new_v)) return false;

			if (att === 'expanded') {
				this.__update_aria_expanded();
			}
		}

	}
	customElements.define('blinky-child-fields', BlinkyChildFields);



// Nested linked fieldsets

	var fieldset_group = (function() {
		
		var __init = function(el) {
			// Check for the items to check and the class to toggle.
			if (!el) el = {};
			if (el.selector == null) el.selector = '.fieldset-group';
			if (el.clear_hidden == null) el.clear_hidden = 'false';
			
			var els = document.querySelectorAll(el.selector);
			if (els.length === 0) return;
			
			for (var i=0; i<els.length; i++) {
				__prep({selector: els[i], clear_hidden: el.clear_hidden});
			}
		};
	
		var __prep = function(el) {
			// If you call prep directly you need to provide an element to act upon.
			if (!el || !el.selector) return;
			if (el.clear_hidden == null) el.clear_hidden = 'false';
			
			if (!el.selector.classList.contains('js__fieldset-group')) {
				el.selector.classList.add('js__fieldset-group');
				el.selector.setAttribute('data-clear-hidden', el.clear_hidden);
				document.addEventListener('change', __action);
				
				// Check for any pre-checked radio buttons so we can show the child group on inital load.
				__action( {target: el.selector.querySelector('input[checked="checked"]')} );
			}
		};
		
		var __clear_hidden_fields = function(el) {
			if (!el) return;
			var fields = el.querySelectorAll('input:not([type=submit]), textarea, select');
			if (fields.length === 0) return;
			
			for (var i=0; i<fields.length; i++) {
				if (fields[i].type == 'radio' || fields[i].type == 'checkbox') {
					fields[i].checked = false;
				} else {
					fields[i].value = '';
				}
			}

			// Remove the outer container style of large radio blocks.
			var field_containers = el.querySelectorAll('.js__large-radio--selected');
			if (field_containers.length === 0) return;
			for (var x=0; x<field_containers.length; x++) {
				field_containers[x].classList.remove('js__large-radio--selected');
			}
		};
	
		var __action = function(el) {
			// Check that we're in a fieldset group but that we're clicking on an element in the parent group.
			if (!el.target.closest('.js__fieldset-group') || (el.target.getAttribute('aria-controls') === null)) return;
			
			// Check that the radio has the correct aria attribute and that 
			// we're inside a fieldset group we want to toggle sub-fieldsets.
			if (!el.target.hasAttribute('aria-controls') || !el.target.closest('.fieldset-group').classList.contains('js__fieldset-group')) return;
			
			var new_fieldset = el.target.getAttribute('aria-controls');
			
			var current = '';
			if (el.target.closest('.js__fieldset-group').querySelector('[aria-expanded="true"]')) {
				current = el.target.closest('.js__fieldset-group').querySelector('[aria-expanded="true"]').id;
			}
			
			if (new_fieldset !== current && el.target.closest('.js__fieldset-group').getAttribute('data-clear-hidden') == 'true') {
				__clear_hidden_fields(el.target.closest('.js__fieldset-group').querySelector('[aria-expanded="true"]'));
			}
				
			var subfieldsets = el.target.closest('.js__fieldset-group').querySelectorAll('fieldset[aria-expanded=true]');
			if (subfieldsets.length > 0) {
				for (var i=0; i<subfieldsets.length; i++) {
					subfieldsets[i].setAttribute('aria-expanded', "false");
				}
			}
			
			if (new_fieldset == '') return;
			
			// Show the new sub-fieldset.
			el.target.closest('.js__fieldset-group').querySelector('#'+new_fieldset).setAttribute('aria-expanded', "true");

            if (el.type === 'change') {
				// Move the browser window view to show the sub fieldset.
				el.target.closest('.js__fieldset-group').querySelector('#'+new_fieldset).scrollIntoView();
            }
			
		};
		
		return {
			init: __init,
			prep: __prep
		};
		
	})();
	


var file_upload_prettifier = (function() {

	/*
		USAGE
		file_upload_prettifier.init();                finds all instances of .form__file
		file_upload_prettifier.init('.class');        find instances with CSS identifier
		file_upload_prettifier.prettify('#input10');  a specific element or ID
	 */

	var __init = function(els) {

		// Find all instances of .form__file or whatever class is fed in manually.
		var file_fields;
		if (els) {
			file_fields = document.querySelectorAll(els);
		} else {
			file_fields = document.querySelectorAll('.form__file');
		}
		
		for (i=0; i<file_fields.length; i++) {
			__prettify(file_fields[i]);
		}
	};

	var __update_file_list = function(el) {
		var html = '';
		// Add the filenames to the fake listing.
		if (el.files.length > 0) {
			for (i=0; i<el.files.length; i++) {
				html+= '<span><svg class="icon" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true" focusable="false"><use href="'+ui_path({el: el})+'/ui/images/icons.svg#icon-page"></use></svg> '+el.files[i].name+'</span>';
			}
		}

		el.nextSibling.nextSibling.innerHTML = html;
	};

	var __prettify = function(el) {
		if (!el) {
			return;
		}

		// Check if we've been passed a CSS ID or class rather than an element and grab it as an element.
		if (!el.tagName) el = document.querySelector(el);

		// Create the filename display area.
		file_list = document.createElement('span');
		file_list.setAttribute('class', 'form__file__name');
		el.parentNode.insertBefore(file_list, el.nextSibling);

		// Create the browse button.
		drag = document.createElement('span');
		drag.setAttribute('class', 'form__file__drag');
		// drag.innerHTML = 'Drag files here to upload or <span class="button form__file__button">Browse</span> to upload';
		drag.innerHTML = 'Drag file';
		if (el.getAttribute('multiple') !== null) drag.innerHTML+= 's';
		drag.innerHTML+= ' here to upload or <span class="button form__file__button">Browse</span> to upload';
		el.parentNode.insertBefore(drag, el.nextSibling);

		// Set the upload field to the width and height of the button, this needs tidying in future.
		// The better way to do this would be to wrap the button and input then set the input to 100% height and width of the container, this will break the error text styling though as we can't do .child < .parent + .error
		function __set_button_size() {
			if (!drag.offsetHeight) {
				window.requestAnimationFrame(__set_button_size);
			} else {
				el.setAttribute('style', 'height: '+drag.offsetHeight+'px; width: '+drag.offsetWidth+'px');
			}
		}
		// We need to postpone this until button gets set tot the correct size.
		__set_button_size();

		// Watch the file input for any file selections.
		el.addEventListener('change', function() {__update_file_list(el);}, true);



		function __drag_start(e) {
			dragging++;
			e.classList.add('form__file__drag-active');
		}
		function __drag_end(el) {
			dragging--;
			if (dragging === 0) {
				el.classList.remove('form__file__drag-active');
			}
		}
		function __dropped(el) {
			dragging = 0;
			el.classList.remove('form__file__drag-active');
		}

		var dragging = 0;

		el.addEventListener('dragenter', function() {__drag_start(el);}, true);
		el.addEventListener('dragleave', function() {__drag_end(el);}, true);
		el.addEventListener('drop', function() {__dropped(el);}, true);

		// On first run just check we don't have any files actually selected because we only soft refreshed the page.
		__update_file_list(el);
	};

	return {
		init: __init,
		prettify: __prettify
	};

})();



// Progress bar/circle

class BlinkyProgress extends HTMLElement {
	
	// Values that will / might change over time to keep track of.
	// They are mapped to the value and max attributes of a progress element.
	static get observedAttributes() {
		return ['b-value', 'b-max', 'b-type'];
	}
	
	constructor() {
		super();
	}

	connectedCallback() {
		
		if (!this.getAttribute('data-init')) {
			
			// Visually hide the native PROGRESS bar, we keep it "visible" to screen readers as this is the symantic 
			// element and has meaning compared to the visual pseudo element that will be just a collection of SPANs.
			const progress = this.querySelector('progress');
			progress.setAttribute('class', 'visually-hidden');

			let b_type = (this.getAttribute('b-type') == 'circle') ? 'circle' : 'line';

			// Create and attach the pseudo progress element that we can style.
			const loader = document.createElement('span');
			loader.setAttribute('class', 'progress__box progress__box--'+b_type);
			loader.setAttribute('aria-hidden', 'true');
			progress.after(loader);

			// ... And the progress bar.
			const bar = document.createElement('span');
			bar.setAttribute('class', 'progress__fish');

			// Fix so that indeterminate progress bars still show the fish animation because we're not setting the bar to 0 width;
			if (this.hasAttribute('b-value')) {
				bar.setAttribute('style', 'width: 0%');
			}
			loader.appendChild(bar);

			// Chceck for is not circle because on the first run thorough this is still undefined.
			// If it's a line it can be a determinate progress bar so needs the animation speed sestting.
			if (b_type != 'circle') {

				// Because the progress bar can vary in width we can't set the animation speed in CSS,
				// so we take the width of the progress bar and divide it by 4, making sure to floor it,
				// always make sure the speed is a minimum of 100ms as any faster is hard to register visually.
				// Then we make it readable for the custom event later on.
				let animation_speed = Math.floor((loader.offsetWidth / 4));
				if (animation_speed < 100) animation_speed = 100;
				loader.setAttribute('style', '--speed: '+animation_speed+'ms');
				this.animation_speed = animation_speed;
			}
			
		}
		
		this.setAttribute('data-init', '1');
	}
	
	disconnectedCallback() {}
	
	// CE attributes have updated...
	__update_value(att, old_v, new_v) {
		
		if (!new_v) return false;
		const progress = this.querySelector('progress');
		
		// As we use the native attribute name prefixed with 'b-' we know how many characters we can always ignore. 
		// Update the attributes on the PROGRESS element.
		progress.setAttribute(att.substring(2), new_v);
		
		// Update the progress bar current value.
		if (att == 'b-value') {
			
			// Update the text content on the native PROGRESS for a11y.
			progress.innerText = new_v+'%';
			
			// Set the width on pseudo bar.
			const bar = this.querySelector('.progress__fish');
			if (bar) bar.setAttribute('style', 'width: '+new_v+'%');
			
			// If we're using a determinate progress bar where it will eventually fill up to 100% send an event when it does.
			if (new_v == '100') {
				
				// Delay the event by the time of the CSS animation.
				setTimeout(() => {
					this.dispatchEvent(
						new CustomEvent('progressBarFull', {bubbles: true, el: this})
					);
				}, this.animation_speed);
			}
		}
	}
	
	// If either b-value or b-max is changed on the main <blinky-progress> element update their usage in the CE.
	attributeChangedCallback(att, oldValue, newValue) {
		
		// Force connectedCallback() to run before doing anything with the attribute values.
		if (!this.getAttribute('data-init')) {
			this.connectedCallback();
		}
		
		let b_type = (this.getAttribute('b-type') == 'circle') ? 'circle' : 'line';
		
		if (b_type != 'circle') {
			this.__update_value(att, oldValue, newValue);
		}
	}
}

customElements.define("blinky-progress", BlinkyProgress);



// Blinky Progress controller.
var blinky_progress = (function() {
	
	let __add = function(bits) {
		if (!bits.el) return false;

		let blinky_progress_tag = document.createElement('blinky-progress');
		blinky_progress_tag.setAttribute('class', 'progress');
		let blinky_progress_bar = document.createElement('progress');
		blinky_progress_tag.appendChild(blinky_progress_bar);

		if (bits.el.tagName == 'BUTTON' || bits.el.tagName == 'LI') bits.type = 'line';
		if (bits.type) blinky_progress_tag.setAttribute('b-type', bits.type);

		if (bits.value) blinky_progress_tag.setAttribute('b-value', bits.value);
		if (bits.max) blinky_progress_tag.setAttribute('b-max', bits.max);
		if (bits.id) blinky_progress_tag.setAttribute('id', bits.id);
		if (bits.label) blinky_progress_bar.setAttribute('aria-label', bits.label);

		bits.el.append(blinky_progress_tag);
	};
	
	let __remove = function(bits) {
		if (!bits.el) return false;
		bits.el.remove();
	};
	
	return {
		add: __add,
		remove: __remove
	};
	
})();



// Theme Switch web component

var theme_switcher_template = document.createElement('template');
/* jshint ignore:start */
theme_switcher_template.innerHTML = `
	<style>
		.theme-switch .button {
			margin: 0;
			text-align: left;
			white-space: nowrap;
		}
		.theme-switch {
			display: inline-flex;
			flex-direction: column;
			position: relative;
		}
		.theme-switcher__options {
			flex-direction: column;
			position: absolute;
			top: calc(100% - 3px);
		}
		.theme-switch.theme-switch--right .theme-switcher__options {
			right: 0;
		}
		.theme-switch.theme-switch--up .theme-switcher__options {
			bottom: calc(100% - 3px);
			top: auto;
		}
		
		.theme-switch__chooser[aria-expanded="false"] + .theme-switcher__options {
			display: none;
		}
		.theme-switch__chooser[aria-expanded="true"] + .theme-switcher__options {
			display: inline-flex;
		}
		.theme-switch .icon {
			display: none;
		}
		.theme-switch [data-theme="theme--light"] [data-id="theme--light"], 
		.theme-switch [data-theme="theme--system"] [data-id="theme--system"], 
		.theme-switch [data-theme="theme--dark"] [data-id="theme--dark"] {
			display: inline-block;
		}
		.theme-switch [aria-pressed="true"] {
			background: rgb(var(--tango)) !important;
			color: rgb(var(--menu-current-text))!important;
		}
		.theme-switch [aria-pressed="true"] .icon {
			fill: rgb(var(--menu-current-text));
		}
		
		.theme-switcher__options .button:first-child {
			border-bottom-left-radius: 0;
			border-bottom-right-radius: 0;
		}
		.theme-switcher__options .button[data-id="theme--system"] {
			border-radius: 0;
		}
		.theme-switcher__options .button:last-child {
			border-top-left-radius: 0;
			border-top-right-radius: 0;
		}
	</style>
	<section class="theme-switch" aria-labelledby="theme-switch__title">
		<button class="button theme-switch__chooser" aria-haspopup="true" aria-expanded="false">
			<svg class="icon" data-id="theme--light" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true" focusable="false" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2"><ellipse cx="8.015" cy="7.583" rx="5.372" ry="5.76" transform="matrix(.93073 0 0 .86806 .555 1.418)"/><path d="M7.5 0h1v2h-1zM7.5 14h1v2h-1zM0 8.5v-1h2v1zM14 8.5v-1h2v1zM1.99 2.697l.707-.707 1.414 1.414-.707.707zM11.89 12.596l.707-.707 1.414 1.415-.707.707zM2.697 14.01l-.707-.707 1.414-1.414.707.707zM12.596 4.11l-.707-.707 1.415-1.414.707.707z"/></svg>
			<svg class="icon" data-id="theme--system" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true" focusable="false" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2"><path style="fill:none" d="M17.615.019h.737v5.004h-.737z" transform="matrix(21.7105 0 0 3.19767 -382.422 -.062)"/><path d="M6.998 11h2v1h-2zM4.998 12h6v1h-6zM13.999 3h-12v8h12V3Zm-1.5 1.5h-9v5h9v-5Z"/></svg>
			<svg class="icon" data-id="theme--dark" width="1em" height="1em" viewBox="0 0 16 16" aria-hidden="true" focusable="false" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2"><path style="fill:none" d="M17.615.019h.737v5.004h-.737z" transform="matrix(21.7105 0 0 3.19767 -382.422 -.062)"/><path d="M10.513 13.455A6.003 6.003 0 0 1 2.013 8a6.003 6.003 0 0 1 8.5-5.455A6.003 6.003 0 0 0 7.013 8a6.004 6.004 0 0 0 3.5 5.455Z"/></svg>
			<span class="button__text"></span>
		</button>
		
		<span class="theme-switcher__options">
			<button data-id="theme--light" class="button">
				<span class="button__text"></span>
			</button><button data-id="theme--system" class="button">
				<span class="button__text"></span>
			</button><button data-id="theme--dark" class="button">
				<span class="button__text"></span>
			</button>
		</span>
	</section>
`;

class ThemeSwitcher extends HTMLElement {
	connectedCallback() {
		this.innerHTML = theme_switcher_template.innerHTML;
		
		// Grab any passed in settings or set defaults.
		var default_settings = {
			'default_theme': 'light',
			'title': 'Choose Theme',
			'light': 'Light',
			'system': 'Follow System',
			'dark': 'Dark',
			'display_label': 'true',
			'h_align': 'left',
			'v_align': 'down'
		};
		var settings;
		try {
			settings = JSON.parse(this.attributes.settings.value)[0];
		} catch(e) {}
		
		// Merge defaults with anything explicity set.
		settings = Object.assign(default_settings, settings);
		
		var btns = this.querySelectorAll('button');
		if (btns) {
			for(var i=0; btns.length>i; i++) {
				if (!btns[i].classList.contains("theme-switch__chooser")) {
					btns[i].setAttribute('aria-pressed', 'false');
				} else {
					if (settings.display_label === 'false') {
						btns[i].querySelector('.button__text').classList.add('visually-hidden');
					}
				}
			}
		}
		// Apply the CSS class to the body from weither the cookie or the default.
		this.add_class( this.get_cookie('theme_setting', settings.default_theme) || 'theme--'+settings.default_theme );
		
		// Set the alignment.
		this.querySelector('.theme-switch').classList.add('theme-switch--'+settings.h_align, 'theme-switch--'+settings.v_align);
		
		// Set the corresponding button to pressed.
		this.querySelector('.theme-switcher__options [data-id="'+(this.get_cookie('theme_setting', settings.default_theme) || 'theme--'+settings.default_theme)+'"]').setAttribute('aria-pressed', 'true');
		
		// Set title.
		this.querySelector('.theme-switch__chooser .button__text').innerHTML = settings.title;
		
		// Set button labels.
		this.querySelector('[data-id="theme--light"] .button__text').innerHTML = settings.light;
		this.querySelector('[data-id="theme--system"] .button__text').innerHTML = settings.system;
		this.querySelector('[data-id="theme--dark"] .button__text').innerHTML = settings.dark;
		
		// Set data attribute to the chooser so we can show the correct icon.
		this.querySelector('.theme-switch__chooser').setAttribute('data-theme', (this.get_cookie('theme_setting', settings.default_theme) || 'theme--'+settings.default_theme));
		
		// Watch for the clicks here not the button because we need to catch the off click on anything other than the theme switch.
		document.addEventListener('click', this, true);
	}
	
	// This ensures we keep access to 'this'.
	handleEvent(e) {
		if (e.type === 'click') {
			if (e.target.closest('.theme-switch')) {
				if (e.target.classList.contains("theme-switch__chooser")) {
					this.chooser(e);
				} else {
					this.action(e);
				}
			} else {
				this.close_chooser(e);
			}
		 }
	}
	
	close_chooser(e) {
		document.querySelector('.theme-switch__chooser').setAttribute('aria-expanded',  'false');
	}
	
	chooser(e) {
		// Toggle the menu open anad closed.
		e.target.setAttribute('aria-expanded', (e.target.getAttribute('aria-expanded') === 'true') ? 'false' : 'true');
	}
	
	add_class(css_class) {
		document.querySelector('body').classList.remove('theme--light', 'theme--dark', 'theme--system');
		document.querySelector('body').classList.add(css_class);
	}
	
	get_cookie(name, d) {
		var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
		return v ? v[2] : null;
		
	}
	
	set_cookie(name, value, days) {
		var d = new Date();
		d.setTime(d.getTime() + 24*60*60*1000*days);
		document.cookie = name + "=" + value + ";path=/;expires=" + d.toGMTString();
	}
	
	action(e) {
		this.set_cookie('theme_setting', e.target.dataset.id, 365*10);
		this.add_class(e.target.dataset.id);
		e.target.closest('.theme-switch').querySelector('[aria-pressed="true"]').setAttribute('aria-pressed', 'false');
		e.target.setAttribute('aria-pressed', 'true');
		
		// Set data attribute to the chooser so we can show the correct icon.
		e.target.closest('.theme-switch').querySelector('.theme-switch__chooser').setAttribute('data-theme', e.target.dataset.id);
		
		// Close the menu once a choice has been made.
		e.target.closest('.theme-switch').querySelector('.theme-switch__chooser').setAttribute('aria-expanded', 'false');
	}
}

customElements.define('theme-switch', ThemeSwitcher);
/* jshint ignore:end */


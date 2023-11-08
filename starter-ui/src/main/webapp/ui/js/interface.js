/*! Payara Pattern Library version: 0.51.2 */
/*! DO NOT MODIFY THIS FILE, CHANGES WILL BE OVERWRITTEN! */

// Always set a top level class to indicate if we have JS available.

document.getElementsByTagName("html")[0].className = document.getElementsByTagName("html")[0].className.replace(/no-js/,'').trim() + ' js';



// IE11 Deson't support linked SVG sprite files, annd this polyfill.
// svg4everybody();




// Polyfill for .closest.

if (window.Element && !Element.prototype.closest) {
    Element.prototype.closest =
    function(s) {
        var matches = (this.document || this.ownerDocument).querySelectorAll(s),
            i,
            el = this;
        do {
            i = matches.length;
            while (--i >= 0 && matches.item(i) !== el) {}
        } while ((i < 0) && (el = el.parentElement));
        return el;
    };
}


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



// Scroll bars

var scrollbar = function() {

	// Throw the bottom shadow on on page load.
	__init_first_shadow = function(e) {
		e.classList.add('scroll-shadow--bottom');
	};


	__scrolling = function(e) {
		if (!scrolling) {
			window.requestAnimationFrame(function() {
				if (e.target.scrollTop > 0) {
					e.target.classList.add('scroll-shadow--top');
				} else {
					e.target.classList.remove('scroll-shadow--top');
				}

				// Height of containing element - height of scrollable content.
				if ( e.target.scrollTop < (e.target.firstElementChild.offsetHeight - e.target.offsetHeight) ) {
					e.target.classList.add('scroll-shadow--bottom');
				} else {
					e.target.classList.remove('scroll-shadow--bottom');
				}
				scrolling = false;
			});
			scrolling = true;
		}
	};


	__calc = function() {
		var scrollers;
		scrollers = document.querySelectorAll('.scroll');
		if (scrollers !== null) {
			for (i=0; i<scrollers.length; i++) {

				// Remove any existing scrollers.
				scrollers[i].classList.remove('js__scroll');
				scrollers[i].removeEventListener('scroll', __scrolling);

				// Bail if there's nothing to scroll.
				if (scrollers[i].firstElementChild === null) return false;

				if ( scrollers[i].firstElementChild.offsetHeight > scrollers[i].offsetHeight ) {
					scrollers[i].classList.add('js__scroll');
					scrollers[i].addEventListener('scroll', __scrolling);
					__init_first_shadow(scrollers[i]);
				}
			}
		}
	};


	var scrolling = false;

	// Recalculate the scrollers when the user resizes their screen.
	window.addEventListener('resize-end', __calc);

	// First run.
	__calc();
};

scrollbar();



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



var sidebar_toggle = (function() {

	// Toggle sidebar.
	var __sidebar_toggle = function(b) {

		var pressed = (b.getAttribute('aria-pressed') === 'true');
		b.setAttribute('aria-pressed', !pressed);

		// If the button
		if (!pressed) {
			b.closest('[aria-labelledby='+b.getAttribute('id')+']').classList.add('page__sidebar--minimised');
			// Title stuff is only used on Monitoring Console.
			// if (b.getAttribute('data-show-title')) {
			// 	document.querySelector(b.getAttribute('data-show-title')).textContent = b.closest('[aria-labelledby='+b.getAttribute('id')+']').querySelector('[aria-current="page"]').textContent;
			// }

			// Switched for cloud
			// window.localStorage.setItem(b.getAttribute('id'), 'true');
		} else {
			b.closest('[aria-labelledby='+b.getAttribute('id')+']').classList.remove('page__sidebar--minimised');
			// Title stuff is only used on Monitoring Console.
			// if (b.getAttribute('data-show-title')) {
			// 	document.querySelector(b.getAttribute('data-show-title')).textContent = '';
			// }

			// Switched for cloud
			// window.localStorage.removeItem(b.getAttribute('id'));
		}
		// Switched for cloud
		window.localStorage.setItem(b.getAttribute('id'), !pressed);
	};

	function toggle(sidebarElement) {
		sidebarElement.addEventListener('click', function () {
			__sidebar_toggle(this);
		}, false);

		// Possibly need to look at inlining this to stop the flash of open sidebar on page load.

		// Switched for cloud
		// if (window.localStorage.getItem(sidebars[i].getAttribute('id'))) __sidebar_toggle(sidebars[i]);
		if (window.localStorage.getItem(sidebarElement.getAttribute('id')) != sidebarElement.getAttribute('aria-pressed')) {
			__sidebar_toggle(sidebarElement);
		}
	}

	function init() {
		// Find all sidebar toggles and loop through them.
		var sidebars = document.querySelectorAll('.js__sidebar__toggle');
		for (i = 0; i < sidebars.length; i++) {
			var sidebarElement = sidebars[i];
			toggle(sidebarElement);
		}
	}

	return {
		init: init,
		toggle: toggle,
	};
})();



var sidebar_toggle_mob = (function() {

	// Toggle mobile sidebar.
	var __sidebar_toggle = function(b) {

		var pressed = (b.getAttribute('aria-pressed') === 'true');
		b.setAttribute('aria-pressed', !pressed);

		// If the button
		if (!pressed) {
			document.getElementsByTagName('body')[0].classList.add('page__sidebar--expanded');
		} else {
			document.getElementsByTagName('body')[0].classList.remove('page__sidebar--expanded');
		}
	};

	function __toggle(sidebarElement) {
		sidebarElement.addEventListener('click', function () {
			__sidebar_toggle(this);
		}, false);
	}

	function __init() {
		// Find all sidebar toggles and loop through them.
		var sidebars = document.querySelectorAll('.js__sidebar__toggle__mob');
		for (i = 0; i < sidebars.length; i++) {
			var sidebarElement = sidebars[i];
			__toggle(sidebarElement);
		}
	}

	return {
		init: __init,
		toggle: __toggle
	};
})();



var sidebar_height = (function() {

	var __init = function() {
		window.addEventListener('resize-end', __sidebar_height);
		
		// Fix to wait for any images to load in the header.
		window.addEventListener('load', function () {
			__sidebar_height();
		});
	};

	var __sidebar_height = function() {
		var header_height = 0;
		// Get the height of the page header if the header element exists, otherwise the height value remains set to 0.
		try {
			header_height = document.querySelector('.page--sticky-header .page__header').offsetHeight;
		} catch (e) {}
		
		if (!header_height) return false;

		var sidebar = document.querySelector('.page__sidebar .sidebar');
		if (sidebar) {
			sidebar.setAttribute('style', 'top: '+header_height+'px; max-height: calc(100vh - '+header_height+'px);');
		}
	};

	return {
		init: __init
	};
})();



var tab_panel = (function() {

	var __switch_tab = function() {

		// Reset all tabs in the panel.
		var panel_tabs = this.closest('.tab-panel').querySelectorAll('[role="tab"]');
		for (i=0; i<panel_tabs.length; i++) {
			panel_tabs[i].setAttribute('aria-selected', 'false');
		}
		this.setAttribute('aria-selected', 'true');

		// Reset all the tab content states.
		var panel_contents = this.closest('.tab-panel').querySelectorAll('[role="tabpanel"]');
		for (i=0; i<panel_contents.length; i++) {
			panel_contents[i].classList.remove('tab-panel__active');
		}

		this.closest('.tab-panel').querySelector('[aria-labelledby="'+this.getAttribute('id')+'"]').classList.add('tab-panel__active');
	};

	var __init = function() {
		var tabs = document.querySelectorAll('.tab-panel [role="tab"]');
		for (i=0; i<tabs.length; i++) {
			tabs[i].addEventListener('click', __switch_tab);
		}
	};

	return {
		init: __init
	};

})();



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
var copy_box = (function() {

	/*
		v0.3.2

		This can either be run to grab all of the .copy-box__button elements by 
		default a group of elements, or in a more specific per element way.

		Find all default: copy_box.init();

		Find all: copy_box.init('.CSS-class-goes-here');

		Target specific element: copy_box.copy(el);

		Roll your own eventListener:
		var test = document.querySelector('.test');
		test.addEventListener('click', copy_box.copy);
		
		In-page: onclick="copy_box.copy(this)"
	*/
	
	// Stores the elements to check against.
	var el;
	
	// Add the eventListener to the whole page, this should be more performant over looping through individual elements and adding separate eventListeners. Also, store the element list so we can use it to test for whether an elements is to be listened to.
	var __init = function(els) {
		el = (els) ? els : '.copy-box__button';
		document.addEventListener('click', __bouncer);
	};
	
	// If we want to add more elements after the init has already been called.
	var __inject = function(els) {
		el += ', '+els;
	};
	
	// "You're not coming in wearing those trainers..." Checks against the list of elements, uses .closest to cope with instances where the we have something like <button class="el"><button>
	var __bouncer = function(e) {
		if (e.target.closest(el)) __action(e.target.closest(el));
	};
	
	// This is used if someone wants to forego any of the init stuff and setup their own eventListener.
	var __copy = function(btn) {
		__action(btn.target);
	};
	
	var __action = function(copy_btn) {
		// Grab the element containing the copy content.
		var to_copy = copy_btn.previousElementSibling;

		// We can't directly select something that's not an input but being an input wouldn't be great for accessibility, so we create a temporary visually hidden textarea, add the content to copy, copy that into the clipboard then nuke the temporary textarea.
		temp_copy_box = document.createElement('textarea');
		temp_copy_box.setAttribute('style', 'position: absolute; left: 9999px;');
		temp_copy_box.textContent = to_copy.textContent;
		to_copy.parentNode.insertBefore(temp_copy_box, to_copy.nextSibling);
		temp_copy_box.select();

		// Add a CSS animation to the copy box content, then after some time remove the class so it could be played again.
		to_copy.classList.add('copy-box__content--copied');
		setTimeout(function() {
			to_copy.classList.remove('copy-box__content--copied');
		}, 1000);

		document.execCommand("copy");

		// Change the icon on the button to a tick, then change it back after some time.
		var icon = copy_btn.querySelector('use').getAttribute('href');
		var default_icon = icon;
		icon = icon.split('-');
		icon[icon.length - 1] = 'tick';
		copy_btn.querySelector('use').setAttribute('href', icon.join('-'));
		setTimeout(function() {
			copy_btn.querySelector('use').setAttribute('href', default_icon);
		}, 2000);
		
		temp_copy_box.remove();
	};

	return {
		init: __init,
		inject: __inject,
		copy: __copy
	};
	
})();



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



// Charts JS

var chart_colors = {
	tango: 'rgb(240, 152, 27)',
	midnight: 'rgb(0, 44, 62)',
	smurf: 'rgb(5, 152, 214)',
	basalt: 'rgb(67, 68, 69)',
	concrete: 'rgb(218, 224, 226)',
	coal: 'rgb(23, 24, 24)'
};



// Button Toggle

var button_toggle = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	
	// Finds all toggles and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default .button--toggle, note this is only to init stuff, 
		// toggles still need to use the .toggle class after this point.
		el = (el) ? el : '.button--toggle';
		var toggles = document.querySelectorAll(el);
		if (toggles.length === 0) return;
		
		for (var i=0; i<toggles.length; i++) {
			__setup(toggles[i]);
		}
		
		// Add the event listener only once, do this on capture rather than bubble up so that this always gets actioned before any other toggle JS. 
		if (!event_listener) document.addEventListener('click', __action, true);
	};
	
	
	var __setup = function(el) {
		
		// If data-toggled attribute is found it means the default state is pressed so the aria-label should be set to pause.
		if (!el.hasAttribute('data-toggle-1') || !el.hasAttribute('data-toggle-2')) return false;

		el.querySelector('.toggle__text').innerHTML = (el.hasAttribute('data-toggled')) ? el.getAttribute('data-toggle-2') : el.getAttribute('data-toggle-1');
		
		// Add a class we attach the event listener to, we do this to force a requirement of the aria attributes.
		el.classList.add('js__button--toggle');
	};
	
	
	// Called by the listener.
	var __action = function(e) {
		
		// We only ever want to add one event listener.
		event_listener = true;
		
		// Ignore anything that isn't a click on a toggle.
		if (!e.target.closest('.js__button--toggle')) return false;

		// If the toggle is already pressed.
		if (e.target.hasAttribute('data-toggled')) {
			e.target.removeAttribute('data-toggled');
			e.target.querySelector('.toggle__text').innerHTML = e.target.getAttribute('data-toggle-1');
		} else {
			e.target.setAttribute('data-toggled', '');
			e.target.querySelector('.toggle__text').innerHTML = e.target.getAttribute('data-toggle-2');
		}
	};
	
	
	return {
		init: __init
	};
	
})();



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



var play_pause = (function() {
	
	// The value that ensures we only ever add one event listener.
	var event_listener = false;
	
	
	// Finds all toggles and kickstarts the setup process.
	var __init = function(el) {
		
		// If nothing was sent across then use the default .toggle, note this is only to init stuff, 
		// toggles still need to use the .toggle class after this point.
		el = (el) ? el : '.toggle--play-pause';
		var toggles = document.querySelectorAll(el);
		if (toggles.length === 0) return;
		
		for (var i=0; i<toggles.length; i++) {
			__setup(toggles[i]);
		}
		
		// Add the event listener only once. 
		if (!event_listener) document.addEventListener('click', __action);
	};
	
	
	var __setup = function(el) {
		
		// If data-toggled attribute is found it means the default state is pressed so the aria-label should be set to pause.
		if (!el.hasAttribute('data-toggle-play') || !el.hasAttribute('data-toggle-pause')) return false;
		
		el.setAttribute('aria-label', (el.hasAttribute('data-toggled')) ? el.getAttribute('data-toggle-pause') : el.getAttribute('data-toggle-play'));

		el.querySelector('.button__text').innerHTML = (el.hasAttribute('data-toggled')) ? el.getAttribute('data-toggle-pause') : el.getAttribute('data-toggle-play');

// console.log( el.querySelector('.button__text').innerHTML = ; );
		// if (el.hasAttribute('data-toggled')) {
		// 	el.setAttribute('aria-label', el.getAttribute('data-toggle-pause'));
		// 	el.querySelector('.button__text').innerHTML = ;
		// } else {
		// 	el.setAttribute('aria-label', el.getAttribute('data-toggle-play'));
		// }
		
		// Add a class we attach the event listener to, we do this to force a requirement of the aria attributes.
		el.classList.add('js__toggle--play-pause');
	};
	
	
	// Called by the listener.
	var __action = function(e) {
		
		// We only ever want to add one event listener.
		event_listener = true;
		
		// Ignore anything that isn't a click on a toggle.
		if (!e.target.closest('.js__toggle--play-pause')) return false;
		// console.log( e.target.querySelector('.button__text').innerHTML );
		// If the toggle is already pressed.
		if (e.target.hasAttribute('data-toggled')) {
			e.target.removeAttribute('data-toggled');
			e.target.setAttribute('aria-label', e.target.getAttribute('data-toggle-play'));
			e.target.querySelector('.button__text').innerHTML = e.target.getAttribute('data-toggle-play');
		} else {
			e.target.setAttribute('data-toggled', '');
			e.target.setAttribute('aria-label', e.target.getAttribute('data-toggle-pause'));
			e.target.querySelector('.button__text').innerHTML = e.target.getAttribute('data-toggle-pause');
		}
	};
	
	
	return {
		init: __init
	};
	
})();



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

	var child_fields = (function() {
		
		var __init = function(el) {
			// Check for the items to check and the class to toggle.
			if (!el) el = {};
			if (el.selector == null) el.selector = '.form__child-fields';
			
			// Get all nested field groups.
			var els = document.querySelectorAll(el.selector);
			if (els.length === 0) return;
			
			for (el of els) {
				__prep({el: el});
			}
		};

		var __prep = function(el) {
			// If you call prep directly you need to provide an element to act upon.
			if (!el || !el.el) return;
			
			// Only prep if not previously prepped this element.
			if (!el.el.classList.contains('js__form__child-fields')) {
				el.el.classList.add('js__form__child-fields');
				el.el.addEventListener('change', __action);
				
				// Check for any pre-checked radio buttons or checkboxes so we can show the child group on inital load.
				try {
					__action( {target: el.el.querySelector('input[checked="checked"]')} );
				} catch (err) {}
			}
		};


		var __action = function(el) {

			var all = el.target.closest('.js__form__child-fields').querySelectorAll('input[aria-controls]');

			if (el.target.closest('.js__form__child-fields')) {
				
				var clear = el.target.closest('.js__form__child-fields').dataset.clear;
				if (!clear) clear = "true";

				for (var item of all) {
					if (!item.checked) {
						var children = document.querySelector('#'+item.getAttribute('aria-controls'));
						if (children.getAttribute('aria-expanded') == 'true') {
							document.querySelector('#'+item.getAttribute('aria-controls')).setAttribute('aria-expanded', 'false');
							if (clear === 'true') {
								__clear_hidden_fields(document.querySelector('#'+item.getAttribute('aria-controls')));
							}
						}
					}
				}

				if ((el.target.checked) && (el.target.hasAttribute('aria-controls'))) {
					document.querySelector('#'+el.target.getAttribute('aria-controls')).setAttribute('aria-expanded', 'true');
				}
			}
		};

		var __clear_hidden_fields = function(el) {
			if (!el) return;
			var fields = el.querySelectorAll('input:not([type=submit]), textarea, select');
			if (fields.length === 0) return;
			
			for (var field of fields) {
				if (field.type == 'radio' || field.type == 'checkbox') {
					field.checked = false;
				} else {
					field.value = '';
				}
			}
		};
		
		return {
			init: __init,
			prep: __prep
		};
		
	})();


	
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


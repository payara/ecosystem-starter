/*! Payara Pattern Library version: 0.75.0 */
// ONLY FOR DEMONSTRATION!


// UI path function example.

	var element_to_search = document.getElementById('ui-path-example');
	if (element_to_search) {
		document.getElementById('ui-path-data').value = ui_path( {el: element_to_search, tree: true});
	}



// Main menu
menu_wrap.init();


// Tab panel.
tab_group.init();

accordion.init();
notification_toggle.init();


// Start menus.
menu.init();

// Start the longer forms with navigation.
form_sections.init();


// Step through.
step_through.init();



// Form Steps.
form_steps.init();



// Start help toggle.
help_toggle.init();

// Multi-suggest field
multi_suggest.init();


// Large Radio Block
grid_to_column.init({
	selector: '.form__large-radios',
	toggleClass: 'form__large-radios--column'
});


switch_toggle.init();
// Nested linked fieldsets

fieldset_group.init({
	selector: '.fieldset-group',
	clear_hidden: true
});


// Start file upload preffitier.
file_upload_prettifier.init();

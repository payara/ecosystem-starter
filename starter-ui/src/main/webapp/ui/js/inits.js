/*! Payara Pattern Library version: 0.50.2 */
// ONLY FOR DEMONSTRATION!

// UI path function example.

	var element_to_search = document.getElementById('ui-path-example');
	if (element_to_search) {
		document.getElementById('ui-path-data').value = ui_path( {el: element_to_search, tree: true});
	}



// Main menu
menu_wrap.init();


// Start sidebar toggle.
sidebar_toggle.init();
sidebar_toggle_mob.init();
sidebar_height.init();

// Tab panel.
tab_panel.init();

// Tab panel.
tab_group.init();

accordion.init();
// Start copy box.
copy_box.init();

notification_toggle.init();


button_toggle.init();
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


// Play/pause toggles
play_pause.init();

switch_toggle.init();
// Nested linked fieldsets

child_fields.init();



// Nested linked fieldsets

fieldset_group.init({
	selector: '.fieldset-group',
	clear_hidden: true
});


// Start file upload preffitier.
file_upload_prettifier.init();

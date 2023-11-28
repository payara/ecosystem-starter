/*
 *
 * Copyright (c) 2023 Payara Foundation and/or its affiliates. All rights reserved.
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
const buildInputs = document.querySelectorAll('input[name="build"]');
buildInputs.forEach(input => {
    input.addEventListener('click', updateLabelsForBuildSystem);
});

function updateLabelsForBuildSystem() {
    const buildSystem = document.querySelector('input[name="build"]:checked').value;

    if (buildSystem === 'maven') {
        document.querySelector('label[for="groupId"]').textContent = 'Group ID:';
        document.querySelector('label[for="artifactId"]').textContent = 'Artifact ID:';
        showIncludeTests(); // Show and enable Include Tests checkbox
    } else if (buildSystem === 'gradle') {
        document.querySelector('label[for="groupId"]').textContent = 'Group:';
        document.querySelector('label[for="artifactId"]').textContent = 'Project Name:';
        hideAndDeselectIncludeTests(); // Hide and deselect Include Tests checkbox
    }
}

function showIncludeTests() {
    const includeTestsCheckbox = document.getElementById('includeTests');
    includeTestsCheckbox.style.display = 'block'; // Show the checkbox
    includeTestsCheckbox.disabled = false; // Enable the checkbox
}

function hideAndDeselectIncludeTests() {
    const includeTestsCheckbox = document.getElementById('includeTests');
    includeTestsCheckbox.style.display = 'none'; // Hide the checkbox
    includeTestsCheckbox.checked = false; // Deselect the checkbox
}

// Get all the input fields in the form
const formInputs = document.querySelectorAll('form input, form select');
const form = document.getElementById('appForm');

const inputToConfigurationMap = {
    'version': 'project-configuration',
    'javaVersion': 'jakarta-configuration',
    'profile': 'microprofile-configuration',
    'mpMetrics': 'payara-configuration'
};

formInputs.forEach((input, index) => {
    input.addEventListener('keydown', function (event) {
        if (event.keyCode === 13) {
            event.preventDefault(); // Prevent the default Enter key behavior
        }
    });
});

// Add event listener for form submission
form.addEventListener('submit', function (event) {
    event.preventDefault();

    document.getElementById('loadingBar').style.display = 'block';
    document.getElementById('loadingBar').scrollIntoView({ behavior: 'smooth' });

    const form = event.target;
    const formData = new FormData(form);
    const jsonObject = {};
    formInputs.forEach((input) => {
        const key = input.name;
        if (input.type === 'checkbox') {
            jsonObject[key] = input.checked.toString();
        } else if (input.type === 'radio') {
            if (!input.checked) {
                return;
            } else {
                jsonObject[key] = input.value;
            }
        } else {
            jsonObject[key] = input.value;
        }
    });

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jsonObject),
    };

    fetch('resources/starter', requestOptions)
            .then(response => {
                if (response.status === 200) {
                    return response.blob();
                } else {
                    alert('Error generating application. Please try again.');
                }
            })
            .then(blob => {
                document.getElementById('loadingBar').style.display = 'none';

                const filename = jsonObject['artifactId'] + '.zip';
                const url = window.URL.createObjectURL(blob);

                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                document.body.appendChild(a);
                a.click();

                window.URL.revokeObjectURL(url);
            })
            .catch(error => {
                document.getElementById('loadingBar').style.display = 'none';
                console.error('Error:', error);
                alert('An error occurred while generating the application.');
            });
});


// Define the order of Jakarta EE versions
const jakartaEEVersionOrder = ["10", "9.1", "9", "8"];
const jakartaVersions = {
    "10": "Jakarta EE 10",
    "9.1": "Jakarta EE 9.1",
    "9": "Jakarta EE 9",
    "8": "Jakarta EE 8"
};

const javaVersions = {
    "17": "Java SE 17",
    "11": "Java SE 11",
    "8": "Java SE 8"
};


// Function to populate a select element with options
function populateSelect(selectId, optionOrder, optionMap) {
    const select = document.getElementById(selectId);

    optionOrder.forEach(value => {
        const option = document.createElement('option');
        option.value = value;
        option.text = optionMap[value];
        select.appendChild(option);
    });
}

// Call the function to populate the select elements
populateSelect('jakartaEEVersion', jakartaEEVersionOrder, jakartaVersions);
populateSelect('javaVersion', Object.keys(javaVersions), javaVersions);

// Function to fetch Payara versions and populate the dropdown
function populatePayaraVersionsDropdown(jakartaEEVersion) {
    // Modify the URL to include the selected Jakarta EE version
    const url = `resources/version?jakartaEEVersion=${jakartaEEVersion}`;

    fetch(url)
            .then(response => {
                if (response.status === 200) {
                    return response.json();
                } else {
                    console.error('Error fetching Payara versions.');
                }
            })
            .then(versions => {
                const payaraVersion = document.getElementById('payaraVersion');
                payaraVersion.innerHTML = ''; // Clear existing options

                // Add an option for each version
                versions.forEach(version => {
                    const option = document.createElement('option');
                    option.value = version;
                    option.text = version;
                    payaraVersion.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
            });
}


const jakartaEEVersionSelect = document.getElementById('jakartaEEVersion');
const javaVersionSelect = document.getElementById('javaVersion');
const webProfileRadioButton = document.getElementById('web');
const coreProfileRadioButton = document.getElementById('core');

// Attach an event listener to the jakartaEEVersion select element
jakartaEEVersionSelect.addEventListener('change', function (event) {
    const selectedValue = event.target.value;
    const selectedVersion = parseFloat(selectedValue);

    // Call the function to populate the Payara version dropdown with the selected Jakarta EE version
    populatePayaraVersionsDropdown(selectedValue);

    // Get the "Java Version" options select element
    const javaVersionOptions = javaVersionSelect.querySelectorAll('option');
    // Clear existing options
    javaVersionOptions.forEach(option => {
        option.remove();
    });

    // Add options based on the selected Jakarta EE Version
    if (selectedValue === '8') {
        addJavaVersionOption('11', 'Java SE 11');
        addJavaVersionOption('8', 'Java SE 8');
    } else {
        addJavaVersionOption('17', 'Java SE 17');
        addJavaVersionOption('11', 'Java SE 11');
    }

    // Disable the Core Profile radio button for versions below 10
    if (selectedVersion < 10) {
        if(coreProfileRadioButton.checked) {
            webProfileRadioButton.checked = true;
        }
        coreProfileRadioButton.disabled = true;
        coreProfileRadioButton.checked = false;
    } else {
        coreProfileRadioButton.disabled = false;
    }
});

// Function to add an option to the "Java Version" select element
function addJavaVersionOption(value, text) {
    const option = document.createElement('option');
    option.value = value;
    option.text = text;
    javaVersionSelect.appendChild(option);
}

// Trigger the change event initially to set the default options
jakartaEEVersionSelect.dispatchEvent(new Event('change'));

// MP Select ALL
const selectAllMP = document.getElementById('selectAllMP');
const mpConfig = document.getElementById('mpConfig');
const mpOpenAPI = document.getElementById('mpOpenAPI');
const mpFaultTolerance = document.getElementById('mpFaultTolerance');
const mpMetrics = document.getElementById('mpMetrics');

selectAllMP.addEventListener('change', function () {
    const isChecked = selectAllMP.checked;
    mpConfig.checked = isChecked;
    mpOpenAPI.checked = isChecked;
    mpFaultTolerance.checked = isChecked;
    mpMetrics.checked = isChecked;
});

// Handle individual checkboxes
mpConfig.addEventListener('change', function () {
    selectAllMP.checked = mpConfig.checked && mpOpenAPI.checked && mpFaultTolerance.checked && mpMetrics.checked;
});

mpOpenAPI.addEventListener('change', function () {
    selectAllMP.checked = mpConfig.checked && mpOpenAPI.checked && mpFaultTolerance.checked && mpMetrics.checked;
});

mpFaultTolerance.addEventListener('change', function () {
    selectAllMP.checked = mpConfig.checked && mpOpenAPI.checked && mpFaultTolerance.checked && mpMetrics.checked;
});

mpMetrics.addEventListener('change', function () {
    selectAllMP.checked = mpConfig.checked && mpOpenAPI.checked && mpFaultTolerance.checked && mpMetrics.checked;
});

const platformRadioButton = document.getElementById('full');
const webRadioButton = document.getElementById('web');
const coreRadioButton = document.getElementById('core');

const noneAuthCheckbox = document.getElementById('noneAuth');
const fileRealmCheckbox = document.getElementById('formAuthFileRealm');
const databaseCheckbox = document.getElementById('formAuthDB');
const ldapCheckbox = document.getElementById('formAuthLDAP');

coreRadioButton.addEventListener('change', function(event) {
    if (event.target.checked) {
        // Disable Form Authentication checkboxes for the Core Profile
        fileRealmCheckbox.disabled = true;
        databaseCheckbox.disabled = true;
        ldapCheckbox.disabled = true;

        fileRealmCheckbox.checked = false;
        databaseCheckbox.checked = false;
        ldapCheckbox.checked = false;
        noneAuthCheckbox.checked = true;
    }
});

platformRadioButton.addEventListener('change', toggleFormAuthCheckboxes);
webRadioButton.addEventListener('change', toggleFormAuthCheckboxes);

// Function to toggle Form Authentication checkboxes for Platform and Web profiles
function toggleFormAuthCheckboxes(event) {
    if (platformRadioButton.checked || webRadioButton.checked) {
        // Enable Form Authentication checkboxes for Platform or Web Profile
        fileRealmCheckbox.disabled = false;
        databaseCheckbox.disabled = false;
        ldapCheckbox.disabled = false;
    }
}

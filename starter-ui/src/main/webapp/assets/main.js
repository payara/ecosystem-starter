
// JavaScript to handle accordion toggling
const accordionTitles = document.querySelectorAll('.accordion-title');
const prevButtons = document.querySelectorAll('.previous-button');
const nextButtons = document.querySelectorAll('.next-button');


accordionTitles.forEach(title => {
    title.addEventListener('click', () => {
        const contentId = title.getAttribute('data-toggle');
        const content = document.getElementById(contentId);

        if (content.style.maxHeight) {
            content.style.maxHeight = null;
            title.classList.remove('active');
        } else {
            closeAllAccordions();
            content.style.maxHeight = content.scrollHeight + 'px';
            title.classList.add('active');
        }

        updateAccordionIcons();
    });
});

function updateAccordionIcons() {
    const activeIndex = Array.from(accordionTitles).findIndex((title) =>
        title.classList.contains('active')
    );

    accordionTitles.forEach((title, index) => {
        const icon = title.querySelector('.bi');
        if (index === activeIndex) {
            icon.classList.remove('bi-circle', 'bi-circle-fill');
            icon.classList.add('bi-circle-half');
        } else if (index < activeIndex) {
            icon.classList.remove('bi-circle-half', 'bi-circle');
            icon.classList.add('bi-circle-fill');
        } else {
            icon.classList.remove('bi-circle-half', 'bi-circle-fill');
            icon.classList.add('bi-circle');
        }
    });
}

// Call the function to set initial icon states


prevButtons.forEach(button => {
    button.addEventListener('click', () => {
        const prevContentId = button.getAttribute('data-previous');
        const prevContent = document.getElementById(prevContentId);

        if (prevContent) {
            closeAllAccordions();
            prevContent.style.maxHeight = prevContent.scrollHeight + 'px';
            const prevTitle = prevContent.previousElementSibling;
            prevTitle.classList.add('active');
        }
    });
});

nextButtons.forEach(button => {
    button.addEventListener('click', () => {
        const nextContentId = button.getAttribute('data-next');
        const nextContent = document.getElementById(nextContentId);

        if (nextContent) {
            closeAllAccordions();
            nextContent.style.maxHeight = nextContent.scrollHeight + 'px';
            const nextTitle = nextContent.previousElementSibling;
            nextTitle.classList.add('active');
            
            updateLabelsForBuildSystem();
        }
    });
});

const buildInputs = document.querySelectorAll('input[name="build"]');
buildInputs.forEach(input => {
    input.addEventListener('click', updateLabelsForBuildSystem);
});

function updateLabelsForBuildSystem() {
    const buildSystem = document.querySelector('input[name="build"]:checked').value;

    if (buildSystem === 'maven') {
        document.querySelector('label[for="groupId"]').textContent = 'Group ID:';
        document.querySelector('label[for="artifactId"]').textContent = 'Artifact ID:';
    } else if (buildSystem === 'gradle') {
        document.querySelector('label[for="groupId"]').textContent = 'Group:';
        document.querySelector('label[for="artifactId"]').textContent = 'Project Name:';
    }
}

// Close all accordion sections
function closeAllAccordions() {
    accordionTitles.forEach(title => {
        const contentId = title.getAttribute('data-toggle');
        const content = document.getElementById(contentId);
        content.style.maxHeight = null;
        title.classList.remove('active');
    });
}

// Open the first accordion by default
const defaultAccordion = document.getElementById('project-details');
const defaultAccordionTitle = defaultAccordion.previousElementSibling;
defaultAccordion.style.maxHeight = defaultAccordion.scrollHeight + 'px';
defaultAccordionTitle.classList.add('active');
updateAccordionIcons();


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

            // If it's not the last input field, move focus to the next input
            if (index < formInputs.length - 1) {
                const configurationKey = input.name;
                
                // Check if the input's name exists in the map
                if (configurationKey in inputToConfigurationMap) {
                    const configurationAccordionTitle = document.querySelector(`[data-toggle="${inputToConfigurationMap[configurationKey]}"]`);
                    if (configurationAccordionTitle) {
                        closeAllAccordions();
                        const configurationContent = document.getElementById(inputToConfigurationMap[configurationKey]);
                        configurationContent.style.maxHeight = configurationContent.scrollHeight + 'px';
                        configurationAccordionTitle.classList.add('active');
                    }
                    updateAccordionIcons();
                }

                formInputs[index + 1].focus();
            } else {
                form.submit();
            }
        }
    });
});

// Add event listener for form submission
form.addEventListener('submit', function (event) {
    event.preventDefault();

    document.getElementById('loadingBar').style.display = 'block';
    document.getElementById('loadingBar').scrollIntoView({ behavior: 'smooth' });
    const loadingBarFill = document.getElementById('loadingBarFill');
    loadingBarFill.style.width = '0%';

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

// Attach an event listener to the jakartaEEVersion select element
jakartaEEVersionSelect.addEventListener('change', function (event) {
    const selectedValue = event.target.value;

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

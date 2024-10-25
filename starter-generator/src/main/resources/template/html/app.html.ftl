<#-- 
    Copyright 2024 the original author or authors from the Jeddict project (https://jeddict.github.io/).

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
-->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entity Management</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <link href="https://getbootstrap.com/docs/4.0/examples/dashboard/dashboard.css" rel="stylesheet">

    <!-- Bootstrap Icons CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.5.0/font/bootstrap-icons.min.css">

    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .content {
            margin-top: 56px; /* Height of the top navbar */
            flex-grow: 1;
            padding: 20px;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
        <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#">${model.getTitle()}</a>
        <input class="form-control form-control-dark w-100" type="text" placeholder="Search" aria-label="Search">
        <ul class="navbar-nav px-3">
            <li class="nav-item text-nowrap">
                <a class="nav-link" href="#">Sign out</a>
            </li>
        </ul>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <nav class="col-md-2 d-none d-md-block bg-light sidebar">
                <div class="sidebar-sticky">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link active" href="#" id="homeLink">
                                <i class="bi bi-house"></i>
                                Dashboard <span class="sr-only">(current)</span>
                            </a>
                        </li>
                    <#list model.entities as entity>
                        <li class="nav-item">
                            <a class="nav-link" href="#" id="${entity.getLowerCaseName()}Link">
                                <i class="bi bi-${entity.getIcon()}"></i>
                                ${entity.getTitleCaseName()}
                            </a>
                        </li>
                    </#list>
                    </ul>

                    <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
                        <span>Others</span>
                        <a class="d-flex align-items-center text-muted" href="#">
                            <i class="bi bi-plus-circle"></i>
                        </a>
                    </h6>
                    <ul class="nav flex-column mb-2">
                        <li class="nav-item">
                            <a class="nav-link" href="#" id="aboutUsLink">
                                <i class="bi bi-info-circle"></i>
                                About US
                            </a>
                        </li>
                    </ul>
                </div>
            </nav>

            <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4 content" id="content">
            </main>
        </div>
    </div>

    <!-- Custom script to load content -->
    <script>
    $(document).ready(function () {
            function loadContent(page) {
                $('#content').load(page);
            }
        <#list model.entities as entity>
            $('#${entity.getLowerCaseName()}Link').on('click', function () {
                loadContent('${entity.getLowerCaseName()}.html');
            });
        </#list>
            $('#homeLink').on('click', function () {
                loadContent('home.html');
            });
            $('#aboutUsLink').on('click', function () {
                loadContent('about-us.html');
            });
            // Load default page
            loadContent('home.html');
        });
    </script>
</body>
</html>

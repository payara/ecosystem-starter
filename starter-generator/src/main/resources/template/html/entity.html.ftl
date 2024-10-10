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
<div class="container mt-5">
    <h1 class="mb-4">${entity.getTitle()}</h1>
    <p>${entity.getDescription()}</p>
    <button class="btn btn-success mb-4" id="new${entityNameTitleCase}Button">Add ${entity.getTitle()}</button>
    
    <!-- ${entityNameTitleCase} Table -->
    <table id="${entityNameLowerCasePluralize}Table" class="table table-bordered">
        <thead>
            <tr>
                <#list entity.attributes as attribute>
                <th>${attribute.getStartCaseName()}</th>
                </#list>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
</div>

<!-- ${entityNameTitleCase} Modal -->
<div class="modal fade" id="${entityNameLowerCase}Modal" tabindex="-1" role="dialog" aria-labelledby="${entityNameLowerCase}ModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="${entityNameLowerCase}ModalLabel">Add ${entity.getTitle()}</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="${entityNameLowerCase}Form">
                    <#list entity.attributes as attribute>
                        <div class="form-group">
                            <#if attribute.relation??>
                                <#if attribute.multi>
                                <#else>
                            <label for="${attribute.name}Select">${attribute.getTitleCaseName()}:</label>
                            <select class="form-control" id="${attribute.name}Select" name="${attribute.name}">
                                <!-- ${attribute.getTitleCasePluralizeName()} will be populated here -->
                            </select>
                                </#if>
                            <#else>
                        <#if attribute.getType() == "LocalDate">
                            <label for="${attribute.name}">${attribute.getStartCaseName()}:</label>
                            <input type="date" class="form-control" id="${attribute.name}" name="${attribute.name}"<#if attribute.required> required</#if>>
                        <#elseif attribute.getType() == "LocalDateTime">
                            <label for="${attribute.name}">${attribute.getStartCaseName()}:</label>
                            <input type="datetime-local" class="form-control" id="${attribute.name}" name="${attribute.name}"<#if attribute.required> required</#if>>
                        <#elseif attribute.isNumber()>
                            <label for="${attribute.name}">${attribute.getStartCaseName()}:</label>
                            <input type="number" class="form-control" id="${attribute.name}" name="${attribute.name}"<#if attribute.required> required</#if>>
                        <#else>
                            <label for="${attribute.name}">${attribute.getStartCaseName()}:</label>
                            <input type="text" class="form-control" id="${attribute.name}" name="${attribute.name}"<#if attribute.required> required</#if>>
                        </#if>
                            </#if>
                        </div>
                    </#list>
                    <button type="submit" class="btn btn-primary" id="saveButton">Save</button>
                    <button type="button" class="btn btn-primary" id="updateButton">Update</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        // Fetch all ${entityNameLowerCase}s and display them
        function load${entityNameTitleCasePluralize}() {
            $.ajax({
                url: 'resources/api/${entityNameLowerCase}',
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    var tableBody = $('#${entityNameLowerCasePluralize}Table tbody');
                    tableBody.empty();
                    data.forEach(function (${entityNameLowerCase}) {
                        var row = '<tr>' +
                        <#list entity.attributes as attribute>
                            <#if attribute.relation??>
                                <#if attribute.multi>
                                <#else>
                            '<td>' + (${entityNameLowerCase}?.${attribute.name}?.${attribute.relation.getDisplayName()} || '') + '</td>' +
                                </#if>
                            <#else>
                            '<td>' + ${entityNameLowerCase}.${attribute.name} + '</td>' +
                            </#if>
                        </#list>
                            '<td class="table-buttons">' +
                            '<button class="btn btn-sm btn-primary mr-1" onclick="edit${entityNameTitleCase}(\'' + ${entityNameLowerCase}.${entity.getPrimaryKeyName()} + '\')">Edit</button>' +
                            '<button class="btn btn-sm btn-danger" onclick="delete${entityNameTitleCase}(\'' + ${entityNameLowerCase}.${entity.getPrimaryKeyName()} + '\')">Delete</button>' +
                            '</td>' +
                            '</tr>';
                        tableBody.append(row);
                    });
                }
            });
        }

        load${entityNameTitleCasePluralize}();

    <#list entity.attributes as attribute>
        <#if attribute.relation??>
            <#if attribute.multi>
            <#else>
        function load${attribute.getTitleCasePluralizeName()}() {
            $.ajax({
                url: 'resources/api/${attribute.name}',
                method: 'GET',
                contentType: 'application/json',
                success: function (data) {
                    var ${attribute.name}Select = $('#${attribute.name}Select');
                    ${attribute.name}Select.empty();
                    data.forEach(function (${attribute.name}) {
                        var option = '<option value="' + ${attribute.name}.${attribute.relation.getPrimaryKeyName()} + '">' + ${attribute.name}.${attribute.relation.getDisplayName()} + '</option>';
                        ${attribute.name}Select.append(option);
                    });
                }
            });
        }
        load${attribute.getTitleCasePluralizeName()}();
            </#if>
        </#if>
    </#list>

        // Open modal for new ${entityNameLowerCase}
        $('#new${entityNameTitleCase}Button').on('click', function () {
            $('#${entityNameLowerCase}Form')[0].reset();
            $('#${entity.getPrimaryKeyName()}').closest('.form-group').hide();
            $('#${entityNameLowerCase}ModalLabel').text('Add ${entity.getTitle()}');
            $('#saveButton').show();
            $('#updateButton').hide();
            $('#${entityNameLowerCase}Modal').modal('show');
    <#list entity.attributes as attribute>
        <#if attribute.relation??>
            <#if attribute.multi>
            <#else>
            load${attribute.getTitleCasePluralizeName()}();
            </#if>
        </#if>
    </#list>
        });

        // Create new ${entityNameLowerCase}
        $('#${entityNameLowerCase}Form').on('submit', function (e) {
            e.preventDefault();
            var ${entityNameLowerCase} = {
                <#list entity.attributes as attribute>
                    <#if attribute.relation??>
                        <#if attribute.multi>
                        <#else>
                    ${attribute.name}: {
                        ${attribute.relation.getPrimaryKeyName()}: $('#${attribute.name}Select').val()
                    },
                        </#if>
                    <#else>
                    <#if entity.getPrimaryKeyName() != attribute.name>
                        ${attribute.name}: $('#${attribute.name}').val(),
                    </#if>
                    </#if>
                </#list>
            };
            $.ajax({
                url: 'resources/api/${entityNameLowerCase}',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(${entityNameLowerCase}),
                success: function () {
                    load${entityNameTitleCasePluralize}();
                    $('#${entityNameLowerCase}Modal').modal('hide');
                    $('#${entityNameLowerCase}Form')[0].reset();
                }
            });
        });

        // Update existing ${entityNameLowerCase}
        $('#updateButton').on('click', function () {
            var ${entityNameLowerCase} = {
                <#list entity.attributes as attribute>
                    <#if attribute.relation??>
                        <#if attribute.multi>
                        <#else>
                    ${attribute.name}: {
                        ${attribute.relation.getPrimaryKeyName()}: $('#${attribute.name}Select').val()
                    },
                        </#if>
                    <#else>
                    ${attribute.name}: $('#${attribute.name}').val(),
                    </#if>
                </#list>
            };
            $.ajax({
                url: 'resources/api/${entityNameLowerCase}',
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(${entityNameLowerCase}),
                success: function () {
                    load${entityNameTitleCasePluralize}();
                    $('#${entityNameLowerCase}Modal').modal('hide');
                    $('#${entityNameLowerCase}Form')[0].reset();
                }
            });
        });

        // Edit ${entityNameLowerCase} (populate form)
        window.edit${entityNameTitleCase} = function (${entity.getPrimaryKeyName()}) {
            $.ajax({
                url: 'resources/api/${entityNameLowerCase}/' + ${entity.getPrimaryKeyName()},
                method: 'GET',
                contentType: 'application/json',
                success: function (${entityNameLowerCase}) {
                    <#list entity.attributes as attribute>
                        <#if attribute.relation??>
                            <#if attribute.multi>
                            <#else>
                    $('#${attribute.name}Select').val(${entityNameLowerCase}.${attribute.name}.${attribute.relation.getPrimaryKeyName()});
                            </#if>
                        <#else>
                    $('#${attribute.name}').val(${entityNameLowerCase}.${attribute.name})<#if attribute.isPrimaryKey()>.prop('disabled', true).closest('.form-group').show()</#if>;
                        </#if>
                    </#list>
                    $('#${entityNameLowerCase}ModalLabel').text('Edit ${entity.getTitle()}');
                    $('#saveButton').hide();
                    $('#updateButton').show();
                    $('#${entityNameLowerCase}Modal').modal('show');
                }
            });
        };

        // Delete ${entityNameLowerCase}
        window.delete${entityNameTitleCase} = function (${entity.getPrimaryKeyName()}) {
            $.ajax({
                url: 'resources/api/${entityNameLowerCase}/' + ${entity.getPrimaryKeyName()},
                method: 'DELETE',
                success: function () {
                    load${entityNameTitleCasePluralize}();
                }
            });
        };
    });
</script>

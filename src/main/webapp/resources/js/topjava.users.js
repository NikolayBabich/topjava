let ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "admin/users/",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        }),
        updateFunc: updateTable
    };
    makeEditable();
});

function toggleEnabled(checkBox, id) {
    const enabled = checkBox.prop("checked");
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + id,
        data: {enabled: enabled}
    }).done(function () {
        $("tr#" + id).attr("data-userEnabled", enabled);
        const text = enabled ? "enabled " : "disabled ";
        successNoty("User " + text);
    }).fail(function () {
        checkBox.prop("checked", !enabled);
    });
}
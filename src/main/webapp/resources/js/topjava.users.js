var ctx;

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
        })
    };
    makeEditable();
    $("tr").each(function () {
        const id = $(this).attr("id");
        if (typeof id !== typeof undefined && id !== false) {
            toggleEnabled(id);
        }
    });
});

function toggleEnabled(id) {
    const tr = $("tr#" + id);
    const enabled = tr.find(".enabled").prop("checked");
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + id,
        data: "enabled=" + enabled
    }).done(function () {
        tr.attr("data-userEnabled", enabled);
    });
}
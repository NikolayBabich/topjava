let ctx, mealAjaxUrl = "profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: mealAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(mealAjaxUrl, updateTableByData);
}

$(function () {
    ctx = {
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type) {
                        if (type === "display") {
                            return data.replace('T', ' ').substring(0, 16);
                        }
                        return data;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data) {
                $(row).attr("data-mealExcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable
    };
    makeEditable();
    dateTimePickerConfig();
});

// function dateTimePickerConfig() {
//     $("#dateTime").datetimepicker({
//         format: 'Y-m-d H:i'
//     })
//
//     $("#startDate, #endDate").datetimepicker({
//         timepicker: false,
//         format: 'Y-m-d'
//     })
//
//     $("#startTime, #endTime").datetimepicker({
//         datepicker: false,
//         format: 'H:i'
//     })
// }

function dateTimePickerConfig() {
    $("#dateTime").datetimepicker({
        format: 'Y-m-d H:i'
    })

    const startDate = $('#startDate');
    const endDate = $('#endDate');
    startDate.datetimepicker({
        format: 'Y-m-d',
        onShow: function () {
            this.setOptions({
                maxDate: endDate.val() ? endDate.val() : false
            })
        },
        timepicker: false
    });
    endDate.datetimepicker({
        format: 'Y-m-d',
        onShow: function () {
            this.setOptions({
                minDate: startDate.val() ? startDate.val() : false
            })
        },
        timepicker: false
    });

    const startTime = $('#startTime');
    const endTime = $('#endTime');
    startTime.datetimepicker({
        format: 'H:i',
        onShow: function () {
            this.setOptions({
                maxTime: endTime.val() ? endTime.val() : false
            })
        },
        datepicker: false
    });
    endTime.datetimepicker({
        format: 'H:i',
        onShow: function () {
            this.setOptions({
                minTime: startTime.val() ? startTime.val() : false
            })
        },
        datepicker: false
    });
}
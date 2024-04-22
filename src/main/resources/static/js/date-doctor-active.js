function initDatePicker (dates) {

    $("#datepicker").datepicker({
        beforeShowDay: function (date) {
            return [dates.includes(date.toLocaleDateString())];
        },
        onSelect: function (date) {
            $('#datepicker_value').val(date);
        }
    });
    $("#datepicker").select();
    $("#datepicker_value").val(new Date().toLocaleDateString());
    $("#datepicker").datepicker("setDate", $('#datepicker_value').val());
}

async function getDates(){
    let doctorId = document.getElementById("doctorId").value;
    return fetch(`/api/v1/schedule/doctor/${doctorId}/active`);
}

getDates().then(response => response.json())
          .then(dates => initDatePicker(dates));
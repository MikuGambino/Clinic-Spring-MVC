(function ($) {
    $.extend($.datepicker, {

        // Reference the orignal function so we can override it and call it later
        _inlineDatepicker2: $.datepicker._inlineDatepicker,

        // Override the _inlineDatepicker method
        _inlineDatepicker: function (target, inst) {

            // Call the original
            this._inlineDatepicker2(target, inst);

            var beforeShow = $.datepicker._get(inst, 'beforeShow');

            if (beforeShow) {
                beforeShow.apply(target, [target, inst]);
            }
        }
    });
}(jQuery));

function getSchedule(date) {
   fetch(`/api/v1/schedule/${date}/doctor/${document.getElementById("doctorId").value}`)
       .then(response => response.json())
       .then(result => {
          if(document.getElementById("messageAdd") != null) {
             document.getElementById("messageAdd").setAttribute("hidden", "hidden");
          }
          let times = "";
          for (let i = 0; i < result.length; i++) {
             let startWork = formatTime(new Date(result[i].startWork));
             let endWork = formatTime(new Date(result[i].endWork));
             let time = "";
             time += `<input type="radio" name="scheduleId"
                       id='${result[i].id}-time' value='${result[i].id}' required>`
                       time += `<label for='${result[i].id}-time'>${startWork} - ${endWork}</label>`
             times += time;
          }
          document.querySelector(".times").innerHTML = times;
       });
}

function initDatePicker (dates) {
    jsDates = [];
    dates.forEach((dateString) => {
        jsDates.push(new Date(dateString).toDateString());
    });
    $("#datepicker").datepicker({
        dateFormat: 'yy-mm-dd',
        beforeShowDay: function (date) {
            return [jsDates.includes(date.toDateString())];
        },
        onSelect: function (date) {
            var dateObject = $('#datepicker').datepicker().val();
            getSchedule(dateObject);
        }
    });
    $("#datepicker").select();
    $("#datepicker").datepicker("setDate", new Date());
}

async function getDates(){
    let doctorId = document.getElementById("doctorId").value;
    return fetch(`/api/v1/schedule/${doctorId}/dates`);
}

getDates().then(response => response.json())
    .then(dates => initDatePicker(dates));

function formatTime(date) {
    var res = [date.getHours(),date.getMinutes()].map(function (x) {
      return x < 10 ? "0" + x : x
    }).join(":");

    return res;
}

function validateForm() {
    let checkboxes = document.querySelectorAll('input[type=checkbox]:checked');
    console.log(checkboxes);
    if(checkboxes.length == 0) {
        document.querySelector("#chooseTreatment").removeAttribute("hidden");
        return false;
    } else {
        document.querySelector("#chooseTreatment").addAttribute("hidden");
        return true;
    }
}
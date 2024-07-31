document.addEventListener("DOMContentLoaded", function() {
   document.getElementById("addTime").addEventListener("click", function (event) {
      event.preventDefault();
      let start = document.getElementById("timepicker-start-val").value;
      let end = document.getElementById("timepicker-end-val").value;
      if(compareTime(start, end)){
         addSchedule(start, end);
      }
      else{
         displayMessage("Время начала должно быть раньше время конца!", "red")
      }
   });

function addSchedule(start, end) {
      let date = $('#datepicker').datepicker().val();

      let schedule = {
         doctorId: document.getElementById("doctorId").value,
         startWork: date + "T" + start,
         endWork: date + "T" + end,
      };

      fetch("/api/v1/schedule", {
         method: "post",
         headers: {
            'Content-Type': "application/json"
         },
         body: JSON.stringify(schedule)
      })
       .then(response => {
          if(response.ok){
             getSchedule(date)
          }
          else {
             displayMessage("Этот промежуток времени уже занят!", "red");
          }
       })
   }
});


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
             time += `<input type="button" class='button' id='${result[i].id}'
                        onclick="deleteAppointmentTime(this.id, this.value, this)"
                        value="${startWork} - ${endWork}">`
             times += time;
          }
          document.querySelector(".times").innerHTML = times;
       });
}

function deleteAppointmentTime(id, date, element) {
   if(confirm(`Вы уверены что хотите удалить эту дату: ${date}?`)) {
      fetch("/api/v1/schedule/" + id, {
         method: "delete"
      })
       .then(response => {
          if(response.ok) {
            element.remove(element);
            displayMessage("Успешно удалено!", "green");
          }
       });
   }
}

function displayMessage(text, color){
   let message = document.getElementById("messageAdd");
   message.innerText = text;
   message.setAttribute("class", color);
   message.removeAttribute("hidden");
}

$(function () {
   $("#datepicker").datepicker("setDate", new Date());
   $("#datepicker").datepicker({
      dateFormat: 'yy-mm-dd',
      beforeShowDay: function (date) {
         let dayOfWeek = date.getDay();
         if (dayOfWeek === 0) {
            return [false];
         } else {
            return [true];
         }
      },
      onSelect: function () {
        var dateObject = $('#datepicker').datepicker().val();
        getSchedule(dateObject);
      }
   });

   $('#timepicker-start').timepicker({
      timeFormat: 'H:mm',
      interval: 5,
      minTime: '8',
      maxTime: '9:00pm',
      startTime: '8:00am',
      dynamic: false,
      dropdown: true,
      scrollbar: true,
      change: function (time) {
         $("#timepicker-start-val").val(formatTime(time));
      }
   });
   $('#timepicker-end').timepicker({
      timeFormat: 'H:mm',
      interval: 5,
      minTime: '8',
      maxTime: '9:00pm',
      startTime: '8:00am',
      dynamic: false,
      dropdown: true,
      scrollbar: true,
      change: function (time) {
         $("#timepicker-end-val").val(formatTime(time));
      }
   });

   getSchedule($('#datepicker').datepicker().val());
});

function formatTime(date) {
    var res = [date.getHours(),date.getMinutes()].map(function (x) {
      return x < 10 ? "0" + x : x
    }).join(":");

    return res;
}

function compareTime(t1, t2) {
    let t1_split = t1.split(":");
    t1_h = t1_split[0];
    t1_m = t1_split[1];

    let t2_split = t2.split(":");
    t2_h = t2_split[0];
    t2_m = t2_split[1];

    if(t1_h < t2_h || (t1_h == t2_h && t1_m < t2_m)) {
        return true;
    } else {
        return false;
    }
}
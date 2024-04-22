let lastSpecId = 0;

function showServices(speId){
    document.querySelector(".services").removeAttribute("hidden");
    document.querySelectorAll(".serviceSpec-" + lastSpecId).forEach(value => {
       value.setAttribute("hidden", "hidden");
    });
    document.querySelectorAll(".serviceSpec-" + speId).forEach(value => {
       value.removeAttribute("hidden");
    });
    lastSpecId = speId;
}
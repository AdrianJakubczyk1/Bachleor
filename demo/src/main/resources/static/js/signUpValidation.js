document.addEventListener("DOMContentLoaded", function() {
    const usernameInput = document.getElementById("signupUsername");
    const emailInput = document.getElementById("signupEmail");

    if(usernameInput){
        usernameInput.addEventListener("blur", function() {
            let username = this.value;
            if(username.trim() === "") return;
            fetch("/checkUsername?username=" + encodeURIComponent(username))
                .then(response => response.json())
                .then(data => {
                    const feedback = document.getElementById("usernameFeedback");
                    if(data.exists) {
                        feedback.style.display = "block";
                    } else {
                        feedback.style.display = "none";
                    }
                })
                .catch(error => {
                    console.error("Error checking username:", error);
                });
        });
    }

    if(emailInput){
        emailInput.addEventListener("blur", function() {
            let email = this.value;
            if(email.trim() === "") return;
            fetch("/checkEmail?email=" + encodeURIComponent(email))
                .then(response => response.json())
                .then(data => {
                    const feedback = document.getElementById("emailFeedback");
                    if(data.exists) {
                        feedback.style.display = "block";
                    } else {
                        feedback.style.display = "none";
                    }
                })
                .catch(error => {
                    console.error("Error checking email:", error);
                });
        });
    }
});
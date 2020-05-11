<template>
    <!-- Page Heading -->
    <div class="row" id="main" >
        <div class="col-sm-12 col-md-12 well" id="content">
            <h3>Modify your account credentials.</h3>
            <h4>Username or password can be modified, fill the form below</h4>
            <hr/>
           <form action="javascript:void(0);">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" required class="form-control" id="username" v-bind:value="username">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" required class="form-control" id="password" placeholder="New password">
                </div>
                <div class="form-group">
                    <div class="alert alert-danger" role="alert" id="update_account_error" style="display:none;width:100%">
                        Something went wrong! check username or password provided.
                    </div>
                </div>
                <div class="form-group">
                    <div class="alert alert-success" role="alert" id="update_account_ok" style="display:none;width:100%">
                    Credentials modified!
                    </div>
                </div>
              <button type="submit" class="btn btn-warning" v-on:click="modifyAccount()">Modify</button>
            </form>
            
        </div>
    </div>
</template>



<script>
    module.exports = {
      props:["username","relativeRoute"],
      methods: {
        modifyAccount: function() {
            $('#update_account_ok').css("display", "none");
             $('#update_account_error').css("display", "none");
            var xhr = new XMLHttpRequest();
            var url = "/helio-api/account";
            xhr.open("POST", url, true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Accept", "application/json");
            xhr.onreadystatechange = function (data) {
              if (xhr.readyState === 4) {
                  if(xhr.status==200 || xhr.status==201){
                    $('#update_account_ok').css("display", "inline-block");
                  }else if(xhr.status==401){
                    $('#update_account_error').css("display", "inline-block");
                  }
                  
               }
            };
            var username = $("#username").val();
            var password = $("#password").val();
            var payload = "{\"username\":\""+username+"\", \"password\":\""+password+"\"}";
            xhr.send(payload);
        }
      }
    }
   
   
</script>
<template>
    <!-- Page Heading -->
    <div class="row" id="main" >
        <div class="col-sm-12 col-md-12 well" id="content">
            <h1>Welcome {{username}}!</h1>
        </div>
    </div>
</template>



<script>
module.exports = {
  props:["username","relativeRoute"]
}
</script>
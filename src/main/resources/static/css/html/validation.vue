<template>
 <div class="row" id="main">
  <div class="col-sm-12 col-md-12 well" id="content">
    <h3> Validate data with SHACL shapes, supported formats Turtle(.ttl), N-Triples(.nt), RDF/XML(.rdf), N-Quads(.nq), and JSON-LD(.jsonld).</h3>
    <hr/>
    <form> 
	  <div class="form-group">
	    <label class="custom-file-label" for="file">Choose shape file...</label>
	    <input type="file" class="custom-file-input" ref="file" id="file" required v-on:change="handleFileUpload()">
	  </div>
        <div class="form-group">
            <div class="alert alert-danger" role="alert" id="alert-error-validation-format" style="display:none;width:100%;margin-top:10px;">
                        File format not supported!
            </div>
        </div>
        <div class="form-group">
            <div class="alert alert-success" role="alert" id="alert-ok-validation" style="display:none;width:100%;margin-top:10px;">
                    Validation report generated!
            </div>
        </div>
        <div class="form-group">
            <div class="alert alert-danger" role="alert" id="alert-error-validation" style="display:none;width:100%;margin-top:10px;">
                    Error during validation, check the provided shapes!
            </div>
        </div>
        <div class=form-group">
            <button id="load"  class="btn btn-info mb-2 btn-custom" v-on:click="submitShapes()" >Validate!</button>
        </div>
	</form>
   

    
   </div>
</div>
</template>



<script>
module.exports = {
  props:["username"],
  data(){
      return {
        file: '',
        format : '',
        name : '',
        correct : false
      }
    },
  methods: {
        handleFileUpload: function() {
            $('#alert-error-validation-format').css("display", "none");
            $('#alert-ok-validation').css("display", "none");
            $('#alert-error-validation').css("display", "none");
            this.file = this.$refs.file.files[0];
            this.name = this.file.name;
            this.format = this.file.name;
            if(this.format.endsWith(".ttl")){
                this.format="Turtle";
            }else if(this.format.endsWith(".nt")){
                this.format="N-Triples";
            }else if(this.format.endsWith(".nq")){
                this.format="N-Quads";
            }else if(this.format.endsWith(".rdf")){
                this.format="RDF/XML";
            }else if(this.format.endsWith(".jsonld")){
                this.format="JSON-LD";
            } else {
                this.format="";
                $('#alert-error-validation-format').css("display", "inline-block");
            }
            this.correct = this.format.length>0 && this.file.size>0;
            
           
        },
        submitShapes: function() {
            $('#alert-ok-validation').css("display", "none");
            $('#alert-error-validation').css("display", "none");
            var content = "";
            var reader = new FileReader();
            reader.onload = function(event) {
                content = event.target.result;
            };

            if(this.correct)
                reader.readAsText(this.file);


            var xhr = new XMLHttpRequest();
            xhr.open("POST", "/helio-api/shapes/validate", true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Accept", "text/turtle");
            xhr.onreadystatechange = function (data) {
              if (xhr.readyState === 4) {
                  if(xhr.status==200){
                     $('#alert-ok-validation').css("display", "inline-block");
                     //download
                     download("validation-report.ttl", xhr.responseText);
                  }else if(xhr.status!=200){
                     $('#alert-error-validation').css("display", "inline-block");
                  }
                  
               }
            };


            var payload = JSON.parse("{\"name\":\""+this.name+"\",\"content\":\"\", \"format\":\""+this.format+"\"}");
            payload["content"] = content;
            var data = JSON.stringify(payload);
            console.log(data);
            xhr.send(data);
                
        }
        
    }
}
</script>
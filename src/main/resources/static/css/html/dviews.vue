<template>
	<div class="row" id="main">
		<div class="col-sm-12 col-md-12 well" id="content">  
			<h3>Add resource for dynamic views</h3>
			<hr/>
			<form action="javascript:void(0);">
				<h4>Include dynamic views for resources</h4>
                <div class="form-group">
                    <label for="resource">Resource URI:</label>
                    <input type="text" required class="form-control" id="resource">
                </div>
                <div class="form-group">
                    <label for="existingTemplates">Template:</label>
                    <select class="form-control" id="existingTemplates">
                        <option v-for="template in views.templates" v-bind:value="template" v-html="template"></option>
                    </select>
                </div>
                <div class="form-group">
                  	<label for="isRegex">Is resource URI a regex:</label>
                    <select class="custom-select form-control" id="isRegex" required>
				      <option value="false">No</option>
                      <option value="true">Yes</option>
				    </select>
                </div>
                <div class="form-group">
                    <label for="SPARQLDialog">Use a SPARQL query to feed the view:</label>
                    <input type="checkbox" name="sparql_query_dialog_to_show" value="False" v-on:click="showSPARQLDialog = !showSPARQLDialog">
                    <textarea class="form-control" id="SPARQLDialog" rows="15" v-show="showSPARQLDialog"></textarea> 
                </div>
                <div class="form-group">
                    <div class="alert alert-success" role="alert" id="dviews-resource-ok" style="display:none;width:100%">
                    New view included !
                    </div>
                </div>
				<button type="submit" class="btn btn-info" v-on:click="addResourceTemplate()">Add resource</button>
            </form>
            <hr/>
            <form action="javascript:void(0);">
	            <h4>Include dynamic views from csv, file must contain the same first 3 columns as the table below and ';' must be their separator. SPARQL queries can not be used to feed the view using the import.</h4>
	            <div class="form-group">
	                <input type="file" class="custom-file-input" ref="file_resources" id="file-resources" required v-on:change="handleFileUpload()">
	            </div>
                <div class="form-group">
                    <div class="alert alert-success" role="alert" id="upload-csv-format-ok" style="display:none;width:100%">
                    Dynamic views imported!
                    </div>
                </div>
                <div class="form-group">
                    <div class="alert alert-danger" role="alert" id="upload-csv-format-error" style="display:none;width:100%">
                    Only CSV accepted!
                    </div>
                </div>
                <div class="form-group">
                    <div class="alert alert-danger" role="alert" id="upload-csv-error" style="display:none;width:100%">
                    An error ocurred, check the provided CSV!
                    </div>
                </div>
                <div class="form-group">
                    <div class="alert alert-warning" role="alert" id="upload-csv-empty-error" style="display:none;width:100%">
                      Provided file is empty
                    </div>
                </div>
              <button type="submit" class="btn btn-info" v-on:click="uploadResources()">Import resources</button>
            </form>
            <hr/> 
 		
			<br/>  

	        <div>
	        	<div class="form-group">
                    <div class="alert alert-success" role="alert" id="dviews-resource-deleted" style="display:none;width:100%">
                    Resource deleted correctly!
                    </div>
                </div>
	        	<b-table :items="views.views" :fields="fields" :striped="striped" :bordered="bordered" :borderless="borderless"  :outlined="outlined" :small="small" :hover="hover"  :dark="dark"  :fixed="fixed" :foot-clone="footClone" :no-border-collapse="noCollapse" :head-variant="headVariant" :table-variant="tableVariant">
	        		<template v-slot:cell(-)="data">
				        <button :id="data.item.resource" type="button" class="btn btn-danger" v-on:click="deleteView(data.item.resource)"><i class="fas fa-fw fa-trash-alt"></i></button>
				      </template>
	        	</b-table>
	        </div>     
	    </div>
	</div>
    
</template>



<script>
module.exports = {
	props:["views"],
 	methods: {
        deleteView: function(idToRemove) {
         	$('#dviews-resource-deleted').css("display", "none");
        	var xhr = new XMLHttpRequest();
            var url = "/helio-api/views?viewId="+idToRemove;
            console.log(">"+url);
            xhr.open("DELETE", url, true);
            xhr.onreadystatechange = function (data) {
              if (xhr.readyState === 4) {
                  if(xhr.status==200){
                    $('#dviews-resource-deleted').css("display", "inline-block");
                  }
               }
            };
            xhr.send(null);
        },
        addResourceTemplate : function() {
         	var xhr = new XMLHttpRequest();
            xhr.open("POST", "/helio-api/views", true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Accept", "text/turtle");
            xhr.onreadystatechange = function (data) {
              if (xhr.readyState === 4) {
                  if(xhr.status==200 || xhr.status==201){
                    $('#dviews-resource-ok').css("display", "inline-block");
                  }else if(xhr.status!=200){
                     $('#alert-error-validation').css("display", "inline-block");
                  }
                  
               }
            };
            var resource = $("#resource").val().trim();
            var temp = $('#existingTemplates').val().trim();
            var isRegex = $("#isRegex").val().trim();
            var sparql = $("#SPARQLDialog").val().trim();
            var payload = JSON.parse("{\"resource\":\""+resource+"\", \"template\":\""+temp+"\",\"isRegex\":"+isRegex+",\"sparqlQuery\":\"\"}");
            if(sparql.length>0)
                payload["sparqlQuery"]=sparql;
            var data = JSON.stringify(payload);
            console.log(data);
            xhr.send(data);
        },
        handleFileUpload: function() {
        	$('#upload-csv-error').css("display", "none");
           	$('#upload-csv-format-error').css("display", "none");
        	$('#upload-csv-empty-error').css("display", "none");
        	$('#upload-csv-format-ok').css("display", "none");
            this.file_resources = this.$refs.file_resources.files[0];
            if(this.file_resources.name.endsWith(".csv")){
        		if(this.file_resources.size>0){
        			this.correct = true;
            	}else{
        			$('#upload-csv-empty-error').css("display", "inline-block");
        		}
        	}else{
        		$('#upload-csv-format-error').css("display", "inline-block");
        		
        	} 
        },
        uploadResources : function() {
        	$('#upload-csv-error').css("display", "none");
        	$('#upload-csv-format-ok').css("display", "none");
        	if(this.correct){
        			var content = "";
		            var reader = new FileReader();
		            reader.onload = function(event) {
		                content = event.target.result;
			            var lines = content.split("\n");
			            var i;
						for (i = 1; i < lines.length; i++) {
							var xhr = new XMLHttpRequest();
				            xhr.open("POST", "/helio-api/views", true);
				            xhr.setRequestHeader("Content-Type", "application/json");
				            xhr.setRequestHeader("Accept", "text/turtle");
				            xhr.onreadystatechange = function (data) {
				              if (xhr.readyState === 4) {
				                  if(xhr.status==200 || xhr.status==201){
				                     $('#upload-csv-format-ok').css("display", "inline-block");
				                  }else if(xhr.status!=200){
				                     $('#upload-csv-error').css("display", "inline-block");
				                  }
				                  
				               }
				            };
				            
							elements = lines[i].split(";");			
							var payload = "{\"resource\":\""+elements[0]+"\", \"template\":\""+elements[1]+"\",\"isRegex\":"+elements[2].toLowerCase()+"}";
				
							xhr.send(payload);
						}

		            };
		            reader.readAsText(this.file_resources);
		            
		            /*
		            
		            payload["content"] = content;
		            var data = JSON.stringify(payload);
		            console.log(data);
		            xhr.send(data);*/
		    }else{
		    	$('#upload-csv-error').css("display", "inline-block");
		    }	
        }
  	},
  	data() {
      	return {
	        fields: [
	          {
	            key: 'resource',
	            sortable: true
	          },{
	            key: 'template',
	            sortable: true
	          },{
	            key: 'is_regex',
	            sortable: true,
	          },{
                key: 'sparql_query',
                sortable: false,
              },{
	            key: '-',
	            sortable: false,
	          }
	        ],
	        striped: true,
	        bordered: true,
	        borderless: false,
	        outlined: false,
	        small: false,
	        hover: true,
	        dark: false,
	        fixed: false,
	        footClone: false,
	        headVariant: "dark",
	        tableVariant: '',
	        noCollapse: false,
	        file_resources: '',
	        correct : false,
            showSPARQLDialog : false
    	}
    }
}
</script>



<style>

</style>
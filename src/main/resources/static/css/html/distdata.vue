<template>
	<div class="row" id="main">
		<div class="col-sm-12 col-md-12 well" id="content">  
			<h3>Add distributed data endpoints</h3>
			<hr/>
			<form action="javascript:void(0);">
				<h4>Distributed data</h4>
                <div class="form-group">
                    <label for="endpoint">SPARQL endpoint:</label>
                    <input type="text" required class="form-control" id="endpoint">
                </div>
				<button type="submit" class="btn btn-info" v-on:click="addSPARQLEndpoint()">Add resource</button>
            </form>
            <hr/> 
			<br/>  
	        <div>
	        	<div class="form-group">
                    <div class="alert alert-success" role="alert" id="distData-endpoint-deleted" style="display:none;width:100%">
                    Endpoint deleted correctly!
                    </div>
            </div>
	        	<b-table :items="endpoints" :fields="fields" :striped="striped" :bordered="bordered" :borderless="borderless"  :outlined="outlined" :small="small" :hover="hover"  :dark="dark"  :fixed="fixed" :foot-clone="footClone" :no-border-collapse="noCollapse" :head-variant="headVariant" :table-variant="tableVariant">
	        		<template v-slot:cell(-)="data">
				        <button :id="data.item.endpoint" type="button" class="btn btn-danger" v-on:click="deleteView(data.item.endpoint)"><i class="fas fa-fw fa-trash-alt"></i></button>
				      </template>
	        	</b-table>
	        </div>     
	    </div>
	</div>
    
</template>



<script>
module.exports = {
	props:["endpoints"],
 	methods: {
        deleteView: function(idToRemove) {
         	$('#dviews-resource-deleted').css("display", "none");
        	var xhr = new XMLHttpRequest();
            var url = "/helio-api/distributed-endpoints?endpoint="+idToRemove;
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
        addSPARQLEndpoint: function() {
         	var xhr = new XMLHttpRequest();
            xhr.open("POST", "/helio-api/distributed-endpoints", true);
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
            var endpoint = $("#endpoint").val().trim();
            var payload = JSON.parse("{\"endpoint\":\""+endpoint+"\"}");
            var data = JSON.stringify(payload);
            console.log(data);
            xhr.send(data);
        }
  	},
  	data() {
      	return {
	        fields: [
	          {
	            key: 'endpoint',
	            sortable: true
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
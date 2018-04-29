$(document).ready(function() {
$('a.delete').click(function(e) {
			var url=$(this).attr('data-url');
	        e.preventDefault();
			bootbox.dialog({
				  message: "Are you sure?",
				  title: "Confirmation",
				  buttons: {
				    success: {
				      label: "No",
				      className: "btn-success",
				    },
				    danger: {
				      label: "Delete",
				      className: "btn-danger",
				      callback: function() {
				    	  $.ajax(
							{	
								url : url,
								type : "get",	
							})
							.done(function(msg,status, data){
								console.log(" msg - " + msg + " status " + status );
								if(status=='success'){
									bootbox.dialog({message: msg,title: "Confirmation",buttons: {success: {label: "OK",className: "btn-success",}} });
									window.location.reload();
								}
								else { bootbox.dialog({message: msg,title: "Deletation Failed",buttons: {danger: {label: "OK",className: "btn-danger",}} });
					             }
								 
							})
							.error(function(msg){
								bootbox.dialog({message: msg,title: "Deletation Failed",buttons: {danger: {label: "OK",className: "btn-danger",}} });  
							});
				      }
				    }
				  }
				});// dialog
		});
});

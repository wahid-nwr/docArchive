//IMPORTANT: This JS using datatable.js, So include datatable.js before this file.
//INPUT: Json array and if after filtering, filter=0/1  -> true=1 or false=0 
(function() {
	var columnArr = [];
	var valueArr = [];
	var table=null;
	mPowersortIT=true;
}());


function mpower_Json_to_table(dataJson,filter) {

//******CONVERTING JSON INTO HTML TABLE**********

var showTable=function createTable(data, reload){
	 
	if(data.length!=0)
	{
		columnArr = [];
		valueArr = [];
		$.each(data[0], function(key, value) {
		    columnArr.push({"sTitle" : key});
		    
		});
		$.each(data, function(key, value) {
		    var innerArr = [];
		    var dataID=0;
		    $.each(value, function(innerKey, innerValue) {
		        innerArr.push(innerValue);
		    });
		    valueArr.push(innerArr);
		});
		//If 1st time loading
		if(reload==0)
		{
			table=$('#example').DataTable( {
				"bProcessing": true,
		        "aaData": valueArr,
		        "bSort": mPowersortIT,
		        "aoColumns": columnArr
		    });
		}
		else //if search data loading
		{
			$('#example').dataTable().fnDestroy();
			$('#example').DataTable( {
				"bProcessing": true,
		        "aaData": valueArr,
		        "aoColumns": columnArr
		    });
		}
	}//IF Data.len!=0 END
	
	else //if Data is empty
	{   valueArr=[];
		console.log("******5**** "+data);
		$('#example').dataTable().fnDestroy();
		$('#example').DataTable( {
			"bProcessing": true,
	        "aaData": valueArr,
	        "aoColumns": columnArr
	    });
	}
};
//EXECUTE TABLE AT 1st Time Page loading
showTable(dataJson,filter);
mpower_Json_to_table.showTable=showTable;
};
/**
 * file name : mpower-geolocation.js
 * This a file that used jQuery and helps to load cascade geo-location from sever and update UI.
 * To use this code, 'div' element should contains a 'select' element and
 * needs following  attributes:
 * 		class : class attribute must contain 'geo'
 * 		load-data-container : selector of an element that will contain the response that comes from server 
 * 		url : server url to load data
 * */
$("document").ready(function(){
	$(".geo").on("change", function() {
		//alert('here');
		var currElement =  $(this); 
		var loadDataContainer = $(currElement.attr("load-data-container"));
		loadDataContainer.empty();//to clear data container element
		loadDataContainer.trigger("change");//to clear all child needs a trigger
		var url = currElement.attr("url");
		var firstSelectElement = currElement.find("select:first");
		var geoId = $(firstSelectElement).find(":selected").attr("value");
				
		if(geoId) {
			loadData(url, "get", geoId).done(function(data) {
				loadDataContainer.html(data);
			});
		}
	});
	/**
	 * return data from successful AJAX request 
	 * */
	function loadData(url, type, geoId) {
		return $.ajax(
			{
				url : url,
				type : type,
				data : {id : geoId}
			}
		);
	}
});

<script type="text/javascript">
$('.filter').on('change', 'select:eq(0)', function(){
	var sel = $(this).find(':selected');
	var district = $("#beneficiaryProfile\\.district");
	//alert(district);
	if(district!=null && district!='null' && district!='undefined')
	{
		district.value = sel.val();
	}
	var oldItems = $(this);
	if(sel.val()){
		$.post('@@{Areas.loadGEOUpazilla}', {id:sel.val()}, function(data){
			$('.filter select:eq(1)').find('option:not(:first)').remove().end().append(data);
			for(var i = 2; i<=3; i++){
				$('.filter select:eq('+i+')').find('option:not(:first)').remove();
			}
		});
	}	
	else{
		$('.filter select:eq(1)').find('option:not(:first)').remove();
		for(var i = 2; i<=3; i++){
			$('.filter select:eq('+i+')').find('option:not(:first)').remove();
		}
	}
});

$('.filter').on('change', 'select:eq(1)', function(){
	var sel = $(this).find(':selected');
	var upazilla = $("#beneficiaryProfile\\.upazilla");
	if(upazilla!=null && upazilla!='null' && upazilla!='undefined')
	{
		upazilla.val(sel.val());
	}
	var oldItems = $(this);
	if(sel.val()){
		$.post('@@{Areas.loadGEOUnion}', {id:sel.val()}, function(data){
			$('.filter select:eq(2)').find('option:not(:first)').remove().end().append(data);
			for(var i = 3; i<=3; i++){
				$('.filter select:eq('+i+')').find('option:not(:first)').remove();
			}
		});
	}	
	else{
		$('.filter select:eq(2)').find('option:not(:first)').remove();
		for(var i = 2; i<=3; i++){
			$('.filter select:eq('+i+')').find('option:not(:first)').remove();
		}
	}
});

$(document).ready(function(){

    var counter = 2;
    var count = document.getElementById("count").value;
    console.log('count::'+count);
    
    var districtChange = function(){  
		console.log('counter==1=='+counter);
		var index = $('.filter select').index($(this));
		var groupIndex = parseInt($('.filter select').index($(this))/4);
		console.log('index::'+index+' groupIndex::'+groupIndex);

	    var sel = $(this).find(':selected');
   		var oldItems = $(this);
   		if(sel.val()){
   			$.post('@@{Areas.loadGEOUpazilla}', {id:sel.val()}, function(data){
   				$('.filter select:eq('+((groupIndex*4)+2)+')').find('option:not(:first)').remove().end().append(data);
   				for(var i = ((groupIndex*4)+3); i<=((groupIndex*4)+3); i++){
   					$('.filter select:eq('+i+')').find('option:not(:first)').remove();
   				}
   			});
   		}	
   		else{
   			$('.filter select:eq('+((groupIndex*4)+2)+')').find('option:not(:first)').remove();
   			for(var i = ((groupIndex*4)+3); i<=((groupIndex*4)+3); i++){
   				$('.filter select:eq('+i+')').find('option:not(:first)').remove();
   			}
   		}
	};	
    var upazillaChange = function(){
		console.log('counter==3=='+counter);
		var groupIndex = parseInt($('.filter select').index($(this))/4);
	    var sel = $(this).find(':selected');
 		var oldItems = $(this);
   		if(sel.val()){
   			$.post('@@{Areas.loadGEOUnion}', {id:sel.val()}, function(data){
   				$('.filter select:eq('+((groupIndex*4)+3)+')').find('option:not(:first)').remove().end().append(data);
   				for(var i = ((groupIndex*4)+4); i<=((groupIndex*4)+4); i++){
   					//$('.filter select:eq('+i+')').find('option:not(:first)').remove();
   				}
   			});
   		}	
   		else{
   			$('.filter select:eq('+((groupIndex*4)+3)+')').find('option:not(:first)').remove();
   			for(var i = ((groupIndex*4)+4); i<=((groupIndex*4)+4); i++){
   				//$('.filter select:eq('+i+')').find('option:not(:first)').remove();
   			}
   		}
	};	

    if(count>2)
    {
    	for(var i=2;i<count;i++)
    	{
    		var newSel = document.getElementById('TextBoxDiv'+i);
        	var children = newSel.getElementsByTagName('select');
            
            for(var j=0;j<children.length;j++)
            {
            	if(j==0)
            	{
            		children[j].id = "gkdivision.id_"+counter;
            		children[j].name = "gkdivision.id_"+counter;
            		children[j].onchange = divisionChange;
            	}
            	else if(j==1)
            	{
            		children[j].id = "gkdistrict.id_"+counter;
            		children[j].name = "gkdistrict.id_"+counter;
            		children[j].onchange = districtChange;
            	}
            	else if(j==2)
            	{
            		children[j].id = "gkupazilla.id_"+counter;
            		children[j].name = "gkupazilla.id_"+counter;
            		children[j].onchange = upazillaChange;
            	}
            	else if(j==3)
            	{
            		children[j].id = "gkunion.id_"+counter;
            		children[j].name = "gkunion.id_"+counter;	
            	}
            	
            }	
    	}    	
    }
 	
    if(count!=null && count.length>0)
    {
    	counter = count;
    }
    
    $("#addButton").click(function () {

    if(counter>10){
            alert("Only 10 textboxes allow");
            return false;
    }   

    var newTextBoxDiv = $(document.createElement('div')).attr("id", 'TextBoxDiv' + counter).appendTo("#TextBoxesGroup");
    var divSelect = document.getElementById('selectGroup');
    var newSel = divSelect.cloneNode(true);
    var children = newSel.getElementsByTagName('select');
    
    for(var j=0;j<children.length;j++)
   	{
   		////console.log('child::'+children[j].id);
   		if(j==0)
   		{
   			children[j].id = "gkdivision.id_"+counter;
   			children[j].name = "gkdivision.id_"+counter;
   			children[j].onchange = divisionChange;   					
   		}
   		if(j==1)
   		{   
   			children[j].id = "gkdistrict.id_"+counter;
   			children[j].name = "gkdistrict.id_"+counter;
   			children[j].onchange = districtChange;
   		}
   		if(j==2)
   		{
   			children[j].id = "gkupazilla.id_"+counter;
   			children[j].name = "gkupazilla.id_"+counter;
   			children[j].onchange = upazillaChange;
   		}
   		if(j==3)
   		{
   			children[j].id = "gkunion.id_"+counter;
   			children[j].name = "gkunion.id_"+counter;
   		}
   	}
    var fieldset = $(document.createElement('fieldset'));
    var legend = $(document.createElement('legend'))
    legend.html('Area group '+counter);
    fieldset.html(newSel);
    legend.appendTo(fieldset);
    
    fieldset.appendTo(newTextBoxDiv);
	for(var k = (((parseInt(counter-1))*4)+1); k<=(((parseInt(counter-1))*4)+3); k++){
    	////console.log('cc::'+counter);
		$('.filter select:eq('+k+')').find('option:not(:first)').remove();
	}
	document.getElementById("count").value = counter;
    counter++;    
});

    $("#removeButton").click(function () {
	    if(counter==1){
		    alert("No more textbox to remove");
	        return false;
	    }	
	    document.getElementById("count").value = counter;
	    console.log('kkk::'+document.getElementById("count").value);
	    if(counter>2)
	    {
	    	counter--;	
		    $("#TextBoxDiv" + counter).remove();	
	    }	    
    });

    $("#getButtonValue").click(function () {
	    var msg = '';
	    for(i=1; i<counter; i++){
	      msg += "\n Textbox #" + i + " : " + $('#textbox' + i).val();
	    }
        alert(msg);
        //console.log("msg::"+msg);
    });     
});
</script>

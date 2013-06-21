/*
	Credits
	Author: Shane Strong
	Website: http://www.shanestrong.com
	Email: shane@shanestrong.com
	License: Free to use and edit as you see fit. Only thing I ask is if you find something to 
	make this better please let me know.
*/

//Setting up the popup
//0 is disabled; 1 is enabled;
var popupStatus = 0;

function loadPopup(){
	//Will only be loaded if the status is 0
	if(popupStatus==0){
		$(".backgroundPopup").css({"opacity": "0.8"});
		$(".backgroundPopup").fadeIn("slow");
		$(".popupContent").fadeIn("slow");
		popupStatus = 1;
	}
}

//This will disable the popup when needed
function disablePopup(){
	//Will only disable if status is 1
	if(popupStatus==1){
		$(".backgroundPopup").fadeOut("slow");
		$(".popupContent").fadeOut("slow");
		popupStatus = 0;
	}
	document.getElementById("upload_form").innerHTML = "Name:<input type='text' name='alias1'><br /><input type='file' name='file1' size='50' /><br/><br/>";
}

//Centers the popup to your window size 
function centerPopup(){
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = $(".popupContent").height();
	var popupWidth = $(".popupContent").width();
	$(".popupContent").css({
		"position": "absolute",
		"top": windowHeight/2-popupHeight/2,
		"left": windowWidth/2-popupWidth/2
	});
	//this is needed for ie6
	$(".backgroundPopup").css({ "height": windowHeight });
}

//Click event controller
$(document).ready(function(){
	//Open the popup and center
	$("#upload_link").click(function(){
		centerPopup();
		loadPopup();
	});
	//Close popup by clicking the x
	$(".popupClose").click(function(){ disablePopup(); });
	//Close popup by clicking outside the box
	$(".backgroundPopup").click(function(){ disablePopup(); });
	//Close popup by clicking escape
	$(document).keypress(function(e){
		if(e.keyCode==27 && popupStatus==1){
			disablePopup();
		}
	});
});
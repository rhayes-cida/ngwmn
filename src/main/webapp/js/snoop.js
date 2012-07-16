function snoop(form, type, id){
	if (type == null){ 
		alert("Please select a cache type");
		return;
	}
	if (isNaN(parseInt(id))){
		alert("Please enter a numeric cache id");
		return;
	}
	alert(form.action + type + "/" + id);
	return form.action + type + "/" + id;
};
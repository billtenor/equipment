function draw(myChart,preDraw,mainDraw,url){
    var runStatus={
     	status:"uncompleted"
    };
	preDraw(myChart);
	dataGet(myChart,mainDraw,url,runStatus);
    var runID=setInterval(function(){
    	//alert(runStatus.status);
    	if(runStatus.status=="uncompleted"){
    		//timeout
    	}
    	else if(runStatus.status=="success"||runStatus.status=="error"){
    		clearInterval(runID);
    	}
    	else if(runStatus.status=="dataMissing"){
			dataGet(myChart,mainDraw,url,runStatus);
    	}
    },5000);
}
function dataGet(myChart,mainDraw,url,runStatus){
	$.ajax({
        type:"GET",
        url:url,
        dataType:"json",
        success:function (data){
        	mainDraw(myChart,runStatus,data);
        },
        error:function(data){  
        	status.status="dataMissing";
        },
    });
}
function dataCheck(json){
	var result=false;
	if("parameter" in json && "data" in json){
		var parameter = json["parameter"];
		var data = json["data"];
		if("dataConfig" in parameter){
			if(parameter["dataConfig"].length==data.length){
		    	result=true;
			}
		}
	}
	return result;
}
function createSeries(json){
	var data=json["data"];
	var result=json["parameter"]["dataConfig"];
	for(var i=0;i<result.length;i++){
		result[i]["data"]=data[i];
	}
	return result;
}
function get_parameter(parameter,name){
	if(name in parameter){
		return parameter[name];
	}
	else{
		return [];
	}
}

function coord_dataCheck(json){
	return dataCheck(json);
}
function coord_preDraw(myChart){
    var option = {
        title: {
        	text: '数据加载中'
    	},
    	tooltip: {},
    	legend: {
        	data:[]
    	},
    	xAxis: {
        	data:[]
    	},
    	yAxis: {},
    	series: [{
        	type: 'bar'
    	}]
    }
    // 显示标题，图例和空的坐标轴
    myChart.setOption(option);
    myChart.showLoading();
}
function coord_mainDraw(myChart,status,data){
    myChart.hideLoading();
    if(coord_dataCheck(data)){
    	status.status="success";
    	// 填入数据
    	myChart.setOption({
    		title: {
    			text: get_parameter(data.parameter,"title")
    		},
    		legend: {
    			data: get_parameter(data.parameter,"legend")
    		},
    	    xAxis: {
    	        data: get_parameter(data.parameter,"xAxis")
    	    },
    	    yAxis: {
    	    	data: get_parameter(data.parameter,"yAxis")
    	    },
    	    series: createSeries(data)
    	});
    }
    else{
    	status.status="error";
    	myChart.setOption({
    		title: {
    			text: "数据格式错误"
    		}
    	});
    }
}
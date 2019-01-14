wordList = []
loadFile = function(fileURI) {
    
    $.ajax({
    	type: "GET",
        url: fileURI,
        async: false,
        xhrFields: {'withCredentials': false},
        contentType: 'text/plain',
        success: function(data) {processData(data);}
     });
}

processData = function(data){
	wordList.length = 0
	list = data.split(/\r\n|\n/)
	for(i = 0; i < list.length; i++){
		split = list[i].toString().split(/\t/)
		word = {"text":split[0],"size":parseInt(split[1])}
		wordList.push(word)
	}

	wordList.sort(function(n_0, n_1){
		return n_1.size - n_0.size
	})
	// console.log(wordList.length)
	$("#data_list").text(wordList[2])
	$("#data_list").css("display","inline")
	
    create_select(wordList.length)
}

function create_select(size){
	
	for(i = 0; i < size; i++)
		if( i == 9 )
			$("#cloud_size").append("<option value='"+ (i+1)+ "' selected>"+ (i+1) + "</option>")
		else
			$("#cloud_size").append("<option value='"+ (i+1)+ "'>"+ (i+1) + "</option>")
}

function load_cloud(){
	if($("#cloud_window").length) $("#cloud_window").remove()
    var lower = 0
    var upper = parseInt($('#cloud_size').val()) 

    var frequency_list = wordList.slice(0, upper)
    // console.log("upper: "+ upper)
    // console.log(frequency_list)
    
    var color = d3.scale.linear()
            .domain([0,1,2,3,4,5,6,10,15,20,100])
            .range(["#ddd", "#ccc", "#bbb", "#aaa", "#999", "#888", "#777", "#666", "#555", "#444", "#333", "#222"]);
    // console.log(frequency_list)
    d3.layout.cloud().size([850, 350])
            .words(frequency_list)
            .rotate(0)
            .fontSize(function(d) { return d.size; })
            .on("end", draw)
            .start();

    function draw(words) {
        d3.select("body").append("svg")
                .attr("width", 850)
                .attr("height", 350)
                .attr("class", "wordcloud")
                .attr("id","cloud_window")
                .append("g")
                // without the transform, words words would get cutoff to the left and top, they would
                // appear outside of the SVG area
                .attr("transform", "translate(320,200)")
                .selectAll("text")
                .data(words)
                .enter().append("text")
                .style("font-size", function(d) { return d.size + "px"; })
                .style("fill", function(d, i) { return color(i); })
                .attr("transform", function(d) {
                    return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                })
                .text(function(d) { return d.text; });
    }
}


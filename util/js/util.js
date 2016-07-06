$.extend({
	/**
	 * 表格数据加载
	 */
	loadTable: function(params){
    	$.ajax({
    	    type: params.type,
    	    url: params.url,
    	    data: $.extend({},params.datas,params.data ),
    	    success: function(_data){
    	    	if(_data.code == 200){
    	    		params.success(_data.datas);
    	    	}else{
    	    		params.error(_data.msg);
    	    	}
    	    } ,
    	    dataType: params.dataType
    	});
    }
});

jQuery.fn.extend({
	/**
	 * select初始化
	 * @param url 获取数据url
	 * @param async 同步还是异步
	 */
	initSelect : function (url, async) {
		var options = "";
		$.ajax({
			url : url,
			type : "get",
			async : async ? true : false,
			success : function(data) {
				for (var idx in data) {
					options += "<option value='" + data[idx].value + "'>" + data[idx].text
								+ "</option>";
				}
			}
		});
		$(this).html(options);
		return $(this);
	},
	dateFormat : function(fmt) {
		if (fmt == null || fmt == "" || fmt == undefined) {
			fmt = "yyyy-MM-dd";
		}
		var o = {
			"M+" : this.getMonth()+1,                 //月份
			"d+" : this.getDate(),                    //日
			"h+" : this.getHours(),                   //小时
			"m+" : this.getMinutes(),                 //分
			"s+" : this.getSeconds(),                 //秒
			"q+" : Math.floor((this.getMonth()+3)/3), //季度
			"S"  : this.getMilliseconds()             //毫秒
		};

		if(/(y+)/.test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
		}

		for(var k in o) {
			if(new RegExp("("+ k +")").test(fmt)) {
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
			}
		}
		return fmt;
	}
});

/**
 * 日期格式化方法
 * @param fmt
 * @returns
 * 调用方式: new Date().format('yyyy-MM-dd')
 */
Date.prototype.format = function(fmt) {
	if (fmt == null || fmt == "" || fmt == undefined) {
		fmt = "yyyy-MM-dd";
	}
	var o = {
		"M+" : this.getMonth()+1,                 //月份
		"d+" : this.getDate(),                    //日
		"h+" : this.getHours(),                   //小时
		"m+" : this.getMinutes(),                 //分
		"s+" : this.getSeconds(),                 //秒
		"q+" : Math.floor((this.getMonth()+3)/3), //季度
		"S"  : this.getMilliseconds()             //毫秒
	};

	if(/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}

	for(var k in o) {
		if(new RegExp("("+ k +")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		}
	}
	return fmt;
};

/**
 * 解压缩文件包
 * @param blob 文件对象  $("input[type='file']")[0]
 * @param callback 解压缩文件是否符合规范的结果 处理函数
 * @returns {___anonymous1981_2001}
 */
function unzip(blob, callback) {
	// zip解压的js文件存放路径
	zip.workerScriptsPath = "static/plugin/zip/";
	
	// 解压之后的文件结构
	/**
	 * {
	 * 	"book" : [xxx.dat, xxx.xml, cover.png, ..书下的文件名],
	 *  "directory1" : [345_345_word.png, ...目录下的文件名],
	 *  ..
	 *  ..
	 * }
	 */
	var zipfile = {
		"book" : []
	};
	zip.createReader(new zip.BlobReader(blob), function(reader) {
		reader.getEntries(function(entries) {
			for (var index = 0; index < entries.length; index ++) {
				var entry = entries[index];
				if (entry.directory) {  // 目录直接继续下一个
					continue;
				}
				var filename = entry.filename;
				var slashIndex = filename.indexOf("/");
				var directory = "book";  // 默认是书本下的文件实体
				if (slashIndex != -1) {  // 是书本中目录下的文件实体
					directory = filename.substring(0, slashIndex);
				}
				if (zipfile[directory] == undefined) {
					zipfile[directory] = [];
				}
				zipfile[directory].push(filename.substring(slashIndex + 1));
			}
			callback(zipCheck(zipfile));
		});
	}, function(error) {
		console.log("压缩包格式错误");
	});
}

/**
 * 压缩包检测
 * @param zipfileContent 压缩文件解压后的文件结构
 */
function zipCheck(zipfileContent) {
	var result = {
	};
	// 循环处理每个目录下的文件是否符合规则
	for (var props in zipfileContent) {
		if (props == "book") {  // 验证书本下的文件
			result["book"] = checkBook(zipfileContent[props]);
		} else { // 验证书本目录中的文件
			result[props] = checkContent(zipfileContent[props]);
		}
	}
	return result;
}

/**
 * 检测书级别
 * @param arr_book  书本下必须包含的文件的正确格式: ["xxx.dat", "xxx.xml", "xxx.png", "xxx.txt"]
 * @returns {___anonymous3411_3468}
 */
function checkBook(arr_book) {
	var bookFormat = [".png", ".xml", ".dat", ".txt"];
	var result = {
		".png" : 1,
		".xml" : 1,
		".dat" : 1,
		".txt" : 1
	};
	var msg = {
		".png" : [true, "书的封面文件不存在或者格式错误（要求png）"],
		".xml" : [true, "书的xml配置文件不存在"],
		".dat" : [true, "书的dat配置文件不存在"],
		".txt" : [true, "缺少内容排序文件(xxx.txt)"]
	}
	bookFormat.map(function(value) {
		var i = 0;
		for (; i < arr_book.length; i++) {
			if (arr_book[i].endsWith(value)) {
				result[value] = result[value] - 1;
				break;
			}
		}
		result[value] = msg[value][result[value]];
	});
	return result;
}

/**
 * 检测书本的内容级别 书本内容的正确格式: ["_word.png(2个)", "_icon.png(2个)", ".mp3", ".assetbundle(<=2个)|.mov(<=3个)"]
 * @param arr_content
 */
function checkContent(arr_content) {
	var content = ["_word.png", "_icon.png", ".mp3"];
	var result = {
		"_word.png" : 4,
		"_icon.png" : 4,
		".mp3"      : 1,
		"model"     : 1
	};
	var msg = {
		"_word.png" : [true, "有一张内容标题命名格式错误（1242_1490_icon.png）", "内容标题命名格式错误（1242_1490_icon.png）", "缺少一张标题图片，请检查（要求png，并以_icon.png结尾）", "内容标题图片不存在，请检查（要求png，并以_icon.png结尾）"],
		"_icon.png" : [true, "有一张内容图片命名格式错误（350_350_word.png）", "内容图片命名格式错误（350_350_word.png）", "缺少一张内容图片，请检查（要求png，并以_word.png结尾）", "内容图片不存在，请检查（要求png，并以_word.png结尾）"],
		".mp3"      : [true, "缺少声音文件，请检查（要求mp3）"],
		"model"     : [true, "缺少模型或视频文件，请检查（要求assetbundle或者mov）"]
	}
	
	var checkModal = false;
	var regxp = /^[1-9]{1}\d{2,3}\_[1-9]{1}\d{2,3}\_(icon)|(word).png/; // 验证图片命名格式是否正确(111_111_word.png|111_111_icon.png)
	content.map(function(value) {
		for (var i = 0; i < arr_content.length; i++) {
			if (arr_content[i].endsWith(value)) {
				result[value] = result[value] - 1;
			}
		}
	});
	
	for (var i = 0; i < arr_content.length; i++) {
		var arrContent = arr_content[i];
		if ((arrContent.endsWith(".assetbundle") || arrContent.endsWith(".mov")) && !checkModal) {
			result["model"] = result["model"] - 1;
			checkModal = true;
		}
		if (arrContent.endsWith("_word.png") && regxp.test(arrContent)) {
			result["_word.png"] = result["_word.png"] - 1;
		}
		if (arrContent.endsWith("_icon.png") && regxp.test(arrContent)) {
			result["_icon.png"] = result["_icon.png"] - 1;
		}
	}
	
	for (var resultProp in result) {
		result[resultProp] = msg[resultProp][result[resultProp]];
	}
	return result;
}
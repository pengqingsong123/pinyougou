//绑定controler
app.controller("typeTemplateController", function($scope, $controller,
		typeTemplateService, brandService, specificationService) {

	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	// 读取列表中所有的数据
	$scope.findAll = function() {
		typeTemplateService.findAll().success(function(data) {
			$scope.list = data;
		});
	}

	// 重新加载列表 数据
	$scope.reloadList = function() {
		// 切换页码
		$scope.findPage($scope.paginationConf.currentPage,
				$scope.paginationConf.itemsPerPage);

	}
	// 分页控件配置
	$scope.paginationConf = {
		currentPage : 1,
		totalItems : 10,
		itemsPerPage : 10,
		perPageOptions : [ 10, 20, 30, 40, 50 ],
		onChange : function() {
			$scope.reloadList();// 重新加载
		}
	};

	// 分页查询信息
	$scope.findPage = function(page, rows) {

		typeTemplateService.findPage(page, rows).success(function(data) {
			// 把总记录数赋值给totalitems
			$scope.paginationConf.totalItems = data.total;
			$scope.list = data.rows;
		});
	}

	// 添加品牌数据---修改也是一样
	$scope.add = function() {

		var obj = null;
		if ($scope.entity.id != null) {
			obj = typeTemplateService.update($scope.entity);
		} else {
			obj = typeTemplateService.add($scope.entity);
		}

		// 发送请求,保存商品
		obj.success(function(data) {
			// 判断时候保存
			if (data.success) {
				// 保存成功,再次刷新查询一次
				$scope.reloadList();
			} else {
				alert(data.message);
			}
		})
	};

	// 修改商品品牌
	$scope.findOne = function(id) {
		typeTemplateService.findOne(id).success(function(data) {
			$scope.entity = data;
			$scope.entity.specIds=JSON.parse($scope.entity.specIds);
			$scope.entity.brandIds=JSON.parse($scope.entity.brandIds);
			$scope.entity.customAttributeItems=JSON.parse($scope.entity.customAttributeItems);
		})
	}

	// 定义方法,接收要删除 的id
	$scope.selectIds = [];
	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			$scope.selectIds.push(id);
		} else {
			var idx = $scope.delectIds.indexOf(id);
			$scope.selectIds.splice(idx, 1);
		}
	}
	// 批量删除
	$scope.dele = function() {
		typeTemplateService.dele($scope.selectIds).success(function(data) {
			if (data.success) {
				// 删除成功,就再次刷新查询
				$scope.reloadList();
			} else {
				alert(data.message);
			}
		})
	};

	// 初始化
	// 新增规格选项optionList是空的,entity也没有啊,所以要初始化

	$scope.addTableRow = function() {
		// 向规格选项集合属性中推送空对象
		$scope.entity.optionList.push({});
	};
	// 删除行
	$scope.deleTabelRow = function(index) {
		$scope.entity.optionList.splice(index, 1);
	};

	// 写一个通用的方法将后端传来的json格式字符串转换
	$scope.jsonToStr = function(key, jsonString) {
		// 将json格式字符串变为json对象
		var jsonObj = JSON.parse(jsonString);
		var str = "";
		for (var i = 0; i < jsonObj.length; i++) {
			if (i == jsonObj.length - 1) {
				str += jsonObj[i][key];
			} else {
				str += jsonObj[i][key];
				str += ",";
			}
		}
		return str;
	};

	$scope.brandList = {
		data : []
	}
	// 从tb_brand表里里面查询id和name
	$scope.findBrandList = function() {
		// 查询品牌数据
		brandService.findBrandList().success(function(datas) {
			$scope.brandList = {
				data : datas
			}
		});
	};

	$scope.specList = {
		data : []
	}
	// 从规格tb_specification表里查询id和name,显示select2下拉列表里
	$scope.findSpecList = function() {
		specificationService.findSpecList().success(function(datas) {
			$scope.specList = {
				data : datas
			};
		})
	};

	// 新增行
	$scope.addTabelRow = function() {
		$scope.entity.customAttributeItems.push({});
	}
	// 删除行
	$scope.deleteTableRow = function(index) {
		$scope.entity.customAttributeItems.splice(index, 1);
	}

});
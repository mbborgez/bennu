
function BootstrapCtrl($scope, $http) {
	
	$scope.bootstrappers = null;

	$scope.currentStepNumber = 0;
	
	$scope.currentBootstrapperNumber = 0;
	
	$scope.error = null;
	
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		console.log(data);
		$scope.bootstrappers = data;
	});

	$scope.submitWizard = function() {
		clearErrors();
		
		$http.post('api/bennu-portal/bootstrap', allFields()).
			success(function(data, status, headers, config) {
				console.log("success");
			}).
			error(function(data, status, headers, config) {
				$scope.error = data;
				if(data instanceof Object) {
					findField(data.fieldName).hasError = true;
					$scope.currentStepNumber = findSectionNumber(data.fieldName);
				}
			});
	};

	function allFields() {
		var fields = new Object();
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper){
			$.each(bootstrapper.sections, function(sectionIndex, section){
				$.each(section.fields, function(fieldIndex, field) {
					fields[field.name] = field.value;
				});
			});
		});
		return fields;
	}

	function findSectionNumber(fieldName) {
		var result = 0;
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper) {
			$.each(bootstrapper.sections, function(sectionIndex, section) {
				$.each(section.fields, function(fieldIndex, field){
					if(angular.equals(fieldName, field.name)) {
						result = sectionIndex;
					}
				});
			});
		});
		return result;
	}

	function findField(fieldName) {
		var result = null;
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper) {
			$.each(bootstrapper.sections, function(sectionIndex, section) {
				$.each(section.fields, function(fieldIndex, field){
					if(angular.equals(fieldName, field.name)) {
						result = field;
					}
				});
			});
		});
		return result;
	}

	function clearErrors() {
		$scope.error = null
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper) {
			$.each(bootstrapper.sections, function(sectionIndex, section) {
				$.each(section.fields, function(fieldIndex, field){
					field.hasError = false;
				});
			});
		});
		
	}

	$scope.hasAnyError = function() {
		return $scope.error != null;
	}

	$scope.hasError = function (section, field) {
		return $scope.error != null && angular.equals($scope.error.sectionName, section.name) && angular.equals($scope.error.fieldName, field.name);
	}

	$scope.hasSections = function() {
		return selectedBootstrapper()!=null && selectedBootstrapper().sections!=null && selectedBootstrapper().sections.length!=0;
	};

	$scope.getCurrentStep = function() {
		return selectedBootstrapper().sections[$scope.currentStepNumber];
	};

	$scope.nextStep = function() {
		if($scope.currentStepNumber < selectedBootstrapper().sections.length) {
			$scope.currentStepNumber++;
		}
	};

	$scope.previousStep = function() {
		if($scope.currentStepNumber > 0) {
			$scope.currentStepNumber--;
		}
	};

	function selectedBootstrapper() {
		return $scope.bootstrappers[$scope.currentBootstrapperNumber];
	}

	$scope.nextBootstrapper = function() {
		if($scope.currentBootstrapperNumber < selectedBootstrapper().length) {
			$scope.currentBootstrapperNumber++;
		}
	};

	$scope.previousBootstrapper = function() {
		if($scope.currentBootstrapperNumber > 0) {
			$scope.currentBootstrapperNumber--;
		}
	};
};



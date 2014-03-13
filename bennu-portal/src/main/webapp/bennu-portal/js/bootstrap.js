
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
					showFieldError(data.fieldName, data);
				}
			});
	};

	function allFields() {
		var fields = new Object();
		mapValues(function(bootstrapper, section, field){
			fields[field.name] = field.value;			
		});
		return fields;
	}

	function showFieldError(fieldName, fieldError) {
		mapIndex(function(bootstrapperIndex, sectionIndex, fieldIndex){
			var field = $scope.bootstrappers[bootstrapperIndex].sections[sectionIndex].fields[fieldIndex];
			if(angular.equals(fieldName, field.name)) {
				$scope.currentBootstrapperNumber = bootstrapperIndex;
				$scope.currentStepNumber = sectionIndex;
				field.hasError = true;
			}
		});
	}

	function clearErrors() {
		$scope.error = null;
		mapValues(function(bootstrapper, section, field){
			field.hasError = false;
		});
	}

	$scope.hasAnyError = function() {
		return $scope.error != null;
	}

	$scope.hasError = function (section, field) {
		return $scope.error != null && 
			angular.equals($scope.error.sectionName, section.name) && 
			angular.equals($scope.error.fieldName, field.name);
	}

	$scope.hasSections = function() {
		return $scope.bootstrappers[$scope.currentBootstrapperNumber]!=null && 
			$scope.bootstrappers[$scope.currentBootstrapperNumber].sections!=null &&
			$scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length!=0;
	};

	$scope.getCurrentStep = function() {
		return $scope.bootstrappers[$scope.currentBootstrapperNumber].sections[$scope.currentStepNumber];
	};

	$scope.nextStep = function() {
		if($scope.currentStepNumber < $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1) {
			$scope.currentStepNumber++;
		} else {
			if($scope.currentBootstrapperNumber < $scope.bootstrappers.length-1) {
				$scope.currentBootstrapperNumber++;
				$scope.currentStepNumber = 0;
			}
		}
	};

	$scope.previousStep = function() {
		if($scope.currentStepNumber > 0) {
			$scope.currentStepNumber--;
		} else {
			if($scope.currentBootstrapperNumber > 0) {
				$scope.currentBootstrapperNumber--;
				$scope.currentStepNumber = $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1;
			}
		}
	};

	$scope.lastBootstrapper = function() {
		return $scope.currentBootstrapperNumber == $scope.bootstrappers.length-1;
	};
	
	$scope.firstBootstrapper = function() {
		return $scope.currentBootstrapperNumber == 0;
	};

	function mapIndex(mappingFunction) {
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper){
			$.each(bootstrapper.sections, function(sectionIndex, section){
				$.each(section.fields, function(fieldIndex, field) {
					mappingFunction(bootstrapperIndex, sectionIndex, fieldIndex);
				});
			});
		});
	}

	function mapValues(mappingFunction) {
		$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper){
			$.each(bootstrapper.sections, function(sectionIndex, section){
				$.each(section.fields, function(fieldIndex, field) {
					mappingFunction(bootstrapper, section, field);
				});
			});
		});
	}
};



function BootstrapCtrl($scope, $http) {
	
	$scope.sections = null;
	
	$scope.currentStepNumber = 0;

	$scope.error = null;
	
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		$scope.sections = data;
	});

	$scope.submitWizard = function() {
		$http.post('api/bennu-portal/bootstrap', $scope.sections).
			success(function(data, status, headers, config) {
				console.log("SUCCESS");
			}).
			error(function(data, status, headers, config) {
				$scope.error = data;
				$scope.currentStepNumber = findSectionNumber(data.sectionName, data.fieldName);
			});
	};

	$scope.hasError = function (section, field) {
		var invalid = $scope.error != null && $scope.error.sectionName == section.name && $scope.error.fieldName == field.name;
		if(invalid) {

		}
		return invalid;
	}

	function findSectionNumber(sectionName, fieldName) {
		$.each($scope.sections, function(scopeIndex, section){
			if(section.name == sectionName) {
				$.each(section.fields, function(fieldIndex, field) {
					if(field.name==fieldName) {
						return scopeIndex;
					}
				})
			}
		});
		return 0;
	}

	$scope.hasSections = function() {
		return $scope.sections!=null && $scope.sections.length!=0;
	};

	$scope.getCurrentStep = function() {
		return $scope.sections[$scope.currentStepNumber];
	};

	$scope.nextStep = function() {
		$scope.currentStepNumber++;
	};

	$scope.previousStep = function() {
		$scope.currentStepNumber--;
	};

};
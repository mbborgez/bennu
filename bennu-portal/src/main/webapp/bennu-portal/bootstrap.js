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
				showError(data);
			});
	};

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

	$scope.hasAnyError = function() {
		return $scope.error != null;
	}

	$scope.hasError = function (section, field) {
		return $scope.error != null && $scope.error.sectionName == section.name && $scope.error.fieldName == field.name;
	}

	function showError(error) {
		console.log($scope.bootstrapForm);
		$scope.bootstrapForm.$setValidity(false);
	}

	$scope.hasSections = function() {
		return $scope.sections!=null && $scope.sections.length!=0;
	};

	$scope.getCurrentStep = function() {
		return $scope.sections[$scope.currentStepNumber];
	};

	$scope.nextStep = function() {
		if($scope.currentStepNumber<$scope.sections.length) {
			$scope.currentStepNumber++;
		}
	};

	$scope.previousStep = function() {
		if($scope.currentStepNumber>0) {
			$scope.currentStepNumber--;
		}
	};

};
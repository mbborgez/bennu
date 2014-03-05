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

				$scope.currentStepNumber = findSectionNumber(data.section, data.field);
			});
	};

	function findSectionNumber(originalSection, originalField) {
		var result = 0;
		$.each($scope.sections, function(sectionIndex, section) {
			if(angular.equals(originalSection.name, section.name)) {
				result = sectionIndex;
			}
		});
		return result;
	}
	
	$scope.hasAnyError = function() {
		return $scope.error != null;
	}

	$scope.hasError = function (section, field) {
		return $scope.error != null && $scope.error.sectionName == section.name && $scope.error.fieldName == field.name;
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
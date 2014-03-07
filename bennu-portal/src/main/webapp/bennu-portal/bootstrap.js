
function BootstrapCtrl($scope, $http) {
	
	$scope.bootstrapper = null;

	$scope.currentStepNumber = 0;

	$scope.error = null;
	
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		$scope.bootstrapper = data;
	});

	$scope.submitWizard = function() {
		clearErrors();

		$http.post('api/bennu-portal/bootstrap', $scope.bootstrapper).
			success(function(data, status, headers, config) {
				window.location.reload(false);
			}).
			error(function(data, status, headers, config) {
				console.log(data);
				$scope.error = data;
				findField(data.section, data.field).hasError = true;
				$scope.currentStepNumber = findSectionNumber(data.section, data.field);
			});
	};

	function findSectionNumber(originalSection, originalField) {
		var result = 0;
		$.each($scope.bootstrapper.sections, function(sectionIndex, section) {
			if(angular.equals(originalSection.name, section.name)) {
				result = sectionIndex;
			}
		});
		return result;
	}

	function findField(originalSection, originalField) {
		var result = null;
		$.each($scope.bootstrapper.sections, function(sectionIndex, section) {
			$.each(section.fields, function(fieldIndex, field){
				if(angular.equals(originalSection.name, section.name) && angular.equals(originalField.name, field.name)) {
					result = field;
				}
			});
		});
		return result;
	}

	function clearErrors() {
		$.each($scope.bootstrapper.sections, function(sectionIndex, section){
			$.map(section.fields, function(field, fieldIndex) {
				field.hasError = false;
			})
		});
	}

	$scope.hasAnyError = function() {
		return $scope.error != null;
	}

	$scope.hasError = function (section, field) {
		return $scope.error != null && angular.equals($scope.error.sectionName, section.name) && angular.equals($scope.error.fieldName, field.name);
	}

	$scope.hasSections = function() {
		return $scope.bootstrapper!=null && $scope.bootstrapper.sections!=null && $scope.bootstrapper.sections.length!=0;
	};

	$scope.getCurrentStep = function() {
		return $scope.bootstrapper.sections[$scope.currentStepNumber];
	};

	$scope.nextStep = function() {
		if($scope.currentStepNumber<$scope.bootstrapper.sections.length) {
			$scope.currentStepNumber++;
		}
	};

	$scope.previousStep = function() {
		if($scope.currentStepNumber>0) {
			$scope.currentStepNumber--;
		}
	};

};



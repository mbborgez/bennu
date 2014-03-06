function BootstrapCtrl($scope, $http) {
	
	$scope.sections = null;
	
	$scope.currentStepNumber = 0;

	$scope.error = null;
	
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		$scope.sections = data;
	});

	$scope.submitWizard = function() {
		clearErrors();
		$http.post('api/bennu-portal/bootstrap', $scope.sections).
			success(function(data, status, headers, config) {
				console.log("SUCCESS");
			}).
			error(function(data, status, headers, config) {
				$scope.error = data;
				findField(data.section, data.field).hasError = true;
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

	function findField(originalSection, originalField) {
		var result = null;
		$.each($scope.sections, function(sectionIndex, section) {
			$.each(section.fields, function(fieldIndex, field){
				if(angular.equals(originalSection.name, section.name) && angular.equals(originalField.name, field.name)) {
					result = field;
				}
			});
		});
		return result;
	}

	function clearErrors() {
		$.each($scope.sections, function(sectionIndex, section){
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


var app = angular.module('bootstrap-app', []);

app.directive('validate-field', function() {
    return {
        require: 'ngModel',
        link: function($scope, $element, $attrs, $ctrl) {
            $ctrl.$parsers.unshift(function(viewValue) {
            	console.log($element, $ctrl);
                /*if(angular.equals($scope.error.field.name, $ctrl.name)) {
                    $ctrl.$setValidity('pwd', true);
                    return viewValue;
                } else {
                    $ctrl.$setValidity('pwd', false);                    
                    return undefined;
                }*/
        	});
        }
    };
});
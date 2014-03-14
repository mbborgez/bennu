angular.module('bootstrapModule', [])
	.controller('BootstrapCtrl', function ($scope, $http) {
	
	$scope.bootstrappers = null;
	$scope.currentSectionNumber = 0;
	$scope.currentBootstrapperNumber = 0;
	$scope.error = null;
	$scope.locale = null;
	$scope.tempLocale = null;
	$scope.availableLocales = null;
	
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		console.log(JSON.stringify(data));
		$scope.bootstrappers = data.bootstrappers;
		$scope.availableLocales = data.availableLocales;
		//$scope.availableLocales = ['pt-PT', 'en-GB'];

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

	$scope.submitInitialConfig = function() {
		$scope.locale = $scope.tempLocale;
	}

	$scope.showInitialConfig = function() {
		$scope.locale = null;
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
		return $scope.bootstrappers[$scope.currentBootstrapperNumber].sections[$scope.currentSectionNumber];
	};

	$scope.nextStep = function() {
		if($scope.currentSectionNumber < $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1) {
			$scope.currentSectionNumber++;
		} else {
			if($scope.currentBootstrapperNumber < $scope.bootstrappers.length-1) {
				$scope.currentBootstrapperNumber++;
				$scope.currentSectionNumber = 0;
			}
		}
	};

	$scope.previousStep = function() {
		if($scope.currentSectionNumber > 0) {
			$scope.currentSectionNumber--;
		} else {
			if($scope.currentBootstrapperNumber > 0) {
				$scope.currentBootstrapperNumber--;
				$scope.currentSectionNumber = $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1;
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
				$scope.currentSectionNumber = sectionIndex;
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
});

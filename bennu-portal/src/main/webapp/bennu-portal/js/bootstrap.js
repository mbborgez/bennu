angular.module('bootstrapModule', [])
	.controller('BootstrapCtrl', 
		function ($scope, $http) {
		
		$scope.bootstrappers = null;
		$scope.currentSectionNumber = 0;
		$scope.currentBootstrapperNumber = 0;
		$scope.errors = null;
		$scope.locale = null;
		$scope.tempLocale = null;
		$scope.availableLocales = null;
		$scope.defaultLocale = null;
		
		$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
			$scope.bootstrappers = data.bootstrappers;
			$scope.availableLocales = data.availableLocales;
			$scope.defaultLocale = data.defaultLocale;
		});

		$scope.submitWizard = function() {
			clearErrors();
			if(validateCurrentStep()) {
				$http.post('api/bennu-portal/bootstrap', allFields()).
					success(function(data, status, headers, config) {
						window.location.reload(false);
					}).
					error(function(data, status, headers, config) {
						showErrors(data);
					});
			}
		};

		function showErrors(errors) {
			$.each(errors.reverse(), function(errorIndex, error) {
				if(error.fieldName!=null) {
					showFieldError(error.fieldName, error);
				} else {
					$scope.errors.push(error);
				}
			});
		}

		$scope.submitInitialConfig = function() {
			$scope.locale = $scope.tempLocale;
		}

		$scope.showInitialConfig = function() {
			$scope.locale = null;
		}

		$scope.hasAnyError = function() {
			return $scope.error != null;
		}

		function validateCurrentStep() {
			var valid = true;
			var fields = $scope.getCurrentStep().fields;
			$.each(fields, function(fieldIndex, field) {
				if(!field.value) {
					field.hasError = true;
					field.hasRequiredError = true;
					valid = false;
				} else {
					field.hasError = false;
					field.hasRequiredError = false;
				}
			});
			return valid;
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
			if(validateCurrentStep()) {
				if($scope.currentSectionNumber < $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1) {
					$scope.currentSectionNumber++;
				} else {
					if($scope.currentBootstrapperNumber < $scope.bootstrappers.length-1) {
						$scope.currentBootstrapperNumber++;
						$scope.currentSectionNumber = 0;
					}
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
				if(typeof field.value == "undefined") field.value = "";
				fields[field.name[$scope.defaultLocale.key]] = field.value;
			});
			return fields;
		}

		function showFieldError(fieldName, fieldError) {
			mapIndex(function(bootstrapperIndex, sectionIndex, fieldIndex){
				var field = $scope.bootstrappers[bootstrapperIndex].sections[sectionIndex].fields[fieldIndex];
				if(angular.equals(fieldName[$scope.defaultLocale.key], field.name[$scope.defaultLocale.key])) {
					$scope.currentBootstrapperNumber = bootstrapperIndex;
					$scope.currentSectionNumber = sectionIndex;
					field.hasError = true;
					field.error = fieldError;
				}
			});
		}

		function clearErrors() {
			$scope.errors = [];
			mapValues(function(bootstrapper, section, field){
				field.hasError = false;
				field.error = null;
			});
		}
	});
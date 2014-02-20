function BootstrapCtrl($scope, $http) {
	$scope.sections = null;
	$scope.currentStepNumber = 0;
	$http.get('api/bennu-portal/bootstrap').success(function(data, status, headers, config) {
		console.log("data received: " + JSON.stringify(data));
		$scope.sections = data;
	});

	$scope.submitWizard = function() {
		console.log("data sent: " + JSON.stringify($scope.sections));

		$http.post('api/bennu-portal/bootstrap', $scope.sections).
			success(function(data, status, headers, config) {
				console.log("SUCCESS");
			}).
			error(function(data, status, headers, config) {
				console.log("ERROR");
			});

	};

	$scope.hasSections = function() {
		return $scope.sections!=null && $scope.sections.length!=0;
	}

	$scope.getCurrentStep = function() {
		return $scope.sections[$scope.currentStepNumber];
	}

	$scope.nextStep = function() {
		$scope.currentStepNumber++;
	}

	$scope.previousStep = function() {
		$scope.currentStepNumber--;
	}

};
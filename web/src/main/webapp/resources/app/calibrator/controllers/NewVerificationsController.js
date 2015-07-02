angular
    .module('calibratorModule')
    .controller('NewVerificationsController', ['$scope', '$log',
                                               '$modal', 'VerificationService',
                                               '$rootScope','ngTableParams',
        function ($scope, $log, $modal, verificationService, $rootScope, ngTableParams) {

            
            $scope.search = {
            		text:"",
            		type:""
            }
            
            $scope.clearInput = function(){
            	$scope.search.text="";
            }
            
            $scope.doSearch = function() {
                $log.debug('search text from scope inside reload:', $scope.search.text);
                $scope.tableParams.reload();
            }

            
			$scope.tableParams = new ngTableParams({
				page: 1, 
				count: 10
						}, {
							total: 0,
							getData: function($defer, params) {
			        
							        var queryOptions = {
										pageNumber: params.page(),
										itemsPerPage: params.count(),
										searchType: $scope.search.type,
										searchText: $scope.search.text 	 
									}
							         
							        verificationService.searchNewVerifications(queryOptions)
								          	.success(function(result) {
												$defer.resolve(result.content);
												params.total(result.totalItems);
									  		}, function(result) {
													 $log.debug('error fetching data:', result);
									  			});  
							}
			});
            
                   
			 $scope.markAsRead = function (id) {
				 var dataToSend = {
							verificationId: id,
							readStatus: 'READ'
						};
				 $log.info("data to send in mark as read : " + dataToSend.verificationId); 
		         	verificationService.markVerificationAsRead(dataToSend).success(function () {
		         		$log.info('succesfully sent to database');
		         		$rootScope.$broadcast('verification-was-read');
		         		$scope.tableParams.reload();
		            });
	         };
			
			
            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {
                $modal.open({
                    animation: true,
                    templateUrl: '/resources/app/calibrator/views/modals/new-verification-details.html',
                    controller: 'DetailsModalController',
                    size: 'lg',
                    resolve: {
                        response: function () {
                            return verificationService.getNewVerificationDetails(verifId)
                                .success(function (verification) {
                                    verification.id = verifId;
                                    verification.initialDate = verifDate;
	                                  if(verifReadStatus=='UNREAD'){
	                                	  $scope.markAsRead(verifId);
	                                  } 
                                   return verification;
                                });
                        }
                    }
                });
            };

            $scope.idsOfVerifications = [];
            $scope.checkedItems = [];
            $scope.allIsEmpty = true;

            $scope.resolveVerificationId = function (id) {

                var index = $scope.idsOfVerifications.indexOf(id);
                if (index === -1) {
                    $scope.idsOfVerifications.push(id);
                    index = $scope.idsOfVerifications.indexOf(id);
                }

                if (!$scope.checkedItems[index]) {
                    $scope.idsOfVerifications.splice(index, 1, id);
                    $scope.checkedItems.splice(index, 1, true);
                } else {
                    $scope.idsOfVerifications.splice(index, 1);
                    $scope.checkedItems.splice(index, 1);
                }
                checkForEmpty();
            };


            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: '/resources/app/calibrator/views/modals/verification-sending.html',
                        controller: 'SendingModalController',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return verificationService.getVerificators()
                                    .success(function (verificators) {
                                        return verificators;
                                    });
                            }
                        }
                    });

                    //executes when modal closing
                    modalInstance.result.then(function (calibrator) {
                     
                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            calibrator: calibrator
                        };

                        $log.info(dataToSend);

                        verificationService
                            .sendVerificationsToCalibrator(dataToSend)
                            .success(function () {
                            	$scope.tableParams.reload();
                            	$rootScope.$broadcast('verification-sent-to-verifikator');
                            });
                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                    });
                } else {
                    $scope.isClicked = true;
                }
            };

            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };
        }]);

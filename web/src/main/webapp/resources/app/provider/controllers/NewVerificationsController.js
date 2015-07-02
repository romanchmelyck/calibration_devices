angular
    .module('providerModule')
    .controller('NewVerificationsController', ['$scope', '$log',
                                               '$modal', 'VerificationService',
                                               '$rootScope', 'ngTableParams', '$filter',
        function ($scope, $log, $modal, verificationService, $rootScope, ngTableParams, $filter) {
                
        $scope.search = {
        		idText:"",
        		formattedDate :"2000",
        		lastNameText:"",
        		streetText:""
        }
        
        $scope.clearInput = function(){
        	$scope.search.text="";
        }
        
        $scope.doSearch = function() {
            $log.debug(' from scope d: ', $scope.dt);
	          
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
//								searchType: $scope.search.type,
//								searchText: $scope.search.text,
								
								searchById: $scope.search.idText,
								searchByDate: $scope.search.formattedDate,
					        	searchByLastName: $scope.search.lastNameText,
					        	searchByStreet: $scope.search.streetText
							}
						verificationService.searchNewVerifications(queryOptions).success(function(result) {
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
           
            /**
             * open modal
             */
            $scope.openDetails = function (verifId, verifDate, verifReadStatus) {
            	
                $modal.open({
                    animation: true,
                    templateUrl: '/resources/app/provider/views/modals/new-verification-details.html',
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

            /**
             * push verification id to array
             */
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

            /**
             * open modal
             */
            $scope.openSendingModal = function () {
                if (!$scope.allIsEmpty) {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: '/resources/app/provider/views/modals/verification-sending.html',
                        controller: 'SendingModalController',
                        size: 'md',
                        resolve: {
                            response: function () {
                                return verificationService.getCalibrators()
                                    .success(function (calibrators) {
                                        return calibrators;
                                    });
                            }
                        }
                    });

                    /**
                     * executes when modal closing
                     */
                    modalInstance.result.then(function (calibrator) {
                        $log.info(calibrator);

                        var dataToSend = {
                            idsOfVerifications: $scope.idsOfVerifications,
                            calibrator: calibrator
                        };

                        $log.info(dataToSend);

                        verificationService
                            .sendVerificationsToCalibrator(dataToSend)
                            .success(function () {
                            	$scope.tableParams.reload();
                            	$rootScope.$broadcast('verification-sent-to-calibrator');
                            });
                        $scope.idsOfVerifications = [];
                        $scope.checkedItems = [];
                       
                    });
                } else {
                    $scope.isClicked = true;
                }
            };
            /**
             * check if idsOfVerifications array is empty
             */
            var checkForEmpty = function () {
                $scope.allIsEmpty = $scope.idsOfVerifications.length === 0;
            };
           
            
            $scope.openState = {};
            $scope.openState.isOpen = false;
           
            $scope.today = function() {
                $scope.dt = new Date();
            };
            $scope.today();

            $scope.clear = function () {
                $scope.dt = null;
              };

             $scope.open = function($event) {
                $event.preventDefault();
                $event.stopPropagation();

                $scope.openState.isOpen = true;
              };

              $scope.dateOptions = {
                formatYear: 'yyyy',
                startingDay: 1
              };

              $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
              $scope.format = $scope.formats[0];

              
              $scope.changeDateToSend = function(val){
            	  $log.debug( 'befor change date '+ val);
            	  if(val.isUndefined){
            		  $scope.search.formattedDate = '2000';
            	  
            	  } else {
            		  var datefilter = $filter('date');
                	  $scope.search.formattedDate = datefilter(val, 'dd-MM-yyyy');
                	  $log.debug( 'after change date '+ $scope.search.formattedDate);
            	  }
              }
           
            
             var tomorrow = new Date();
              tomorrow.setDate(tomorrow.getDate() + 1);
              var afterTomorrow = new Date();
              afterTomorrow.setDate(tomorrow.getDate() + 2);
              $scope.events =
                [
                  {
                    date: tomorrow,
                    status: 'full'
                  },
                  {
                    date: afterTomorrow,
                    status: 'partially'
                  }
                ];

              $scope.getDayClass = function(date, mode) {
                if (mode === 'day') {
                  var dayToCheck = new Date(date).setHours(0,0,0,0);

                  for (var i=0;i<$scope.events.length;i++){
                    var currentDay = new Date($scope.events[i].date).setHours(0,0,0,0);

                    if (dayToCheck === currentDay) {
                      return $scope.events[i].status;
                    }
                  }
                }

                return '';
              };
        }]);

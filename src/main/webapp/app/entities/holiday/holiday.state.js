(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('holiday', {
            parent: 'entity',
            url: '/holiday',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Holidays'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/holiday/holidays.html',
                    controller: 'HolidayController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('holiday-detail', {
            parent: 'entity',
            url: '/holiday/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Holiday'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/holiday/holiday-detail.html',
                    controller: 'HolidayDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Holiday', function($stateParams, Holiday) {
                    return Holiday.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'holiday',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('holiday-detail.edit', {
            parent: 'holiday-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/holiday/holiday-dialog.html',
                    controller: 'HolidayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Holiday', function(Holiday) {
                            return Holiday.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('holiday.new', {
            parent: 'holiday',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/holiday/holiday-dialog.html',
                    controller: 'HolidayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                startDate: null,
                                endDate: null,
                                days: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('holiday', null, { reload: 'holiday' });
                }, function() {
                    $state.go('holiday');
                });
            }]
        })
        .state('holiday.edit', {
            parent: 'holiday',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/holiday/holiday-dialog.html',
                    controller: 'HolidayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Holiday', function(Holiday) {
                            return Holiday.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('holiday', null, { reload: 'holiday' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('holiday.delete', {
            parent: 'holiday',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/holiday/holiday-delete-dialog.html',
                    controller: 'HolidayDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Holiday', function(Holiday) {
                            return Holiday.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('holiday', null, { reload: 'holiday' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

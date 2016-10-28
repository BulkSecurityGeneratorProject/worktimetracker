(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('stand-in', {
            parent: 'entity',
            url: '/stand-in',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'StandIns'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stand-in/stand-ins.html',
                    controller: 'StandInController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('stand-in-detail', {
            parent: 'entity',
            url: '/stand-in/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'StandIn'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stand-in/stand-in-detail.html',
                    controller: 'StandInDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'StandIn', function($stateParams, StandIn) {
                    return StandIn.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'stand-in',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('stand-in-detail.edit', {
            parent: 'stand-in-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stand-in/stand-in-dialog.html',
                    controller: 'StandInDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['StandIn', function(StandIn) {
                            return StandIn.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stand-in.new', {
            parent: 'stand-in',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stand-in/stand-in-dialog.html',
                    controller: 'StandInDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('stand-in', null, { reload: 'stand-in' });
                }, function() {
                    $state.go('stand-in');
                });
            }]
        })
        .state('stand-in.edit', {
            parent: 'stand-in',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stand-in/stand-in-dialog.html',
                    controller: 'StandInDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['StandIn', function(StandIn) {
                            return StandIn.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stand-in', null, { reload: 'stand-in' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stand-in.delete', {
            parent: 'stand-in',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stand-in/stand-in-delete-dialog.html',
                    controller: 'StandInDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['StandIn', function(StandIn) {
                            return StandIn.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stand-in', null, { reload: 'stand-in' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

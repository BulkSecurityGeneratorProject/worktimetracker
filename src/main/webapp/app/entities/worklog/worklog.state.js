(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('worklog', {
            parent: 'entity',
            url: '/worklog',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Worklogs'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/worklog/worklogs.html',
                    controller: 'WorklogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('worklog-detail', {
            parent: 'entity',
            url: '/worklog/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Worklog'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/worklog/worklog-detail.html',
                    controller: 'WorklogDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Worklog', function($stateParams, Worklog) {
                    return Worklog.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'worklog',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('worklog-detail.edit', {
            parent: 'worklog-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worklog/worklog-dialog.html',
                    controller: 'WorklogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Worklog', function(Worklog) {
                            return Worklog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('worklog.new', {
            parent: 'worklog',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worklog/worklog-dialog.html',
                    controller: 'WorklogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                hours: null,
                                comment: null,
                                approved: false,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('worklog', null, { reload: 'worklog' });
                }, function() {
                    $state.go('worklog');
                });
            }]
        })
        .state('worklog.edit', {
            parent: 'worklog',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worklog/worklog-dialog.html',
                    controller: 'WorklogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Worklog', function(Worklog) {
                            return Worklog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('worklog', null, { reload: 'worklog' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('worklog.delete', {
            parent: 'worklog',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worklog/worklog-delete-dialog.html',
                    controller: 'WorklogDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Worklog', function(Worklog) {
                            return Worklog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('worklog', null, { reload: 'worklog' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

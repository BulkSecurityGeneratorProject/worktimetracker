(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('WorklogDetailController', WorklogDetailController);

    WorklogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Worklog', 'Project', 'User'];

    function WorklogDetailController($scope, $rootScope, $stateParams, previousState, entity, Worklog, Project, User) {
        var vm = this;

        vm.worklog = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('worktimetrackerApp:worklogUpdate', function(event, result) {
            vm.worklog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

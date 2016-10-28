(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('StandInDetailController', StandInDetailController);

    StandInDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'StandIn', 'Holiday', 'User'];

    function StandInDetailController($scope, $rootScope, $stateParams, previousState, entity, StandIn, Holiday, User) {
        var vm = this;

        vm.standIn = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('worktimetrackerApp:standInUpdate', function(event, result) {
            vm.standIn = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

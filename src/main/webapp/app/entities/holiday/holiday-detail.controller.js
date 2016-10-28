(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('HolidayDetailController', HolidayDetailController);

    HolidayDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Holiday', 'User'];

    function HolidayDetailController($scope, $rootScope, $stateParams, previousState, entity, Holiday, User) {
        var vm = this;

        vm.holiday = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('worktimetrackerApp:holidayUpdate', function(event, result) {
            vm.holiday = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

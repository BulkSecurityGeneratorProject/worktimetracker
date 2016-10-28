(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('HolidayController', HolidayController);

    HolidayController.$inject = ['$scope', '$state', 'Holiday'];

    function HolidayController ($scope, $state, Holiday) {
        var vm = this;
        
        vm.holidays = [];

        loadAll();

        function loadAll() {
            Holiday.query(function(result) {
                vm.holidays = result;
            });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('StandInController', StandInController);

    StandInController.$inject = ['$scope', '$state', 'StandIn'];

    function StandInController ($scope, $state, StandIn) {
        var vm = this;
        
        vm.standIns = [];

        loadAll();

        function loadAll() {
            StandIn.query(function(result) {
                vm.standIns = result;
            });
        }
    }
})();

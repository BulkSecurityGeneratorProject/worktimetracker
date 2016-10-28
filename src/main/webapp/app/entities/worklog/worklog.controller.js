(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('WorklogController', WorklogController);

    WorklogController.$inject = ['$scope', '$state', 'Worklog'];

    function WorklogController ($scope, $state, Worklog) {
        var vm = this;
        
        vm.worklogs = [];

        loadAll();

        function loadAll() {
            Worklog.query(function(result) {
                vm.worklogs = result;
            });
        }
    }
})();

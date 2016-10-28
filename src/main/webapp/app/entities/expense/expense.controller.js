(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('ExpenseController', ExpenseController);

    ExpenseController.$inject = ['$scope', '$state', 'DataUtils', 'Expense'];

    function ExpenseController ($scope, $state, DataUtils, Expense) {
        var vm = this;
        
        vm.expenses = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll() {
            Expense.query(function(result) {
                vm.expenses = result;
            });
        }
    }
})();

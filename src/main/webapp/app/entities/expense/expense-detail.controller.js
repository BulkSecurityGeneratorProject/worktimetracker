(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('ExpenseDetailController', ExpenseDetailController);

    ExpenseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Expense', 'User'];

    function ExpenseDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Expense, User) {
        var vm = this;

        vm.expense = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('worktimetrackerApp:expenseUpdate', function(event, result) {
            vm.expense = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('ExpenseDialogController', ExpenseDialogController);

    ExpenseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Expense', 'User'];

    function ExpenseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Expense, User) {
        var vm = this;

        vm.expense = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.expense.id !== null) {
                Expense.update(vm.expense, onSaveSuccess, onSaveError);
            } else {
                Expense.save(vm.expense, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('worktimetrackerApp:expenseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        vm.setReceipt = function ($file, expense) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        expense.receipt = base64Data;
                        expense.receiptContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

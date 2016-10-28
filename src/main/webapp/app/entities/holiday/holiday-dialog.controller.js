(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('HolidayDialogController', HolidayDialogController);

    HolidayDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Holiday', 'User'];

    function HolidayDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Holiday, User) {
        var vm = this;

        vm.holiday = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
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
            if (vm.holiday.id !== null) {
                Holiday.update(vm.holiday, onSaveSuccess, onSaveError);
            } else {
                Holiday.save(vm.holiday, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('worktimetrackerApp:holidayUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

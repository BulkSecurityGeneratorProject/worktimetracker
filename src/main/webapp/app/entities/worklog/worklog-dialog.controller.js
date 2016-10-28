(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('WorklogDialogController', WorklogDialogController);

    WorklogDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Worklog', 'Project', 'User'];

    function WorklogDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Worklog, Project, User) {
        var vm = this;

        vm.worklog = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.projects = Project.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.worklog.id !== null) {
                Worklog.update(vm.worklog, onSaveSuccess, onSaveError);
            } else {
                Worklog.save(vm.worklog, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('worktimetrackerApp:worklogUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();

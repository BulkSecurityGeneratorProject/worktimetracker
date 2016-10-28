(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('StandInDialogController', StandInDialogController);

    StandInDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'StandIn', 'Holiday', 'User'];

    function StandInDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, StandIn, Holiday, User) {
        var vm = this;

        vm.standIn = entity;
        vm.clear = clear;
        vm.save = save;
        vm.forholidays = Holiday.query({filter: 'standin-is-null'});
        $q.all([vm.standIn.$promise, vm.forholidays.$promise]).then(function() {
            if (!vm.standIn.forHoliday || !vm.standIn.forHoliday.id) {
                return $q.reject();
            }
            return Holiday.get({id : vm.standIn.forHoliday.id}).$promise;
        }).then(function(forHoliday) {
            vm.forholidays.push(forHoliday);
        });
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.standIn.id !== null) {
                StandIn.update(vm.standIn, onSaveSuccess, onSaveError);
            } else {
                StandIn.save(vm.standIn, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('worktimetrackerApp:standInUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

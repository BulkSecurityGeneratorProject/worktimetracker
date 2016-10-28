(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('HolidayDeleteController',HolidayDeleteController);

    HolidayDeleteController.$inject = ['$uibModalInstance', 'entity', 'Holiday'];

    function HolidayDeleteController($uibModalInstance, entity, Holiday) {
        var vm = this;

        vm.holiday = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Holiday.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

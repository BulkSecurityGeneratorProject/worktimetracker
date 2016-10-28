(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('StandInDeleteController',StandInDeleteController);

    StandInDeleteController.$inject = ['$uibModalInstance', 'entity', 'StandIn'];

    function StandInDeleteController($uibModalInstance, entity, StandIn) {
        var vm = this;

        vm.standIn = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            StandIn.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

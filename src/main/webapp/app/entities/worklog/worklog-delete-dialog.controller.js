(function() {
    'use strict';

    angular
        .module('worktimetrackerApp')
        .controller('WorklogDeleteController',WorklogDeleteController);

    WorklogDeleteController.$inject = ['$uibModalInstance', 'entity', 'Worklog'];

    function WorklogDeleteController($uibModalInstance, entity, Worklog) {
        var vm = this;

        vm.worklog = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Worklog.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

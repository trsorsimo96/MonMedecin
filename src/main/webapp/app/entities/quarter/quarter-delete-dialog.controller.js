(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('QuarterDeleteController',QuarterDeleteController);

    QuarterDeleteController.$inject = ['$uibModalInstance', 'entity', 'Quarter'];

    function QuarterDeleteController($uibModalInstance, entity, Quarter) {
        var vm = this;

        vm.quarter = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Quarter.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('ArrondissementDeleteController',ArrondissementDeleteController);

    ArrondissementDeleteController.$inject = ['$uibModalInstance', 'entity', 'Arrondissement'];

    function ArrondissementDeleteController($uibModalInstance, entity, Arrondissement) {
        var vm = this;

        vm.arrondissement = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Arrondissement.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

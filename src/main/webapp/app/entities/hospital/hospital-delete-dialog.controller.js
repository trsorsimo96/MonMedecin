(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('HospitalDeleteController',HospitalDeleteController);

    HospitalDeleteController.$inject = ['$uibModalInstance', 'entity', 'Hospital'];

    function HospitalDeleteController($uibModalInstance, entity, Hospital) {
        var vm = this;

        vm.hospital = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Hospital.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

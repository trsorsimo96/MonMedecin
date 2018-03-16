(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('MedecinDialogController', MedecinDialogController);

    MedecinDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Medecin', 'Service'];

    function MedecinDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Medecin, Service) {
        var vm = this;

        vm.medecin = entity;
        vm.clear = clear;
        vm.save = save;
        vm.services = Service.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.medecin.id !== null) {
                Medecin.update(vm.medecin, onSaveSuccess, onSaveError);
            } else {
                Medecin.save(vm.medecin, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('monMedecinApp:medecinUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

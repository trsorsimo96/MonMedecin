(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('HospitalDialogController', HospitalDialogController);

    HospitalDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Hospital', 'SubCategory', 'Quarter', 'Arrondissement'];

    function HospitalDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Hospital, SubCategory, Quarter, Arrondissement) {
        var vm = this;

        vm.hospital = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.subcategories = SubCategory.query();
        vm.quarters = Quarter.query();
        vm.arrondissements = Arrondissement.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.hospital.id !== null) {
                Hospital.update(vm.hospital, onSaveSuccess, onSaveError);
            } else {
                Hospital.save(vm.hospital, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('monMedecinApp:hospitalUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, hospital) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        hospital.image = base64Data;
                        hospital.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();

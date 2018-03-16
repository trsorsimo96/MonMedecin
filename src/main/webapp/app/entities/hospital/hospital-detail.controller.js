(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('HospitalDetailController', HospitalDetailController);

    HospitalDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Hospital', 'SubCategory', 'Quarter', 'Arrondissement'];

    function HospitalDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Hospital, SubCategory, Quarter, Arrondissement) {
        var vm = this;

        vm.hospital = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('monMedecinApp:hospitalUpdate', function(event, result) {
            vm.hospital = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

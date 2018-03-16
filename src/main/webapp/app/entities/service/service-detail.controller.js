(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('ServiceDetailController', ServiceDetailController);

    ServiceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Service', 'Hospital'];

    function ServiceDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Service, Hospital) {
        var vm = this;

        vm.service = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('monMedecinApp:serviceUpdate', function(event, result) {
            vm.service = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
